package com.orego.corporation.orego.fragments.cameraFragment

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.annotation.TargetApi
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.CoordinatorLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView

import com.orego.corporation.orego.R
import com.orego.corporation.orego.views.cameraview.CameraManager
import com.orego.corporation.orego.views.cameraview.CameraUtils

import java.io.File
import java.util.Objects

import android.view.View.GONE
import android.view.View.VISIBLE

class CameraFragment : Fragment(), SurfaceHolder.Callback, View.OnClickListener {
    private var mSurfaceView: SurfaceView? = null
    private var mPictureView: ImageView? = null
    private var mCameraListener: CameraListener? = null
    private var mPicture: Bitmap? = null
    private var isSurfaceCreated: Boolean = false
    private var captureRetryLayout: View? = null
    private var btnCapture: ImageView? = null
    private var btnRetry: ImageView? = null
    private var btnSwitchCamera: ImageView? = null
    private var btnInfo: ImageView? = null
    private var isExpanded: Boolean = false
    private var mBottomSheetBehavior: BottomSheetBehavior<*>? = null
    private var mTextViewState: TextView? = null
    private var btnSheetOpen: ImageView? = null
    private var buttonCollapse: ImageView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val coordinatorLayout = inflater.inflate(R.layout.camera_fragment, container, false) as CoordinatorLayout
        val mPath = Objects.requireNonNull<FragmentActivity>(activity).intent.getStringExtra(MediaStore.EXTRA_OUTPUT)

