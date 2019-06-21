package com.myhost.spyros.environmentdetector;

import android.location.Address;
import android.location.Geocoder;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.myhost.spyros.environmentdetector.Adapters.ViewAllUsersAdapter;
import com.myhost.spyros.environmentdetector.Models.ViewAllObjectsModel;
import com.myhost.spyros.environmentdetector.Models.ViewAllUsersModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class ViewUserProfileActivity extends AppCompatActivity {

    //declare recycler view variables
    //ViewAllUsersModel user;
    TextView emailTxt, objectsDetected, locationsDetected, emailInfo, fullNameInfo, mostDetectedObjInfo, cityOfMostDetectedObj;

    //this arraylist will store all locations that user has detected an object
    ArrayList<String> locations;
    ArrayList<String> objectsDetectedList;


    //Firebase reference
    DatabaseReference dbUsers;

    //variables to get info about location from latitude and longitude of user
    Geocoder geocoder;
    List<Address> addresses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_user_profile);

        //init views and variables
        //user = new ViewAllUsersModel();
        locations = new ArrayList<String>();
        objectsDetectedList = new ArrayList<String>();
        emailTxt = (TextView) findViewById(R.id.user_email_profile);
        objectsDetected = (TextView) findViewById(R.id.objects_detected_profile);
        locationsDetected = (TextView) findViewById(R.id.locations_detected_profile);
        emailInfo = (TextView) findViewById(R.id.emailInfoProfile);
        fullNameInfo = (TextView) findViewById(R.id.fullNameInfoProfile);
        mostDetectedObjInfo = (TextView) findViewById(R.id.mostDetectedObjInfoProfile);
        cityOfMostDetectedObj = (TextView) findViewById(R.id.mostDetectedObjectInCityInfoProfile);

        //call methods to set Data to views
        getUserInfo();
        getNumberOfObjectsDetectedForUser();
        getNumberOfLocationsForUser(); //gets number of different locations that user has detected an object
        findMostDetectedObject();
    }

    private void getUserInfo(){
        dbUsers = FirebaseDatabase.getInstance("https://detectordata.firebaseio.com/").getReference("roles").child(
                FirebaseAuth.getInstance().getCurrentUser().getUid()
        );
        dbUsers.addListenerForSingleValueEvent(valueEventListener);
    }

    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if(dataSnapshot.exists()){
                //for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    ViewAllUsersModel current_user = dataSnapshot.getValue(ViewAllUsersModel.class);
                    //user = current_user;
                    emailTxt.setText(current_user.getUserEmail());
                    emailInfo.setText(current_user.getUserEmail());
                //}
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            Toast.makeText(getApplicationContext(),"Something went wrong.",Toast.LENGTH_SHORT).show();
        }
    };




    private void getNumberOfObjectsDetectedForUser(){
        Query query = FirebaseDatabase.getInstance("https://detectordata.firebaseio.com/").getReference("Users").child(
                FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Objects");
        query.addListenerForSingleValueEvent(valueEventListenerForNumberOfObjects);
    }

    ValueEventListener valueEventListenerForNumberOfObjects = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if(dataSnapshot.exists()){
                objectsDetected.setText(String.valueOf(dataSnapshot.getChildrenCount()));
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };




    private void getNumberOfLocationsForUser(){
        Query query = FirebaseDatabase.getInstance("https://detectordata.firebaseio.com/").getReference("Users").child(
                FirebaseAuth.getInstance().getCurrentUser().getUid()
        )
                .child("Objects");
        query.addListenerForSingleValueEvent(valueEventListenerForLocations);
    }


    //method to get different Locations that user has detected an object, based on lat/long of each object in database
    ValueEventListener valueEventListenerForLocations = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            //We will use cities to find the town with most detected objects
            //we pass cities in method findCityOfMostDetectedObjects
            ArrayList<String> cities = new ArrayList<>();
            if(dataSnapshot.exists()){
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    snapshot.getRef();
                    ViewAllObjectsModel object = snapshot.getValue(ViewAllObjectsModel.class);
                    //objectsDetectedList.add(object.getDetectedObjectName());
                    geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                    try {
                        String city = "";
                        addresses = geocoder.getFromLocation(object.getDetectedObjectLatitude(),object.getDetectedObjectLongitude(),1);
                        if(addresses.size() > 0){
                            city = addresses.get(0).getLocality();
                            cities.add(city);
                        }

                        if(!locations.contains(city))
                            locations.add(city);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            findCityOfMostDetectedObjects(cities);
            locationsDetected.setText(String.valueOf(locations.size()));

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };





    private void findMostDetectedObject(){
        //we will use objectDetectedList which contains the objects user has detected
        //and we will find most common

        Query query = FirebaseDatabase.getInstance("https://detectordata.firebaseio.com/").getReference("Users").child(
                FirebaseAuth.getInstance().getCurrentUser().getUid()
        )
                .child("Objects");
        query.addListenerForSingleValueEvent(valueEventListenerForMostDetectedObject);

    }

    ValueEventListener valueEventListenerForMostDetectedObject = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if(dataSnapshot.exists()){
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    snapshot.getRef();
                    ViewAllObjectsModel object = snapshot.getValue(ViewAllObjectsModel.class);
                    objectsDetectedList.add(object.getDetectedObjectName());
                }
            }

            HashMap<String, Integer> map = new HashMap<>();

            for(int i = 0; i < objectsDetectedList.size(); i++){
                String object = objectsDetectedList.get(i);
                if(map.containsKey(object)){
                    int frequency = map.get(object);
                    frequency++;
                    map.put(object,frequency);
                }
                else{
                    map.put(object, 1);
                }
            }

            // find max frequency.
            int max_count = 0;
            String object = "-";

            for(Map.Entry<String, Integer> val : map.entrySet())
            {
                if (max_count < val.getValue())
                {
                    object = val.getKey();
                    max_count = val.getValue();
                }
            }

            mostDetectedObjInfo.setText(object+"  ("+max_count+")");
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };



    private void findCityOfMostDetectedObjects(ArrayList<String> cities){
        HashMap<String, Integer> map = new HashMap<>();

        for(int i = 0; i < cities.size(); i++){
            String city = cities.get(i);
            if(map.containsKey(city)){
                int frequency = map.get(city);
                frequency++;
                map.put(city,frequency);
            }
            else{
                map.put(city, 1);
            }
        }

        // find max frequency.
        int max_count = 0;
        String object = "-";

        for(Map.Entry<String, Integer> val : map.entrySet())
        {
            if (max_count < val.getValue())
            {
                object = val.getKey();
                max_count = val.getValue();
            }
        }

        cityOfMostDetectedObj.setText(object+"  ("+max_count+")");
    }

}
