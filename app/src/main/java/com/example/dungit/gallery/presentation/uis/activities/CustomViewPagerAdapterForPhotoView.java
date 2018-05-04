package com.example.dungit.gallery.presentation.uis.activities;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by DUNGIT on 4/30/2018.
 */

public class CustomViewPagerAdapterForPhotoView extends ViewPager {
    public CustomViewPagerAdapterForPhotoView(@NonNull Context context) {
        super(context);
    }

    public CustomViewPagerAdapterForPhotoView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        try {
            return super.onInterceptTouchEvent(ev);
        } catch (IllegalArgumentException e) {
            //uncomment if you really want to see these errors
            //e.printStackTrace();
            return false;
        } catch (Exception e) {
            return false;
        }
    }
}
