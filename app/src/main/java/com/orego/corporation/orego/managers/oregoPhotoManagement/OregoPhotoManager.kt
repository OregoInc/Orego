package com.orego.corporation.orego.managers.oregoPhotoManagement

import android.os.Environment
import java.io.File

object OregoPhotoManager {

    private val file: File = File(Environment.getExternalStorageDirectory(), "/OREGO")


    private val oregoPhotoList = mutableListOf<OregoPhoto>()

    init {
        var count = 0
        var photo = File(file, "directory$count")

        while (photo.exists()) {
            add(OregoPhoto(photo))
            count++
            photo = File(file, "directory$count")
        }
    }

    fun add(oregoPhoto: OregoPhoto) = oregoPhotoList.add(oregoPhoto)

    fun remove(oregoPhoto: OregoPhoto) = oregoPhotoList.remove(oregoPhoto)

    fun getSpacePhotos(): List<OregoPhoto> = oregoPhotoList
}