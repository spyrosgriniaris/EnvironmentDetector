package com.myhost.spyros.environmentdetector;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.myhost.spyros.environmentdetector.Helpers.Photo_Resize;

import java.io.File;

public class BaseGalleryActivity extends AppCompatActivity {

    public static final int RC_STORAGE_PERMS1 = 101;
    public static final int RC_SELECT_PICTURE = 103;
    public static final String ACTION_BAR_TITLE = "action_bar_title";
    public File imageFile;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case RC_STORAGE_PERMS1:
            //If the permission request is granted, then...//
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //...call selectPicture//
                    selectPicture();
                    //If the permission request is denied, then...//

                } else {
                    //...display the “permission_request” string//
                    Photo_Resize.needPermission(this, requestCode, R.string.permission_request);
                }
                break;

        }
    }

    //Check whether the user has granted the WRITE_STORAGE permission//

    public void checkStoragePermission(int requestCode) {
        switch (requestCode) {
            case RC_STORAGE_PERMS1:
                int hasWriteExternalStoragePermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                //If we have access to external storage...//
                if (hasWriteExternalStoragePermission == PackageManager.PERMISSION_GRANTED) {
                //...call selectPicture, which launches an Activity where the user can select an image//
                    selectPicture();

                    //If permission hasn’t been granted, then...//

                } else {
                    //...request the permission//
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, requestCode);
                }
                break;

        }
    }

    private void selectPicture() {
        //DetectFromGalleryActivity.image_uploaded_name = imageFile.getName();
        //DetectFromGalleryActivity.image_uploaded_path = imageFile.getPath();
        imageFile = Photo_Resize.createTempFile(imageFile);
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, RC_SELECT_PICTURE);
    }
}
