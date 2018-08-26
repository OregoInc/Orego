package com.orego.corporation.orego.fragments.galleryFragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.orego.corporation.orego.R
import com.orego.corporation.orego.fragments.otherActivities.OldMainActivity
import com.orego.corporation.orego.views.adapters.oregoGalleryAdapter.OregoGalleryAdapter


var OREGO_GALLERY_ADAPTER: OregoGalleryAdapter? = null
var VIEW: View? = null
var THIS: OregoGalleryFragment? = null

class OregoGalleryFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?
                              , savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_gallery, container, false) as View
        this.initRecyclerView(view)
        VIEW = view
        return view
    }


    private fun initRecyclerView(view: View) {
        //Init recyclerView adapter:
        val adapter = OregoGalleryAdapter()
        THIS = this
        OREGO_GALLERY_ADAPTER = adapter
        //Init recyclerView:
        val recyclerView = view.findViewById<RecyclerView>(R.id.rv_images)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = GridLayoutManager(OldMainActivity.THIS, 2)
        recyclerView.adapter = adapter
    }

    companion object INIT {
        fun init(){
            val adapter = OregoGalleryAdapter()
            OREGO_GALLERY_ADAPTER = adapter
            //Init recyclerView:
            val recyclerView = VIEW!!.findViewById<RecyclerView>(R.id.rv_images)
            recyclerView.setHasFixedSize(true)
            recyclerView.layoutManager = GridLayoutManager(OldMainActivity.THIS, 2)
            recyclerView.adapter = adapter
        }
        fun getTHIS(): OregoGalleryFragment? {
            return THIS
        }
    }
}