        val bottomSheet = coordinatorLayout.findViewById<View>(R.id.bottom_sheet)
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        mTextViewState = coordinatorLayout.findViewById(R.id.text_view_state)
        btnSheetOpen = coordinatorLayout.findViewById<View>(R.id.btn_sheet_open) as ImageView
        buttonCollapse = coordinatorLayout.findViewById<View>(R.id.btn_sheet_close) as ImageView
        btnSheetOpen!!.setOnClickListener { mBottomSheetBehavior!!.setState(BottomSheetBehavior.STATE_EXPANDED) }
        buttonCollapse!!.setOnClickListener { mBottomSheetBehavior!!.setState(BottomSheetBehavior.STATE_COLLAPSED) }
        btnInfo = coordinatorLayout.findViewById(R.id.btn_info)
        mBottomSheetBehavior!!.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    btnCapture!!.visibility = VISIBLE
                    btnSheetOpen!!.visibility = VISIBLE
                    btnSwitchCamera!!.visibility = VISIBLE
                    btnRetry!!.visibility = VISIBLE
                } else {
                    btnCapture!!.visibility = GONE
                    btnSheetOpen!!.visibility = GONE
                    btnSwitchCamera!!.visibility = GONE
                    btnRetry!!.visibility = GONE
                }
                when (newState) {
                    BottomSheetBehavior.STATE_COLLAPSED -> mTextViewState!!.text = "Collapsed"
                    BottomSheetBehavior.STATE_DRAGGING -> mTextViewState!!.text = "Dragging..."
                    BottomSheetBehavior.STATE_EXPANDED -> mTextViewState!!.text = "Expanded"
                    BottomSheetBehavior.STATE_HIDDEN -> mTextViewState!!.text = "Hidden"
                    BottomSheetBehavior.STATE_SETTLING -> mTextViewState!!.text = "Settling..."
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                mTextViewState!!.text = "Sliding..."
            }
        })

        isExpanded = false
        mCameraListener = object : CameraListener {
            override fun onCapture(bitmap: Bitmap) {
                Log.d(TAG, bitmap.toString())
            }

            override fun onCameraError(th: Throwable) {
                Log.e(TAG, th.message)
            }
        }
        mSurfaceView = coordinatorLayout.findViewById<View>(R.id.camera_surface) as SurfaceView
        mSurfaceView!!.setOnLongClickListener {
            onSwitchClick()
            true
        }
        mPictureView = coordinatorLayout.findViewById<View>(R.id.camera_picture_preview) as ImageView

        CameraManager.getInstance().init(context)

        // fix `java.lang.RuntimeException: startPreview failed` on api 10
        mSurfaceView!!.holder.addCallback(this)
        btnInfo!!.visibility = if (CameraManager.getInstance().hasMultiCamera()) VISIBLE else GONE
        btnInfo!!.setOnClickListener(this)

        captureRetryLayout = coordinatorLayout.findViewById(R.id.camera_capture_retry_layout)

        btnCapture = coordinatorLayout.findViewById<View>(R.id.camera_capture) as ImageView
        btnRetry = coordinatorLayout.findViewById<View>(R.id.camera_retry) as ImageView
        btnSwitchCamera = coordinatorLayout.findViewById<View>(R.id.btn_switch) as ImageView

        btnCapture!!.setOnClickListener(this)
        btnRetry!!.setOnClickListener(this)
        btnRetry!!.isEnabled = false
        btnSwitchCamera!!.setOnClickListener(this)

        return coordinatorLayout
    }


    override fun onResume() {
        super.onResume()
        //        mCameraView.onResume();
        Log.d(TAG, "onResume")

        if (!CameraManager.getInstance().isOpened && isSurfaceCreated) {
            CameraManager.getInstance().open { success ->
                if (!success && mCameraListener != null) {
                    mCameraListener!!.onCameraError(Exception("open camera failed"))
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause")

        if (CameraManager.getInstance().isOpened) {
            CameraManager.getInstance().close()
        }
        //        mCameraView.onPause();
    }


    interface CameraListener {
        fun onCapture(bitmap: Bitmap)

        fun onCameraError(th: Throwable)

    }


    //    private void init() {
    //
    //    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        Log.d(TAG, "surfaceCreated")
        isSurfaceCreated = true
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        Log.d(TAG, "surfaceChanged")
        CameraManager.getInstance().setSurfaceHolder(holder, width, height)

        if (CameraManager.getInstance().isOpened) {
            CameraManager.getInstance().close()
        }

        CameraManager.getInstance().open { success ->
            if (!success && mCameraListener != null) {
                mCameraListener!!.onCameraError(Exception("open camera failed"))
            }
        }
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        Log.d(TAG, "surfaceDestroyed")
        isSurfaceCreated = false
        CameraManager.getInstance().setSurfaceHolder(null, 0, 0)
    }

    private fun onCaptureClick() {
        CameraManager.getInstance().takePicture { bitmap ->
            if (bitmap != null) {
                mSurfaceView!!.visibility = GONE
                btnInfo!!.visibility = GONE
                btnSheetOpen!!.visibility = GONE
                mPictureView!!.visibility = VISIBLE
                mPicture = bitmap
                mPictureView!!.setImageBitmap(mPicture)
                expand()
            } else {
                isExpanded = false
                onRetryClick()
            }
        }
    }

    private fun onOkClick() {
        if (mPicture != null && mCameraListener != null) {
            mCameraListener!!.onCapture(mPicture!!)
        }
    }

    private fun onRetryClick() {
        mPicture = null
        mSurfaceView!!.visibility = VISIBLE
        btnInfo!!.visibility = if (CameraManager.getInstance().hasMultiCamera()) VISIBLE else GONE
        btnSheetOpen!!.visibility = VISIBLE
        mPictureView!!.setImageBitmap(null)
        mPictureView!!.visibility = GONE
        fold()
    }

    private fun onSwitchClick() {
        CameraManager.getInstance().switchCamera { success ->
            if (!success && mCameraListener != null) {
                mCameraListener!!.onCameraError(Exception("switch camera failed"))
            }
        }
    }

    override fun onClick(v: View) {
        if (v === btnInfo) {
            Log.d(TAG, "info click")
        }

        if (v === btnCapture) {
            if (!isExpanded) {
                onCaptureClick()
            } else {
                onOkClick()
            }
        } else if (v === btnRetry) {
            onRetryClick()
        } else if (v === btnSwitchCamera) {
            onSwitchClick()
        }
    }

    private fun expand() {
        isExpanded = true
        btnCapture!!.setImageResource(R.drawable.ic_camera_done)
        btnRetry!!.isEnabled = true
        btnSwitchCamera!!.visibility = GONE

        playExpandAnimation()
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private fun playExpandAnimation() {
        val scaleAnimator = ValueAnimator.ofInt(CameraUtils.dp2px(context!!, 60f), CameraUtils.dp2px(context!!, 80f))
        scaleAnimator.interpolator = LinearInterpolator()
        scaleAnimator.duration = 100
        scaleAnimator.addUpdateListener { animation ->
            val value = animation.animatedValue as Int
            val captureParams = btnCapture!!.layoutParams as FrameLayout.LayoutParams
            captureParams.width = value
            captureParams.height = value
            captureParams.gravity = Gravity.CENTER
            btnCapture!!.requestLayout()
        }

        val transAnimator = ValueAnimator.ofInt(CameraUtils.dp2px(context!!, 80f), CameraUtils.dp2px(context!!, 280f))
        transAnimator.interpolator = LinearInterpolator()
        transAnimator.duration = 200
        transAnimator.addUpdateListener { animation ->
            val value = animation.animatedValue as Int
            val captureParams = btnCapture!!.layoutParams as FrameLayout.LayoutParams
            captureParams.gravity = Gravity.END

            val layoutParams = captureRetryLayout!!.layoutParams as CoordinatorLayout.LayoutParams
            layoutParams.width = value
            captureRetryLayout!!.requestLayout()
        }

        val animatorSet = AnimatorSet()
        animatorSet.playSequentially(scaleAnimator, transAnimator)
        animatorSet.start()
    }

    private fun fold() {
        isExpanded = false
        btnCapture!!.setImageResource(0)
        btnRetry!!.isEnabled = false
        btnSwitchCamera!!.visibility = VISIBLE

        val length = CameraUtils.dp2px(context!!, 60f)
        val captureParams = btnCapture!!.layoutParams as FrameLayout.LayoutParams
        captureParams.width = length
        captureParams.height = length
        captureParams.gravity = Gravity.CENTER

        val layoutParams = captureRetryLayout!!.layoutParams as CoordinatorLayout.LayoutParams
        layoutParams.width = CameraUtils.dp2px(context!!, 80f)
        captureRetryLayout!!.requestLayout()
    }

    companion object {
        private const val TAG = "CameraFragment"

        fun startForResult(activity: Activity, path: File, requestCode: Int) {
            val intent = Intent(activity, CameraFragment::class.java)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, path.absolutePath)
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            activity.startActivityForResult(intent, requestCode)
        }
    }
}
