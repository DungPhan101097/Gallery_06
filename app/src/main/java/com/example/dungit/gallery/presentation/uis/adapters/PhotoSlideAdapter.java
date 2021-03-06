package com.example.dungit.gallery.presentation.uis.adapters;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.dungit.gallery.R;
import com.example.dungit.gallery.presentation.GlideApp;
import com.example.dungit.gallery.presentation.entities.Photo;
import com.github.chrisbanes.photoview.PhotoView;

import java.util.ArrayList;
import java.util.List;

public class PhotoSlideAdapter  extends PagerAdapter{

    private List<Photo> images;
    private LayoutInflater inflater;
    private Context context;


    public PhotoSlideAdapter(Context context, List<Photo> images) {
        this.context = context;
        this.images=images;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getItemPosition(Object object){
        return PagerAdapter.POSITION_NONE;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public Object instantiateItem(final ViewGroup view, int position) {
        View myImageLayout = inflater.inflate(R.layout.preview_photo_layout, view, false);
        final Photo photo=images.get(position);
        PhotoView myImage = myImageLayout
                .findViewById(R.id.im_preview_photo);

        GlideApp.with(context).load(photo.getUrl())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(myImage);

        myImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onInnerViewClick(v);
            }
        });

        Log.i("Postion: ",String.valueOf(position) + " "+photo.getFile());
        view.addView(myImageLayout);
        return myImageLayout;
    }



    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }


    public void onInnerViewClick(View v){

    }

}
