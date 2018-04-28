package com.example.dungit.gallery.presentation.uis.adapters;

import android.view.View;

/**
 * Created by DUNGIT on 4/23/2018.
 */

public interface ItemClickListener {
    void onClick(View view , int position);

    void onLongClick(View view, int position);
}
