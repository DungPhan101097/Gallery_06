package com.example.dungit.gallery.presentation.uis.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.support.v7.widget.Toolbar;
import android.widget.Scroller;

import com.example.dungit.gallery.R;
import com.example.dungit.gallery.presentation.entities.Photo;
import com.example.dungit.gallery.presentation.uis.adapters.PhotoSlideAdapter;
import com.example.dungit.gallery.presentation.uis.animation.FixedSpeedScroller;

import java.lang.reflect.Field;
import java.util.ArrayList;

import static com.example.dungit.gallery.presentation.Utils.MenuUtils.removeShiftMode;
import static com.example.dungit.gallery.presentation.Utils.MenuUtils.setScroller;

/**
 * Created by DUNGIT on 4/23/2018.
 */

public class PreviewPhotoActivity extends AppCompatActivity {
    public static final String IMG_URL_KEY = "img_url_key";
    public static final String IMG_POSITION = "img_postion_key";

    private ViewPager mPager;
    private int currentPage = 0;

    private BottomNavigationView bNV = null;
    private Toolbar toolbar = null;

    private boolean isSlideRunning = false;
    private boolean isShowTB = true;
    private Scroller defaultScroller;
    private Scroller slideShowScoller;

    private static ArrayList<Photo> photos = null;
    private static int slideShowDelay= 5000;
    private static ViewPager.PageTransformer slideAnimation = null;
    //private static ViewPager.PageTransformer slideShowAnimation = new FadePageTransformer();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_photo_slide);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setWindowAnimations(Animation.ZORDER_TOP);

        Intent intent = getIntent();
        if (photos != null && intent != null) {
            currentPage = intent.getIntExtra(IMG_POSITION, 0);

            defaultScroller = new Scroller(this);
            slideShowScoller =new FixedSpeedScroller(this);

            toolbar = findViewById(R.id.photo_toolbar_top);
            setSupportActionBar(toolbar);
            bNV = findViewById(R.id.bottom_navigation);
            removeShiftMode(bNV);


            mPager = findViewById(R.id.pager_photo);
            PhotoSlideAdapter adapter = new PhotoSlideAdapter(PreviewPhotoActivity.this, photos){
                @Override
                public void onInnerViewClick(View v) {
                    if(isSlideRunning){
                        stopSlideShow();
                    }else{
                        toogleShowTB();
                    }
                }
            };
            mPager.setAdapter(adapter);
            mPager.setCurrentItem(currentPage);
            mPager.setPageTransformer(false,slideAnimation);

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

            mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    final Photo photo = photos.get(position);
                    String name = photo.getFile().getName();
                    getSupportActionBar().setTitle(name);
                }

                @Override
                public void onPageSelected(int position) {
                    currentPage = position;
                    Log.i("Curent Page:",String.valueOf(currentPage));
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });

            bNV.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    int id = item.getItemId();
                    if(id == R.id.photo_action_slideshow){
                        if(handler != null) {
                            if (!isSlideRunning) {
                                startSlideShow();
                            } else {
                                stopSlideShow();
                            }
                        }
                    }
                    return false;
                }
            });


        }
    }

    private void startSlideShow(){
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setScroller(mPager,slideShowScoller,this);
        handler.postDelayed(slideShow,slideShowDelay);
        hideTB();
        isSlideRunning = true;
    }

    private void stopSlideShow(){
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setScroller(mPager,defaultScroller,this);
        handler.removeCallbacks(slideShow);
        showTB();
        isSlideRunning = false;
    }

    public static void setPhotos(ArrayList<Photo> photos) {
        PreviewPhotoActivity.photos = photos;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private Handler handler =new Handler();

    private Runnable slideShow = new Runnable() {
        @Override
        public void run() {
            int pos = currentPage+1;
            if(pos >= photos.size()){
                pos = 0;
            }
            mPager.setCurrentItem(pos, true);
            handler.postDelayed(slideShow, slideShowDelay);
        }
    };

    @Override
    public void onPause() {
        super.onPause();
        stopSlideShow();
    }

    public void toogleShowTB(){
        if (toolbar != null && bNV != null) {
            if (isShowTB) {
                hideTB();
            } else {
                showTB();
            }
        }
    }

    public void showTB(){
            toolbar.animate().translationY(0).setInterpolator(new DecelerateInterpolator()).start();
            bNV.animate().translationY(0).setInterpolator(new DecelerateInterpolator()).start();
            isShowTB = true;

    }

    public void hideTB(){

            toolbar.animate().translationY(-toolbar.getBottom()).setInterpolator(new AccelerateInterpolator()).start();
            bNV.animate().translationY(bNV.getBottom()).setInterpolator(new AccelerateInterpolator()).start();
            isShowTB = false;
    }

    public static int getSlideShowDelay() {
        return slideShowDelay;
    }

    public static void setSlideShowDelay(int slideShowDelay) {
        if(slideShowDelay < 0)
            return;
        PreviewPhotoActivity.slideShowDelay = slideShowDelay;
    }

    public static ViewPager.PageTransformer getSlideAnimation() {
        return slideAnimation;
    }

    public static void setSlideAnimation(ViewPager.PageTransformer slideAnimation) {
        PreviewPhotoActivity.slideAnimation = slideAnimation;
    }


}
