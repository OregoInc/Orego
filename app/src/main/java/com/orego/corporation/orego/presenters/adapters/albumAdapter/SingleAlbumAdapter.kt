package com.orego.corporation.orego.presenters.adapters.albumAdapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.orego.corporation.orego.R
import com.orego.corporation.orego.utils.PermissionUtils
import java.io.File
import java.util.ArrayList
import java.util.HashMap

class SingleAlbumAdapter(private val activity: Activity, private val data: ArrayList<HashMap<String, String>>) : BaseAdapter() {

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
        var convertView = convertView
        val holder: SingleAlbumViewHolder
        if (convertView == null) {
            holder = SingleAlbumViewHolder()
            convertView = LayoutInflater.from(activity).inflate(
                    R.layout.single_album_row, parent, false)

            holder.galleryImage = convertView!!.findViewById(R.id.galleryImage)

            convertView.tag = holder
        } else {
            holder = convertView.tag as SingleAlbumViewHolder
        }
        holder.galleryImage!!.id = position

        val song: HashMap<String, String>
        song = data[position]
        try {

            Glide.with(activity)
                    .load(File(song[PermissionUtils.KEY_PATH])) // Uri of the picture
                    .into(holder.galleryImage)


        } catch (ignored: Exception) {
        }

        return convertView
    }
    internal class SingleAlbumViewHolder {
        var galleryImage: ImageView? = null
    }
}