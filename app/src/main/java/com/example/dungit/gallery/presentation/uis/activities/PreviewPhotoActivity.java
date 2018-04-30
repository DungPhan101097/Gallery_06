package com.example.dungit.gallery.presentation.uis.activities;

import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.transition.Explode;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.support.v7.widget.Toolbar;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.dungit.gallery.R;
import com.example.dungit.gallery.presentation.GlideApp;
import com.example.dungit.gallery.presentation.entities.Photo;
import com.example.dungit.gallery.presentation.uis.adapters.PhotoSlideAdapter;

import java.io.File;
import java.util.ArrayList;

import me.relex.circleindicator.CircleIndicator;

/**
 * Created by DUNGIT on 4/23/2018.
 */

public class PreviewPhotoActivity extends AppCompatActivity {
    public static final String IMG_URL_KEY = "img_url_key";
    public static final String IMG_POSITION = "img_postion_key";

    private static ViewPager mPager;
    private static int currentPage = 0;

    private static ArrayList<Photo> photos = null;
    private BottomNavigationView bNV = null;
    private Toolbar toolbar = null;

    AlertDialog dialog;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_photo_slide_test);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setWindowAnimations(Animation.ZORDER_TOP);

        Intent intent = getIntent();
        if (photos != null && intent != null) {
            currentPage = intent.getIntExtra(IMG_POSITION, 0);
            toolbar = findViewById(R.id.photo_toolbar_top);
            setSupportActionBar(toolbar);
            bNV = findViewById(R.id.bottom_navigation);

            mPager = findViewById(R.id.pager_photo);
            mPager.setAdapter(new PhotoSlideAdapter(PreviewPhotoActivity.this, photos));
            mPager.setCurrentItem(currentPage);

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

            final ViewPager viewPager = findViewById(R.id.pager_photo);
            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    final Photo photo = photos.get(position);
                    String name = photo.getFile().getName();
                    getSupportActionBar().setTitle(name);
                }

                @Override
                public void onPageSelected(int position) {

                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });


            bNV.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                    switch (item.getItemId()){
                        case R.id.photo_action_delete:{
                            dialog = new AlertDialog.Builder(PreviewPhotoActivity.this).create();
                            dialog.setTitle("Delete image?");
                            dialog.setMessage("You sure?");
                            dialog.setButton("Yes", new DialogInterface.OnClickListener(){
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    startDelete();
                                }
                            });
                            dialog.setButton2("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialog.dismiss();
                                }
                            });
                            dialog.show();
                            return true;
                        }
                        case R.id.photo_action_send:{
                            startShare();
                            return true;
                        }
                        case R.id.photo_action_edit:{

                            return true;
                        }
                        default:
                            return false;
                    }
                }
            });

        }
    }

    public BottomNavigationView getbNV() {
        return bNV;
    }

    public Toolbar getToolbar() {
        return toolbar;
    }

    public static void setPhotos(ArrayList<Photo> photos) {
        PreviewPhotoActivity.photos = photos;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void startShare(){
        Photo cur = photos.get(currentPage);
        File fileShare = cur.getFile();
        fileShare.setReadable(true, false);
        final Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(fileShare));
        intent.setType("image/jpg");
        startActivity(Intent.createChooser(intent, "Share image via"));
    }

    public void startDelete(){
        Photo cur = photos.get(currentPage);
        File fileDelete = cur.getFile();
        boolean bDelFile = fileDelete.delete();
        if (bDelFile){
            Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show();
            refreshGallery(fileDelete);
            return;
        }
        Toast.makeText(this, "Fail", Toast.LENGTH_SHORT).show();
    }
    public void refreshGallery(File file){
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(Uri.fromFile(file));
        sendBroadcast(intent);
    }
}
