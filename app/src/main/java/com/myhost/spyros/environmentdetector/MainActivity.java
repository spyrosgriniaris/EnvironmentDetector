package com.myhost.spyros.environmentdetector;

import android.Manifest;
import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.myhost.spyros.environmentdetector.Adapters.WheelImageAdapter;
import com.myhost.spyros.environmentdetector.Data.ImageData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import github.hellocsl.cursorwheel.CursorWheelLayout;

public class MainActivity extends AppCompatActivity implements CursorWheelLayout.OnMenuSelectedListener {


    //variables to get info about location from latitude and longitude of user
    Geocoder geocoder;
    List<Address> addresses;


    //Cursor Wheel Picker
    CursorWheelLayout wheel_image;
    List<ImageData> lstImage;
    private int first_selection = 0;//used to avoid going to next activity while opening MainActivity for the first time

    //declare and init request codes for activity result
    private final int REQUEST_IMAGE_LABELING = 1;
    private final int LOCATION_PERMISSION_REQUEST_CODE = 2;
    private final int SIGN_OUT_REQUEST = 3;
    private final int REQUEST_GALLERY = 4;
    //---------------------------------------------------

    //variable that helps getting the intent's data from GPS_Service
    private Intent serviceIntentHelper;

    //variables for managing gps_Service binding and unbinding processes
    private boolean isServiceBound = false;
    private ServiceConnection serviceConnection;
    private GPS_Service locationService;

    //variable to store user's current Location
    public static Location usersCurrentLocation = null;


    //declare views
    TextView profileTxt, locationTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //methods to get location from service
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(
                mMessageReceiver, new IntentFilter("location_update"));

        bindService();

        //Toolbar set up
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initViews();
        loadData();
        wheel_image.setOnMenuSelectedListener(this);



        //permission check for GPS-------------------------------------------------------------------------
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }
        //--------------------------------------------------------------------------------------------------

        //enable GPS Service if not Active
        if(!GPS_Service.isLocationServiceOn)
            startService(new Intent(this,GPS_Service.class));


    }

    private void initViews(){
        wheel_image = (CursorWheelLayout)findViewById(R.id.wheelImage);
        profileTxt = (TextView) findViewById(R.id.profile_lbl_main);
        locationTxt = (TextView) findViewById(R.id.location_lbl_main);
    }


    private void loadData(){
        lstImage = new ArrayList<>();
        lstImage.add(new ImageData(R.drawable.pic_archive,"Archive"));
        lstImage.add(new ImageData(R.drawable.pic_camera,"Camera"));
        lstImage.add(new ImageData(R.drawable.pic_gallery,"Gallery"));
        lstImage.add(new ImageData(R.drawable.pic_profile,"Profile"));

        WheelImageAdapter imageAdapter = new WheelImageAdapter(getBaseContext(),lstImage);
        wheel_image.setAdapter(imageAdapter);

        profileTxt.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());

        if(usersCurrentLocation == null)
            locationTxt.setText("Not available");
        else{
            geocoder = new Geocoder(getApplicationContext(),Locale.getDefault());
            try {
                addresses = geocoder.getFromLocation(usersCurrentLocation.getLatitude(),usersCurrentLocation.getLongitude(),1);
                String city = addresses.get(0).getLocality();
                String country = addresses.get(0).getCountryName();
                locationTxt.setText(city+", "+country);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }



    }


    //method to receive location from GPS_Service
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle b = intent.getBundleExtra("Location");
            Location loc = (Location) b.getParcelable("Location");
            usersCurrentLocation = loc;

            if(usersCurrentLocation!=null) {
                locationTxt.setText(String.valueOf(usersCurrentLocation.getLatitude()));
                geocoder = new Geocoder(getApplicationContext(),Locale.getDefault());
                try {
                    addresses = geocoder.getFromLocation(usersCurrentLocation.getLatitude(),usersCurrentLocation.getLongitude(),1);
                    String city = addresses.get(0).getLocality();
                    String country = addresses.get(0).getCountryName();
                    locationTxt.setText(city+", "+country);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        unbindService();
    }

    //method for unbinding from Service
    private void unbindService(){
        if(isServiceBound && serviceConnection != null){
            LocalBroadcastManager.getInstance(MainActivity.this).unregisterReceiver(
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
        serviceIntentHelper = new Intent(MainActivity.this,GPS_Service.class);
        LocalBroadcastManager.getInstance(MainActivity.this).registerReceiver(mMessageReceiver,new IntentFilter("GetLocation"));
        bindService(serviceIntentHelper,serviceConnection,Context.BIND_AUTO_CREATE);
    }



    //methods to control menu options in MainActivity-------------------------------------------------------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
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
        startActivityForResult(new Intent(MainActivity.this,LoginActivity.class),SIGN_OUT_REQUEST);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if(requestCode == REQUEST_IMAGE_LABELING && resultCode == RESULT_OK){
            startActivity(intent);
        }
        else if(requestCode == SIGN_OUT_REQUEST && resultCode == RESULT_OK){
            startActivity(intent);
        }
        else if(requestCode == REQUEST_GALLERY && resultCode == RESULT_OK){
            startActivity(intent);
        }

    }


    @Override
    public void onItemSelected(CursorWheelLayout parent, View view, int pos) {
        if(first_selection == 0)
            first_selection++;
        else{
            if(lstImage.get(pos).imageDescription.equals("Camera")) {
                Intent intent = new Intent(getApplicationContext(), ImageLabelingActivity.class);
                startActivityForResult(intent, REQUEST_IMAGE_LABELING);
            }
            else if(lstImage.get(pos).imageDescription.equals("Gallery")){
                Intent intent = new Intent(getApplicationContext(),DetectFromGalleryActivity.class);
                startActivityForResult(intent,REQUEST_GALLERY);
            }
            else if(lstImage.get(pos).imageDescription.equals("Archive")){

            }
            else if(lstImage.get(pos).imageDescription.equals("Profile")){
                Intent intent = new Intent(getApplicationContext(),ViewUserProfileActivity.class);
                startActivity(intent);
            }

        }

    }
}

