package com.orego.corporation.orego.fragments.cameraFragment;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.orego.corporation.orego.R;
import com.orego.corporation.orego.views.cameraview.CameraManager;
import com.orego.corporation.orego.views.cameraview.CameraUtils;

import java.io.File;
import java.util.Objects;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class CameraFragment extends Fragment implements SurfaceHolder.Callback,
        View.OnClickListener {
    private static final String TAG = "CameraFragment";
    private SurfaceView mSurfaceView;
    private View mSwitchWrapper;
    private ImageView mPictureView;
    private CameraListener mCameraListener;
    private Bitmap mPicture;
    private boolean isSurfaceCreated;
    private View captureRetryLayout;
    private ImageView btnCapture;
    private ImageView btnRetry;
    private ImageView btnSwitchCamera;
    private boolean isExpanded;
    private BottomSheetBehavior mBottomSheetBehavior;
    private TextView mTextViewState;
    private ImageView btnSheetOpen;
    private ImageView buttonCollapse;

    public static void startForResult(Activity activity, File path, int requestCode) {
        Intent intent = new Intent(activity, CameraFragment.class);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, path.getAbsolutePath());
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivityForResult(intent, requestCode);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        CoordinatorLayout coordinatorLayout = (CoordinatorLayout) inflater.inflate(R.layout.camera_fragment, container, false);
//        mCameraView = coordinatorLayout.findViewById(R.id.camera_view);
        //    private CameraView mCameraView;
        String mPath = Objects.requireNonNull(getActivity()).getIntent().getStringExtra(MediaStore.EXTRA_OUTPUT);
//        mCameraView.setCameraListener(this);


        View bottomSheet = coordinatorLayout.findViewById(R.id.bottom_sheet);
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        mTextViewState = coordinatorLayout.findViewById(R.id.text_view_state);
        btnSheetOpen = (ImageView) coordinatorLayout.findViewById(R.id.btn_sheet_open);
        buttonCollapse = (ImageView) coordinatorLayout.findViewById(R.id.btn_sheet_close);
        btnSheetOpen.setOnClickListener(v -> mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED));
        buttonCollapse.setOnClickListener(v -> mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED));
        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        mTextViewState.setText("Collapsed");
                        break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        mTextViewState.setText("Dragging...");
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        mTextViewState.setText("Expanded");
                        break;
                    case BottomSheetBehavior.STATE_HIDDEN:
                        mTextViewState.setText("Hidden");
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        mTextViewState.setText("Settling...");
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                mTextViewState.setText("Sliding...");
            }
        });


//        setClickable(true);
        isExpanded = false;
