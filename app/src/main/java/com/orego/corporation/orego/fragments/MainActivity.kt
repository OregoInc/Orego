package com.orego.corporation.orego.fragments


import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Window
import android.view.WindowManager
import butterknife.ButterKnife
import com.orego.corporation.orego.R
import com.orego.corporation.orego.fragments.cameraFragment.CameraFragment
import com.orego.corporation.orego.fragments.galleryFragment.GalleryFragment
import com.orego.corporation.orego.fragments.infoFragment.InfoFragment
import com.orego.corporation.orego.fragments.menuFragment.MenuFragment
import com.orego.corporation.orego.fragments.otherActivities.camera.PermissionsDelegate
import com.orego.corporation.orego.gallery.PermissionUtils
import com.orego.corporation.orego.TestFragment

/**
 * Главное activity отображающая один из 4 фрагментов
 * и содержащее контроллер этих фрагментов.
 */


class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MAIN_ACTIVITY"

        lateinit var THIS: MainActivity

        private const val EXIT_STATUS = 0
        internal val REQUEST_PERMISSION_KEY = 1
    }

    private lateinit var menuFragment: MenuFragment
    private lateinit var cameraFragment: CameraFragment
    private lateinit var galleryFragment: GalleryFragment
    private lateinit var infoFragment: InfoFragment
    private lateinit var testFragment: TestFragment

    private var currentIndexFragment = 0

    private val permissionsDelegate = PermissionsDelegate(this)


    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i(TAG, "Create Main Activity")

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initFragments()
        if (!permissionsDelegate.hasCameraPermission()) permissionsDelegate.requestCameraPermission()
        replaceFragment(currentIndexFragment)
        THIS = this


        /*new Code*/
        val PERMISSIONS = arrayOf<String>(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
        if (!PermissionUtils.hasPermissions(this, *PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, 1)
        }
        ButterKnife.bind(this)
    }

    private fun initFragments() {
        testFragment = TestFragment()
        menuFragment = MenuFragment()
        cameraFragment = CameraFragment()
        galleryFragment = GalleryFragment()
        infoFragment = InfoFragment()
    }

    fun replaceFragment(index: Int?) {
        Log.i(TAG, "replace Fragment new index:$index")
        currentIndexFragment = index!!
        when (index) {
        //TODO: supportFragmentManager.beginTransaction().setCustomAnimations()
            0 -> supportFragmentManager.beginTransaction().replace(R.id.container, cameraFragment).commit()
            1 -> supportFragmentManager.beginTransaction().replace(R.id.container, galleryFragment).commit()
            2 -> supportFragmentManager.beginTransaction().replace(R.id.container, infoFragment).commit()
            3 -> supportFragmentManager.beginTransaction().replace(R.id.container, menuFragment).commit()
            10 -> supportFragmentManager.beginTransaction().replace(R.id.container, testFragment).commit()

        }
    }

    override fun onBackPressed() {
        Log.i(TAG, "OnBackPressed index:$currentIndexFragment")
        if (currentIndexFragment != 0) replaceFragment(0)
        else {
            super.onBackPressed()
            val intent = Intent(Intent.ACTION_MAIN)
            intent.addCategory(Intent.CATEGORY_HOME)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            this.startActivity(intent)
            this.finish()
            System.exit(EXIT_STATUS)
        }
    }


    fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.container, fragment).commit()
    }
}
