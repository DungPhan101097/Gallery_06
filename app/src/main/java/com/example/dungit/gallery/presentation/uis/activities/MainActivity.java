package com.example.dungit.gallery.presentation.uis.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;
import com.example.dungit.gallery.R;
import com.example.dungit.gallery.presentation.databasehelper.updatedatadao.DBHelper;
import com.example.dungit.gallery.presentation.uis.adapters.MyViewPagerAdapter;
import com.example.dungit.gallery.presentation.uis.fragments.AlbumFragment;
import com.example.dungit.gallery.presentation.uis.fragments.PictureFragment;
import java.util.ArrayList;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements MainCallback {

public class MainActivity extends AppCompatActivity {
    private static final int MY_PERMISSION_EXTERNAL_STORAGE = 1;
    private ArrayList<Fragment> lstFragment = new ArrayList<>();
    private DBHelper dbHelper;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbHelper = new DBHelper(this);

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
        Toolbar toolbarTop = findViewById(R.id.tb_top);
        setSupportActionBar(toolbarTop);

        PictureFragment pictureFragment = PictureFragment.newInstance(dbHelper.getListPhotoByDate());
        AlbumFragment albumFragment = AlbumFragment.newInstance(MainActivity.this, dbHelper.getListAlbum(),
                dbHelper.getListHiddenAlbums());
        lstFragment.add(pictureFragment);
        lstFragment.add(albumFragment);

        viewPager = findViewById(R.id.viewpager);
        TabLayout tabLayout = findViewById(R.id.tab_layout);

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
            case R.id.action_settings:
                Intent intent = new Intent(MainActivity.this,SettingActivity.class);
                this.startActivity(intent);
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
    public DBHelper getDBHelper() {
        return dbHelper;
    }
}
