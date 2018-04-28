package com.example.dungit.gallery.presentation.uis.adapters;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.dungit.gallery.R;
import com.example.dungit.gallery.presentation.GlideApp;
import com.example.dungit.gallery.presentation.MyAppGlideModule;
import com.example.dungit.gallery.presentation.entities.Photo;
import com.example.dungit.gallery.presentation.uis.activities.MainActivity;
import com.example.dungit.gallery.presentation.uis.activities.PreviewPhotoActivity;

import java.util.ArrayList;

/**
 * Created by DUNGIT on 4/18/2018.
 */

public  class AdapterInnerRecyclerView extends RecyclerView.Adapter<AdapterInnerRecyclerView.InnerViewHolder> implements Filterable {

    private ArrayList<Photo> data;
    private ArrayList<Photo> mFilterdata;
    private Context context;
    private static final int LIST_ITEM = 1;
    private static final int GRID_ITEM = 0;
    private static boolean isSwitchView = true;

    public AdapterInnerRecyclerView(Context context, ArrayList<Photo> data) {
        this.data = data;
        this.mFilterdata =data;
        this.context = context;
    }

    @NonNull
    @Override
    public AdapterInnerRecyclerView.InnerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if(isSwitchView) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_img_item,
                    parent, false);
        }else
        {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_img_item_list,
                    parent, false);
        }
        return new InnerViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull InnerViewHolder holder, int position) {
        final Photo curPhoto = mFilterdata.get(position);

        GlideApp.with(context).load(curPhoto.getUrl())
                .placeholder(R.drawable.place_holder)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .transition(DrawableTransitionOptions.withCrossFade())
                .centerCrop()
                .into(holder.ivItem);
        holder.txtNAme.setText(curPhoto.getNameImg());
        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent intent = new Intent(context, PreviewPhotoActivity.class);
                int imgPostion = 0;
                if(context instanceof  MainActivity ){
                    MainActivity mainActivity = (MainActivity) context;
                    ArrayList<Photo> photos = mainActivity.getArrListPhoto();
                    if(data != photos) {
                        PreviewPhotoActivity.setPhotos(photos);
                        imgPostion = photos.indexOf(data.get(position));
                        imgPostion = imgPostion >= 0 ? imgPostion : 0;
                    }
                }else{
                    PreviewPhotoActivity.setPhotos(data);
                    imgPostion=position;
                }
                intent.putExtra(PreviewPhotoActivity.IMG_POSITION,imgPostion);
                try {
                    context.startActivity(intent);
                }catch (Exception ex){
                    ex.printStackTrace();
                }

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        });
    }


    @Override
    public int getItemViewType(int position) {
        if(isSwitchView)
        {
            return GRID_ITEM;
        }else
        {
            return LIST_ITEM;
        }
    }
    public boolean getViewType()
    {
        return isSwitchView;
    }
    public boolean toggleItemViewType () {
        isSwitchView = !isSwitchView;
        return isSwitchView;
    }


    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString= charSequence.toString();
                if(charString.isEmpty())
                {
                    mFilterdata= data;
                }else{
                    ArrayList<Photo> filteredData = new ArrayList<>();
                    for(Photo photo : data){
                        if(photo.getNameImg().toLowerCase().contains(charString.toLowerCase()))
                        {
                            filteredData.add(photo);
                        }
                    }
                    mFilterdata = filteredData;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = mFilterdata;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mFilterdata = (ArrayList<Photo>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    @Override
    public int getItemCount() {
        return mFilterdata.size();
    }

    public static class InnerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnLongClickListener {
        private ImageView ivItem;
        private TextView txtNAme;
        private ItemClickListener itemClickListener;

        public InnerViewHolder(View itemView) {
            super(itemView);
            ivItem = itemView.findViewById(R.id.iv_item);
            txtNAme = itemView.findViewById(R.id.txtName);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        public void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

        @Override
        public void onClick(View view) {
            itemClickListener.onClick(view, getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View view) {
            itemClickListener.onLongClick(view, getAdapterPosition());
            return true;
        }
    }
}