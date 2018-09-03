package com.orego.corporation.orego.fragments.otherActivities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.orego.corporation.orego.R;
import com.orego.corporation.orego.fragments.cameraFragment.CameraFrag;
import com.orego.corporation.orego.fragments.galleryFragment.GalleryFragment;
import com.orego.corporation.orego.fragments.otherActivities.camera.PermissionsDelegate;
import com.orego.corporation.orego.views.adapters.FragmentAdapter;

import java.util.ArrayList;
import java.util.List;

public class OldMainActivity extends AppCompatActivity {
    public static OldMainActivity THIS;

    private DrawerLayout drawer;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private RelativeLayout relative_main;
    private ImageView img_page_start;

    private final PermissionsDelegate permissionsDelegate = new PermissionsDelegate(this);
    private boolean hasCameraPermission;

    private static boolean isShowPageStart = true;
    private final int MESSAGE_SHOW_DRAWER_LAYOUT = 0x001;
    private final int MESSAGE_SHOW_START_PAGE = 0x002;

    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_SHOW_DRAWER_LAYOUT:
                    drawer.openDrawer(GravityCompat.START);
                    SharedPreferences sharedPreferences = getSharedPreferences("app", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("isFirst", false);
                    editor.apply();
                    break;

                case MESSAGE_SHOW_START_PAGE:
                    AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
                    alphaAnimation.setDuration(300);
                    alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            relative_main.setVisibility(View.GONE);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                    relative_main.startAnimation(alphaAnimation);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        THIS = this;
        setContentView(R.layout.activity_main_old);
        hasCameraPermission = permissionsDelegate.hasCameraPermission();
        if (hasCameraPermission) {
//            cameraView.setVisibility(View.VISIBLE);
//            TODO:
        } else {
            permissionsDelegate.requestCameraPermission();

        }
        initView();
        initViewPager();
    }

    private void initView() {

    }

    private void initViewPager() {
        List<String> titles = new ArrayList<>();
        titles.add(getString(R.string.tab_title_main_1));
        titles.add(getString(R.string.tab_title_main_2));
        mTabLayout.addTab(mTabLayout.newTab().setText(titles.get(0)));
        mTabLayout.addTab(mTabLayout.newTab().setText(titles.get(1)));

        List<Fragment> fragments = new ArrayList<>();
        CameraFrag cameraFragment = new CameraFrag();
//        fragments.add(cameraFragment);
//        cameraFragment.setParent(this);
        fragments.add(new GalleryFragment());
        mViewPager.setOffscreenPageLimit(1);

        FragmentAdapter mFragmentAdapter = new FragmentAdapter(getSupportFragmentManager(), fragments, titles);
        mViewPager.setAdapter(mFragmentAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setTabsFromPagerAdapter(mFragmentAdapter);

        mViewPager.addOnPageChangeListener(pageChangeListener);
    }

    private ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        mHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (permissionsDelegate.resultGrantedCamera(requestCode, permissions, grantResults)) {
            hasCameraPermission = true;
//            fotoapparat.start();
//            cameraView.setVisibility(View.VISIBLE);
//            TODO:
        }

    }

}
