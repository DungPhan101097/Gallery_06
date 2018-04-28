package com.example.dungit.gallery.presentation.uis.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dungit.gallery.R;
import com.example.dungit.gallery.presentation.entities.Album;
import com.example.dungit.gallery.presentation.uis.viewholder.AlbumViewHolder;

import java.util.LinkedList;

public abstract class SelectAlbumAdapter extends ArrayAdapter {
    private Context context;
    private LinkedList<Album> albums;

    public SelectAlbumAdapter(Context context, LinkedList<Album> albums) {
        super(context, R.layout.album_list_item,albums);
        this.context=context;
        this.albums=albums;
    }

    @Override
    public View getView(int position, final View convertView, ViewGroup parent) {
        AlbumViewHolder myViewHolder;
        View row =convertView;
        if(row == null) {
            LayoutInflater layout = ((Activity) context).getLayoutInflater();
            row = layout.inflate(R.layout.select_album_item, null);
            myViewHolder=new AlbumViewHolder();
            myViewHolder.imgAlbum = (ImageView) row.findViewById(R.id.ImgAlbum);
            myViewHolder.txtNameAlbum = (TextView) row.findViewById(R.id.txtNameAlbum);
            myViewHolder.txtItem = (TextView) row.findViewById(R.id.txtItem);
            row.setTag(myViewHolder);
        }else {
            myViewHolder=(AlbumViewHolder)row.getTag();
        }

        final Album album=albums.get(position);
        myViewHolder.txtNameAlbum.setText(album.getName());
        myViewHolder.txtItem.setText( String.valueOf(album.getSize()));

        final View finalRow = row;
        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onRowClick(album);
            }
        });
        return (row);
    }

    public abstract void onRowClick(Album clickedAlbum);
}
