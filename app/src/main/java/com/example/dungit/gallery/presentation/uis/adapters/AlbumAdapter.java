package com.example.dungit.gallery.presentation.uis.adapters;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.dungit.gallery.R;
import com.example.dungit.gallery.presentation.GlideApp;
import com.example.dungit.gallery.presentation.Utils.SortType;
import com.example.dungit.gallery.presentation.databasehelper.updatedatadao.DBHelper;
import com.example.dungit.gallery.presentation.entities.Album;
import com.example.dungit.gallery.presentation.entities.Photo;
import com.example.dungit.gallery.presentation.uis.viewholder.AlbumViewHolder;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by thaib on 17/04/2018.
 */

public class AlbumAdapter extends ArrayAdapter implements Observer{

    private Context context;
    private List<Album> albums;
    public static final File albumsLocation = new File(Environment.getExternalStorageDirectory(), "Albums06/");

    public AlbumAdapter(Context context, List<Album> albums) {
        super(context, R.layout.album_list_item, albums);
        this.context = context;
        this.albums = albums;

    }

    @Override
    public View getView(int position, final View convertView, ViewGroup parent) {
        AlbumViewHolder myViewHolder;
        View row = convertView;
        if (row == null) {
            LayoutInflater layout = ((Activity) context).getLayoutInflater();
            row = layout.inflate(R.layout.album_list_item, null);
            myViewHolder = new AlbumViewHolder();
            myViewHolder.imgAlbum = (ImageView) row.findViewById(R.id.ImgAlbum);
            myViewHolder.txtNameAlbum = (TextView) row.findViewById(R.id.txtNameAlbum);
            myViewHolder.txtItem = (TextView) row.findViewById(R.id.txtItem);
            row.setTag(myViewHolder);
        } else {
            myViewHolder = (AlbumViewHolder) row.getTag();
        }

        Album album = albums.get(position);
        myViewHolder.txtNameAlbum.setText(album.getName());
        myViewHolder.txtItem.setText(String.valueOf(album.getSize()));

        Photo lastedPhoto =  album.getLastestPhotos();
        if (lastedPhoto == null) {
            GlideApp.with(this.context).load(R.drawable.place_holder)
                    .fitCenter().into(myViewHolder.imgAlbum);
        } else {
            GlideApp.with(this.context).load(lastedPhoto.getUrl())
                    .placeholder(R.drawable.place_holder)
                    .fitCenter().priority(Priority.IMMEDIATE)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(myViewHolder.imgAlbum);
        }

        return (row);
    }

    public boolean containsAlbum(String name) {
        for (Album album : albums) {
            if (album.getName().equals(name))
                return true;
        }
        return false;
    }


    public void reloadList() {
        this.albums = albums;
        this.notifyDataSetChanged();
    }

    public List<Album> getAlbums() {
        return albums;
    }

    public void sortAlbum(final SortType type) {
        Collections.sort(albums, new Comparator<Album>() {
            @Override
            public int compare(Album o1, Album o2) {
                switch (type) {
                    case NAME_A_Z:
                        return o1.getName().compareTo(o2.getName());
                    case NAME_Z_A:
                        return o2.getName().compareTo(o1.getName());
                    case ITEMS_INC:
                        return o1.getSize() - o2.getSize();
                    case ITEMS_DESC:
                        return o2.getSize() - o1.getSize();
                }
                return o1.getName().compareTo(o2.getName());
            }
        });
        this.notifyDataSetChanged();
    }

    @Override
    public void update(Observable observable, Object o) {
        if(observable instanceof DBHelper){
            DBHelper dbHelper = (DBHelper)observable;
            this.albums = dbHelper.getListAlbum();
            this.notifyDataSetChanged();
        }
    }
}
