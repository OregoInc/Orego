package com.orego.corporation.orego.fragments.cameraFragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.orego.corporation.orego.R;
import com.orego.corporation.orego.fragments.otherActivities.face3dActivity.model3D.view.ModelActivity;
import com.orego.corporation.orego.views.cameraview.CameraView;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class CameraFragment extends Fragment implements CameraView.CameraListener {
    private static final String TAG = "CameraFragment";
    private CameraView mCameraView;
    private String mPath;

    public static void startForResult(Activity activity, File path, int requestCode) {
        Intent intent = new Intent(activity, CameraFragment.class);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, path.getAbsolutePath());
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivityForResult(intent, requestCode);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ConstraintLayout constraintLayout = (ConstraintLayout) inflater.inflate(R.layout.camera_fragment, container, false);
        mCameraView = constraintLayout.findViewById(R.id.camera_view);
        mPath = getActivity().getIntent().getStringExtra(MediaStore.EXTRA_OUTPUT);
        mCameraView.setCameraListener(this);
        return  constraintLayout;
    }


    @Override
    public void onCapture(Bitmap bitmap) {
        File file = new File(mPath);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        try {
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (IOException e) {
            Log.e(TAG, "save picture error", e);
        }

        if (file.exists()) {
            Intent data = new Intent();
            data.setData(Uri.parse(mPath));
//            setResult(RESULT_OK, data);
        }
        CameraFrag.setImage();
        Intent intent = new Intent(getContext(), ModelActivity.class);
        Bundle b = new Bundle();
        b.putInt("countModel", CameraFrag.getCount() - 1);
        b.putString("model", "null");
        intent.putExtras(b);
        startActivity(intent);
    }

    @Override
    public void onCameraClose() {

    }

    @Override
    public void onCameraError(Throwable th) {
        Log.e(TAG, "camera error", th);
        onCameraClose();
    }

    @Override
    public void onResume() {
        super.onResume();
        mCameraView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mCameraView.onPause();
    }
}
