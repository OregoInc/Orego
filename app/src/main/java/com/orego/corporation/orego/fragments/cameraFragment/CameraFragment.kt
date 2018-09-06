package com.orego.corporation.orego.fragments.cameraFragment

import android.Manifest
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.database.MergeCursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
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
import com.bumptech.glide.Glide
import com.orego.corporation.orego.R
import com.orego.corporation.orego.R.id.main_recycle1
import com.orego.corporation.orego.base.BaseRestoreFragment
import com.orego.corporation.orego.fragments.MainActivity
import com.orego.corporation.orego.fragments.cameraFragment.CameraFrag.directoryPhoto
import com.orego.corporation.orego.fragments.galleryFragment.OREGO_GALLERY_ADAPTER
import com.orego.corporation.orego.fragments.otherActivities.face3dActivity.model3D.view.ModelActivity
import com.orego.corporation.orego.gallery.AlbumFragment
import com.orego.corporation.orego.gallery.MapComparator
import com.orego.corporation.orego.gallery.PermissionUtils
import com.orego.corporation.orego.layout.impl.ScaleTransformer
import com.orego.corporation.orego.managers.GalleryLayoutManager
import com.orego.corporation.orego.utils.ClientMultipartFormPost
import com.orego.corporation.orego.utils.SwipeListener
import com.orego.corporation.orego.views.adapters.oregoGalleryAdapter.OregoGalleryAdapter
import com.orego.corporation.orego.views.cameraview.CameraManager
import com.orego.corporation.orego.views.cameraview.CameraUtils
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

class CameraFragment : BaseRestoreFragment(), SurfaceHolder.Callback, View.OnClickListener {

    private var mMainRecycle1: RecyclerView? = null
    private lateinit var mSurfaceView: SurfaceView
    private lateinit var mPictureView: ImageView
    private var mPicture: Bitmap? = null
    private var isSurfaceCreated: Boolean = false
    private lateinit var captureRetryLayout: View
    lateinit var btnCapture: ImageView
    lateinit var btnRetry: ImageView
    lateinit var btnSwitchCamera: ImageView
    private lateinit var btnInfo: ImageView
    private var isExpanded: Boolean = false
    private lateinit var mBottomSheetBehavior: BottomSheetBehavior<*>
    lateinit var btnSheetOpen: ImageView
    private lateinit var buttonCollapse: ImageView
    private val REQUEST_PERMISSION_KEY = 1
    private var mCameraListener: CameraListener? = null

