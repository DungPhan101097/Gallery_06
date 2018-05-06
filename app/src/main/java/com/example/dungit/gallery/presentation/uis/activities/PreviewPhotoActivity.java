package com.example.dungit.gallery.presentation.uis.activities;

import android.app.AlertDialog;
import android.app.WallpaperManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.support.v7.widget.Toolbar;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.Scroller;
import android.widget.ToggleButton;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import com.example.dungit.gallery.R;
import com.example.dungit.gallery.presentation.databasehelper.PhotoDatabaseHelper;
import com.example.dungit.gallery.presentation.databasehelper.updatedatadao.DBHelper;
import com.example.dungit.gallery.presentation.entities.Photo;
import com.example.dungit.gallery.presentation.uis.adapters.PhotoSlideAdapter;
import com.example.dungit.gallery.presentation.uis.animation.FixedSpeedScroller;
import com.example.dungit.gallery.presentation.uis.dialog.InputDialog;
import com.example.dungit.gallery.presentation.Utils.*;
import com.example.dungit.gallery.presentation.uis.fragments.PictureFragment;


/**
 * Created by DUNGIT on 4/23/2018.
 */

public class PreviewPhotoActivity extends AppCompatActivity {
    public static final String IMG_POSITION = "img_postion_key";

    private Handler handler = new Handler();
    private ViewPager mPager;
    private int currentPage = 0;

    private BottomNavigationView bNV = null;
    private Toolbar toolbar = null;

    private boolean isSlideRunning = false;
    private boolean isShowTB = true;
    private Scroller defaultScroller;
    private Scroller slideShowScoller;
    private PhotoDatabaseHelper dbHelper;

    private static List<Photo> photos = null;
    private static int slideShowDelay = 5000;
    private static ViewPager.PageTransformer slideAnimation = null;

    AlertDialog dialog;

    String temp = "";

    private ToggleButton toggleButton;


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

            dbHelper = new PhotoDatabaseHelper(this);
            defaultScroller = new Scroller(this);
            slideShowScoller = new FixedSpeedScroller(this);

