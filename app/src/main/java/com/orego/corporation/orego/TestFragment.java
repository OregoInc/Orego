package com.orego.corporation.orego;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.MergeCursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
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
import com.orego.corporation.orego.adapter.DemoAdapter;
import com.orego.corporation.orego.base.BaseRestoreFragment;
import com.orego.corporation.orego.fragments.MainActivity;
import com.orego.corporation.orego.gallery.AlbumFragment;
import com.orego.corporation.orego.gallery.MapComparator;
import com.orego.corporation.orego.gallery.PermissionUtils;
import com.orego.corporation.orego.layout.impl.ScaleTransformer;
import com.orego.corporation.orego.managers.GalleryLayoutManager;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TestFragment extends BaseRestoreFragment {

    @BindView(R.id.recycle)
    RecyclerView mMainRecycle1;



    static final int REQUEST_PERMISSION_KEY = 1;

    GridView galleryGridView;
    LoadAlbum loadAlbumTask;
    private BottomSheetBehavior mBottomSheetBehavior;
    ArrayList<HashMap<String, String>> albumList = new ArrayList<HashMap<String, String>>();


    public static TestFragment newInstance() {

        Bundle args = new Bundle();

        TestFragment fragment = new TestFragment();
        fragment.setArguments(args);
        return fragment;
    }


    /**
     * Generate by live templates.
     * Use FragmentManager to find this Fragment's instance by tag
     */
    public static TestFragment findFragment(FragmentManager manager) {
        return (TestFragment) manager.findFragmentByTag(TestFragment.class.getSimpleName());
    }

    @Override
    protected View onCreateContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        CoordinatorLayout coordinatorLayout = (CoordinatorLayout) inflater.inflate(R.layout.fragment_test, container, false);
        final TextView textView = (TextView) coordinatorLayout.findViewById(R.id.text_view_state);
        ImageView buttonCollapse = (ImageView) coordinatorLayout.findViewById(R.id.btn_sheet_close);
        View bottomSheet = coordinatorLayout.findViewById(R.id.bottom_sheet);
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });
        buttonCollapse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });
        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        textView.setText("Collapsed");
                        bottomSheet.setNestedScrollingEnabled(true);
                        break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        textView.setText("Dragging...");
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        textView.setText("Expanded");
                        bottomSheet.setNestedScrollingEnabled(false);
                        break;
                    case BottomSheetBehavior.STATE_HIDDEN:
                        textView.setText("Hidden");
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        textView.setText("Settling...");
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                textView.setText("Sliding...");

            }
        });

        galleryGridView = (GridView) coordinatorLayout.findViewById(R.id.galleryGridView);
        String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        if (!PermissionUtils.hasPermissions(getActivity(), PERMISSIONS)) {
            ActivityCompat.requestPermissions(Objects.requireNonNull(getActivity()), PERMISSIONS, 1);
        } else {
            loadAlbumTask = new LoadAlbum();
            loadAlbumTask.execute();
        }
        return coordinatorLayout;
    }

    @Override
    protected void initView(View root, Bundle savedInstanceState) {
        int iDisplayWidth = getResources().getDisplayMetrics().widthPixels;
        Resources resources = getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = iDisplayWidth / (metrics.densityDpi / 160f);

        if (dp < 360) {
            dp = (dp - 17) / 2;
            float px = PermissionUtils.convertDpToPixel(dp, Objects.requireNonNull(getContext()));
            galleryGridView.setColumnWidth(Math.round(px));
        }
        ButterKnife.bind(this, root);
        final List<String> title = new ArrayList<String>();
        int size = 50;
        for (int i = 0; i < size; i++) {
            title.add("Hello" + i);
        }
        GalleryLayoutManager layoutManager1 = new GalleryLayoutManager(GalleryLayoutManager.HORIZONTAL);
        layoutManager1.attach(mMainRecycle1, 0);
        layoutManager1.setItemTransformer(new ScaleTransformer());
        DemoAdapter demoAdapter1 = new DemoAdapter(title) {
            @Override
            public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return super.onCreateViewHolder(parent, viewType);
            }
        };
        demoAdapter1.setOnItemClickListener(new DemoAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                mMainRecycle1.smoothScrollToPosition(position);
            }
        });