//        setBackgroundColor(Color.BLACK);
        mCameraListener = new CameraListener() {
            @Override
            public void onCapture(Bitmap bitmap) {
                Log.d(TAG, bitmap.toString());
            }

            @Override
            public void onCameraError(Throwable th) {
                Log.e(TAG, th.getMessage());
            }
        };
        mSurfaceView = (SurfaceView) coordinatorLayout.findViewById(R.id.camera_surface);
        mSurfaceView.setOnLongClickListener((v) -> {
            onSwitchClick();
            return true;
        });
        mSwitchWrapper = coordinatorLayout.findViewById(R.id.camera_switch_wrapper);
        mPictureView = (ImageView) coordinatorLayout.findViewById(R.id.camera_picture_preview);

        CameraManager.getInstance().init(getContext());

        // fix `java.lang.RuntimeException: startPreview failed` on api 10
        mSurfaceView.getHolder().addCallback(this);
        mSwitchWrapper.setVisibility(CameraManager.getInstance().hasMultiCamera() ? VISIBLE : GONE);
        mSwitchWrapper.setOnClickListener(this);

        captureRetryLayout = coordinatorLayout.findViewById(R.id.camera_capture_retry_layout);

        btnCapture = (ImageView) coordinatorLayout.findViewById(R.id.camera_capture);
        btnRetry = (ImageView) coordinatorLayout.findViewById(R.id.camera_retry);
        btnSwitchCamera = (ImageView) coordinatorLayout.findViewById(R.id.camera_close);

        btnCapture.setOnClickListener(this);
        btnRetry.setOnClickListener(this);
        btnRetry.setEnabled(false);
        btnSwitchCamera.setOnClickListener(this);


        return coordinatorLayout;
    }


    @Override
    public void onResume() {
        super.onResume();
//        mCameraView.onResume();
        Log.d(TAG, "onResume");

        if (!CameraManager.getInstance().isOpened() && isSurfaceCreated) {
            CameraManager.getInstance().open(success -> {
                if (!success && mCameraListener != null) {
                    mCameraListener.onCameraError(new Exception("open camera failed"));
                }
            });
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");

        if (CameraManager.getInstance().isOpened()) {
            CameraManager.getInstance().close();
        }
//        mCameraView.onPause();
    }


    public interface CameraListener {
        void onCapture(Bitmap bitmap);

        void onCameraError(Throwable th);

    }


//    private void init() {
//
//    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d(TAG, "surfaceCreated");
        isSurfaceCreated = true;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.d(TAG, "surfaceChanged");
        CameraManager.getInstance().setSurfaceHolder(holder, width, height);

        if (CameraManager.getInstance().isOpened()) {
            CameraManager.getInstance().close();
        }

        CameraManager.getInstance().open(success -> {
            if (!success && mCameraListener != null) {
                mCameraListener.onCameraError(new Exception("open camera failed"));
            }
        });
    }

    @Override
    public void surfaceDestroyed(final SurfaceHolder holder) {
        Log.d(TAG, "surfaceDestroyed");
        isSurfaceCreated = false;
        CameraManager.getInstance().setSurfaceHolder(null, 0, 0);
    }

    public void onCaptureClick() {
        CameraManager.getInstance().takePicture(bitmap -> {
            if (bitmap != null) {
                mSurfaceView.setVisibility(GONE);
                mSwitchWrapper.setVisibility(GONE);
                btnSheetOpen.setVisibility(GONE);
                mPictureView.setVisibility(VISIBLE);
                mPicture = bitmap;
                mPictureView.setImageBitmap(mPicture);
                expand();
            } else {
                isExpanded = false;
                onRetryClick();
            }
        });
    }

    public void onOkClick() {
        if (mPicture != null && mCameraListener != null) {
            mCameraListener.onCapture(mPicture);
        }
    }

    public void onRetryClick() {
        mPicture = null;
        mSurfaceView.setVisibility(VISIBLE);
        mSwitchWrapper.setVisibility(CameraManager.getInstance().hasMultiCamera() ? VISIBLE : GONE);
        btnSheetOpen.setVisibility(VISIBLE);
        mPictureView.setImageBitmap(null);
        mPictureView.setVisibility(GONE);
        fold();
    }

    public void onSwitchClick() {
        CameraManager.getInstance().switchCamera(success -> {
            if (!success && mCameraListener != null) {
                mCameraListener.onCameraError(new Exception("switch camera failed"));
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v == mSwitchWrapper) {
            Log.d(TAG, "info click");
        }

        if (v == btnCapture) {
            if (!isExpanded) {
                onCaptureClick();
            } else {
                onOkClick();
            }
        } else if (v == btnRetry) {
            onRetryClick();
        } else if (v == btnSwitchCamera) {
            onSwitchClick();
        }
    }

    private void expand() {
        isExpanded = true;
        btnCapture.setImageResource(R.drawable.ic_camera_done);
        btnRetry.setEnabled(true);
        btnSwitchCamera.setVisibility(GONE);

        playExpandAnimation();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void playExpandAnimation() {
        ValueAnimator scaleAnimator = ValueAnimator.ofInt(CameraUtils.dp2px(getContext(), 60), CameraUtils.dp2px(getContext(), 80));
        scaleAnimator.setInterpolator(new LinearInterpolator());
        scaleAnimator.setDuration(100);
        scaleAnimator.addUpdateListener(animation -> {
            int value = (int) animation.getAnimatedValue();
            FrameLayout.LayoutParams captureParams = (FrameLayout.LayoutParams) btnCapture.getLayoutParams();
            captureParams.width = value;
            captureParams.height = value;
            captureParams.gravity = Gravity.CENTER;
            btnCapture.requestLayout();
        });

        ValueAnimator transAnimator = ValueAnimator.ofInt(CameraUtils.dp2px(getContext(), 80), CameraUtils.dp2px(getContext(), 280));
        transAnimator.setInterpolator(new LinearInterpolator());
        transAnimator.setDuration(200);
        transAnimator.addUpdateListener(animation -> {
            int value = (int) animation.getAnimatedValue();
            FrameLayout.LayoutParams captureParams = (FrameLayout.LayoutParams) btnCapture.getLayoutParams();
            captureParams.gravity = Gravity.END;

            CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) captureRetryLayout.getLayoutParams();
            layoutParams.width = value;
            captureRetryLayout.requestLayout();
        });

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playSequentially(scaleAnimator, transAnimator);
        animatorSet.start();
    }

    private void fold() {
        isExpanded = false;
        btnCapture.setImageResource(0);
        btnRetry.setEnabled(false);
        btnSwitchCamera.setVisibility(VISIBLE);

        int length = CameraUtils.dp2px(getContext(), 60);
        FrameLayout.LayoutParams captureParams = (FrameLayout.LayoutParams) btnCapture.getLayoutParams();
        captureParams.width = length;
        captureParams.height = length;
        captureParams.gravity = Gravity.CENTER;

        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) captureRetryLayout.getLayoutParams();
        layoutParams.width = CameraUtils.dp2px(getContext(), 80);
        captureRetryLayout.requestLayout();
    }
}
