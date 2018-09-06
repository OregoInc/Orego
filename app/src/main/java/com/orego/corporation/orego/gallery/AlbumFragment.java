package com.orego.corporation.orego.gallery;

import android.app.Activity;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.MergeCursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.orego.corporation.orego.R;
import com.orego.corporation.orego.base.BaseRestoreFragment;
import com.orego.corporation.orego.fragments.cameraFragment.CameraFragment;
import com.orego.corporation.orego.utils.ClientMultipartFormPost;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;


public class AlbumFragment extends BaseRestoreFragment {
    GridView galleryGridView;
    ArrayList<HashMap<String, String>> imageList = new ArrayList<HashMap<String, String>>();
    String album_name = "";
    LoadAlbumImages loadAlbumTask;

    @Override
    protected View onCreateContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout linearLayout = (LinearLayout) inflater.inflate(R.layout.activity_album, container, false);
        galleryGridView = linearLayout.findViewById(R.id.galleryGridView);
        return linearLayout;
    }

    @Override
    protected void initView(View root, Bundle savedInstanceState) {

        album_name = getArguments().getString("name");
        getActivity().setTitle(album_name);

        int iDisplayWidth = getResources().getDisplayMetrics().widthPixels;
        Resources resources = getActivity().getApplicationContext().getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = iDisplayWidth / (metrics.densityDpi / 160f);

        if (dp < 360) {
            dp = (dp - 17) / 2;
            float px = PermissionUtils.convertDpToPixel(dp, getActivity().getApplicationContext());
            galleryGridView.setColumnWidth(Math.round(px));
        }


        loadAlbumTask = new LoadAlbumImages();
        loadAlbumTask.execute();
    }

    @Override
    protected void initData(Bundle savedInstanceState) {

    }

    class LoadAlbumImages extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            imageList.clear();
        }

        protected String doInBackground(String... args) {
            String xml = "";

            String path = null;
            String album = null;
            String timestamp = null;
            Uri uriExternal = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            Uri uriInternal = MediaStore.Images.Media.INTERNAL_CONTENT_URI;

            String[] projection = {MediaStore.MediaColumns.DATA,
                    MediaStore.Images.Media.BUCKET_DISPLAY_NAME, MediaStore.MediaColumns.DATE_MODIFIED};

            Cursor cursorExternal = getActivity().getContentResolver().query(uriExternal, projection, "bucket_display_name = \"" + album_name + "\"", null, null);
            Cursor cursorInternal = getActivity().getContentResolver().query(uriInternal, projection, "bucket_display_name = \"" + album_name + "\"", null, null);
            Cursor cursor = new MergeCursor(new Cursor[]{cursorExternal, cursorInternal});
            while (cursor.moveToNext()) {

                path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA));
                album = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));
                timestamp = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_MODIFIED));

                imageList.add(PermissionUtils.mappingInbox(album, path, timestamp, PermissionUtils.converToTime(timestamp), null));
            }
            cursor.close();
            Collections.sort(imageList, new MapComparator(PermissionUtils.KEY_TIMESTAMP, "dsc")); // Arranging photo album by timestamp decending
            return xml;
        }

        @Override
        protected void onPostExecute(String xml) {

            SingleAlbumAdapter adapter = new SingleAlbumAdapter(getActivity(), imageList);
            galleryGridView.setAdapter(adapter);

            galleryGridView.setOnItemClickListener((parent, view, position, id) -> {
                ImageView imageView = getActivity().findViewById((int) id);//TODO если кто-то знает как вытащить оригин данного изображения будет лучше!
                Bitmap image = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                String path = CameraFragment.Companion.initPath(getActivity());
                ClientMultipartFormPost.sendPictureAndReplace(image, path, getActivity());
            });
        }
    }
}


class SingleAlbumAdapter extends BaseAdapter {
    private Activity activity;
    private ArrayList<HashMap<String, String>> data;

    public SingleAlbumAdapter(Activity a, ArrayList<HashMap<String, String>> d) {
        activity = a;
        data = d;
    }

    public int getCount() {
        return data.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        SingleAlbumViewHolder holder;
        if (convertView == null) {
            holder = new SingleAlbumViewHolder();
            convertView = LayoutInflater.from(activity).inflate(
                    R.layout.single_album_row, parent, false);

            holder.galleryImage = convertView.findViewById(R.id.galleryImage);

            convertView.setTag(holder);
        } else {
            holder = (SingleAlbumViewHolder) convertView.getTag();
        }
        holder.galleryImage.setId(position);

        HashMap<String, String> song;
        song = data.get(position);
        try {

            Glide.with(activity)
                    .load(new File(song.get(PermissionUtils.KEY_PATH))) // Uri of the picture
                    .into(holder.galleryImage);


        } catch (Exception ignored) {
        }
        return convertView;
    }
}


class SingleAlbumViewHolder {
    ImageView galleryImage;
}
