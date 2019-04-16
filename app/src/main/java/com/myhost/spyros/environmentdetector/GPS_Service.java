package com.myhost.spyros.environmentdetector;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

public class GPS_Service extends Service {

    private final long UPDATE_TIME_FREQUENCY = 300000; //5 minutes in millis
    private final float UPDATE_DISTANCE_FREQUENCY = 500;
    private static String LOG_TAG = "Service in Bind";

    private LocationListener locationListener;
    private LocationManager locationManager;

    //variable to check if service is on
    public static boolean isLocationServiceOn = false;

    private IBinder mBinder = new myServiceBinder();


    @Override
    public void onCreate() {
        isLocationServiceOn = true;
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                ImageLabelingActivity.usersCurrentLocation = location;
                sendLocation(location);
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {
                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
        };



        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, UPDATE_TIME_FREQUENCY, UPDATE_DISTANCE_FREQUENCY, locationListener);
    }





    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        isLocationServiceOn = true;
        return START_STICKY;
    }





    //method to send current location of user in DisplayInfoActivity
    //every time user changes location, activity receives the new coordinates
    private void sendLocation(Location location){
        Intent locationIntent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putParcelable("Location",location);
        locationIntent.putExtra("Location",bundle);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(locationIntent);
    }




    class myServiceBinder extends Binder{
        public GPS_Service getService(){
            return GPS_Service.this;
        }
    }



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.v(LOG_TAG,"In onBind");
        isLocationServiceOn = true;
        return mBinder;
    }


    @Override
    public void onRebind(Intent intent) {
        Log.v(LOG_TAG,"In onRebind");
        isLocationServiceOn = true;
    }


    @Override
    public boolean onUnbind(Intent intent) {
        Log.v(LOG_TAG,"In onUnbind");
        isLocationServiceOn = false;
        return true;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if(locationManager != null){
            locationManager.removeUpdates(locationListener);
        }
    }
}
