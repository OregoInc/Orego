package com.orego.corporation.orego.fragments.otherActivities.face3dActivity.model3D.view;

import android.annotation.SuppressLint;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

import com.orego.corporation.orego.fragments.otherActivities.face3dActivity.model3D.controller.TouchController;
import com.orego.corporation.orego.fragments.otherActivities.face3dActivity.model3D.modelRender.ModelRender;
import com.orego.corporation.orego.fragments.otherActivities.face3dActivity.model3D.portrait.headComposition.HeadComposition;

@SuppressLint("ViewConstructor")
public final class ModelSurfaceView extends GLSurfaceView {

    private ModelRender mRenderer;
    private ModelActivity parent;
    private TouchController touchHandler;

    public ModelSurfaceView(final ModelActivity parent, HeadComposition headComposition) {
        super(parent);
        this.parent = parent;
        setEGLContextClientVersion(3);
        mRenderer = new ModelRender(this, headComposition);
        setRenderer(mRenderer);
        touchHandler = new TouchController(this, mRenderer);

    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return touchHandler.onTouchEvent(event);
    }


    public ModelActivity getModelActivity() {
        return parent;
    }

    public ModelRender getmRenderer() {
        return mRenderer;
    }

}