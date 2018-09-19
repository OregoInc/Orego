package com.orego.corporation.orego.utils;

import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.Point;
import android.hardware.Camera;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class CameraUtils {
    private static final String TAG = "CameraUtils";

    public static void setPreviewParams(Point surfaceSize, Camera.Parameters parameters) {
        if (surfaceSize.x <= 0 || surfaceSize.y <= 0 || parameters == null) {
            return;
        }

        List<Camera.Size> previewSizeList = parameters.getSupportedPreviewSizes();
        Camera.Size previewSize = findProperSize(surfaceSize, previewSizeList);
        if (previewSize != null) {
            parameters.setPreviewSize(previewSize.width, previewSize.height);
            Log.d(TAG, "previewSize: width: " + previewSize.width + ", height: " + previewSize.height);
        }

        List<Camera.Size> pictureSizeList = parameters.getSupportedPictureSizes();
        Camera.Size pictureSize = findProperSize(surfaceSize, pictureSizeList);
        if (pictureSize != null) {
            parameters.setPictureSize(pictureSize.width, pictureSize.height);
            Log.d(TAG, "pictureSize: width: " + pictureSize.width + ", height: " + pictureSize.height);
        }

        List<String> focusModeList = parameters.getSupportedFocusModes();
        if (focusModeList != null && focusModeList.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        }

        List<Integer> pictureFormatList = parameters.getSupportedPictureFormats();
        if (pictureFormatList != null && pictureFormatList.contains(ImageFormat.JPEG)) {
            parameters.setPictureFormat(ImageFormat.JPEG);
            parameters.setJpegQuality(100);
        }
    }

    private static Camera.Size findProperSize(Point surfaceSize, List<Camera.Size> sizeList) {
        if (surfaceSize.x <= 0 || surfaceSize.y <= 0 || sizeList == null) {
            return null;
        }

        int surfaceWidth = surfaceSize.x;
        int surfaceHeight = surfaceSize.y;

        List<List<Camera.Size>> ratioListList = new ArrayList<>();
        for (Camera.Size size : sizeList) {
            addRatioList(ratioListList, size);
        }

        final float surfaceRatio = (float) surfaceWidth / surfaceHeight;
        List<Camera.Size> bestRatioList = null;
        float ratioDiff = Float.MAX_VALUE;
        for (List<Camera.Size> ratioList : ratioListList) {
            float ratio = (float) ratioList.get(0).width / ratioList.get(0).height;
            float newRatioDiff = Math.abs(ratio - surfaceRatio);
            if (newRatioDiff < ratioDiff) {
                bestRatioList = ratioList;
                ratioDiff = newRatioDiff;
            }
        }

        Camera.Size bestSize = null;
        int diff = Integer.MAX_VALUE;
        assert bestRatioList != null;
        for (Camera.Size size : bestRatioList) {
            int newDiff = Math.abs(size.width - surfaceWidth) + Math.abs(size.height - surfaceHeight);
            if (size.height >= surfaceHeight && newDiff < diff) {
                bestSize = size;
                diff = newDiff;
            }
        }

        if (bestSize != null) {
            return bestSize;
        }

        diff = Integer.MAX_VALUE;
        for (Camera.Size size : bestRatioList) {
            int newDiff = Math.abs(size.width - surfaceWidth) + Math.abs(size.height - surfaceHeight);
            if (newDiff < diff) {
                bestSize = size;
                diff = newDiff;
            }
        }

        return bestSize;
    }

    private static void addRatioList(List<List<Camera.Size>> ratioListList, Camera.Size size) {
        float ratio = (float) size.width / size.height;
        for (List<Camera.Size> ratioList : ratioListList) {
            float mine = (float) ratioList.get(0).width / ratioList.get(0).height;
            if (ratio == mine) {
                ratioList.add(size);
                return;
            }
        }

        List<Camera.Size> ratioList = new ArrayList<>();
        ratioList.add(size);
        ratioListList.add(ratioList);
    }

    public static int dp2px(Context context, float dpValue) {
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * density + 0.5f);
    }
}
