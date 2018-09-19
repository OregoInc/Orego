package com.orego.corporation.orego.models.portrait.personModel;

import com.orego.corporation.orego.utils.tuple.Tuple;

import java.text.DecimalFormat;


/**
 * Created by ilya dolgushev on 03.04.2018.
 *
 */

public final class ModelDimensions {
    // edge coordinates
    private float leftPt, rightPt; // on x-axis
    private float topPt, bottomPt; // on y-axis
    private float farPt, nearPt; // on z-axis

    // for reporting
    private DecimalFormat df = new DecimalFormat("0.##"); // 2 dp

    ModelDimensions() {
        leftPt = 0.0f;
        rightPt = 0.0f;
        topPt = 0.0f;
        bottomPt = 0.0f;
        farPt = 0.0f;
        nearPt = 0.0f;
    } // end of ModelDimensions()

    void set(float x, float y, float z)
    // initialize the model's edge coordinates
    {
        rightPt = x;
        leftPt = x;

        topPt = y;
        bottomPt = y;

        nearPt = z;
        farPt = z;
    } // end of set()

    void update(float x, float y, float z)
    // update the edge coordinates using vert
    {
        if (x > rightPt)
            rightPt = x;
        if (x < leftPt)
            leftPt = x;

        if (y > topPt)
            topPt = y;
        if (y < bottomPt)
            bottomPt = y;

        if (z > nearPt)
            nearPt = z;
        if (z < farPt)
            farPt = z;
    } // end of update()

    // ------------- use the edge coordinates ----------------------------

    public float getWidth() {
        return (rightPt - leftPt);
    }

    public float getHeight() {
        return (topPt - bottomPt);
    }

    private float getDepth() {
        return (nearPt - farPt);
    }

    public float getLargest() {
        float height = getHeight();
        float depth = getDepth();

        float largest = getWidth();
        if (height > largest)
            largest = height;
        if (depth > largest)
            largest = depth;
        return largest;
    }

    public Tuple<Float, Float, Float> getCenter() {
        float xc = (rightPt + leftPt) / 2.0f;
        float yc = (topPt + bottomPt) / 2.0f;
        float zc = (nearPt + farPt) / 2.0f;
        return new Tuple<>(xc, yc, zc);
    }
}