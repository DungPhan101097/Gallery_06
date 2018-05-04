package com.example.dungit.gallery.presentation.uis.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.example.dungit.gallery.R;
import com.example.dungit.gallery.presentation.entities.Photo;
import com.example.dungit.gallery.presentation.uis.adapters.AdapterInnerRecyclerView;
import com.example.dungit.gallery.presentation.uis.fragments.AlbumFragment;

import java.util.ArrayList;

/**
 * Created by DUNGIT on 4/26/2018.
 */

public class PreviewPhotoOfAlbumActivity extends AppCompatActivity {
    public static final String ALBUM_KEY = "album_key";
    private static final long ANIM_DURATION = 500;
    private RecyclerView rvWrapperPreviewLstPhoto;
    private ArrayList<Photo> lstPhoto;
    private AdapterInnerRecyclerView adapterInnerRecyclerView;

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
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
