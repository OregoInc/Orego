package com.orego.corporation.orego.models.portrait.headComposition;


import android.opengl.GLES31;
import android.opengl.Matrix;
import android.os.SystemClock;

import com.orego.corporation.orego.models.portrait.personModel.PersonPart;
import com.orego.corporation.orego.models.portrait.personModel.Shader;
import com.orego.corporation.orego.models.portrait.personModel.exceptions.NotLoadedBufferException;
import com.orego.corporation.orego.models.portrait.personModel.face.Face;
import com.orego.corporation.orego.utils.tuple.Tuple;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.logging.Logger;

public final class HeadComposition {

    private static final Logger LOG = Logger.getLogger(HeadComposition.class.getName());

    private Face face;

    private Shader shader;

    private FloatBuffer totalVertexBuffer;

    private FloatBuffer totalColorPerVertexBuffer;

    private final float[] mMatrix = new float[16];
    private final float[] mvMatrix = new float[16];
    private final float[] mvpMatrix = new float[16];

    private int mProgram;
    private double shift = -1d;

    private float[] position = new float[]{0f, 0f, 0f};
    private float[] rotation = new float[]{0f, 0f, 0f};
    private float[] scale = new float[]{5, 5, 5};

    public HeadComposition(final InputStream faceIS) {
        this.face = new Face(faceIS);
        //        this.currentHairStyle = new HairStyle(hairStyleIS);
        long time = System.currentTimeMillis();

        try {
            this.totalVertexBuffer = createNativeByteBuffer(face.getVertexBufferObject().capacity() * 4).asFloatBuffer();
            this.totalColorPerVertexBuffer = createNativeByteBuffer(face.getColorPerVertexObject().capacity() * 4).asFloatBuffer();
            totalVertexBuffer.put(face.getVertexBufferObject());
            totalColorPerVertexBuffer.put(face.getColorPerVertexObject());
        } catch (NotLoadedBufferException e) {
            e.printStackTrace();
        }
        centerScale(face);
        System.out.println("Total time = " + (System.currentTimeMillis() - time));
        totalColorPerVertexBuffer.position(0);
        totalVertexBuffer.position(0);
//        for (int i = 0; i < totalColorPerVertexBuffer.capacity(); i++) {
//            System.out.println(totalColorPerVertexBuffer.get(i));
//        }
    }

    public void loadShader(final InputStream vertexShader, final InputStream fragmentShader){
        this.shader = new Shader(vertexShader, fragmentShader);
    }


    private ByteBuffer createNativeByteBuffer(int length) {
        ByteBuffer bb = ByteBuffer.allocateDirect(length);
        bb.order(ByteOrder.nativeOrder());
        return bb;
    }

    public void draw(float[] pMatrix, float[] vMatrix) {
        mProgram = shader.getProgramm();
        GLES31.glUseProgram(mProgram);

        float[] mMatrix = getMMatrix();
        float[] mvMatrix = getMvMatrix(mMatrix, vMatrix);
        float[] mvpMatrix = getMvpMatrix(mvMatrix, pMatrix);

        setMvpMatrix(mvpMatrix);


        //setColors
        totalColorPerVertexBuffer.position(0);
        int colorHandle = GLES31.glGetAttribLocation(mProgram, "vColor");
        GLES31.glEnableVertexAttribArray(colorHandle);
        GLES31.glVertexAttribPointer(colorHandle, 3, GLES31.GL_FLOAT
                , false, 12, totalColorPerVertexBuffer);

        //setPosition
        int vertexHandle = GLES31.glGetAttribLocation(mProgram, "a_Position");
        GLES31.glEnableVertexAttribArray(vertexHandle);
        totalVertexBuffer.position(0);
        GLES31.glVertexAttribPointer(vertexHandle, 3, GLES31.GL_FLOAT
                , false, 12, totalVertexBuffer);


        setMvMatrix(mvMatrix);

        int drawCount = totalVertexBuffer.capacity() / 3;
        if (this.shift >= 0) {
            double rotation = ((SystemClock.uptimeMillis() % 10000) / 10000f) * (Math.PI * 2);
            if (this.shift == 0d) {
                this.shift = rotation;
            }
            drawCount = (int) ((Math.sin(rotation - this.shift + Math.PI / 2 * 3) + 1)
                    / 2f * drawCount);
        }
        GLES31.glDrawArrays(GLES31.GL_TRIANGLES, 0, drawCount);

        GLES31.glDisableVertexAttribArray(vertexHandle);

    }

    private float[] getMMatrix() {
        // calculate object transformation
        Matrix.setIdentityM(mMatrix, 0);
        if (rotation != null) {
            Matrix.rotateM(mMatrix, 0, rotation[0], 1f, 0f, 0f);
            Matrix.rotateM(mMatrix, 0, rotation[1], 0, 1f, 0f);
            Matrix.rotateM(mMatrix, 0, rotation[2], 0, 0, 1f);
        }
        if (scale != null) {
            Matrix.scaleM(mMatrix, 0, scale[0], scale[1], scale[2]);
        }
        if (position != null) {
            Matrix.translateM(mMatrix, 0, position[0], position[1]
                    , position[2]);
        }
        return mMatrix;
    }

    private float[] getMvMatrix(float[] mMatrix, float[] vMatrix) {
        Matrix.multiplyMM(mvMatrix, 0, vMatrix, 0, mMatrix, 0);
        return mvMatrix;
    }

    private float[] getMvpMatrix(float[] mvMatrix, float[] pMatrix) {
        Matrix.multiplyMM(mvpMatrix, 0, pMatrix, 0, mvMatrix, 0);
        return mvpMatrix;
    }


    private void setMvpMatrix(float[] mvpMatrix) {
        int mMVPMatrixHandle = GLES31.glGetUniformLocation(mProgram, "u_MVPMatrix");
        GLES31.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);

    }

    private void setMvMatrix(float[] mvMatrix) {
        int mMVMatrixHandle = GLES31.glGetUniformLocation(mProgram, "u_MVMatrix");
        GLES31.glUniformMatrix4fv(mMVMatrixHandle, 1, false, mvMatrix, 0);

    }

    private void centerScale(PersonPart personPart) {
        float scaleFactor = 1.0f;
        float largest = personPart.getDimension().getLargest();

        if (largest != 0.0f)
            scaleFactor = (1.0f / largest);

        Tuple center = personPart.getDimension().getCenter();
        float x0, y0, z0;
        float x, y, z;

        for (int i = 0; i < totalVertexBuffer.capacity() / 3; i++) {
            x0 = totalVertexBuffer.get(i * 3);
            y0 = totalVertexBuffer.get(i * 3 + 1);
            z0 = totalVertexBuffer.get(i * 3 + 2);
            x = (x0 - (float) center.getX()) * scaleFactor;
            totalVertexBuffer.put(i * 3, x);
            y = (y0 - (float) center.getY()) * scaleFactor;
            totalVertexBuffer.put(i * 3 + 1, y);
            z = (z0 - (float) center.getZ()) * scaleFactor;
            totalVertexBuffer.put(i * 3 + 2, z);
        }
        totalVertexBuffer.position(0);
    }

    public void print() {
        for (int i = 0; i < totalColorPerVertexBuffer.capacity(); i++) {
            System.out.println(totalColorPerVertexBuffer.get(i));
        }
    }
}
