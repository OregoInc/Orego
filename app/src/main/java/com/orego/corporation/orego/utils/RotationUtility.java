package com.orego.corporation.orego.utils;

import java.nio.FloatBuffer;

public class RotationUtility {
    public static void rotateX(final int index, final FloatBuffer floatBuffer, final float angle){
        final float oldY = floatBuffer.get(index + 1);
        final float oldZ = floatBuffer.get(index + 2);
        //Считаем по формуле новые y и z:
        final float newY = (float) (oldY * Math.cos(angle) - oldZ * Math.sin(angle));
        final float newZ = (float) (oldY * Math.sin(angle) + oldZ * Math.cos(angle));
    }

    public static void rotateY(final int index, final FloatBuffer floatBuffer, final float angle){
        final float oldX = floatBuffer.get(index);
        final float oldZ = floatBuffer.get(index + 2);
        //Считаем по формуле новые x и z:
        final float newX = (float) (oldX * Math.cos(angle) + oldZ * Math.sin(angle));
        final float newZ = (float) (oldZ * Math.cos(angle) - oldX * Math.sin(angle));
    }

    public static void rotateZ(final int index, final FloatBuffer floatBuffer, final float angle){
        final float oldX = floatBuffer.get(index);
        final float oldY = floatBuffer.get(index + 1);
        //Считаем по формуле новые x и y:
        final float newX = (float) (oldX * Math.cos(angle) - oldY * Math.sin(angle));
        final float newY = (float) (oldX * Math.sin(angle) + oldY * Math.cos(angle));
    }
}
