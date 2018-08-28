package com.orego.corporation.orego.fragments.galleryFragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.NestedScrollView
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.orego.corporation.orego.R
import com.orego.corporation.orego.fragments.MainActivity
import com.orego.corporation.orego.fragments.otherActivities.OldMainActivity
import com.orego.corporation.orego.views.adapters.oregoGalleryAdapter.OregoGalleryAdapter

var OREGO_GALLERY_ADAPTER: OregoGalleryAdapter? = null
var VIEW: View? = null
var THIS: GalleryFragment? = null

class GalleryFragment : Fragment(){


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val nestedScrollView = inflater.inflate(R.layout.fragment_gallery, container, false) as NestedScrollView
        this.initRecyclerView(nestedScrollView)
        VIEW = view
        return nestedScrollView
    }


    private fun initRecyclerView(view: View) {
        //Init recyclerView adapter:
        val adapter = OregoGalleryAdapter()
        THIS = this
        OREGO_GALLERY_ADAPTER = adapter
        //Init recyclerView:
        val recyclerView = view.findViewById<RecyclerView>(R.id.rv_images)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = GridLayoutManager(MainActivity.THIS, 2)
        recyclerView.adapter = adapter
    }

    companion object INIT {
        fun init(){
            val adapter = OregoGalleryAdapter()
            OREGO_GALLERY_ADAPTER = adapter
//            Init recyclerView:
            val recyclerView = VIEW!!.findViewById<RecyclerView>(R.id.rv_images)

            recyclerView.setHasFixedSize(true)
            recyclerView.layoutManager = GridLayoutManager(MainActivity.THIS, 2)
            recyclerView.adapter = adapter
        }
        fun getTHIS(): GalleryFragment? {
            return THIS
        }
    }
}