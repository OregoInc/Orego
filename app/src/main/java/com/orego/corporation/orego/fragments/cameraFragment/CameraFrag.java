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
import com.orego.corporation.orego.fragments.MainActivity;
import com.orego.corporation.orego.fragments.otherActivities.OldMainActivity;
import com.orego.corporation.orego.managers.oregoPhotoManagement.OregoPhoto;
import com.orego.corporation.orego.managers.oregoPhotoManagement.OregoPhotoManager;

import java.io.File;


public class CameraFrag extends Fragment {

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
        NestedScrollView nestedScrollView = (NestedScrollView) inflater.inflate(R.layout.fragment_camera, container, false);
        count = OregoPhotoManager.INSTANCE.getSpacePhotos().size();

        buttonCamera = (ImageButton) nestedScrollView.findViewById(R.id.button_camera);

        buttonCamera.setOnClickListener(v -> {
            final File orego = new File(Environment.getExternalStorageDirectory(), "/OREGO");
            if (!orego.exists()) orego.mkdir();
            directoryPhoto = new File(orego, "directory" + count);
            if (!directoryPhoto.exists()) directoryPhoto.mkdir();
            File photo = new File(directoryPhoto, "result.jpg");
            CameraFragment.Companion.startForResult(parent, photo, 0);
        });

        return nestedScrollView;
    }


    public static void setImage(){
        OregoPhotoManager.INSTANCE.add(new OregoPhoto(directoryPhoto));
        count++;
    }

    public void setParent(MainActivity oldMainActivity) {
        parent = oldMainActivity;
    }
}
