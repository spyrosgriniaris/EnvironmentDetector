package com.myhost.spyros.environmentdetector;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.myhost.spyros.environmentdetector.Adapters.ViewAllObjectsAdapter;
import com.myhost.spyros.environmentdetector.Models.ViewAllObjectsModel;

import java.util.ArrayList;
import java.util.List;

public class ViewObjectsInDateRangeActivity extends AppCompatActivity {

    //declare intent variables
    long first_date_limit, last_date_limit;


    //declare recycler view variables
    private RecyclerView recyclerView;
    private ViewAllObjectsAdapter viewAllObjectsAdapter;
    private ArrayList<ViewAllObjectsModel> objectsList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_objects_in_date_range);

        //get intent variables
        Intent intent = getIntent();
        first_date_limit = intent.getLongExtra("first_date_limit",0);
        last_date_limit = intent.getLongExtra("last_date_limit", 0);

        //init recycler view
        objectsList = new ArrayList<>();
        recyclerView = (RecyclerView) findViewById(R.id.recyler_view_objects_in_date_range);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        getUserIdsAndSetData();
        //setData();
    }



    private void getUserIdsAndSetData(){
        objectsList.clear();
        Query query = FirebaseDatabase.getInstance("https://detectordata.firebaseio.com/").getReference("Users");
        query.addListenerForSingleValueEvent(valueEventListener);

    }


    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

            if(dataSnapshot.exists()){
                for(DataSnapshot users : dataSnapshot.getChildren()){
                    Log.d("prwto foreach: ",users.getKey());//pairnei to id kathe xristi katw apo to users
                    Query query = users.getRef().child("Objects").orderByChild("timestamp").startAt(first_date_limit).endAt(last_date_limit);
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            Log.d("Rooms", String.valueOf(dataSnapshot.getChildrenCount()));
                            for(DataSnapshot snap : dataSnapshot.getChildren()){
                                snap.getRef();
                                ViewAllObjectsModel object = snap.getValue(ViewAllObjectsModel.class);
                                objectsList.add(object);
                            }
                            viewAllObjectsAdapter = new ViewAllObjectsAdapter(objectsList,ViewObjectsInDateRangeActivity.this,recyclerView);
                            recyclerView.setAdapter(viewAllObjectsAdapter);

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };



}
