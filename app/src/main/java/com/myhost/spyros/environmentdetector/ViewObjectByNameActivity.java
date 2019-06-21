package com.myhost.spyros.environmentdetector;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.myhost.spyros.environmentdetector.Adapters.ViewAllObjectsAdapter;
import com.myhost.spyros.environmentdetector.Models.ViewAllObjectsModel;

import java.util.ArrayList;

public class ViewObjectByNameActivity extends AppCompatActivity {

    //declare intent variables
    String objectNameToSearch;


    //declare recycler view variables
    private RecyclerView recyclerView;
    private ViewAllObjectsAdapter viewAllObjectsAdapter;
    private ArrayList<ViewAllObjectsModel> objectsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_object_by_name);

        //get intent variables
        Intent intent = getIntent();
        objectNameToSearch = intent.getStringExtra("objectName");

        //init recycler view
        objectsList = new ArrayList<>();
        recyclerView = (RecyclerView) findViewById(R.id.recyler_view_object_by_name);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        setData();
    }

    private void setData(){
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
                    Query query = users.getRef().child("Objects").orderByChild("detectedObjectName").equalTo(objectNameToSearch);
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for(DataSnapshot snap : dataSnapshot.getChildren()){
                                snap.getRef();
                                ViewAllObjectsModel object = snap.getValue(ViewAllObjectsModel.class);
                                objectsList.add(object);
                            }
                            viewAllObjectsAdapter = new ViewAllObjectsAdapter(objectsList,ViewObjectByNameActivity.this,recyclerView);
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
