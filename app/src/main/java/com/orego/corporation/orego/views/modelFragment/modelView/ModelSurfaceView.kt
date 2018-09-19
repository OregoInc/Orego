package com.orego.corporation.orego.views.modelFragment.modelView

import android.annotation.SuppressLint
import android.opengl.GLSurfaceView
import android.view.MotionEvent
import com.orego.corporation.orego.presenters.MainActivity
import com.orego.corporation.orego.models.controllers.TouchController
import com.orego.corporation.orego.presenters.modelRender.ModelRender
import com.orego.corporation.orego.models.portrait.headComposition.HeadComposition

@SuppressLint("ViewConstructor")
class ModelSurfaceView (mainActivity: MainActivity, headComposition: HeadComposition): GLSurfaceView(mainActivity) {
    private var mRenderer: ModelRender
    private val parent = mainActivity
    private var touchHandler: TouchController

    init{
        setEGLContextClientVersion(3)
        mRenderer = ModelRender(this, headComposition)
        setRenderer(mRenderer)
        touchHandler = TouchController(this, mRenderer)

    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        return touchHandler.onTouchEvent(event)
    }


    fun getMainActivity(): MainActivity {
        return parent
    }

}