    lateinit var galleryGridView: GridView
    private lateinit var loadAlbumTask: LoadAlbum
    internal var albumList = ArrayList<HashMap<String, String>>()

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateContentView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val coordinatorLayout = inflater!!.inflate(R.layout.camera_fragment, container, false) as CoordinatorLayout
        mMainRecycle1 = coordinatorLayout.findViewById(main_recycle1)
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
                ClientMultipartFormPost.sendPictureAndReplace(bitmap, initPath(activity!!), activity!!)
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
            ActivityCompat.requestPermissions(activity!!, permissions, REQUEST_PERMISSION_KEY)
        } else {
            loadAlbumTask = LoadAlbum()
            loadAlbumTask.execute()
        }
        return coordinatorLayout
    }

    override fun initView(root: View?, savedInstanceState: Bundle?) {
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
        Log.i("CAMERA_FRAGMENT", "mMainRecycle1 = $mMainRecycle1")
        layoutManager1.attach(mMainRecycle1, 0)
        layoutManager1.setItemTransformer(ScaleTransformer())
        val adapter = OregoGalleryAdapter()
        OREGO_GALLERY_ADAPTER = adapter
        mMainRecycle1!!.setHasFixedSize(true)
        mMainRecycle1!!.adapter = adapter
        mMainRecycle1!!.layoutManager = layoutManager1
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_PERMISSION_KEY -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    loadAlbumTask = LoadAlbum()
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
        if (mPicture != null && mCameraListener != null) {
            mCameraListener!!.onCapture(mPicture!!)
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

    companion object {
        private const val TAG = "CameraFragment"

        fun initPath(activity: Activity): String {
            val orego = File(activity.cacheDir, "/OREGO")
            if (!orego.exists()) orego.mkdir()
            directoryPhoto = File(orego, "directory${CameraFrag.getCount()}")
            if (!directoryPhoto.exists()) directoryPhoto.mkdir()
            val photo = File(directoryPhoto, "result.jpg")
            val mPath = photo.absolutePath
            return mPath!!
        }

        fun startForResult(activity: Activity, path: File, requestCode: Int) {
            val intent = Intent(activity, CameraFragment::class.java)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, path.absolutePath)
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            activity.startActivityForResult(intent, requestCode)
        }
    }

    @SuppressLint("StaticFieldLeak")
    inner class LoadAlbum : AsyncTask<String, Void, String>() {
        override fun onPreExecute() {
            super.onPreExecute()
            albumList.clear()
        }

        override fun doInBackground(vararg args: String): String {
            val xml = ""

            var path: String?
            var album: String?
            var timestamp: String?
            var countPhoto: String?
            val uriExternal = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            val uriInternal = MediaStore.Images.Media.INTERNAL_CONTENT_URI


            val projection = arrayOf(MediaStore.MediaColumns.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME, MediaStore.MediaColumns.DATE_MODIFIED)
            val cursorExternal = activity!!.contentResolver.query(uriExternal, projection, "_data IS NOT NULL) GROUP BY (bucket_display_name", null, null)
            val cursorInternal = activity!!.contentResolver.query(uriInternal, projection, "_data IS NOT NULL) GROUP BY (bucket_display_name", null, null)
            val cursor = MergeCursor(arrayOf(cursorExternal, cursorInternal))

            while (cursor.moveToNext()) {

                path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA))
                album = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME))
                timestamp = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_MODIFIED))
                countPhoto = PermissionUtils.getCount(activity!!.applicationContext, album)

                albumList.add(PermissionUtils.mappingInbox(album, path, timestamp, PermissionUtils.converToTime(timestamp), countPhoto))
            }
            cursor.close()
            Collections.sort(albumList, MapComparator(PermissionUtils.KEY_TIMESTAMP, "dsc")) // Arranging photo album by timestamp decending
            return xml
        }

        override fun onPostExecute(xml: String) {
            val adapter = AlbumAdapter(activity!!, albumList)
            galleryGridView.adapter = adapter
            galleryGridView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
                //                    Intent intent = new Intent(getActivity().getBaseContext(), AlbumFragment.class);
                //                    intent.putExtra("name", albumList.get(+position).get(Function.KEY_ALBUM));
                //                    startActivity(intent);
                val bundle = Bundle()
                bundle.putString("name", albumList[+position][PermissionUtils.KEY_ALBUM])
                val albumFragment = AlbumFragment()
                albumFragment.arguments = bundle
                (activity as MainActivity).replaceFragment(albumFragment)
            }
        }
    }


    internal inner class AlbumAdapter(private val activity: Activity, private val data: ArrayList<HashMap<String, String>>) : BaseAdapter() {

        override fun getCount(): Int {
            return data.size
        }

        override fun getItem(position: Int): Any {
            return position
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            var viewConvert = convertView
            val holder: AlbumViewHolder?
            if (viewConvert == null) {
                holder = AlbumViewHolder()
                viewConvert = LayoutInflater.from(activity).inflate(
                        R.layout.album_row, parent, false)

                holder.galleryImage = viewConvert!!.findViewById<View>(R.id.galleryImage) as ImageView
                holder.galleryCount = viewConvert.findViewById<View>(R.id.gallery_count) as TextView
                holder.galleryTitle = viewConvert.findViewById<View>(R.id.gallery_title) as TextView

                viewConvert.tag = holder
            } else {
                holder = viewConvert.tag as AlbumViewHolder
            }
            holder.galleryImage!!.id = position
            holder.galleryCount!!.id = position
            holder.galleryTitle!!.id = position

            val song: HashMap<String, String> = data[position]
            try {
                holder.galleryTitle!!.text = song[PermissionUtils.KEY_ALBUM]
                holder.galleryCount!!.text = song[PermissionUtils.KEY_COUNT]

                Glide.with(activity)
                        .load(File(song[PermissionUtils.KEY_PATH])) // Uri of the picture
                        .into(holder.galleryImage!!)


            } catch (ignored: Exception) {
            }

            return viewConvert
        }
    }


    internal inner class AlbumViewHolder {
        var galleryImage: ImageView? = null
        var galleryCount: TextView? = null
        var galleryTitle: TextView? = null
    }
}
