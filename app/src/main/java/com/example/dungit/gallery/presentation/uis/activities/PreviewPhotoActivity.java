package com.example.dungit.gallery.presentation.uis.activities;

import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.app.AlertDialog;
import android.app.WallpaperManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.transition.Explode;
import android.util.DisplayMetrics;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.support.v7.widget.Toolbar;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.dungit.gallery.R;
import com.example.dungit.gallery.presentation.GlideApp;
import com.example.dungit.gallery.presentation.entities.Photo;
import com.example.dungit.gallery.presentation.uis.adapters.PhotoSlideAdapter;

import java.io.IOException;
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


            mPager = findViewById(R.id.pager_photo);
            mPager.setAdapter(new PhotoSlideAdapter(PreviewPhotoActivity.this, photos));
            mPager.setCurrentItem(currentPage);

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

            ViewPager viewPager = findViewById(R.id.pager_photo);

            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    final Photo photo = photos.get(position);
                    String name = photo.getFile().getName();
                    getSupportActionBar().setTitle(name);
                    currentPage = position;
                    if(photos.get(position).getDescriptImg()==null)
                        txtDes.setVisibility(View.INVISIBLE);
                    else
                        txtDes.setVisibility(View.VISIBLE);
                    txtDes.setText(photos.get(position).getDescriptImg());
                }

                @Override
                public void onPageSelected(int position) {

                }

                @Override
                public void onPageScrollStateChanged(int state) {

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

    public TextView getTextDescip(){return txtDes;}

    public static void setPhotos(ArrayList<Photo> photos) {
        PreviewPhotoActivity.photos = photos;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


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
                AlertDialog.Builder builder_1 = new AlertDialog.Builder(PreviewPhotoActivity.this);
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
                WallpaperManager wm = WallpaperManager.getInstance(PreviewPhotoActivity.this);
                try {
                    wm.setBitmap(decodedSampleBitmap);
                    Toast.makeText(PreviewPhotoActivity.this, "Đặt hình nền thành công", Toast.LENGTH_SHORT).show();
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
