package com.orego.corporation.orego.presenters


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
import com.orego.corporation.orego.models.suppliers.PermissionsDelegate
import com.orego.corporation.orego.views.cameraFragment.CameraFragment
import com.orego.corporation.orego.utils.PermissionUtils
import com.orego.corporation.orego.views.modelFragment.ModelFragment

/**
 * Главное activity отображающая
 * либо фрагмент камеры либо фрагмент 3D модели
 * и содержащее контроллер этих фрагментов.
 */


class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MAIN_ACTIVITY"
        private const val EXIT_PERMISSION_DENIED = 137
    }

    private lateinit var cameraFragment: CameraFragment
    private lateinit var modelFragment: ModelFragment

    private val permissionsDelegate = PermissionsDelegate(this)


    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i(TAG, "Create Main Activity")

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (!permissionsDelegate.hasCameraPermission()) permissionsDelegate.requestCameraPermission()
        if (!permissionsDelegate.hasCameraPermission()){
            val intent = Intent(Intent.ACTION_MAIN)
            intent.addCategory(Intent.CATEGORY_HOME)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            this.startActivity(intent)
            this.finish()
            System.exit(EXIT_PERMISSION_DENIED)
        }else{
            initFragments()
            replaceFragment(cameraFragment)
        }

        /*new Code*/
        val permissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
        if (!PermissionUtils.hasPermissions(this, *permissions)) {
            ActivityCompat.requestPermissions(this, permissions, 1)
        }
        ButterKnife.bind(this)
    }

    private fun initFragments() {
        cameraFragment = CameraFragment()
        modelFragment = ModelFragment()
    }

    fun replaceFragment(fragment: Fragment) {
        Log.i(TAG, "replace Fragment")
        supportFragmentManager.beginTransaction().replace(R.id.container, fragment).commit()
    }
}
