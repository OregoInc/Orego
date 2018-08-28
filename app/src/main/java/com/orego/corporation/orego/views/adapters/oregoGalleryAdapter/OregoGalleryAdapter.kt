package com.orego.corporation.orego.views.adapters.oregoGalleryAdapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.orego.corporation.orego.managers.oregoPhotoManagement.OregoPhotoManager
import com.orego.corporation.orego.R
import com.orego.corporation.orego.fragments.MainActivity
import com.orego.corporation.orego.fragments.otherActivities.OldMainActivity
import java.io.File


class OregoGalleryAdapter : RecyclerView.Adapter<OregoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OregoViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val photoView = inflater.inflate(R.layout.orego_photo_item, parent, false)
        return OregoViewHolder(photoView)
    }

    override fun onBindViewHolder(holder: OregoViewHolder, position: Int) {
        val spacePhoto = OregoPhotoManager.getSpacePhotos()[position]
        val photo = File(spacePhoto.file, "result.jpg")
        val imageView = holder.photoImageView
        Glide.with(MainActivity.THIS)
                .load(photo)
                .into(imageView)

    }

    override fun getItemCount(): Int = OregoPhotoManager.getSpacePhotos().size
}