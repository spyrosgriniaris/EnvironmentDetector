package com.myhost.spyros.environmentdetector;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.label.FirebaseVisionCloudImageLabelerOptions;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabel;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabeler;
import com.google.firebase.ml.vision.label.FirebaseVisionOnDeviceImageLabelerOptions;
import com.wonderkiln.camerakit.CameraKitError;
import com.wonderkiln.camerakit.CameraKitEvent;
import com.wonderkiln.camerakit.CameraKitEventListener;
import com.wonderkiln.camerakit.CameraKitImage;
import com.wonderkiln.camerakit.CameraKitVideo;
import com.wonderkiln.camerakit.CameraView;

import java.util.List;

import dmax.dialog.SpotsDialog;

public class ImageLabelingActivity extends AppCompatActivity {

    static final int REQUEST_OBJECT_INFO = 1;

    //init views
    CameraView cameraView;
    Button btnDetectImgLbl;
    Button btnClearImgLbl;
    AlertDialog waitingDialog;

    //variables for managing gps_Service binding and unbinding processes
    private boolean isServiceBound = false;
    private ServiceConnection serviceConnection;
    private GPS_Service locationService;

    //variable that helps getting the intent's data from GPS_Service
    private Intent serviceIntentHelper;

    //variable to store user's current Location
    public static Location usersCurrentLocation = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_labeling);
        //startService(new Intent(this,GPS_Service.class));
        //init views
        btnClearImgLbl = (Button) findViewById(R.id.btn_clear_img_lbl);
        cameraView = (CameraView)findViewById(R.id.camera_view);
        btnDetectImgLbl = (Button)findViewById(R.id.btn_detect_img_lbl);
        waitingDialog = new SpotsDialog.Builder().setContext(this).setMessage("Please wait...").setCancelable(false).build();

        //add listeners ti views
        cameraView.addCameraKitListener(new CameraKitEventListener() {
            @Override
            public void onEvent(CameraKitEvent cameraKitEvent) {

            }

            @Override
            public void onError(CameraKitError cameraKitError) {

            }

            @Override
            public void onImage(CameraKitImage cameraKitImage) {
                waitingDialog.show();
                Bitmap bitmap = cameraKitImage.getBitmap();
                bitmap = Bitmap.createScaledBitmap(bitmap,cameraView.getWidth(),cameraView.getHeight(),false);
                cameraView.stop();
                runDetector(bitmap);
            }

            @Override
            public void onVideo(CameraKitVideo cameraKitVideo) {

            }
        });

        btnDetectImgLbl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cameraView.start();
                cameraView.captureImage();
            }
        });

        btnClearImgLbl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cameraView.stop();
                cameraView.start();
            }
        });

        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(
                mMessageReceiver, new IntentFilter("location_update"));

        bindService();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if(requestCode == REQUEST_OBJECT_INFO && resultCode == RESULT_OK){
            startActivity(intent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        cameraView.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraView.stop();
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
        serviceIntentHelper = new Intent(ImageLabelingActivity.this,GPS_Service.class);
        LocalBroadcastManager.getInstance(ImageLabelingActivity.this).registerReceiver(mMessageReceiver,new IntentFilter());
        bindService(serviceIntentHelper,serviceConnection,Context.BIND_AUTO_CREATE);
    }

    //method for unbinding from Service
//    private void unbindService(){
//        if(isServiceBound && serviceConnection != null){
//            LocalBroadcastManager.getInstance(ImageLabelingActivity.this).unregisterReceiver(
//                    mMessageReceiver);
//            isServiceBound = false;
//
//        }
//    }

    private void runDetector(Bitmap bitmap){
        final FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);
        new DetectInternetConnection(new DetectInternetConnection.Consumer() {
            @Override
            public void checkInternetConnection(boolean hasInternetConnection) {
                if(hasInternetConnection){
                    //if we have internet, we will use Cloud
                    FirebaseVisionCloudImageLabelerOptions options =
                            new FirebaseVisionCloudImageLabelerOptions.Builder().setConfidenceThreshold(0.7f).build();
                    FirebaseVisionImageLabeler labeler = FirebaseVision.getInstance().getCloudImageLabeler(options);
                    labeler.processImage(image).addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionImageLabel>>() {
                        @Override
                        public void onSuccess(List<FirebaseVisionImageLabel> firebaseVisionImageLabels) {
                            processDataResultCloud(firebaseVisionImageLabels);
                        }
                    })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("ERROR",e.getMessage());
                                }
                            });
                }
                else{
                    FirebaseVisionOnDeviceImageLabelerOptions options =
                            new FirebaseVisionOnDeviceImageLabelerOptions.Builder()
                                    .setConfidenceThreshold(0.7f)
                                    .build();
                    FirebaseVisionImageLabeler labeler = FirebaseVision.getInstance()
                            .getOnDeviceImageLabeler(options);
                    labeler.processImage(image).addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionImageLabel>>() {
                        @Override
                        public void onSuccess(List<FirebaseVisionImageLabel> firebaseVisionImageLabels) {
                            processDataResult(firebaseVisionImageLabels);
                        }
                    })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            });

                }
            }
        });
    }

    private void processDataResult(List<FirebaseVisionImageLabel> firebaseVisionImageLabels) {
        String result = "";
        for(FirebaseVisionImageLabel label :firebaseVisionImageLabels){
            result = label.getText();
            break;
        }
        //Toast.makeText(getApplicationContext(),"Device result: "+result,Toast.LENGTH_LONG).show();
        Intent intent = new Intent(getApplicationContext(),DisplayInfoActivity.class);
        intent.putExtra("objectName",result);
        if(usersCurrentLocation != null){
            intent.putExtra("users_current_latitude",usersCurrentLocation.getLatitude());
            intent.putExtra("users_current_longitude",usersCurrentLocation.getLongitude());
        }
        else{
            intent.putExtra("users_current_latitude",0);
            intent.putExtra("users_current_longitude",0);
        }

        DisplayInfoActivity.shouldPushToDatabase = true;
        startActivityForResult(intent,REQUEST_OBJECT_INFO);
        waitingDialog.dismiss();
    }

    private void processDataResultCloud(List<FirebaseVisionImageLabel> firebaseVisionImageLabels) {
        String result = "";
        for(FirebaseVisionImageLabel label :firebaseVisionImageLabels){
            result = label.getText();
            break;
        }
        //Toast.makeText(getApplicationContext(),"Cloud result: "+result,Toast.LENGTH_LONG).show();
        Intent intent = new Intent(getApplicationContext(),DisplayInfoActivity.class);
        intent.putExtra("objectName",result);
        if(usersCurrentLocation != null){
            intent.putExtra("users_current_latitude",usersCurrentLocation.getLatitude());
            intent.putExtra("users_current_longitude",usersCurrentLocation.getLongitude());
        }
        else{
            intent.putExtra("users_current_latitude",0);
            intent.putExtra("users_current_longitude",0);
        }

        DisplayInfoActivity.shouldPushToDatabase = true;
        startActivityForResult(intent,REQUEST_OBJECT_INFO);
        waitingDialog.dismiss();
    }


}
