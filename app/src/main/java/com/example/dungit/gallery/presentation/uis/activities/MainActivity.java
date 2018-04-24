package com.example.dungit.gallery.presentation.uis.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import android.view.MenuItem;
import android.widget.Toast;

import com.example.dungit.gallery.R;
import com.example.dungit.gallery.presentation.entities.EMODE;
import com.example.dungit.gallery.presentation.entities.ListPhotoSameDate;
import com.example.dungit.gallery.presentation.entities.Photo;
import com.example.dungit.gallery.presentation.uis.adapters.MyViewPagerAdapter;
import com.example.dungit.gallery.presentation.uis.fragments.PictureFragment;

import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private static final String KEY_PICTURE_BY_DATE = "PICTURE_BY_DATE";
    private static final String KEY_PICTURE_GRID = "PICTURE_GRID";
    private static final String KEY_ALBUM = "ALBUM";
    private static final String KEY_STORY = "STORY";

    private ArrayList<Fragment> lstFragment;
    private TabLayout tabLayout;
    private PictureFragment pictureFragment;
    private Toolbar toolbarTop;
    private FragmentTransaction ft;

    private boolean isCheckedChangeView;
    private static final int MY_PERMISSION_EXTERNAL_STORAGE = 1;
    private ArrayList<ListPhotoSameDate> lstPhotoByDate = new ArrayList<>();
    private ArrayList<Photo> arrListPhoto = new ArrayList<>();


    private static final Uri EXTERNAL_URI = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    private static final String ID = MediaStore.Images.ImageColumns._ID;
    private static final String DATE_TAKEN = MediaStore.Images.ImageColumns.DATE_TAKEN;
    private static final String BUCKET_ID = MediaStore.Images.Media.BUCKET_ID;
    private static final String BUCKET_NAME = MediaStore.Images.Media.BUCKET_DISPLAY_NAME;
    private static final String[] IMAGE_PROJECTION_ALBUM =
            new String[]{
                    ID, DATE_TAKEN, BUCKET_NAME, BUCKET_ID
            };
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
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
            fn_imagespath();
            initViews();
        }
    }

    private boolean checkDate(ArrayList<ListPhotoSameDate> lstPhoto, String date) {
        for (ListPhotoSameDate lstPhotoItem : lstPhoto) {
            if (lstPhotoItem.getDate().equals(date)) {
                return true;
            }
        }
        return false;
    }

    public void fn_imagespath() {
        lstPhotoByDate.clear();

        // Cursor for query images.
        Cursor imgCursor = null;
        String SORT_ORDER = " DESC";

        imgCursor = this.getContentResolver().query(EXTERNAL_URI,
                IMAGE_PROJECTION_ALBUM, null, null, DATE_TAKEN + SORT_ORDER);

        final int idIndex = imgCursor.getColumnIndex(ID);
        final int dateIndex = imgCursor.getColumnIndex(DATE_TAKEN);
        final int albumNameIndex = imgCursor.getColumnIndex(BUCKET_NAME);
        final int albumIdIndex = imgCursor.getColumnIndex(BUCKET_ID);

        ListPhotoSameDate lstPhoto = null;
        String date = null;

        while (imgCursor.moveToNext()) {
            final long id = imgCursor.getLong(idIndex);
            final long dateTaken = imgCursor.getLong(dateIndex);
            final String albumName = imgCursor.getString(albumNameIndex);
            final long albumId = imgCursor.getLong(albumIdIndex);

            date = new Date(dateTaken).toString();
            date = date.substring(date.indexOf(" ") + 1, date.indexOf(" ") + 7) + " "
                    + date.substring(date.lastIndexOf(" ") + 1);

            Photo curPhoto = new Photo(id, date, albumId, albumName);
            arrListPhoto.add(curPhoto);

            if (lstPhoto == null) {
                lstPhoto = new ListPhotoSameDate(date);
                lstPhoto.addPhoto(curPhoto);
                lstPhotoByDate.add(lstPhoto);
            } else {
                if (checkDate(lstPhotoByDate, date)) {
                    lstPhoto.addPhoto(curPhoto);
                } else {
                    lstPhoto = new ListPhotoSameDate(date);
                    lstPhoto.addPhoto(curPhoto);
                    lstPhotoByDate.add(lstPhoto);
                }
            }

        }
        imgCursor.close();


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case MY_PERMISSION_EXTERNAL_STORAGE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    fn_imagespath();
                    initViews();
                } else {
                    Toast.makeText(this, "Application don't access any data to show!",
                            Toast.LENGTH_SHORT).show();
                }
        }
    }

    private void initViews() {

        toolbarTop = findViewById(R.id.tb_top);
        toolbarTop.inflateMenu(R.menu.main);

        lstFragment = new ArrayList<>();

        pictureFragment = PictureFragment.newInstance(lstPhotoByDate);
        lstFragment.add(pictureFragment);
        //lstFragment.add(pictureGridFragment);
        lstFragment.add(PictureFragment.newInstance(lstPhotoByDate));
        lstFragment.add(PictureFragment.newInstance(lstPhotoByDate));

        viewPager = findViewById(R.id.viewpager);
        tabLayout = findViewById(R.id.tab_layout);

        MyViewPagerAdapter myViewPagerAdapter = new MyViewPagerAdapter(getSupportFragmentManager());
        myViewPagerAdapter.setLstFragMent(lstFragment);
        viewPager.setAdapter(myViewPagerAdapter);

        tabLayout.setupWithViewPager(viewPager);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setTabsFromPagerAdapter(myViewPagerAdapter);

        toolbarTop.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                switch (id) {
                    case R.id.act_change_view:
                        isCheckedChangeView = !isCheckedChangeView;
                       // ft = getFragmentManager().beginTransaction().repl
                        if(isCheckedChangeView) {
                            item.setIcon(getResources().getDrawable(R.drawable.btn_gallery_grid_mode));
                            pictureFragment.onChangeView(EMODE.MODE_GRID);
                        }
                        else{
                            item.setIcon(getResources().getDrawable(R.drawable.btn_gallery_detail_mode));
                            pictureFragment.onChangeView(EMODE.MODE_BY_DATE);

                        }
                        break;
                    case R.id.act_settings:
                        break;
                    case R.id.act_about:
                        break;
                }
                return true;
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (viewPager.getCurrentItem() == 0) {
            super.onBackPressed();
        } else {
            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
        }
    }

}
