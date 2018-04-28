package com.example.dungit.gallery.presentation.uis.adapters;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.LruCache;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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
import com.example.dungit.gallery.presentation.uis.activities.PreviewPhotoActivity;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by DUNGIT on 4/18/2018.
 */

public class AdapterInnerRecyclerView extends RecyclerView.Adapter<AdapterInnerRecyclerView.InnerViewHolder> {

    private ArrayList<Photo> data;
    private Context context;
    private static final int LIST_ITEM = 1;
    private static final int GRID_ITEM = 0;
    private static boolean isSwitchView = true;

    public AdapterInnerRecyclerView(Context context, ArrayList<Photo> data) {
        this.data = data;
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
    public void onBindViewHolder(@NonNull InnerViewHolder holder, int position) {
        final Photo curPhoto = data.get(position);

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
                intent.putExtra(PreviewPhotoActivity.IMG_URL_KEY, curPhoto.getUrl());
                context.startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {
                final String[] option={"Properties","Set As Wallpaper"};
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,android.R.layout.select_dialog_item,option);
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Option");
                builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                            {
                                AlertDialog.Builder builder_1 = new AlertDialog.Builder(context);
                                builder_1.setTitle("Thông Tin Ảnh");
                                builder_1.setMessage("Name : "+ curPhoto.getNameImg()+
                                        "\n\nPath : "+curPhoto.getPathImg()+
                                        "\n\nSize : "+ curPhoto.getSizeImg()+
                                        "\n\nResolution : "+curPhoto.getResoluImg()+
                                        "\n\nDate taken : "+curPhoto.getDateTaken());
                                builder_1.setCancelable(false);
                                builder_1.setNegativeButton("Đóng", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();

                                    }
                                });
                                AlertDialog alertDialog = builder_1.create();
                                alertDialog.show();
                            }break;
                            case 1:
                            {
                                Bitmap bm = BitmapFactory.decodeFile(curPhoto.getPathImg());
                                WallpaperManager myWallpaperManager
                                        = WallpaperManager.getInstance(context);
                                try {
                                    myWallpaperManager.setBitmap(bm);
                                    Toast.makeText(context, "Done", Toast.LENGTH_SHORT).show();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                                break;
                        }
                    }
                });
                builder.setCancelable(false);
                builder.setNegativeButton("Đóng", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
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