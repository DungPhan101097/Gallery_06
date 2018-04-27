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
    private FragmentTransaction ft;

    private boolean isCheckedChangeView;
    private static final int MY_PERMISSION_EXTERNAL_STORAGE = 1;
    private ArrayList<ListPhotoSameDate> lstPhotoByDate = new ArrayList<>();
    private ArrayList<Photo> arrListPhoto = new ArrayList<>();

    private LinkedList<Album> listAlbum = new LinkedList<>();
    private LinkedList<Album> listHiddenAlbum = new LinkedList<>();
    private static final String USER_ALBUM_FLODER
            = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Albums06/";


    private static final Uri EXTERNAL_URI = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    private static final String ID = MediaStore.Images.ImageColumns._ID;
    private static final String DATE_TAKEN = MediaStore.Images.ImageColumns.DATE_TAKEN;
    private static final String BUCKET_ID = MediaStore.Images.Media.BUCKET_ID;
    private static final String BUCKET_NAME = MediaStore.Images.Media.BUCKET_DISPLAY_NAME;

    private static final String DATA = MediaStore.Images.Media.DATA;
    private static final String[] IMAGE_PROJECTION_ALBUM =
            new String[]{
                    ID, DATE_TAKEN, BUCKET_NAME, BUCKET_ID, DATA
            };
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
 //       getSupportActionBar().hide();