            toolbar = findViewById(R.id.photo_toolbar_top);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);



            initPageViewer();
            initBottomMenu();

        }
    }

    private Runnable slideShow = new Runnable() {
        @Override
        public void run() {
            int pos = currentPage + 1;
            if (pos >= photos.size()) {
                pos = 0;
            }
            mPager.setCurrentItem(pos, true);
            handler.postDelayed(slideShow, slideShowDelay);
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.option_menu_previewphoto, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        final Photo photo = photos.get(currentPage);
        switch (id) {
            case R.id.act_Properties: {
                ImageUtils.showInfoDialog(this,photo);

            }
            break;

            case R.id.act_setAs: {
                DisplayMetrics displayMetrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                ImageUtils.setImageAsWallpaper(displayMetrics,photo,this);
            }
            break;

            case R.id.act_openWith: {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                intent.setDataAndType(Uri.parse("file://mnt" + photos.get(currentPage).getPathImg()), "image/*");
                intent.putExtra("mimeType", "image/*");
                this.startActivity(Intent.createChooser(intent, "Open With:"));
            }
            break;
            case R.id.act_setDesct: {
                String desc = photo.getDescriptImg() == null ? "" : photo.getDescriptImg();
                InputDialog inputDialog = new InputDialog(PreviewPhotoActivity.this, "Đặt chú thích ảnh"
                        , "Thay Đổi", "Hủy", desc) {
                    @Override
                    public void onPositiveButtonClick(AlertDialog inputDialog, String output) {
                        dbHelper.insertPhotoDescription(photo.getIdImg(),output);
                        photo.setDescriptImg(output);
                        inputDialog.cancel();
                    }
                };
                inputDialog.showDialog();
            }
            break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause() {
        super.onPause();
        stopSlideShow();
    }


    @Override
    public void onBackPressed() {
        if(isSlideRunning)
            stopSlideShow();
        else
            super.onBackPressed();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    //////
    private void initPageViewer(){
        mPager = findViewById(R.id.pager_photo);
        PhotoSlideAdapter adapter = new PhotoSlideAdapter(PreviewPhotoActivity.this, photos) {
            @Override
            public void onInnerViewClick(View v) {
                if (isSlideRunning) {
                    stopSlideShow();
                } else {
                    toogleShowTB();
                }
            }
        };


        mPager.setAdapter(adapter);
        mPager.setCurrentItem(currentPage);
        mPager.setPageTransformer(false, slideAnimation);
        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                final Photo photo = photos.get(position);
                String name = photo.getFile().getName();
                getSupportActionBar().setTitle(name);
                SetLove();
            }

            @Override
            public void onPageSelected(int position) {
                if(position >= 0 )
                    currentPage = position;
                Log.i("Curent Page:", String.valueOf(currentPage));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        mPager.setOffscreenPageLimit(2);
    }

    public void initBottomMenu(){
        bNV = findViewById(R.id.bottom_navigation);
        MenuUtils.removeShiftMode(bNV);


        toggleButton = (ToggleButton) findViewById(R.id.myToggleButton);
        toggleButton.setChecked(false);
        toggleButton.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.img_heart_grey));
        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    toggleButton.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.img_heart_red));
                    photos.get(currentPage).setLove(true);
                    File src = photos.get(currentPage).getFile();
                    SimpleDateFormat sd = new SimpleDateFormat("hhssddmmyy");
                    String stamp = sd.format(new Date());
                    File des = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Albums06/Love/",
                            "IMG-" + stamp + ".jpg");
                    temp = des.toString();
                    try {
                        copy(src,des);
                        Toast.makeText(PreviewPhotoActivity.this, "Đã thêm vào mục yêu thích", Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(PreviewPhotoActivity.this, "Fail", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    toggleButton.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.img_heart_grey));
                    photos.get(currentPage).setLove(false);
                    if (temp != ""){
                        File delLove = new File(temp);
                        boolean bDelFile = delLove.delete();
                        if (bDelFile){
                            refreshGallery(delLove);
                            //dbHelper.reshowImg();
                            Toast.makeText(PreviewPhotoActivity.this, "Đã xóa khỏi mục ưa thích", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        temp = "";
                    }
                }
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
//                        dbHelper.loadData();
//                        dbHelper.reshowImg();
                        //main.getDBHelper().reshowImg();
                        mPager.setCurrentItem(currentPage + 1);
                        return true;
                    }
                    case R.id.photo_action_send:{
                        startShare();
                        return true;
                    }

                    case R.id.photo_action_edit:{

                    }
                    case R.id.photo_action_slideshow:{
                        if(handler != null) {
                            if (!isSlideRunning) {
                                startSlideShow();
                            } else {
                                stopSlideShow();
                            }
                        }
                        return true;
                    }
                    default:
                        return false;
                }
            }
        });
    }

    private void startSlideShow() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        MenuUtils.setScroller(mPager, slideShowScoller, this);
        handler.postDelayed(slideShow, slideShowDelay);
        hideTB();
        isSlideRunning = true;
    }

    private void stopSlideShow() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        MenuUtils.setScroller(mPager, defaultScroller, this);
        handler.removeCallbacks(slideShow);
        showTB();
        isSlideRunning = false;
    }

    public void toogleShowTB() {
        if (toolbar != null && bNV != null) {
            if (isShowTB) {
                hideTB();
            } else {
                showTB();
            }
        }
    }

    public void showTB() {
        toolbar.animate().translationY(0).setInterpolator(new DecelerateInterpolator()).start();
        bNV.animate().translationY(0).setInterpolator(new DecelerateInterpolator()).start();
        isShowTB = true;

    }

    public void hideTB() {

        toolbar.animate().translationY(-toolbar.getBottom()).setInterpolator(new AccelerateInterpolator()).start();
        bNV.animate().translationY(bNV.getBottom()).setInterpolator(new AccelerateInterpolator()).start();
        isShowTB = false;
    }



    //STATICS
    public static void setPhotos(List<Photo> photos) {
        PreviewPhotoActivity.photos = photos;
    }

    public static int getSlideShowDelay() {
        return slideShowDelay;
    }

    public static void setSlideShowDelay(int slideShowDelay) {
        if (slideShowDelay < 0)
            return;
        PreviewPhotoActivity.slideShowDelay = slideShowDelay;
    }

    public static ViewPager.PageTransformer getSlideAnimation() {
        return slideAnimation;
    }

    public static void setSlideAnimation(ViewPager.PageTransformer slideAnimation) {
        PreviewPhotoActivity.slideAnimation = slideAnimation;
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
            String idStr = String.valueOf(cur.getIdImg());
            PreviewPhotoActivity.this.getContentResolver().delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    MediaStore.Images.ImageColumns.BUCKET_ID + " = ?",
                    new String[]{idStr});
           // dbHelper.reshowImg();

            return;
        }
        Toast.makeText(this, "Fail", Toast.LENGTH_SHORT).show();
    }
    public void refreshGallery(File file){
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(Uri.fromFile(file));
        sendBroadcast(intent);
    }

    public void copy(File src, File dst) throws IOException {
        FileInputStream inStream = new FileInputStream(src);
        FileOutputStream outStream = new FileOutputStream(dst);
        FileChannel inChannel = inStream.getChannel();
        FileChannel outChannel = outStream.getChannel();
        inChannel.transferTo(0, inChannel.size(), outChannel);
        inStream.close();
        outStream.close();
    }

    public void SetLove(){
        if (photos.get(currentPage).getLoveImg()){
            toggleButton.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.img_heart_red));
        }
        else {
            toggleButton.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.img_heart_grey));
        }
    }
}
