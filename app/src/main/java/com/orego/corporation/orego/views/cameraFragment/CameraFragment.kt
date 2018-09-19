package com.orego.corporation.orego.views.cameraFragment

import android.Manifest
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.CoordinatorLayout
import android.support.v4.app.ActivityCompat
import android.support.v4.widget.NestedScrollView
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.*
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.animation.LinearInterpolator
import android.widget.*
import com.orego.corporation.orego.R
import com.orego.corporation.orego.R.id.horizontal_recycler
import com.orego.corporation.orego.views.base.BaseRestoreFragment
import com.orego.corporation.orego.presenters.MainActivity
import com.orego.corporation.orego.utils.PermissionUtils
import com.orego.corporation.orego.models.suppliers.ScaleTransformer
import com.orego.corporation.orego.presenters.managers.GalleryLayoutManager
import com.orego.corporation.orego.utils.ClientMultipartFormPost
import com.orego.corporation.orego.utils.SwipeListener
import com.orego.corporation.orego.presenters.managers.CameraManager
import com.orego.corporation.orego.utils.CameraUtils
import com.orego.corporation.orego.presenters.loaders.loadersAlbum.LoadAlbum
import java.util.*

class CameraFragment : BaseRestoreFragment(), SurfaceHolder.Callback, View.OnClickListener {


    companion object {
        private const val TAG = "CameraFragment"
        private const val REQUEST_PERMISSION = 1
    }

    private lateinit var mCameraListener: CameraListener
    private lateinit var mSurfaceView: SurfaceView
    private lateinit var mPictureView: ImageView
    private lateinit var captureRetryLayout: View
    private lateinit var btnInfo: ImageView
    private lateinit var mBottomSheetBehavior: BottomSheetBehavior<*>
    private lateinit var buttonCollapse: ImageView
    private lateinit var loadAlbumTask: LoadAlbum
    private lateinit var btnCapture: ImageView
    private lateinit var btnRetry: ImageView
    private lateinit var btnSwitchCamera: ImageView
    private lateinit var btnSheetOpen: ImageView
    private lateinit var galleryGridView: GridView
    private var mPicture: Bitmap? = null
    private var isSurfaceCreated: Boolean = false
    private var isExpanded: Boolean = false



    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateContentView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val coordinatorLayout = inflater!!.inflate(R.layout.camera_fragment, container, false) as CoordinatorLayout


