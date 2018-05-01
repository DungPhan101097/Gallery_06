package com.example.dungit.gallery.presentation.uis.activities;

import android.app.AlertDialog;
import android.app.WallpaperManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dungit.gallery.R;
import com.example.dungit.gallery.presentation.uis.animation.FadePageTransformer;
import com.example.dungit.gallery.presentation.entities.Photo;
import com.example.dungit.gallery.presentation.uis.adapters.PhotoSlideAdapter;

import java.io.IOException;
import java.util.ArrayList;

import static com.example.dungit.gallery.presentation.Utils.MenuUtils.removeShiftMode;

/**
 * Created by DUNGIT on 4/23/2018.
 */

public class PreviewPhotoActivity extends AppCompatActivity {
    public static final String IMG_URL_KEY = "img_url_key";
    public static final String IMG_POSITION = "img_postion_key";

    private ViewPager mPager;
    private static int currentPage = 0;

    private static ArrayList<Photo> photos = null;
    private BottomNavigationView bNV = null;
    private Toolbar toolbar = null;

    private static int slideShowDelay= 5000;
    private static boolean isSlideRunning = false;
    private static boolean isShowTB = true;

    private static ViewPager.PageTransformer slideAnimation = null;
    private static ViewPager.PageTransformer slideShowAnimation = new FadePageTransformer();

    String messDescript="";
    private TextView txtDes;

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
            txtDes = findViewById(R.id.txtDes);
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

            ViewPager viewPager = findViewById(R.id.pager_photo);
            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    final Photo photo = photos.get(position);
                    String name = photo.getFile().getName();
                    getSupportActionBar().setTitle(name);
                    if(photos.get(currentPage).getDescriptImg()==null)
                        txtDes.setVisibility(View.INVISIBLE);
                    else
                        txtDes.setVisibility(View.VISIBLE);
                    txtDes.setText(photos.get(currentPage).getDescriptImg());
                    currentPage = position;
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
        mPager.setPageTransformer(false
                , slideShowAnimation);
        handler.postDelayed(slideShow,slideShowDelay);
        hideTB();
        isSlideRunning = true;
    }

    private void stopSlideShow(){
        mPager.setPageTransformer(false
                , slideAnimation);
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
        if (handler!= null) {
            handler.removeCallbacks(slideShow);
            isSlideRunning = false;
        }
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
            txtDes.animate().translationY(0).setInterpolator(new DecelerateInterpolator()).start();
            isShowTB = true;

    }

    public void hideTB(){

            toolbar.animate().translationY(-toolbar.getBottom()).setInterpolator(new AccelerateInterpolator()).start();
            bNV.animate().translationY(bNV.getBottom()).setInterpolator(new AccelerateInterpolator()).start();
            txtDes.animate().translationY(bNV.getBottom()).setInterpolator(new AccelerateInterpolator()).start();
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



    ////

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.option_menu_previewphoto, menu);
        return  super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.act_Properties:
            {
                if(currentPage <0)
                    currentPage = 0;
                AlertDialog.Builder builder_1 = new AlertDialog.Builder(this);
                builder_1.setTitle("Thông Tin Ảnh");
                builder_1.setMessage("Tên ảnh : "+ photos.get(currentPage).getNameImg()+
                        "\n\nĐường dẫn : "+photos.get(currentPage).getPathImg()+
                        "\n\nKích cỡ : "+ photos.get(currentPage).getSizeImg()+
                        "\n\nKích thước : "+photos.get(currentPage).getResoluImg()+
                        "\n\nNgày tạo : "+photos.get(currentPage).getDateTaken()+
                        "\n\nChú thích : "+photos.get(currentPage).getDescriptImg());
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

            case R.id.act_setAs:
            {
                if(currentPage <0)
                    currentPage = 0;
                DisplayMetrics displayMetrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                int height = displayMetrics.heightPixels;
                int width = displayMetrics.widthPixels << 1;
                final BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(photos.get(currentPage).getPathImg(), options);
                options.inSampleSize = calculateInSampleSize(options, width, height);
                options.inJustDecodeBounds = false;
                Bitmap decodedSampleBitmap = BitmapFactory.decodeFile(photos.get(currentPage).getPathImg(), options);
                WallpaperManager wm = WallpaperManager.getInstance(this);
                try {
                    wm.setBitmap(decodedSampleBitmap);
                    Toast.makeText(this, "Đặt hình nền thành công", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }break;

            case R.id.act_openWith:
            {
                if(currentPage <0)
                    currentPage = 0;
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                intent.setDataAndType(Uri.parse("file://mnt"+photos.get(currentPage).getPathImg()),"image/*");
                intent.putExtra("mimeType", "image/*");
                this.startActivity(Intent.createChooser(intent, "Open With:"));
            }break;


            case R.id.act_setDesct:
            {
                if(currentPage <0)
                    currentPage = 0;

                final EditText edittext = new EditText(this);
                AlertDialog.Builder alert=new AlertDialog.Builder(this);;
                alert.setMessage("Nhập chú thích");
                alert.setTitle("Đặt chú thích ảnh");
                alert.setView(edittext);
                alert.setPositiveButton("Thay đổi", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        messDescript = edittext.getText().toString();
                        Uri Image_URL = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                        ContentResolver contentResolver = getContentResolver();
                        ContentValues values = new ContentValues(1);
                        values.put(MediaStore.Images.Media.DESCRIPTION,messDescript);
                        contentResolver.update(Image_URL,values,"_id=" + photos.get(currentPage).getIdImg(),null);
                        photos.get(currentPage).setDescriptImg(messDescript);
                        txtDes.setText(messDescript);
                        txtDes.setVisibility(View.VISIBLE);
                        Toast.makeText(PreviewPhotoActivity.this, "Thay đổi thành công", Toast.LENGTH_SHORT).show();
                    }
                });
                alert.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                });
                alert.show();

            }break;

        }
        return super.onOptionsItemSelected(item);
    }


    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }
}
