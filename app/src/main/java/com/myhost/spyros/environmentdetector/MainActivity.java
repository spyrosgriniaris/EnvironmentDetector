package com.myhost.spyros.environmentdetector;

import android.Manifest;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {




    //declare and init request codes for activity result
    private final int REQUEST_IMAGE_LABELING = 1;
    private final int LOCATION_PERMISSION_REQUEST_CODE = 2;
    private final int SIGN_OUT_REQUEST = 3;
    //---------------------------------------------------


    //declare views
    private Button detect_btn_main;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Toolbar set up
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //init views--------------------------------------------------------
        detect_btn_main = (Button) findViewById(R.id.detect_btn_main);
        detect_btn_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),ImageLabelingActivity.class);
                startActivityForResult(intent,REQUEST_IMAGE_LABELING);
            }
        });
        //------------------------------------------------------------------

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

    }


}

