package com.orego.corporation.orego.presenters.loaders.loadersAlbum

import android.database.MergeCursor
import android.graphics.drawable.BitmapDrawable
import android.os.AsyncTask
import android.provider.MediaStore
import android.widget.GridView
import android.widget.ImageView
import com.orego.corporation.orego.presenters.MainActivity
import com.orego.corporation.orego.models.suppliers.MapComparator
import com.orego.corporation.orego.utils.ClientMultipartFormPost
import com.orego.corporation.orego.utils.PermissionUtils
import com.orego.corporation.orego.presenters.adapters.albumAdapter.SingleAlbumAdapter
import java.util.*

class LoadAlbumImages(private val galleryGridView: GridView, private val activity: MainActivity, private val albumName: String) : AsyncTask<String, Void, String>() {

    private var imageList = ArrayList<HashMap<String, String>>()
    override fun onPreExecute() {
        super.onPreExecute()
        imageList.clear()
    }

    override fun doInBackground(vararg args: String): String {
        val xml = ""

        var path: String?
        var album: String?
        var timestamp: String?
        val uriExternal = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val uriInternal = MediaStore.Images.Media.INTERNAL_CONTENT_URI

        val projection = arrayOf(MediaStore.MediaColumns.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME, MediaStore.MediaColumns.DATE_MODIFIED)

        val cursorExternal = activity.contentResolver.query(uriExternal, projection, "bucket_display_name = \"$albumName\"", null, null)
        val cursorInternal = activity.contentResolver.query(uriInternal, projection, "bucket_display_name = \"$albumName\"", null, null)
        val cursor = MergeCursor(arrayOf(cursorExternal, cursorInternal))
        while (cursor.moveToNext()) {

            path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA))
            album = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME))
            timestamp = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_MODIFIED))

            imageList.add(PermissionUtils.mappingInbox(album, path, timestamp, PermissionUtils.converToTime(timestamp), null))
        }
        cursor.close()
        Collections.sort(imageList, MapComparator(PermissionUtils.KEY_TIMESTAMP, "dsc")) // Arranging photo album by timestamp decending
        return xml
    }

    override fun onPostExecute(xml: String) {

        val adapter = SingleAlbumAdapter(activity, imageList)
        galleryGridView.adapter = adapter

        galleryGridView.setOnItemClickListener { _, _, _, id ->
            val imageView = activity.findViewById<ImageView>(id.toInt())//TODO если кто-то знает как вытащить оригин данного изображения будет лучше!
            val image = (imageView.drawable as BitmapDrawable).bitmap
            //                String path = CameraFragment.Companion.initPath(getActivity());
            val path = ""
            ClientMultipartFormPost.sendPictureAndReplace(image, path, activity)
        }
    }
}