package com.orego.corporation.orego.fragments.otherActivities.face3dActivity.model3D.modelRender;

import android.graphics.Bitmap;
import android.graphics.Camera;
import android.opengl.GLES31;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import com.orego.corporation.orego.fragments.otherActivities.face3dActivity.model3D.portrait.headComposition.HeadComposition;
import com.orego.corporation.orego.fragments.otherActivities.face3dActivity.model3D.view.ModelSurfaceView;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class ModelRender implements GLSurfaceView.Renderer {
    private int width;
    private int height;

    private HeadComposition headComposition;

    private ModelSurfaceView view;
    private com.orego.corporation.orego.fragments.otherActivities.face3dActivity.model3D.entities.Camera camera;
    private Bitmap bitmap;

    private static final float FAR = 100f;
    private static final float NEAR = 1f;
    private int takeScreenShoot = 0;

    private final float[] modelProjectionMatrix = new float[16];
    private final float[] modelViewMatrix = new float[16];
    private final float[] mvpMatrix = new float[16];

    public ModelRender(ModelSurfaceView view, HeadComposition headComposition) {
        this.view = view;
        this.headComposition = headComposition;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
//        takeScreenShoot = 0;
        GLES31.glClearColor(0.111f, 0.2f, 0.387f, 1.0f);
        GLES31.glEnable(GLES31.GL_DEPTH_TEST);
        GLES31.glEnable(GLES31.GL_BLEND);
        GLES31.glBlendFunc(GLES31.GL_ONE, GLES31.GL_ONE_MINUS_SRC_ALPHA);
        try {
            InputStream isVertex = view.getModelActivity().getAssets().open("shaderFiles/vertex.shader");
            InputStream isFragment = view.getModelActivity().getAssets().open("shaderFiles/fragment.shader");
            headComposition.loadShader(isVertex, isFragment);
        } catch (IOException e) {
            e.printStackTrace();
        }
        camera = new com.orego.corporation.orego.fragments.otherActivities.face3dActivity.model3D.entities.Camera();

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        this.width = width;
        this.height = height;
        GLES31.glViewport(0, 0, width, height);
        Matrix.setLookAtM(modelViewMatrix, 0, camera.xPos, camera.yPos, camera.zPos, camera.xView, camera.yView,
                camera.zView, camera.xUp, camera.yUp, camera.zUp);
        float ratio = (float) width / height;
        Matrix.frustumM(modelProjectionMatrix, 0, -ratio, ratio, -1, 1, NEAR, FAR);

        Matrix.multiplyMM(mvpMatrix, 0, modelProjectionMatrix, 0, modelViewMatrix, 0);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES31.glClear(GLES31.GL_COLOR_BUFFER_BIT | GLES31.GL_DEPTH_BUFFER_BIT);
        camera.animate();
        if (camera.hasChanged()) {
            Matrix.setLookAtM(modelViewMatrix, 0, camera.xPos, camera.yPos, camera.zPos
                    , camera.xView, camera.yView, camera.zView, camera.xUp, camera.yUp, camera.zUp);
            Matrix.multiplyMM(mvpMatrix, 0, modelProjectionMatrix, 0
                    , modelViewMatrix, 0);
            camera.setChanged(false);
        }
        headComposition.draw(modelProjectionMatrix, modelViewMatrix);
//        System.out.println(takeScreenShoot);
//
//        if (takeScreenShoot == 100) {
//            bitmap = ScreenShott.getInstance().takeScreenShotOfRootView(view);
//            try {
//                ScreenShott.getInstance()
//                        .saveScreenshotToPicturesFolder(view.getModelActivity(), bitmap, "screenShoot");
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            takeScreenShoot = 10000;
//        } else if (takeScreenShoot <= 120)
//            takeScreenShoot++;


    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public com.orego.corporation.orego.fragments.otherActivities.face3dActivity.model3D.entities.Camera getCamera() {
        return camera;
    }

}
