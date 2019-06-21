package com.myhost.spyros.environmentdetector;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.myhost.spyros.environmentdetector.Adapters.ViewAllUsersAdapter;
import com.myhost.spyros.environmentdetector.Models.ViewAllUsersModel;

import java.util.ArrayList;

public class ViewAllUsersActivity extends AppCompatActivity {

    //declare recycler view variables
    private RecyclerView recyclerView;
    private ViewAllUsersAdapter viewAllUsersAdapter;
    private ArrayList<ViewAllUsersModel> usersList;

    //Firebase reference
    DatabaseReference dbUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_users);

        //init recycler view
        usersList = new ArrayList<>();
        recyclerView = (RecyclerView) findViewById(R.id.recyler_view_all_users);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        setRecyclerViewData();//adding data to usersList for Recycler View
    }


    private void setRecyclerViewData(){
        dbUsers = FirebaseDatabase.getInstance("https://detectordata.firebaseio.com/").getReference("roles");
        dbUsers.addListenerForSingleValueEvent(valueEventListener);
    }

    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            usersList.clear();
            if(dataSnapshot.exists()){
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    ViewAllUsersModel user = snapshot.getValue(ViewAllUsersModel.class);
                    usersList.add(user);
                }
            }
            viewAllUsersAdapter = new ViewAllUsersAdapter(ViewAllUsersActivity.this,usersList,R.layout.users_layout,recyclerView);
            recyclerView.setAdapter(viewAllUsersAdapter);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            Toast.makeText(getApplicationContext(),"Something went wrong.",Toast.LENGTH_SHORT).show();
        }
    };



}
