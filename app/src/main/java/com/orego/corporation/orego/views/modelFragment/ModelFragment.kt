package com.orego.corporation.orego.views.modelFragment

import android.app.ProgressDialog
import android.os.AsyncTask
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.orego.corporation.orego.R
import com.orego.corporation.orego.presenters.MainActivity
import com.orego.corporation.orego.models.portrait.headComposition.HeadComposition
import com.orego.corporation.orego.views.base.BaseRestoreFragment
import com.orego.corporation.orego.utils.ClientMultipartFormPost
import com.orego.corporation.orego.views.modelFragment.modelView.ModelSurfaceView
import java.io.File
import java.io.FileInputStream
import java.io.IOException

class ModelFragment : BaseRestoreFragment() {


    private var gLView: ModelSurfaceView? = null
    private var countModel: String? = null
    private var headComposition: HeadComposition? = null
    private lateinit var constraintLayout: ConstraintLayout


    override fun onCreateContentView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        constraintLayout = inflater.inflate(R.layout.fragment_model, container, false) as ConstraintLayout
        constraintLayout.findViewById<ConstraintLayout>(R.id.model_container)
        return constraintLayout
    }

    override fun initView(root: View, savedInstanceState: Bundle?) {
        this.countModel = savedInstanceState!!.getInt("countModel").toString()
        AsyncLoader().execute()
    }

    override fun initData(savedInstanceState: Bundle?) {
        AsyncLoader().execute()
    }

    private fun getCountModel(): String {
        return countModel!!
    }

    fun getDirectory(): File {
        return File(File(activity!!.cacheDir, "/OREGO"), "/directory" + getCountModel())
    }

    private inner class AsyncLoader : AsyncTask<Void, Int, Void>() {
        private val dialog: ProgressDialog = ProgressDialog(context)

        override fun onPreExecute() {
            super.onPreExecute()
            this.dialog.setMessage("Loading...")
            this.dialog.setCancelable(false)
            this.dialog.show()
        }

        override fun doInBackground(vararg voids: Void): Void? {
            publishProgress(0)
            try {
                var file = File(getDirectory(), "resultObj.buf")
                publishProgress(1)
                if (!file.exists()) {
                    file = ClientMultipartFormPost.sendFile(getDirectory())
                }
                publishProgress(2)
                val isFace = FileInputStream(file)
                headComposition = HeadComposition(isFace)
                publishProgress(3)

            } catch (e: IOException) {
                e.printStackTrace()
            }

            return null
        }

        override fun onProgressUpdate(vararg values: Int?) {
            super.onProgressUpdate(*values)
            when (values[0]) {
                0 -> this.dialog.setMessage("Готовим Ваше фото")
                1 -> this.dialog.setMessage("Делаем магию с Вашей фотографией")
                2 -> this.dialog.setMessage("Рисуем ваше чудо-личико :)")
                3 -> this.dialog.setMessage("Готово")
            }
        }

        override fun onPostExecute(aVoid: Void) {
            super.onPostExecute(aVoid)
            gLView = ModelSurfaceView(activity as MainActivity, headComposition!!)
            constraintLayout.addView(gLView)
            if (dialog.isShowing) {
                dialog.dismiss()
            }
        }
    }

}