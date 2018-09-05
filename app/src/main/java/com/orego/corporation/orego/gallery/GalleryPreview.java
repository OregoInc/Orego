package com.orego.corporation.orego.gallery;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.orego.corporation.orego.R;
import com.orego.corporation.orego.base.BaseRestoreFragment;

import java.io.File;



public class GalleryPreview extends BaseRestoreFragment {

    ImageView GalleryPreviewImg;
    String path;

    @Override
    protected View onCreateContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout linearLayout = (LinearLayout) inflater.inflate(R.layout.gallery_preview, container, false);
        GalleryPreviewImg = linearLayout.findViewById(R.id.GalleryPreviewImg);
        path = getArguments().getString("path");
        Glide.with(GalleryPreview.this)
                .load(new File(path)) // Uri of the picture
                .into(GalleryPreviewImg);
        return null;
    }

    @Override
    protected void initView(View root, Bundle savedInstanceState) {

    }

    @Override
    protected void initData(Bundle savedInstanceState) {

    }
}
