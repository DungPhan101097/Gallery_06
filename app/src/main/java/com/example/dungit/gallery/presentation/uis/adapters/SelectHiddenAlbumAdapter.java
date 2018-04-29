package com.example.dungit.gallery.presentation.uis.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dungit.gallery.R;
import com.example.dungit.gallery.presentation.entities.Album;
import com.example.dungit.gallery.presentation.uis.viewholder.AlbumViewHolder;

import java.util.LinkedList;
import java.util.List;

public class SelectHiddenAlbumAdapter extends ArrayAdapter {

    private Context context;
    private List<Album> unhideAlbums;
    private List<Album> hiddenAlbums;

    public SelectHiddenAlbumAdapter(Context context
            ,List<Album> hiddenAlbums,List<Album> unhideAlbums) {
        super(context, R.layout.select_hidden_album_item,hiddenAlbums);
        this.context=context;
        this.unhideAlbums=unhideAlbums;
        this.hiddenAlbums = hiddenAlbums;
    }

    @Override
    public View getView(int position, final View convertView, ViewGroup parent) {
        AlbumViewHolder myViewHolder;
        View row =convertView;
        if(row == null) {
            LayoutInflater layout = ((Activity) context).getLayoutInflater();
            row = layout.inflate(R.layout.select_hidden_album_item, null);
            myViewHolder=new AlbumViewHolder();
            myViewHolder.imgAlbum = (ImageView) row.findViewById(R.id.ImgAlbum);
            myViewHolder.txtNameAlbum = (TextView) row.findViewById(R.id.txtNameAlbum);
            myViewHolder.txtItem = (TextView) row.findViewById(R.id.txtItem);
            row.setTag(myViewHolder);
        }else {
            myViewHolder=(AlbumViewHolder)row.getTag();
        }

        final Album album=hiddenAlbums.get(position);
        myViewHolder.txtNameAlbum.setText(album.getName());
        myViewHolder.txtItem.setText( String.valueOf(album.getSize()));


        final CheckBox cb = row.findViewById(R.id.cbHiddenAlbum);
        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cb.toggle();

            }
        });
        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(cb.isChecked()){
                    unhideAlbums.add(album);
                }else {
                    unhideAlbums.remove(album);
                }
            }
        });

        return (row);
    }

}
