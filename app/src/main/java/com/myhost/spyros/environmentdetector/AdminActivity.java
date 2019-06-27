package com.myhost.spyros.environmentdetector;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.myhost.spyros.environmentdetector.Admin_Bottom_Navigation_Fragments.DateRangeFragment;
import com.myhost.spyros.environmentdetector.Admin_Bottom_Navigation_Fragments.SearchFragment;
import com.myhost.spyros.environmentdetector.Admin_Bottom_Navigation_Fragments.SpecificAreaFragment;
import com.myhost.spyros.environmentdetector.Models.User;



public class AdminActivity extends AppCompatActivity implements SearchFragment.SearchFragmentListener,
        DateRangeFragment.DateRangeFragmentListener, SpecificAreaFragment.SpecificAreaFragmentListener{


    //declare and init request codes for activity result
    private final int SIGN_OUT_REQUEST = 3;
    //---------------------------------------------------

    //declare fragments that Activity uses
    Fragment selectedFragment = null;



    //init Dialog and dialog Views for adding a new user
    private Dialog myDialog;
    private Button addUserBtn;
    private EditText FullNameEdTxt, EmailEdTxt, PasswordEdTxt;
    RelativeLayout rel;

    //variable that helps getting the intent's data from GPS_Service
    private Intent serviceIntentHelper;

    //variables for managing gps_Service binding and unbinding processes
    private boolean isServiceBound = false;
    private ServiceConnection serviceConnection;
    private GPS_Service locationService;

    //variable to store user's current Location
    public static Location usersCurrentLocation = null;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        //Toolbar set up
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //init Views
        rel = (RelativeLayout) findViewById(R.id.adminRelativeLayout);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation_admin);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
        bottomNav.setSelectedItemId(R.id.search_for_object_bottom_nav_admin);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_admin, new SearchFragment()).commit();



        //methods to get location from service
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(
                mMessageReceiver, new IntentFilter("location_update"));



    }



    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {


                    switch(menuItem.getItemId()){
                        case R.id.view_all_objects_bottom_nav_admin:
                            startActivity(new Intent(getApplicationContext(),ViewAllObjectsActivity.class));
                            break;
                        case R.id.view_all_users_bottom_nav_admin:
                            startActivity(new Intent(getApplicationContext(),ViewAllUsersActivity.class));
                            break;
                        case R.id.search_for_object_bottom_nav_admin:
                            selectedFragment = new SearchFragment();
                            break;
                        case R.id.search_in_date_range_bottom_nav_admin:
                            selectedFragment = new DateRangeFragment();
                            break;
                        case R.id.search_in_area_bottom_nav_admin:
                            selectedFragment = new SpecificAreaFragment();
                            break;
                    }
                    if(selectedFragment != null)
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_admin, selectedFragment).commit();
                    return true;
                }
            };









    //method to receive location from GPS_Service
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle b = intent.getBundleExtra("Location");
            Location loc = (Location) b.getParcelable("Location");
            usersCurrentLocation = loc;

        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        //if(!isServiceBound)
        bindService();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //if(isServiceBound)
        unbindService();
    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        if(isServiceBound)
//            unbindService();
//    }

    //method for unbinding from Service
    private void unbindService(){
        if(serviceConnection != null){
            LocalBroadcastManager.getInstance(AdminActivity.this).unregisterReceiver(
                    mMessageReceiver);
            isServiceBound = false;

        }
    }

    //method for binding in GPS_Service to get user's current Location
    private void bindService(){
        if(serviceConnection == null){
            serviceConnection = new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                    GPS_Service.myServiceBinder myServiceBinder = (GPS_Service.myServiceBinder)iBinder;
                    locationService = myServiceBinder.getService();
                    isServiceBound = true;
                }

                @Override
                public void onServiceDisconnected(ComponentName componentName) {
                    isServiceBound = false;
                }
            };
        }
        serviceIntentHelper = new Intent(AdminActivity.this,GPS_Service.class);
        LocalBroadcastManager.getInstance(AdminActivity.this).registerReceiver(mMessageReceiver,new IntentFilter("GetLocation"));
        bindService(serviceIntentHelper,serviceConnection,Context.BIND_AUTO_CREATE);
    }




    //methods from Search Fragment Listener -------------------------------------------------------------
    @Override
    public void getObjectToSearch(String objectToSearch) {
        if(!objectToSearch.trim().isEmpty() || objectToSearch.trim() == null){
            Intent intent = new Intent(getApplicationContext(),ViewObjectByNameActivity.class);
            intent.putExtra("objectName",objectToSearch);
            startActivity(intent);
        }
    }

    //-----------------------------------------------------------------------------------------------------



    //methods from Date Range Fragment Listener -----------------------------------------------------------
    @Override
    public void getDates(long date1, long date2) {
        Intent intent = new Intent(getApplicationContext(),ViewObjectsInDateRangeActivity.class);
        intent.putExtra("first_date_limit", date1);
        intent.putExtra("last_date_limit", date2);
        startActivity(intent);
    }

    //------------------------------------------------------------------------------------------------------



    //methods from Specific Area Fragment Listener ----------------------------------------------------------
    @Override
    public void getInfoForMap(double latitude, double longitude, int radius) {
        Intent intent = new Intent(getApplicationContext(), ViewObjectsInMapRangeActivity.class);
        intent.putExtra("selectedPointLatitude", latitude);
        intent.putExtra("selectedPointLongitude", longitude);
        intent.putExtra("radius", radius);
        startActivity(intent);
    }

    //-------------------------------------------------------------------------------------------------------







    //methods to control menu options in MainActivity-------------------------------------------------------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.admin_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case(R.id.action_add_user):
                callAddUserDialog();
            case R.id.action_settings:
                return true;
            case R.id.action_sign_out:
                signOut();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    //-------------------------------------------------------------------------------------------------------

    //method to sign out from current user
    private void signOut(){
        FirebaseAuth.getInstance().signOut();
        finish();
        startActivityForResult(new Intent(AdminActivity.this,LoginActivity.class),SIGN_OUT_REQUEST);
    }

    //method for pop up dialog to add a new user
    private void callAddUserDialog(){
        myDialog = new Dialog(this);
        myDialog.setContentView(R.layout.add_user_pop_up_dialog);
        myDialog.setCancelable(true);

        addUserBtn = (Button) myDialog.findViewById(R.id.btn_add_user);
        FullNameEdTxt = (EditText) myDialog.findViewById(R.id.fullNameEditTextDialog);
        EmailEdTxt = (EditText) myDialog.findViewById(R.id.emailEditTextDialog);
        PasswordEdTxt = (EditText) myDialog.findViewById(R.id.passwordEditTextDialog);

        myDialog.show();

        addUserBtn.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                if (check_inputs(EmailEdTxt.getText().toString().trim(), PasswordEdTxt.getText().toString().trim(), FullNameEdTxt.getText().toString().trim())) {
                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(EmailEdTxt.getText().toString().trim(),PasswordEdTxt.getText().toString().trim())
                            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    User user = new User();
                                    user.setId(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                    user.setEmail(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                                    user.setRole("user");
                                    FirebaseDatabase.getInstance("https://detectordata.firebaseio.com/").getReference("roles")
                                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user);
                                    Toast.makeText(getApplicationContext(),"User Added",Toast.LENGTH_SHORT).show();
                                    myDialog.dismiss();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else{
                    Toast.makeText(getApplicationContext(),"Check input fields again.",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean check_inputs(String email, String password, String fullName){
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches() || password.isEmpty() || password == null || fullName.isEmpty() || fullName == null)
            return false;
        else
            return true;
    }




}
