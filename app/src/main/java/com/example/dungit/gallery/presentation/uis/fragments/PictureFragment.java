package com.example.dungit.gallery.presentation.uis.fragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
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
import com.example.dungit.gallery.presentation.uis.activities.SettingActivity;
import com.example.dungit.gallery.presentation.uis.adapters.AdapterInnerRecyclerView;
import com.example.dungit.gallery.presentation.uis.adapters.AdapterRecyclerView;

import java.util.ArrayList;

/**
 * Created by DUNGIT on 4/22/2018.
 */
public class PictureFragment extends Fragment implements SearchView.OnQueryTextListener {
    private static final String KEY_LIST_PHOTO = "list_photo";
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
    private boolean gridMode=false;
    private SearchView searchView;


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

    public void onChangeView(EMODE mode) {
        this.mode = mode;
        switch (mode) {
            case MODE_BY_DATE:
                gridMode=false;
                searchView.setQueryHint("Tìm kiếm theo ngày tháng năm");
                rvWrapper.setLayoutManager(linearLayoutManager);
                rvWrapper.setAdapter(adapterRecyclerView);
                break;
            case MODE_GRID:
                gridMode=true;
                searchView.setQueryHint("Tìm kiếm theo tên");
                boolean isSwitched = adapterRecyclerView.getViewType();
                if(isSwitched)
                    rvWrapper.setLayoutManager(gridLayoutManager);
                else
                    rvWrapper.setLayoutManager(linearLayoutManager);
                rvWrapper.setAdapter(adapterInnerRecyclerView);
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
            case R.id.act_viewType:
                onChangeViewType();
                break;
            case R.id.action_settings:
                Context context = main;
                Intent intent = new Intent(context,SettingActivity.class);
                context.startActivity(intent);
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
        final MenuItem item = menu.findItem(R.id.action_search);
        searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(this);
        searchView.setQueryHint("Tìm kiếm theo ngày tháng năm");
        super.onCreateOptionsMenu(menu, inflater);
    }



    //Ham cho changeviewtype
    public void onChangeViewType()
    {
        boolean isSwitched = adapterRecyclerView.toggle();
        adapterRecyclerView.setLayout(isSwitched);
        adapterRecyclerView.NotifyChange();
        if(gridMode)
        {
            if(isSwitched)
                rvWrapper.setLayoutManager(gridLayoutManager);
            else
                rvWrapper.setLayoutManager(linearLayoutManager);
        }

    }

    //Hàm cho filter

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if(gridMode) {
            adapterInnerRecyclerView.getFilter().filter(newText);
        }
        else {
            adapterRecyclerView.getFilter().filter(newText);
        }
        return false;
    }

}