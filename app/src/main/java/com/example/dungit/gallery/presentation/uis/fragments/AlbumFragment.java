package com.example.dungit.gallery.presentation.uis.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.dungit.gallery.R;

public class AlbumFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
// Get the view design from file: res/layout/fragment_page1.xml
        View view = inflater.inflate(R.layout.album_fragment, container, false);
        view.setBackgroundColor(Color.parseColor("#55FF0000"));
        return view;
    }
}