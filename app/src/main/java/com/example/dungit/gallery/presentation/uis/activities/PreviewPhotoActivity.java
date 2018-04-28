package com.example.dungit.gallery.presentation.uis.activities;

import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.transition.Explode;
import android.util.Log;
import android.view.Menu;
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
    private PhotoSlideAdapter adpPhotoSlide;

    private static ArrayList<Photo> photos = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_photo_slide_test);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setWindowAnimations(Animation.ZORDER_TOP);

        Intent intent = getIntent();
        if (photos != null && intent != null) {
            currentPage= intent.getIntExtra(IMG_POSITION,0);
            final Toolbar toolbar = findViewById(R.id.photo_toolbar_top);
            setSupportActionBar(toolbar);
            final BottomNavigationView bottomNavigationView= findViewById(R.id.bottom_navigation);


            mPager = findViewById(R.id.pager_photo);
            adpPhotoSlide = new PhotoSlideAdapter(PreviewPhotoActivity.this,photos,toolbar,bottomNavigationView);
            mPager.setAdapter(adpPhotoSlide);
            mPager.setCurrentItem(currentPage);

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_photo, menu);
        return  super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.act_Properties:
            {
                AlertDialog.Builder builder_1 = new AlertDialog.Builder(this);
                builder_1.setTitle("Thông Tin Ảnh");
                builder_1.setMessage("Name : "+ photos.get(currentPage).getNameImg()+
                        "\n\nPath : "+photos.get(currentPage).getPathImg()+
                        "\n\nSize : "+ photos.get(currentPage).getSizeImg()+
                        "\n\nResolution : "+photos.get(currentPage).getResoluImg()+
                        "\n\nDate taken : "+photos.get(currentPage).getDateTaken());
                builder_1.setCancelable(false);
                builder_1.setNegativeButton("Đóng", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();

                    }
                });
                AlertDialog alertDialog = builder_1.create();
                alertDialog.show();
            }break;

            case R.id.act_setas:
            {
                Intent intent = new Intent(Intent.ACTION_ATTACH_DATA);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                intent.setDataAndType(Uri.parse("file://mnt"+photos.get(currentPage).getPathImg()),"image/*");
                intent.putExtra("mimeType", "image/*");
                this.startActivity(Intent.createChooser(intent, "Set as:"));
            }break;

            case R.id.act_openwith:
            {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                intent.setDataAndType(Uri.parse("file://mnt"+photos.get(currentPage).getPathImg()),"image/*");
                intent.putExtra("mimeType", "image/*");
                this.startActivity(Intent.createChooser(intent, "Open With:"));
            }break;

        }
        return super.onOptionsItemSelected(item);
    }

    public static void setPhotos(ArrayList<Photo> photos) {
        PreviewPhotoActivity.photos = photos;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
