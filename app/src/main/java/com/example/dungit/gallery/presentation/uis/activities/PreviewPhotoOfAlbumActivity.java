package com.example.dungit.gallery.presentation.uis.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.dungit.gallery.R;
import com.example.dungit.gallery.presentation.entities.EMODE;
import com.example.dungit.gallery.presentation.entities.Photo;
import com.example.dungit.gallery.presentation.uis.adapters.AdapterInnerRecyclerView;
import com.example.dungit.gallery.presentation.uis.adapters.AdapterRecyclerView;
import com.example.dungit.gallery.presentation.uis.fragments.AlbumFragment;

import java.util.ArrayList;

/**
 * Created by DUNGIT on 4/26/2018.
 */

public class PreviewPhotoOfAlbumActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    public static final String ALBUM_KEY = "album_key";
    private RecyclerView rvWrapperPreviewLstPhoto;
    private ArrayList<Photo> lstPhoto;
    private AdapterInnerRecyclerView adapterInnerRecyclerView;
    SearchView searchView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preview_photo_of_album_layout);

        Intent intent = getIntent();
        Toolbar toolbarTop = findViewById(R.id.tb_top_album);
        setSupportActionBar(toolbarTop);

        if(intent != null){
            AlbumFragment.AlbumExtendSerializable album =
                    (AlbumFragment.AlbumExtendSerializable) intent.getSerializableExtra(ALBUM_KEY);
            lstPhoto = album.getPhotos();
            getSupportActionBar().setTitle(album.getName());
        }
        adapterInnerRecyclerView = new  AdapterInnerRecyclerView(this, lstPhoto);
        rvWrapperPreviewLstPhoto = findViewById(R.id.rv_wrapper_preview_lst_photo);
        rvWrapperPreviewLstPhoto.setHasFixedSize(true);
        if(adapterInnerRecyclerView.getViewType())
            rvWrapperPreviewLstPhoto.setLayoutManager(new GridLayoutManager(this, 4));
        else
            rvWrapperPreviewLstPhoto.setLayoutManager(new LinearLayoutManager(this));
        rvWrapperPreviewLstPhoto.setAdapter(adapterInnerRecyclerView);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.act_viewType:
                adapterInnerRecyclerView.toggleItemViewTypeByPreview();
                if(adapterInnerRecyclerView.getViewType())
                    rvWrapperPreviewLstPhoto.setLayoutManager(new GridLayoutManager(this, 4));
                else
                    rvWrapperPreviewLstPhoto.setLayoutManager(new LinearLayoutManager(this));

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_photoalbum, menu);
        final MenuItem item = menu.findItem(R.id.action_search);
        searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(this);
        searchView.setQueryHint("Tìm kiếm theo tên");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        String text = newText;
            adapterInnerRecyclerView.getFilter().filter(newText);
        return false;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        adapterInnerRecyclerView.setbackViewType();
        return true;
    }
}
