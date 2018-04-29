package com.example.dungit.gallery.presentation.uis.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.dungit.gallery.R;
import com.example.dungit.gallery.presentation.Utils.EmptyFolderFileFilter;
import com.example.dungit.gallery.presentation.databasehelper.AlbumDatabaseHelper;
import com.example.dungit.gallery.presentation.databasehelper.updatedatadao.DBHelper;
import com.example.dungit.gallery.presentation.entities.Album;
import com.example.dungit.gallery.presentation.entities.EMODE;
import com.example.dungit.gallery.presentation.entities.ListPhotoSameDate;
import com.example.dungit.gallery.presentation.entities.Photo;
import com.example.dungit.gallery.presentation.uis.adapters.MyViewPagerAdapter;
import com.example.dungit.gallery.presentation.uis.callbacks.MainCallback;
import com.example.dungit.gallery.presentation.uis.fragments.AlbumFragment;
import com.example.dungit.gallery.presentation.uis.fragments.PictureFragment;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

public class MainActivity extends AppCompatActivity implements MainCallback {

    private ArrayList<Fragment> lstFragment;
    private TabLayout tabLayout;
    private PictureFragment pictureFragment;
    private AlbumFragment albumFragment;
    private Toolbar toolbarTop;

    private boolean isCheckedChangeView;
    private static final int MY_PERMISSION_EXTERNAL_STORAGE = 1;

    private DBHelper dbHelper;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbHelper = new DBHelper(this);
        isCheckedChangeView = false;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                Toast.makeText(this, "Application have to need permission to access photo!",
                        Toast.LENGTH_SHORT).show();

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSION_EXTERNAL_STORAGE);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSION_EXTERNAL_STORAGE);
            }
        } else {
            // Doc du lieu.
            dbHelper.loadData();
            initViews();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case MY_PERMISSION_EXTERNAL_STORAGE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    dbHelper.loadData();
                    initViews();
                } else {
                    Toast.makeText(this, "Application don't access any data to show!",
                            Toast.LENGTH_SHORT).show();
                }
        }
    }

    private void initViews() {
        toolbarTop =findViewById(R.id.tb_top);
        setSupportActionBar(toolbarTop);

        lstFragment = new ArrayList<>();

        pictureFragment = PictureFragment.newInstance(dbHelper.getListPhotoByDate());
        albumFragment = AlbumFragment.newInstance(MainActivity.this, dbHelper.getListAlbum(),
                dbHelper.getListHiddenAlbums());
        lstFragment.add(pictureFragment);
        lstFragment.add(albumFragment);
        //lstFragment.add(PictureFragment.newInstance(dbHelper.getListPhotoByDate()));

        viewPager = findViewById(R.id.viewpager);
        tabLayout = findViewById(R.id.tab_layout);

        MyViewPagerAdapter myViewPagerAdapter = new MyViewPagerAdapter(getSupportFragmentManager());
        myViewPagerAdapter.setLstFragMent(lstFragment);
        viewPager.setAdapter(myViewPagerAdapter);

        tabLayout.setupWithViewPager(viewPager);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setTabsFromPagerAdapter(myViewPagerAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.act_change_view:
                isCheckedChangeView = !isCheckedChangeView;
                if (isCheckedChangeView) {
                    item.setIcon(getResources().getDrawable(R.drawable.btn_gallery_grid_mode));
                    pictureFragment.onChangeView(EMODE.MODE_GRID);
                } else {
                    item.setIcon(getResources().getDrawable(R.drawable.btn_gallery_detail_mode));
                    pictureFragment.onChangeView(EMODE.MODE_BY_DATE);

                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {


        if (viewPager.getCurrentItem() == 0) {
            super.onBackPressed();
        } else {
            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
        }
    }

    @Override
    public void onUpdateListPhotoWhenDelOrHideAlbum(Album... albums) {
//        ArrayList<Photo> listRemovedPhoto;
//        for (Album album : albums) {
//            listRemovedPhoto = album.getArraylistPhoto();
//            arrListPhoto.removeAll(listRemovedPhoto);
//        }
//        lstPhotoByDate = convertListPhoto2ListPhotoSameDate(arrListPhoto);
//
//        pictureFragment.onChangeDataView(lstPhotoByDate, arrListPhoto);
    }

    @Override
    public void onUpdateListPhotoWhenReshowAlbum(Album... albums) {
//        ArrayList<Photo> listAddedPhoto;
//        for (Album album : albums) {
//            listAddedPhoto = album.getArraylistPhoto();
//            arrListPhoto.addAll(listAddedPhoto);
//        }
//        lstPhotoByDate = convertListPhoto2ListPhotoSameDate(arrListPhoto);
//
//        pictureFragment.onChangeDataView(lstPhotoByDate, arrListPhoto);
    }

    public DBHelper getDBHelper() {
        return dbHelper;
    }

    public ArrayList<Photo> getArrListPhoto() {
        return arrListPhoto;
    }
}