//        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.HORIZONTAL);
//        mMainRecycle1.addItemDecoration(dividerItemDecoration);
        mMainRecycle1.setNestedScrollingEnabled(false);
        mMainRecycle1.setAdapter(demoAdapter1);

    }




    @Override
    protected void initData(Bundle savedInstanceState) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    loadAlbumTask = new LoadAlbum();
                    loadAlbumTask.execute();
                } else {
                    Toast.makeText(getActivity().getBaseContext(), "You must accept permissions.", Toast.LENGTH_LONG).show();
                }
            }
        }

    }

    public class LoadAlbum extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            albumList.clear();
        }

        protected String doInBackground(String... args) {
            String xml = "";

            String path = null;
            String album = null;
            String timestamp = null;
            String countPhoto = null;
            Uri uriExternal = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            Uri uriInternal = MediaStore.Images.Media.INTERNAL_CONTENT_URI;


            String[] projection = {MediaStore.MediaColumns.DATA,
                    MediaStore.Images.Media.BUCKET_DISPLAY_NAME, MediaStore.MediaColumns.DATE_MODIFIED};
            Cursor cursorExternal = getActivity().getContentResolver().query(uriExternal, projection, "_data IS NOT NULL) GROUP BY (bucket_display_name",
                    null, null);
            Cursor cursorInternal =getActivity().getContentResolver().query(uriInternal, projection, "_data IS NOT NULL) GROUP BY (bucket_display_name",
                    null, null);
            Cursor cursor = new MergeCursor(new Cursor[]{cursorExternal, cursorInternal});

            while (cursor.moveToNext()) {

                path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA));
                album = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));
                timestamp = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_MODIFIED));
                countPhoto = PermissionUtils.getCount(getActivity().getApplicationContext(), album);

                albumList.add(PermissionUtils.mappingInbox(album, path, timestamp, PermissionUtils.converToTime(timestamp), countPhoto));
            }
            cursor.close();
            Collections.sort(albumList, new MapComparator(PermissionUtils.KEY_TIMESTAMP, "dsc")); // Arranging photo album by timestamp decending
            return xml;
        }

        @Override
        protected void onPostExecute(String xml) {
            AlbumAdapter adapter = new AlbumAdapter(getActivity(), albumList);
            galleryGridView.setAdapter(adapter);
            galleryGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view,
                                        final int position, long id) {
//                    Intent intent = new Intent(getActivity().getBaseContext(), AlbumFragment.class);
//                    intent.putExtra("name", albumList.get(+position).get(Function.KEY_ALBUM));
//                    startActivity(intent);
                    Bundle bundle = new Bundle();
                    bundle.putString("name", albumList.get(+position).get(PermissionUtils.KEY_ALBUM));
                    AlbumFragment albumFragment = new AlbumFragment();
                    albumFragment.setArguments(bundle);
                    ((MainActivity) getActivity()).replaceFragment(albumFragment);
                }
            });
        }
    }

    public class AlbumAdapter extends BaseAdapter {
        private Activity activity;
        private ArrayList<HashMap<String, String>> data;

        public AlbumAdapter(Activity a, ArrayList<HashMap<String, String>> d) {
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
            AlbumViewHolder holder = null;
            if (convertView == null) {
                holder = new AlbumViewHolder();
                convertView = LayoutInflater.from(activity).inflate(
                        R.layout.album_row, parent, false);

                holder.galleryImage = (ImageView) convertView.findViewById(R.id.galleryImage);
                holder.gallery_count = (TextView) convertView.findViewById(R.id.gallery_count);
                holder.gallery_title = (TextView) convertView.findViewById(R.id.gallery_title);

                convertView.setTag(holder);
            } else {
                holder = (AlbumViewHolder) convertView.getTag();
            }
            holder.galleryImage.setId(position);
            holder.gallery_count.setId(position);
            holder.gallery_title.setId(position);

            HashMap<String, String> song = new HashMap<String, String>();
            song = data.get(position);
            try {
                holder.gallery_title.setText(song.get(PermissionUtils.KEY_ALBUM));
                holder.gallery_count.setText(song.get(PermissionUtils.KEY_COUNT));

                Glide.with(activity)
                        .load(new File(song.get(PermissionUtils.KEY_PATH))) // Uri of the picture
                        .into(holder.galleryImage);


            } catch (Exception ignored) {
            }
            return convertView;
        }
    }


    class AlbumViewHolder {
        ImageView galleryImage;
        TextView gallery_count, gallery_title;
    }



}
