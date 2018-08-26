package com.orego.corporation.orego.managers.oregoPhotoManagement;

import android.os.Parcel
import android.os.Parcelable
import java.io.File

data class OregoPhoto(val file: File) : Parcelable {

    constructor(parcel: Parcel) : this(
            File(parcel.readString()))

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(file.absolutePath)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<OregoPhoto> {

        override fun createFromParcel(parcel: Parcel): OregoPhoto = OregoPhoto(parcel)

        override fun newArray(size: Int): Array<OregoPhoto?> = arrayOfNulls(size)
    }
}