package com.example.dungit.gallery.presentation.uis.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.dungit.gallery.R;
import com.example.dungit.gallery.presentation.entities.EMODE;
import com.example.dungit.gallery.presentation.entities.ListPhotoSameDate;
import com.example.dungit.gallery.presentation.entities.Photo;
import com.example.dungit.gallery.presentation.uis.activities.MainActivity;
import com.example.dungit.gallery.presentation.uis.adapters.AdapterInnerRecyclerView;
import com.example.dungit.gallery.presentation.uis.adapters.AdapterRecyclerView;
import com.example.dungit.gallery.presentation.uis.callbacks.PictureFragCallback;

import java.util.ArrayList;

/**
 * Created by DUNGIT on 4/22/2018.
 */
public class PictureFragment extends Fragment implements PictureFragCallback {
    private static final String KEY_LIST_PHOTO = "list_photo";
    private static final String KEY_MODE = "mode_preview_photo";
    private ArrayList<ListPhotoSameDate> lstPhotoSameDate;
    private ArrayList<Photo> lstPhoto;
    private EMODE mode;
    private AdapterRecyclerView adpRecView;
    private AdapterInnerRecyclerView adpInnerRec;
    private boolean gridMode=false;

    private MainActivity main;
    private RecyclerView rvWrapper;

    public static PictureFragment newInstance(ArrayList<ListPhotoSameDate> lstPhoto) {
        PictureFragment fragment = new PictureFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_LIST_PHOTO, lstPhoto);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        main = (MainActivity) getActivity();
        lstPhotoSameDate = (ArrayList<ListPhotoSameDate>) getArguments().getSerializable(KEY_LIST_PHOTO);

        lstPhoto = new ArrayList<>();
        for (ListPhotoSameDate listPhotoSameDate : lstPhotoSameDate) {
            lstPhoto.addAll(listPhotoSameDate.getLstPhotoHaveSameDate());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.picture_fragment, container, false);

        rvWrapper = view.findViewById(R.id.rv_wrapper);
        rvWrapper.setHasFixedSize(true);
        rvWrapper.setLayoutManager(new LinearLayoutManager(main));
        adpRecView=new AdapterRecyclerView(main, lstPhotoSameDate);
        rvWrapper.setAdapter(adpRecView);

        return view;
    }

    public void search(String newText)
    {
        if(gridMode)
            adpInnerRec.getFilter().filter(newText);
        else
            adpRecView.getFilter().filter(newText);
    }

    public void onChangeViewType()
    {
        boolean isSwitched = adpRecView.toggle();
        adpRecView.setLayout(isSwitched);
        adpRecView.NotifyChange();
        if(gridMode)
        {
            if(isSwitched)
                rvWrapper.setLayoutManager( new GridLayoutManager(main, 4));
            else
                rvWrapper.setLayoutManager(new LinearLayoutManager(main));
        }

    }

    @Override
    public void onChangeView(EMODE mode) {
        switch (mode) {
            case MODE_BY_DATE:
                gridMode=false;
                rvWrapper.setLayoutManager(new LinearLayoutManager(main));
                rvWrapper.setAdapter(adpRecView);
                break;
            case MODE_GRID:
                gridMode=true;
                boolean isSwitched = adpRecView.getViewType();
                if(isSwitched)
                    rvWrapper.setLayoutManager( new GridLayoutManager(main, 4));
                else
                    rvWrapper.setLayoutManager(new LinearLayoutManager(main));
                adpInnerRec=new AdapterInnerRecyclerView(main, lstPhoto);
                rvWrapper.setAdapter(adpInnerRec);
                break;
        }
    }
}