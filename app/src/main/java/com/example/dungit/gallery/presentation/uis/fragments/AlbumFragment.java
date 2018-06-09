package com.example.dungit.gallery.presentation.uis.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.dungit.gallery.R;
import com.example.dungit.gallery.presentation.databasehelper.updatedatadao.DBHelper;
import com.example.dungit.gallery.presentation.entities.Photo;
import com.example.dungit.gallery.presentation.uis.activities.MainActivity;
import com.example.dungit.gallery.presentation.uis.activities.PreviewPhotoOfAlbumActivity;
import com.example.dungit.gallery.presentation.uis.adapters.AlbumAdapter;

import java.io.Serializable;
import java.util.ArrayList;


import com.example.dungit.gallery.presentation.Utils.SortType;
import com.example.dungit.gallery.presentation.controller.AlbumController;
import com.example.dungit.gallery.presentation.entities.Album;
import java.util.List;

public class AlbumFragment extends Fragment implements SearchView.OnQueryTextListener{

    private Context context;
    private ListView listAlbum;
    private AlbumController albumController;
    private List<Album> albums;
    private List<Album> hiddenAlbums;
    private AlbumAdapter albumAdapter;
    private MainActivity main;
    private Album curAlbum;
    private SearchView searchView;

    public AlbumFragment() {


    }

    public static AlbumFragment newInstance(Context context, List<Album> albums, List<Album> hiddenAlbums) {
        AlbumFragment fragment = new AlbumFragment();
        fragment.context = context;
        fragment.albums = albums;
        fragment.hiddenAlbums = hiddenAlbums;
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        main = (MainActivity) getActivity();


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.album_fragment, container, false);


        listAlbum = rootView.findViewById(R.id.listAlbum);
        albumAdapter = new AlbumAdapter(context, albums);

        DBHelper.getInstance().addObserver(albumAdapter);

        listAlbum.setAdapter(albumAdapter);

        View footerV = new View(context);
        footerV.setMinimumHeight(350);
        footerV.setOnClickListener(null);
        listAlbum.addFooterView(footerV);

        albumController = new AlbumController(context, albumAdapter, DBHelper.getInstance());

        FloatingActionButton fab = rootView.findViewById(R.id.fabAlbum);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                albumController.addNewAblbum();
            }
        });

        listAlbum.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                curAlbum = albumAdapter.getAlbums().get(i);
                Intent intent = new Intent(context, PreviewPhotoOfAlbumActivity.class);
                PreviewPhotoOfAlbumActivity.setAlbum(curAlbum);
                context.startActivity(intent);
            }
        });


        registerForContextMenu(listAlbum);

        return rootView;
    }

    public void viewAllAlbum(){
        if( albumAdapter != null && albumAdapter.getAlbums() != albums){
            albumAdapter.setListAlbum(albums);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.albumSortAZ:
                albumController.sortAlbums(SortType.NAME_A_Z);
                break;
            case R.id.albumSortZA:
                albumController.sortAlbums(SortType.NAME_Z_A);
                break;
            case R.id.albumSortItemD:
                albumController.sortAlbums(SortType.ITEMS_DESC);
                break;
            case R.id.albumSortItemI:
                albumController.sortAlbums(SortType.ITEMS_INC);
                break;
            case R.id.albumHideList:
                albumController.unhideAlbum();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.option_menu_album, menu);
        super.onCreateOptionsMenu(menu, inflater);
        final MenuItem item = menu.findItem(R.id.action_search);
        searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(this);
        searchView.setQueryHint("Tìm kiếm Album");
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId() == R.id.listAlbum) {
            ListView lv = (ListView) v;
            AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) menuInfo;
            Album album = (Album) lv.getItemAtPosition(acmi.position);
            menu.setHeaderTitle("Album " + album.getName());
            getActivity().getMenuInflater().inflate(R.menu.menu_album, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Album album = (Album) listAlbum.getItemAtPosition(info.position);
        switch (item.getItemId()) {
            case R.id.albumDelete:
                albumController.deleteAlbum(album);
                break;
            case R.id.albumRename:
                albumController.renameAlbum(album);
                break;
            case R.id.albumHide:
                albumController.hideAlbum(album);
                break;
            case R.id.albumMove:
                albumController.moveAlbum(album);
                break;

        }
        return super.onContextItemSelected(item);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        albumAdapter.getFilter().filter(newText);
        return false;
    }


    public static class AlbumExtendSerializable implements Serializable {
        private long id;
        private String name;
        private ArrayList<Photo> photos;

        public AlbumExtendSerializable(Album curAlbum) {
            id = curAlbum.getId();
            name = curAlbum.getName();
            photos = curAlbum.getArraylistPhoto();

        }

        public long getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public ArrayList<Photo> getPhotos() {
            return photos;
        }


    }
}
