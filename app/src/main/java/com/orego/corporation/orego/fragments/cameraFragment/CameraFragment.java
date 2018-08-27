package com.orego.corporation.orego.fragments.cameraFragment;


import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.orego.corporation.orego.R;
import com.orego.corporation.orego.fragments.galleryFragment.OregoGalleryFragment;
import com.orego.corporation.orego.fragments.otherActivities.OldMainActivity;
import com.orego.corporation.orego.fragments.otherActivities.camera.CameraActivity;
import com.orego.corporation.orego.managers.oregoPhotoManagement.OregoPhoto;
import com.orego.corporation.orego.managers.oregoPhotoManagement.OregoPhotoManager;

import java.io.File;


public class CameraFragment extends Fragment {

    ImageButton buttonCamera;
    Activity parent;
    private static int count = 0;
    static File directoryPhoto;

    public static int getCount() {
        return count;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        NestedScrollView nestedScrollView = (NestedScrollView) inflater.inflate(R.layout.useless_fragment_camera, container, false);
        count = OregoPhotoManager.INSTANCE.getSpacePhotos().size();

        buttonCamera = (ImageButton) nestedScrollView.findViewById(R.id.button_camera);

        buttonCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final File orego = new File(Environment.getExternalStorageDirectory(), "/OREGO");
                if (!orego.exists()) orego.mkdir();
                directoryPhoto = new File(orego, "directory" + count);
                if (!directoryPhoto.exists()) directoryPhoto.mkdir();
                File photo = new File(directoryPhoto, "result.jpg");
                CameraActivity.startForResult(parent, photo, 0);
            }
        });

        return nestedScrollView;
    }


    public static void setImage(){
        OregoPhotoManager.INSTANCE.add(new OregoPhoto(directoryPhoto));
        OregoGalleryFragment.INIT.init();
        count++;
        
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        System.out.println("CAMERA FRAGMENT");
//        if (resultCode == RESULT_OK && data != null) {
//
//        }
//    }

    public void setParent(OldMainActivity oldMainActivity) {
        parent = oldMainActivity;
    }
}
