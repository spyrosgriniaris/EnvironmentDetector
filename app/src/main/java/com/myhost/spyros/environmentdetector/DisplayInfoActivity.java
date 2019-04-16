package com.myhost.spyros.environmentdetector;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.TextView;
import android.support.v7.widget.CardView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DisplayInfoActivity extends AppCompatActivity {

    //variable to save the name of the object that was found
    public static String OBJECT_NAME_TO_BE_DISPLAYED;
    private final String TAG = "RealTime DB";

    //declare views
    TextView objectFoundName, objectFoundInfo,usersLatitudeTxtView,usersLongitudeTxtView,dateTxtView;
    CardView displayInfoCardView;

    private ObjectViewModel objectViewModel;

    //variables to store users current location, provided by GPS_Service through ImageLabelingActivity's Intent
    private double usersCurrentLatitude;
    private double usersCurrentLongitude;

    //variables to create FirebaseDetectionEntity to push in Firebase
    private String objectName;
    private double objectLatitude, objectLongitude;
    private long timestamp;

    //variable to check if a new entry of database must be pushed
    public static boolean shouldPushToDatabase = false;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_info);
        //Toast.makeText(getApplicationContext(), FirebaseAuth.getInstance().getCurrentUser().getEmail(),Toast.LENGTH_SHORT).show();

        //get information from the intent that called the activity
        Intent intent = getIntent();
        OBJECT_NAME_TO_BE_DISPLAYED = intent.getStringExtra("objectName");
        usersCurrentLatitude = intent.getDoubleExtra("users_current_latitude",0);
        usersCurrentLongitude = intent.getDoubleExtra("users_current_longitude",0);

        //init views
        objectFoundName = (TextView) findViewById(R.id.objectFoundName);
        objectFoundInfo = (TextView) findViewById(R.id.objectFoundInfo);
        objectFoundInfo.setMovementMethod(new ScrollingMovementMethod());
        usersLatitudeTxtView = (TextView) findViewById(R.id.latitude);
        usersLongitudeTxtView = (TextView) findViewById(R.id.longitude);
        dateTxtView = (TextView) findViewById(R.id.date);
        displayInfoCardView = (CardView) findViewById(R.id.displayInfoCardview);


        objectViewModel = ViewModelProviders.of(this).get(ObjectViewModel.class);

        objectViewModel.getObject(OBJECT_NAME_TO_BE_DISPLAYED).observe(this, new Observer<Object>() {
            @Override
            public void onChanged(@Nullable Object object) {
                if(object != null){
                    //init views
                    objectFoundName.setText(object.getName());
                    objectFoundInfo.setText(object.getObjectInfo());

                    //init Entity's variables
                    objectName = object.getName();
                }
                else{
                    objectName = "Not available";
                    if(OBJECT_NAME_TO_BE_DISPLAYED.isEmpty()){
                        objectFoundName.setText("---");
                        objectFoundInfo.setText("No result available");
                    }

                    else{
                        objectFoundName.setText(OBJECT_NAME_TO_BE_DISPLAYED);
                        objectFoundInfo.setText("There are not any information available for this object yet. Stay tuned!!");
                    }
                }
                if(usersCurrentLatitude == 0 || usersCurrentLongitude == 0){
                    //init views
                    usersLatitudeTxtView.setText("Latitude not available.");
                    usersLongitudeTxtView.setText("Longitude not available");
                    //init Entity's variables
                    objectLatitude = 0;
                    objectLongitude = 0;
                }
                else{
                    //init views
                    usersLatitudeTxtView.setText(String.valueOf(usersCurrentLatitude));
                    usersLongitudeTxtView.setText(String.valueOf(usersCurrentLongitude));
                    //init Entity's variables
                    objectLatitude = usersCurrentLatitude;
                    objectLongitude = usersCurrentLongitude;
                }
                Date currentDate = new Date(System.currentTimeMillis());
                dateTxtView.setText(currentDate.toString());
                timestamp = System.currentTimeMillis();
            }
        });
        if(objectViewModel.getObject(OBJECT_NAME_TO_BE_DISPLAYED).getValue() == null){
            if(OBJECT_NAME_TO_BE_DISPLAYED.isEmpty() || OBJECT_NAME_TO_BE_DISPLAYED == null)
                objectName = "Not available";
            else
                objectName = OBJECT_NAME_TO_BE_DISPLAYED;
            objectLatitude = usersCurrentLatitude;
            objectLongitude = usersCurrentLongitude;
            timestamp = System.currentTimeMillis();
        }
        if(shouldPushToDatabase)
            createEntity(objectName, objectLatitude, objectLongitude, timestamp);

    }

    private void createEntity(String objectName, double objectLatitude, double objectLongitude, long timestamp){
        FirebaseDetectionEntity entity = new FirebaseDetectionEntity(objectName, objectLatitude, objectLongitude, timestamp);
        if(shouldPushToDatabase)
            pushToDatabase(entity);
    }

    private void pushToDatabase(FirebaseDetectionEntity entity) {

        if (!objectName.equals("Not available")) {
            //handling writing data to Firebase
            FirebaseApp.initializeApp(this);
            Map<String, FirebaseDetectionEntity> object = new HashMap<>();
            object.put(entity.getDetectedObjectName(), entity);
            //FirebaseDatabase.getInstance("https://detectordata.firebaseio.com/").getReference("Objects").push().setValue(object);
            FirebaseDatabase.getInstance("https://detectordata.firebaseio.com/").getReference(
                    FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child("Objects").push().setValue(object);
        }
        shouldPushToDatabase = false;
    }
}
