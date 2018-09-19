package com.orego.corporation.orego.views.albumFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import android.widget.LinearLayout
import com.orego.corporation.orego.R
import com.orego.corporation.orego.presenters.MainActivity
import com.orego.corporation.orego.utils.PermissionUtils
import com.orego.corporation.orego.presenters.loaders.loadersAlbum.LoadAlbumImages
import com.orego.corporation.orego.views.base.BaseRestoreFragment

class AlbumFragment : BaseRestoreFragment() {
    private  var albumName: String? = ""
    private lateinit var galleryGridView: GridView
    private lateinit var loadAlbumTask: LoadAlbumImages

    override fun onCreateContentView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val linearLayout = inflater.inflate(R.layout.activity_album, container, false) as LinearLayout
        galleryGridView = linearLayout.findViewById(R.id.galleryGridView)
        return linearLayout    }

    override fun initView(root: View, savedInstanceState: Bundle?) {

        albumName = arguments!!.getString("name")
        activity!!.title = albumName

        val iDisplayWidth = resources.displayMetrics.widthPixels
        val resources = activity!!.applicationContext.resources
        val metrics = resources.displayMetrics
        var dp = iDisplayWidth / (metrics.densityDpi / 160f)

        if (dp < 360) {
            dp = (dp - 17) / 2
            val px = PermissionUtils.convertDpToPixel(dp, activity!!.applicationContext)
            galleryGridView.columnWidth = Math.round(px)
        }


        loadAlbumTask = LoadAlbumImages(galleryGridView, (activity as MainActivity?)!!, albumName!!)
        loadAlbumTask.execute()    }

    override fun initData(savedInstanceState: Bundle?) {
    }


}