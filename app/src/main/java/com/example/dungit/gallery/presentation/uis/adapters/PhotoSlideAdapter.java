package com.example.dungit.gallery.presentation.uis.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.dungit.gallery.R;
import com.example.dungit.gallery.presentation.GlideApp;
import com.example.dungit.gallery.presentation.entities.Photo;
import com.example.dungit.gallery.presentation.uis.activities.PreviewPhotoActivity;

import java.util.ArrayList;

public class PhotoSlideAdapter  extends PagerAdapter{

    private ArrayList<Photo> images;
    private LayoutInflater inflater;
    private Context context;
    private Toolbar toolbar;
    private BottomNavigationView bottomNavigationView;
    private boolean isShowTB = true;

    public PhotoSlideAdapter(Context context, ArrayList<Photo> images) {
        this.context = context;
        this.images=images;
        inflater = LayoutInflater.from(context);
        if(context instanceof  PreviewPhotoActivity) {
            PreviewPhotoActivity ppA = (PreviewPhotoActivity)context;
            this.toolbar = ppA.getToolbar();
            this.bottomNavigationView = ppA.getbNV();
        }
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
        ImageView myImage = myImageLayout
                .findViewById(R.id.im_preview_photo);

        GlideApp.with(context).load(photo.getUrl())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(myImage);

        myImageLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toogleShowTB();
            }
        });

        view.addView(myImageLayout,0);
        return myImageLayout;
    }

    private void toogleShowTB(){
        if (toolbar != null && bottomNavigationView != null) {
            if (isShowTB) {

                toolbar.animate().translationY(-toolbar.getBottom()).setInterpolator(new AccelerateInterpolator()).start();
                bottomNavigationView.animate().translationY(bottomNavigationView.getBottom()).setInterpolator(new AccelerateInterpolator()).start();
            } else {
                toolbar.animate().translationY(0).setInterpolator(new DecelerateInterpolator()).start();
                bottomNavigationView.animate().translationY(0).setInterpolator(new DecelerateInterpolator()).start();
            }
            isShowTB = !isShowTB;
        }
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }


}
