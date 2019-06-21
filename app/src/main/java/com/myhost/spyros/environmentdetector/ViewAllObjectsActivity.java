package com.myhost.spyros.environmentdetector;

import android.provider.ContactsContract;
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
import com.myhost.spyros.environmentdetector.Adapters.ViewAllUsersAdapter;
import com.myhost.spyros.environmentdetector.Models.ViewAllObjectsModel;
import com.myhost.spyros.environmentdetector.Models.ViewAllUsersModel;

import java.util.ArrayList;

public class ViewAllObjectsActivity extends AppCompatActivity {


    //Firebase reference
    DatabaseReference dbObjects;

    //declare recycler view variables
    private RecyclerView recyclerView;
    private ViewAllObjectsAdapter viewAllObjectsAdapter;
    private ArrayList<ViewAllObjectsModel> objectsList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_objects);

        //init recycler view
        objectsList = new ArrayList<>();
        recyclerView = (RecyclerView) findViewById(R.id.recyler_view_all_objects);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        setRecyclerViewData();
    }

    private void setRecyclerViewData(){
        Query query = FirebaseDatabase.getInstance("https://detectordata.firebaseio.com/").getReference("Users");
        query.addListenerForSingleValueEvent(valueEventListener);
    }

    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            objectsList.clear();
            if(dataSnapshot.exists()){
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Log.d("prwto foreach: ",snapshot.getKey());//pairnei to id kathe xristi katw apo to users
                    for(DataSnapshot objectData : snapshot.child("Objects").getChildren()){//to object data exei kathe id tou antikeimenou pou entopistike
                        //for(DataSnapshot objectName : objectData.getChildren()){//periexei to onoma gia kathe antikeimeno kathe xristi
                            //Log.d("edw: ", objectName.getKey().toString()); deixnei to onoma kathe antikeimeniu
                            objectData.getRef();
                            ViewAllObjectsModel object = objectData.getValue(ViewAllObjectsModel.class);
                            //Log.d("timestamp",String.valueOf(object.getTimestamp()));  pairnei to timestamp kathe antikeimenou
                            objectsList.add(object);
                        //}
                    }
                }
            }
            viewAllObjectsAdapter = new ViewAllObjectsAdapter(objectsList,ViewAllObjectsActivity.this,recyclerView);
            recyclerView.setAdapter(viewAllObjectsAdapter);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };
}
