package com.myhost.spyros.environmentdetector;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.location.Geocoder;
import android.location.Location;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.myhost.spyros.environmentdetector.Adapters.OptionsSpinnerAdminCustomAdapter;
import com.myhost.spyros.environmentdetector.Data.OptionsSpinnerAdminData;
import com.myhost.spyros.environmentdetector.Models.User;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AdminActivity extends AppCompatActivity implements View.OnClickListener {


    //declare and init request codes for activity result
    public static final int REQUEST_MAPS = 1;
    private final int SIGN_OUT_REQUEST = 3;
    //---------------------------------------------------


    //declare views for search object layout
    EditText objectToSearch;
    Button searchObjectSubmit;

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

    //declare variables for latitude and longitude admin selected from map activity and from number picker
    double selected_latitude_from_map;
    double selected_longitude_from_map;
    //int selected_radius;
    int number_of_values = 20; //number of options in number picker
    String[] displayedValues = new String[number_of_values];


    //Declare Views
    Spinner optionsSpinner;

    //declare views and helper variables of date range layout
    TextView first_date_display, last_date_display;
    Button submit;
    FloatingActionButton date1, date2;
    Calendar calendar;
    DatePickerDialog datePickerDialog;
    String date1_picked, date2_picked;

    //declare views and helper variables of map point selection
    Button submit_map_selection_point;
    ImageView openMap;
    NumberPicker np;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        //Toolbar set up
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //init Views
        optionsSpinner = (Spinner) findViewById(R.id.optionsSpinnerAdmin);
        rel = (RelativeLayout) findViewById(R.id.adminRelativeLayout);

        //loads and sets listeners for spinner
        optionsSpinnerLoad();

        //methods to get location from service
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(
                mMessageReceiver, new IntentFilter("location_update"));

//        if(!isServiceBound)
//            bindService();

    }

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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == REQUEST_MAPS){
            if(resultCode == Activity.RESULT_OK){
                selected_latitude_from_map = data.getDoubleExtra("selected_latitude",0);
                selected_longitude_from_map = data.getDoubleExtra("selected_longitude",0);
            }
        }
    }

    private void optionsSpinnerLoad(){
        //custom spinner init
        final List<OptionsSpinnerAdminData> adminSpinnerDataList = new ArrayList<>();
        adminSpinnerDataList.add(new OptionsSpinnerAdminData(R.drawable.ic_check,"Select"));
        adminSpinnerDataList.add(new OptionsSpinnerAdminData(R.drawable.ic_account,"View All Users"));
        adminSpinnerDataList.add(new OptionsSpinnerAdminData(R.drawable.ic_search,"Search for Object"));
        adminSpinnerDataList.add(new OptionsSpinnerAdminData(R.drawable.ic_date_range,"View Objects in Date Range"));
        adminSpinnerDataList.add(new OptionsSpinnerAdminData(R.drawable.ic_map,"View Objects in Specific Area"));
        adminSpinnerDataList.add(new OptionsSpinnerAdminData(R.drawable.ic_select_all,"View All Objects"));

        //init custom adapter
        OptionsSpinnerAdminCustomAdapter optionsSpinnerAdminCustomAdapter =
                new OptionsSpinnerAdminCustomAdapter(AdminActivity.this,R.layout.options_spinner_admin,adminSpinnerDataList);
        optionsSpinner.setAdapter(optionsSpinnerAdminCustomAdapter);

        optionsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                rel.removeAllViews();
                setCustomOptionsView(adminSpinnerDataList.get(i).getIconName());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }


    //method that changes layout with choices based on selected item from spinner
    private  void setCustomOptionsView(String choice){
        if(choice.equals("View All Users")){
            rel.removeAllViews();
            View child = getLayoutInflater().inflate(R.layout.admin_default_selection,null);
            rel.addView(child);
            startActivity(new Intent(getApplicationContext(),ViewAllUsersActivity.class));
        }
        else if(choice.equals("View Objects in Date Range")){
            rel.removeAllViews();
            View child = getLayoutInflater().inflate(R.layout.select_date_range_admin,null);
            rel.addView(child);

            //init views of date range layout
            first_date_display = (TextView)child.findViewById(R.id.first_date_display_admin);
            last_date_display = (TextView)child.findViewById(R.id.last_date_display_admin);
            submit = (Button)child.findViewById(R.id.date_range_submit_btn_admin);
            date1 = (FloatingActionButton)child.findViewById(R.id.pick_first_date_btn);
            date2 = (FloatingActionButton)child.findViewById(R.id.pick_last_date_btn);
            date2.setEnabled(false);//na to kanw true

            date1.setOnClickListener(this);
            date2.setOnClickListener(this);
            submit.setOnClickListener(this);


            Toast.makeText(getApplicationContext(),choice,Toast.LENGTH_SHORT).show();
        }
        else if(choice.equals("View Objects in Specific Area")){
            rel.removeAllViews();
            View child = getLayoutInflater().inflate(R.layout.select_point_from_map_admin,null);
            rel.addView(child);

            //init views of select point from map layout
            submit_map_selection_point = (Button)child.findViewById(R.id.admin_select_from_map_submit_button);
            submit_map_selection_point.setOnClickListener(this);

            openMap = (ImageView)child.findViewById(R.id.select_from_map_icon);
            openMap.setOnClickListener(this);

            //init number picker and its values
            for(int i = 0; i < number_of_values; i++){
                displayedValues[i] = String.valueOf(500*(i+1)); //increase value of number picker by 500
            }

            np = (NumberPicker)child.findViewById(R.id.number_picker_admin);
            np.setMinValue(0);
            np.setMaxValue(displayedValues.length-1);
            np.setDisplayedValues(displayedValues);
            np.setValue(1000);
            np.setWrapSelectorWheel(true);
            /* how to get the current value
            *chosenValue = displayedValues[numPicker.getValue()];
             *
            * */


        }
        else if(choice.equals("View All Objects")){
            rel.removeAllViews();
            View child = getLayoutInflater().inflate(R.layout.admin_default_selection,null);
            rel.addView(child);
            startActivity(new Intent(getApplicationContext(),ViewAllObjectsActivity.class));
        }
        else if(choice.equals("Select")){
            rel.removeAllViews();
            View child = getLayoutInflater().inflate(R.layout.admin_default_selection,null);
            rel.addView(child);
        }
        else if(choice.equals("Search for Object")){
            rel.removeAllViews();
            View child = getLayoutInflater().inflate(R.layout.search_object_layout,null);
            rel.addView(child);

            //init Views from search Object layout
            objectToSearch = (EditText)child.findViewById(R.id.search_object_edTxt);
            searchObjectSubmit = (Button)child.findViewById(R.id.search_object_submit_btn);

            searchObjectSubmit.setOnClickListener(this);
        }
    }




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

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.date_range_submit_btn_admin){
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
            Date first_date = null;
            Date last_date = null;
            try {
                first_date = sdf.parse(date1_picked);
                last_date = sdf.parse(date2_picked);
            } catch (ParseException e) {
            }

            if(first_date != null && last_date != null){
                Intent intent = new Intent(getApplicationContext(),ViewObjectsInDateRangeActivity.class);
                intent.putExtra("first_date_limit", first_date.getTime());
                intent.putExtra("last_date_limit", last_date.getTime());
                startActivity(intent);
            }
            else
                Toast.makeText(getApplicationContext(),"Pick Dates",Toast.LENGTH_SHORT).show();



        }
        else if(view.getId() == R.id.pick_first_date_btn){
            calendar = Calendar.getInstance();
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int month = calendar.get(Calendar.MONTH);
            int year = calendar.get(Calendar.YEAR);

            datePickerDialog = new DatePickerDialog(AdminActivity.this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int mYear, int mMonth, int mDay) {
                    first_date_display.setText((mMonth+1)+"/"+mDay+"/"+mYear);
                    date1_picked = (mMonth+1)+"/"+mDay+"/"+mYear;
                    date2.setEnabled(true);//enables second calendar button when first date has been clicked
                }
            },day,month,year);
            datePickerDialog.updateDate(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.setCancelable(true);
            datePickerDialog.show();

        }
        else if(view.getId() == R.id.pick_last_date_btn){
            calendar = Calendar.getInstance();
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int month = calendar.get(Calendar.MONTH);
            int year = calendar.get(Calendar.YEAR);

            datePickerDialog = new DatePickerDialog(AdminActivity.this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int mYear, int mMonth, int mDay) {
                    last_date_display.setText((mMonth+1)+"/"+mDay+"/"+mYear);
                    date2_picked = (mMonth+1)+"/"+mDay+"/"+mYear;

                }
            },day,month,year);
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
            Date first_date = null;
            try {
                first_date = sdf.parse(date1_picked);
            } catch (ParseException e) {
            }
            datePickerDialog.getDatePicker().setMinDate(first_date.getTime());
            //datePickerDialog.updateDate(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.setCancelable(true);
            datePickerDialog.show();

        }
        else if(view.getId() == R.id.admin_select_from_map_submit_button){
            Intent intent = new Intent(getApplicationContext(),ViewObjectsInMapRangeActivity.class);
            intent.putExtra("selectedPointLatitude", selected_latitude_from_map);
            intent.putExtra("selectedPointLongitude", selected_longitude_from_map);
            intent.putExtra("radius", Integer.valueOf(displayedValues[np.getValue()]));
            startActivity(intent);
            //Toast.makeText(getApplicationContext(),String.valueOf(displayedValues[np.getValue()]),Toast.LENGTH_SHORT).show();
        }
        else if(view.getId() == R.id.select_from_map_icon){
            Intent openMapIntent = new Intent(getApplicationContext(),AdminMapsActivity.class);
            if(usersCurrentLocation == null){
                openMapIntent.putExtra("admin_latitude", 0);
                openMapIntent.putExtra("admin_longitude", 0);
            }
            else{
                openMapIntent.putExtra("admin_latitude", usersCurrentLocation.getLatitude());
                openMapIntent.putExtra("admin_longitude", usersCurrentLocation.getLongitude());
            }

            startActivityForResult(openMapIntent,REQUEST_MAPS);
        }
        else if(view.getId() == R.id.search_object_submit_btn){
            if(!objectToSearch.getText().toString().isEmpty() || objectToSearch.getText().toString() == null)
            {
                Intent intent = new Intent(getApplicationContext(),ViewObjectByNameActivity.class);
                intent.putExtra("objectName",objectToSearch.getText().toString());
                startActivity(intent);
            }

        }

    }


}
