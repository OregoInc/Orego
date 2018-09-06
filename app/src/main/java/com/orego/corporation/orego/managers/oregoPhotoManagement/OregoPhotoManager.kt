package com.orego.corporation.orego.managers.oregoPhotoManagement

import com.orego.corporation.orego.fragments.MainActivity
import java.io.File


object OregoPhotoManager {

    private val file: File = File(MainActivity.THIS.baseContext.cacheDir, "/OREGO")

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