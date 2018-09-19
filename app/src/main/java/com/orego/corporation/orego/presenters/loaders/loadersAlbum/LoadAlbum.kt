package com.orego.corporation.orego.presenters.loaders.loadersAlbum

import android.database.MergeCursor
import android.os.AsyncTask
import android.os.Bundle
import android.provider.MediaStore
import android.widget.AdapterView
import android.widget.GridView
import com.orego.corporation.orego.presenters.MainActivity
import com.orego.corporation.orego.models.suppliers.MapComparator
import com.orego.corporation.orego.utils.PermissionUtils
import com.orego.corporation.orego.views.albumFragment.AlbumFragment
import com.orego.corporation.orego.presenters.adapters.albumAdapter.AlbumAdapter
import java.util.*

class LoadAlbum(private val mainActivity: MainActivity, private val galleryGridView: GridView) : AsyncTask<String, Void, String>() {

    var albumList = ArrayList<HashMap<String, String>>()
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
        val cursorExternal = mainActivity.contentResolver.query(uriExternal, projection, "_data IS NOT NULL) GROUP BY (bucket_display_name", null, null)
        val cursorInternal = mainActivity.contentResolver.query(uriInternal, projection, "_data IS NOT NULL) GROUP BY (bucket_display_name", null, null)
        val cursor = MergeCursor(arrayOf(cursorExternal, cursorInternal))

        while (cursor.moveToNext()) {

            path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA))
            album = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME))
            timestamp = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_MODIFIED))
            countPhoto = PermissionUtils.getCount(mainActivity.applicationContext, album)

            albumList.add(PermissionUtils.mappingInbox(album, path, timestamp, PermissionUtils.converToTime(timestamp), countPhoto))
        }
        cursor.close()
        Collections.sort(albumList, MapComparator(PermissionUtils.KEY_TIMESTAMP, "dsc")) // Arranging photo album by timestamp decending
        return xml
    }

    override fun onPostExecute(xml: String) {
        val adapter = AlbumAdapter(mainActivity, albumList)
        galleryGridView.adapter = adapter
        galleryGridView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val bundle = Bundle()
            bundle.putString("name", albumList[+position][PermissionUtils.KEY_ALBUM])
            val albumFragment = AlbumFragment()
            albumFragment.arguments = bundle
            mainActivity.replaceFragment(albumFragment)
        }
    }


}