        val bottomSheet = coordinatorLayout.findViewById<View>(R.id.bottom_sheet) as NestedScrollView
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)

        btnSheetOpen = coordinatorLayout.findViewById<View>(R.id.btn_sheet_open) as ImageView
        buttonCollapse = coordinatorLayout.findViewById<View>(R.id.btn_sheet_close) as ImageView

        btnSheetOpen.setOnClickListener {
            mBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
        buttonCollapse.setOnClickListener { mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED) }
        btnInfo = coordinatorLayout.findViewById(R.id.btn_info)
        mBottomSheetBehavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    btnCapture.visibility = VISIBLE
                    btnSheetOpen.visibility = VISIBLE
                    btnSwitchCamera.visibility = VISIBLE
                    btnRetry.visibility = VISIBLE
                } else {
                    btnCapture.visibility = GONE
                    btnSheetOpen.visibility = GONE
                    btnSwitchCamera.visibility = GONE
                    btnRetry.visibility = GONE
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }
        })

        isExpanded = false
        mCameraListener = object : CameraListener {
            override fun onCapture(bitmap: Bitmap) {
                ClientMultipartFormPost.sendPictureAndReplace(bitmap, "", activity!!)
            }

            override fun onCameraError(th: Throwable) {
                Log.e(TAG, th.message)
            }
        }
        mSurfaceView = coordinatorLayout.findViewById<View>(R.id.camera_surface) as SurfaceView
        mSurfaceView.setOnLongClickListener {
            onSwitchClick()
            true
        }
        mSurfaceView.isScrollContainer = true
        mPictureView = coordinatorLayout.findViewById<View>(R.id.camera_picture_preview) as ImageView
        mSurfaceView.setOnTouchListener(SwipeListener(this.context!!, mBottomSheetBehavior))
        CameraManager.getInstance().init(context)

        // fix `java.lang.RuntimeException: startPreview failed` on api 10
        mSurfaceView.holder.addCallback(this)
        btnInfo.visibility = if (CameraManager.getInstance().hasMultiCamera()) VISIBLE else GONE
        btnInfo.setOnClickListener(this)

        captureRetryLayout = coordinatorLayout.findViewById(R.id.camera_capture_retry_layout)

        btnCapture = coordinatorLayout.findViewById<View>(R.id.camera_capture) as ImageView
        btnRetry = coordinatorLayout.findViewById<View>(R.id.camera_retry) as ImageView
        btnSwitchCamera = coordinatorLayout.findViewById<View>(R.id.btn_switch) as ImageView

        btnCapture.setOnClickListener(this)
        btnRetry.setOnClickListener(this)
        btnRetry.isEnabled = false
        btnSwitchCamera.setOnClickListener(this)
        galleryGridView = coordinatorLayout.findViewById(R.id.galleryGridView) as GridView
        val permissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
        if (!PermissionUtils.hasPermissions(activity, *permissions)) {
            ActivityCompat.requestPermissions(activity!!, permissions, REQUEST_PERMISSION)
        } else {
            loadAlbumTask = LoadAlbum(activity as MainActivity, galleryGridView)
            loadAlbumTask.execute()
        }
        return coordinatorLayout
    }

    override fun initView(root: View, savedInstanceState: Bundle?) {
        val mMainRecycle1 = root.findViewById<RecyclerView>(horizontal_recycler)
        val iDisplayWidth = resources.displayMetrics.widthPixels
        val resources = resources
        val metrics = resources.displayMetrics
        var dp = iDisplayWidth / (metrics.densityDpi / 160f)

        if (dp < 360) {
            dp = (dp - 17) / 2
            val px = PermissionUtils.convertDpToPixel(dp, Objects.requireNonNull(context))
            galleryGridView.columnWidth = Math.round(px)
        }
        val title = ArrayList<String>()
        val size = 50
        for (i in 0 until size) {
            title.add("Hello$i")
        }
        val layoutManager1 = GalleryLayoutManager(GalleryLayoutManager.HORIZONTAL)
        layoutManager1.attach(mMainRecycle1, 0)
        layoutManager1.setItemTransformer(ScaleTransformer())

        mMainRecycle1!!.setHasFixedSize(true)
//        mMainRecycle1!!.adapter = adapter
        mMainRecycle1.layoutManager = layoutManager1
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    loadAlbumTask = LoadAlbum(activity as MainActivity, galleryGridView)
                    loadAlbumTask.execute()
                } else {
                    Toast.makeText(activity!!.baseContext, "You must accept permissions.", Toast.LENGTH_LONG).show()
                }
            }
        }

    }

    override fun initData(savedInstanceState: Bundle?) {
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume")

        if (!CameraManager.getInstance().isOpened && isSurfaceCreated) {
            CameraManager.getInstance().open { success ->
                if (!success) {
                    mCameraListener.onCameraError(Exception("open camera failed"))
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
    }


    interface CameraListener {
        fun onCapture(bitmap: Bitmap)

        fun onCameraError(th: Throwable)
    }

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
            if (!success) {
                mCameraListener.onCameraError(Exception("open camera failed"))
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
                mSurfaceView.visibility = GONE
                btnInfo.visibility = GONE
                btnSheetOpen.visibility = GONE
                mPictureView.visibility = VISIBLE
                mPicture = bitmap
                mPictureView.setImageBitmap(mPicture)
                expand()
            } else {
                isExpanded = false
                onRetryClick()
            }
        }
    }

    private fun onOkClick() {
        if (mPicture != null) {
            mCameraListener.onCapture(mPicture!!)
        }
    }

    private fun onRetryClick() {
        mPicture = null
        mSurfaceView.visibility = VISIBLE
        btnInfo.visibility = if (CameraManager.getInstance().hasMultiCamera()) VISIBLE else GONE
        btnSheetOpen.visibility = VISIBLE
        mPictureView.setImageBitmap(null)
        mPictureView.visibility = GONE
        fold()
    }

    private fun onSwitchClick() {
        CameraManager.getInstance().switchCamera { success ->
            if (!success) {
                mCameraListener.onCameraError(Exception("switch camera failed"))
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
        btnCapture.setImageResource(R.drawable.ic_done_black)
        btnRetry.isEnabled = true
        btnSwitchCamera.visibility = GONE

        playExpandAnimation()
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private fun playExpandAnimation() {
        val scaleAnimator = ValueAnimator.ofInt(CameraUtils.dp2px(context!!, 60f), CameraUtils.dp2px(context!!, 80f))
        scaleAnimator.interpolator = LinearInterpolator()
        scaleAnimator.duration = 100
        scaleAnimator.addUpdateListener { animation ->
            val value = animation.animatedValue as Int
            val captureParams = btnCapture.layoutParams as FrameLayout.LayoutParams
            captureParams.width = value
            captureParams.height = value
            captureParams.gravity = Gravity.CENTER
            btnCapture.requestLayout()
        }

        val transAnimator = ValueAnimator.ofInt(CameraUtils.dp2px(context!!, 80f), CameraUtils.dp2px(context!!, 280f))
        transAnimator.interpolator = LinearInterpolator()
        transAnimator.duration = 200
        transAnimator.addUpdateListener { animation ->
            val value = animation.animatedValue as Int
            val captureParams = btnCapture.layoutParams as FrameLayout.LayoutParams
            captureParams.gravity = Gravity.END

            val layoutParams = captureRetryLayout.layoutParams as CoordinatorLayout.LayoutParams
            layoutParams.width = value
            captureRetryLayout.requestLayout()
        }

        val animatorSet = AnimatorSet()
        animatorSet.playSequentially(scaleAnimator, transAnimator)
        animatorSet.start()
    }

    private fun fold() {
        isExpanded = false
        btnCapture.setImageResource(0)
        btnRetry.isEnabled = false
        btnSwitchCamera.visibility = VISIBLE

        val length = CameraUtils.dp2px(context!!, 60f)
        val captureParams = btnCapture.layoutParams as FrameLayout.LayoutParams
        captureParams.width = length
        captureParams.height = length
        captureParams.gravity = Gravity.CENTER

        val layoutParams = captureRetryLayout.layoutParams as CoordinatorLayout.LayoutParams
        layoutParams.width = CameraUtils.dp2px(context!!, 80f)
        captureRetryLayout.requestLayout()
    }
}