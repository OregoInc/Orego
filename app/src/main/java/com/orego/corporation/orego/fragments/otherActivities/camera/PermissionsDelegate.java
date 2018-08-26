package com.orego.corporation.orego.fragments.otherActivities.camera;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

public class PermissionsDelegate {

    private static final int REQUEST_CODE = 10;
    private final Activity activity;

    public PermissionsDelegate(Activity activity) {
        this.activity = activity;
    }

    public boolean hasCameraPermission() {
        int permissionCheckResult = ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.CAMERA
        );
        int permissionCheckStorageWrite = ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        );
        int permissionCheckStorageRead = ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.READ_EXTERNAL_STORAGE
        );
        int permissionCheckInternet = ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.INTERNET
        );
        return (permissionCheckResult == PackageManager.PERMISSION_GRANTED)
                && (permissionCheckStorageRead == PackageManager.PERMISSION_GRANTED)
                && (permissionCheckStorageWrite == PackageManager.PERMISSION_GRANTED)
                && (permissionCheckInternet == PackageManager.PERMISSION_GRANTED);
    }

    public void requestCameraPermission() {
        ActivityCompat.requestPermissions(
                activity,
                new String[]{Manifest.permission.CAMERA
                        , Manifest.permission.WRITE_EXTERNAL_STORAGE
                        , Manifest.permission.READ_EXTERNAL_STORAGE
                        , Manifest.permission.INTERNET},
                225
        );
    }

    public boolean resultGrantedCamera(int requestCode,
                                       String[] permissions,
                                       int[] grantResults) {
        if (requestCode != REQUEST_CODE) {
            return false;
        }

        if (grantResults.length < 1) {
            return false;
        }
        if (!(permissions[0].equals(Manifest.permission.CAMERA))
                && !(permissions[1].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE))
                && !(permissions[2].equals(Manifest.permission.READ_EXTERNAL_STORAGE))
                && !(permissions[3].equals(Manifest.permission.INTERNET))) {
            return false;
        }

//        View noPermissionView = activity.findViewById(R.id.no_permission);
//
//        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//            noPermissionView.setVisibility(View.GONE);
//            return true;
//        }

        requestCameraPermission();
//        noPermissionView.setVisibility(View.VISIBLE);
        return false;
    }
}
