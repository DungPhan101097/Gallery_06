package com.example.dungit.gallery.presentation.uis.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
    private boolean isCheckedChangeView;


    private MainActivity main;
    private RecyclerView rvWrapper;
    private AdapterRecyclerView adapterRecyclerView;
    private AdapterInnerRecyclerView adapterInnerRecyclerView;
    private LinearLayoutManager linearLayoutManager;
    private GridLayoutManager gridLayoutManager;

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
        isCheckedChangeView = false;
        lstPhotoSameDate = (ArrayList<ListPhotoSameDate>) getArguments().getSerializable(KEY_LIST_PHOTO);

        lstPhoto = new ArrayList<>();
        for (ListPhotoSameDate listPhotoSameDate : lstPhotoSameDate) {
            lstPhoto.addAll(listPhotoSameDate.getLstPhotoHaveSameDate());
        }
        mode = EMODE.MODE_BY_DATE;

        linearLayoutManager = new LinearLayoutManager(main);
        gridLayoutManager = new GridLayoutManager(main, 4);
        adapterRecyclerView = new AdapterRecyclerView(main, lstPhotoSameDate);
        adapterInnerRecyclerView = new AdapterInnerRecyclerView(main, lstPhoto);

        main.getDBHelper().addObserver(adapterInnerRecyclerView);
        main.getDBHelper().addObserver(adapterRecyclerView);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.picture_fragment, container, false);

        rvWrapper = view.findViewById(R.id.rv_wrapper);
        rvWrapper.setHasFixedSize(true);

        rvWrapper.setLayoutManager(linearLayoutManager);
        rvWrapper.setAdapter(adapterRecyclerView);

        return view;
    }

    @Override
    public void onChangeView(EMODE mode) {
        this.mode = mode;
        switch (mode) {
            case MODE_BY_DATE:
                rvWrapper.setLayoutManager(linearLayoutManager);
                rvWrapper.setAdapter(adapterRecyclerView);
                break;
            case MODE_GRID:
                rvWrapper.setLayoutManager(gridLayoutManager);
                rvWrapper.setAdapter(adapterInnerRecyclerView);
                break;
        }
    }

    @Override
    public void onChangeDataView(ArrayList<ListPhotoSameDate> listPhotoByDate, ArrayList<Photo> lstPhoto) {
        this.lstPhotoSameDate = listPhotoByDate;
        this.lstPhoto = lstPhoto;
        adapterRecyclerView.setData(this.lstPhotoSameDate);
        adapterInnerRecyclerView.setData(this.lstPhoto);

        switch (mode) {
            case MODE_BY_DATE:
                adapterRecyclerView.notifyDataSetChanged();
                break;
            case MODE_GRID:
                adapterInnerRecyclerView.notifyDataSetChanged();
                break;
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.act_change_view:
                isCheckedChangeView = !isCheckedChangeView;
                if (isCheckedChangeView) {
                    item.setIcon(getResources().getDrawable(R.drawable.btn_gallery_grid_mode));
                    this.onChangeView(EMODE.MODE_GRID);
                } else {
                    item.setIcon(getResources().getDrawable(R.drawable.btn_gallery_detail_mode));
                    this.onChangeView(EMODE.MODE_BY_DATE);
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_picture, menu);
        if(isCheckedChangeView){
            menu.getItem(0).setIcon(getResources().getDrawable(R.drawable.btn_gallery_grid_mode));
        }
        super.onCreateOptionsMenu(menu, inflater);
    }
}