package com.orego.corporation.orego.presenters.adapters.albumAdapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.orego.corporation.orego.R
import com.orego.corporation.orego.utils.PermissionUtils
import java.io.File
import java.util.ArrayList
import java.util.HashMap

class AlbumAdapter(private val activity: Activity, private val data: ArrayList<HashMap<String, String>>) : BaseAdapter() {

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

    internal inner class AlbumViewHolder {
        var galleryImage: ImageView? = null
        var galleryCount: TextView? = null
        var galleryTitle: TextView? = null

    }
}