//        toolbarTop = findViewById(R.id.tb_top);
//        setSupportActionBar(toolbarTop);
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

    private ListPhotoSameDate checkDate(ArrayList<ListPhotoSameDate> lstPhoto, String date) {
        for (ListPhotoSameDate lstPhotoItem : lstPhoto) {
            if (lstPhotoItem.getDate().equals(date)) {
                return lstPhotoItem;
            }
        }
        return null;
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
        final int dataIndex = imgCursor.getColumnIndex(DATA);

        ListPhotoSameDate lstPhoto = null;
        String date = null;


        HashMap<Long, LinkedList<Photo>> albumMap = new HashMap<>();

        while (imgCursor.moveToNext()) {
            final long id = imgCursor.getLong(idIndex);
            final long dateTaken = imgCursor.getLong(dateIndex);
            final String albumName = imgCursor.getString(albumNameIndex);
            final long albumId = imgCursor.getLong(albumIdIndex);

            final String filePath = imgCursor.getString(dataIndex);

            date = new Date(dateTaken).toString();
            date = date.substring(date.indexOf(" ") + 1, date.indexOf(" ") + 7) + " "
                    + date.substring(date.lastIndexOf(" ") + 1);

            Photo curPhoto = new Photo(id, date, albumId, albumName, new File(filePath));
            arrListPhoto.add(curPhoto);

            if (lstPhoto == null) {
                lstPhoto = new ListPhotoSameDate(date);
                lstPhoto.addPhoto(curPhoto);
                lstPhotoByDate.add(lstPhoto);
            } else {
                if (checkDate(lstPhotoByDate, date) != null) {
                    lstPhoto.addPhoto(curPhoto);
                } else {
                    lstPhoto = new ListPhotoSameDate(date);
                    lstPhoto.addPhoto(curPhoto);
                    lstPhotoByDate.add(lstPhoto);
                }
            }
            if (albumMap.containsKey(albumId)) {
                albumMap.get(albumId).add(curPhoto);
            } else {
                LinkedList<Photo> photos = new LinkedList<>();
                photos.add(curPhoto);
                albumMap.put(albumId, photos);
            }

        }
        imgCursor.close();
        loadAlbums(albumMap);
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
        toolbarTop =findViewById(R.id.tb_top);
        setSupportActionBar(toolbarTop);

        lstFragment = new ArrayList<>();

        pictureFragment = PictureFragment.newInstance(lstPhotoByDate);
        albumFragment = AlbumFragment.newInstance(MainActivity.this, listAlbum, listHiddenAlbum);
        lstFragment.add(pictureFragment);
        lstFragment.add(albumFragment);
        lstFragment.add(PictureFragment.newInstance(lstPhotoByDate));

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
                // ft = getFragmentManager().beginTransaction().repl
                if (isCheckedChangeView) {
                    item.setIcon(getResources().getDrawable(R.drawable.btn_gallery_grid_mode));
                    pictureFragment.onChangeView(EMODE.MODE_GRID);
                } else {
                    item.setIcon(getResources().getDrawable(R.drawable.btn_gallery_detail_mode));
                    pictureFragment.onChangeView(EMODE.MODE_BY_DATE);

                }
                break;
            case R.id.act_settings:
                break;
            case R.id.act_about:
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


    void loadAlbums(HashMap<Long, LinkedList<Photo>> albumMap) {
        Iterator<Long> it = albumMap.keySet().iterator();
        AlbumDatabaseHelper databaseHelper = new AlbumDatabaseHelper(this);
        HashMap<Long, String> albumNames = databaseHelper.getAlbumNamesMap();
        HashSet<Long> hideids = databaseHelper.getHideBucketId();
        while (it.hasNext()) {
            long id = it.next();
            LinkedList<Photo> imgs = albumMap.get(id);
            String name = albumNames.get(id);
            if (name == null || name.isEmpty()) {
                name = imgs.get(0).getAlbumName();
            }
            Album album = new Album(id, name);
            album.setFile(imgs.get(0).getFile().getParentFile());
            album.setPhotos(imgs);
            if (hideids.contains(Long.valueOf(id))) {
                listHiddenAlbum.add(album);
            } else {
                listAlbum.add(album);
            }
        }
        scanUserAlbums(albumNames, hideids);
    }

    void scanUserAlbums(HashMap<Long, String> albumNames, HashSet<Long> hideids) {
        File albumFile = new File(USER_ALBUM_FLODER);
        File[] files = albumFile.listFiles(new EmptyFolderFileFilter());
        if (files == null) return;
        for (File file : files) {
            long id = file.getAbsolutePath().hashCode();

            String name = albumNames.get(id);
            if (name == null || name.isEmpty()) {
                name = file.getName();
            }
            Album album = new Album(id, name);
            album.setFile(file);
            if (hideids.contains(Long.valueOf(id))) {
                listHiddenAlbum.add(album);
            } else {
                listAlbum.add(album);
            }
        }
    }

    public Toolbar getToolbarTop() {
        return toolbarTop;
    }

    public ArrayList<ListPhotoSameDate> convertListPhoto2ListPhotoSameDate(ArrayList<Photo> listPhoto) {
        ArrayList<ListPhotoSameDate> listResult = new ArrayList<>();
        for (Photo photo : listPhoto) {
            ListPhotoSameDate curListPhotoByDate = checkDate(listResult, photo.getDateTaken());
            if (curListPhotoByDate == null) {
                curListPhotoByDate = new ListPhotoSameDate(photo.getDateTaken());
                curListPhotoByDate.addPhoto(photo);
                listResult.add(curListPhotoByDate);
            } else {
                curListPhotoByDate.addPhoto(photo);
            }
        }
        return listResult;
    }

    @Override
    public void onUpdateListPhotoWhenDelOrHideAlbum(Album... albums) {
        ArrayList<Photo> listRemovedPhoto;
        for (Album album : albums) {
            listRemovedPhoto = album.getArraylistPhoto();
            arrListPhoto.removeAll(listRemovedPhoto);
        }
        lstPhotoByDate = convertListPhoto2ListPhotoSameDate(arrListPhoto);

        pictureFragment.onChangeDataView(lstPhotoByDate, arrListPhoto);
    }

    @Override
    public void onUpdateListPhotoWhenReshowAlbum(Album... albums) {
        ArrayList<Photo> listAddedPhoto;
        for (Album album : albums) {
            listAddedPhoto = album.getArraylistPhoto();
            arrListPhoto.addAll(listAddedPhoto);
        }
        lstPhotoByDate = convertListPhoto2ListPhotoSameDate(arrListPhoto);

        pictureFragment.onChangeDataView(lstPhotoByDate, arrListPhoto);
    }
}
