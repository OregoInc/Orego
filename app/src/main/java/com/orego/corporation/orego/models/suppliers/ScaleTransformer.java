package com.orego.corporation.orego.models.suppliers;

import android.view.View;
import com.orego.corporation.orego.presenters.managers.GalleryLayoutManager;

public class ScaleTransformer implements GalleryLayoutManager.ItemTransformer {

    private static final String TAG = "CurveTransformer";


    @Override
    public void transformItem(GalleryLayoutManager layoutManager, View item, float fraction) {
        item.setPivotX(item.getWidth() / 2.f);
        item.setPivotY(item.getHeight() / 2.f);
        float scale = 1 - 0.2f * Math.abs(fraction);
        item.setScaleX(scale);
        item.setScaleY(scale);
    }
}
