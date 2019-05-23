package com.myhost.spyros.environmentdetector;

import android.app.AlertDialog;
import android.location.Location;
import android.os.Bundle;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.label.FirebaseVisionCloudImageLabelerOptions;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabel;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabeler;
import com.google.firebase.ml.vision.label.FirebaseVisionOnDeviceImageLabelerOptions;
import com.myhost.spyros.environmentdetector.Helpers.DetectInternetConnection;
import com.myhost.spyros.environmentdetector.Helpers.Photo_Resize;


import java.io.File;
import java.util.Date;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class DetectFromGalleryActivity extends BaseGalleryActivity implements View.OnClickListener {

    private static final long BYTES_TO_MB = 1024L * 1024L;
    static final int REQUEST_OBJECT_INFO = 1;
    //static variable to get name of picture, before renamed original.jpg from helper class
    static String image_uploaded_name = "";
    static String image_uploaded_path = "";
    static long image_uploaded_date_taken = 0;
    static long image_uploaded_space = 0;

    private Bitmap mBitmap;
    private ImageView mImageView;
    AlertDialog waitingDialog;
    TextView fileNameTxtView, positionTxtView, dateTxtView, spaceTxtView;

    //variable to store user's current Location
    public static Location usersCurrentLocation = null;

    int mCurrRotation = 0; // takes the place of getRotation() in ImageView

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detect_from_gallery);

        //init views
        mImageView = findViewById(R.id.imageView);
        fileNameTxtView = (TextView) findViewById(R.id.filename);
        positionTxtView = (TextView) findViewById(R.id.position);
        dateTxtView = (TextView) findViewById(R.id.date_taken);
        spaceTxtView = (TextView) findViewById(R.id.space);

        //init buttons of UI
        findViewById(R.id.btn_detect_gallery).setOnClickListener(this);
        findViewById(R.id.rotate_left).setOnClickListener(this);
        findViewById(R.id.rotate_right).setOnClickListener(this);

        waitingDialog = new SpotsDialog.Builder().setContext(this).setMessage("Please wait...").setCancelable(false).build();
        //Toolbar set up
        Toolbar toolbar = findViewById(R.id.gallery_toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.gallery_picker_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //If “gallery_action” is selected, then...//
            case R.id.action_gallery:
            //...check we have the WRITE_STORAGE permission//
                checkStoragePermission(RC_STORAGE_PERMS1);
                break;

        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View view) {
        //mTextView.setText(null);
        switch (view.getId()) {
            case R.id.btn_detect_gallery:
                if (mBitmap != null) {
                    runDetector(mBitmap);
                    waitingDialog.show();
                }
                break;
            case R.id.rotate_left:
                imageView_rotate_left();
                break;
            case R.id.rotate_right:
                imageView_rotate_right();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case RC_STORAGE_PERMS1:
                    checkStoragePermission(requestCode);
                    break;
                case RC_SELECT_PICTURE:
                    Uri dataUri = data.getData();
                    String path = Photo_Resize.getPath(this, dataUri);
                    File image = new File(path);
                    image_uploaded_date_taken = image.lastModified();
                    image_uploaded_space = image.length();
                    image_uploaded_path = path;
                    String[] filename = path.split("/");
                    image_uploaded_name = filename[filename.length-1];
                    if (path == null) {
                        mBitmap = Photo_Resize.resizeImage(imageFile, this, dataUri, mImageView);
                    } else {
                        mBitmap = Photo_Resize.resizeImage(imageFile, path, mImageView);
                    }
                    if (mBitmap != null) {
                        //mTextView.setText(null);
                        mImageView.setImageBitmap(mBitmap);
                        getFileInfo(imageFile);
                    }
            }
        }
    }

    private void getFileInfo(File imageFile){
        //set fileInfo in TextViews
        fileNameTxtView.setText(image_uploaded_name);
        positionTxtView.setText(String.valueOf(image_uploaded_path));
        Date date_to_display = new Date(image_uploaded_date_taken);
        dateTxtView.setText(date_to_display.toString());
        spaceTxtView.setText(String.valueOf((float)image_uploaded_space/BYTES_TO_MB)+ " Mb");

        //Toast.makeText(getApplicationContext(),path+""+image_uploaded_name+""+date_taken+""+space_taken,Toast.LENGTH_SHORT).show();
    }

    private void imageView_rotate_left(){
        mCurrRotation %= 360;
        float fromRotation = mCurrRotation;
        float toRotation = mCurrRotation -= 90;
        final RotateAnimation rotateAnim = new RotateAnimation(
                fromRotation, toRotation, mImageView.getWidth()/2, mImageView.getHeight()/2);
        rotateAnim.setDuration(1000); // Use 0 ms to rotate instantly
        rotateAnim.setFillAfter(true); // Must be true or the animation will reset

        mImageView.startAnimation(rotateAnim);
    }

    private void imageView_rotate_right(){
        mCurrRotation %= 360;
        float fromRotation = mCurrRotation;
        float toRotation = mCurrRotation += 90;
        final RotateAnimation rotateAnim = new RotateAnimation(
                fromRotation, toRotation, mImageView.getWidth()/2, mImageView.getHeight()/2);
        rotateAnim.setDuration(1000); // Use 0 ms to rotate instantly
        rotateAnim.setFillAfter(true); // Must be true or the animation will reset

        mImageView.startAnimation(rotateAnim);
    }

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
