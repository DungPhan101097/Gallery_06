package com.example.dungit.gallery.presentation.uis.fragments;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static android.app.Activity.RESULT_OK;

/**
 * Created by DUNGIT on 4/22/2018.
 */
public class PictureFragment extends Fragment implements PictureFragCallback {
    private static final String KEY_LIST_PHOTO = "list_photo";
    private static final String KEY_MODE = "mode_preview_photo";
    private ArrayList<ListPhotoSameDate> lstPhotoSameDate;
    private ArrayList<Photo> lstPhoto;
    private EMODE mode;

    private static final int CAMERA_REQUEST = 1;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.picture_fragment, container, false);

        rvWrapper = view.findViewById(R.id.rv_wrapper);
        rvWrapper.setHasFixedSize(true);

        rvWrapper.setLayoutManager(linearLayoutManager);
        rvWrapper.setAdapter(adapterRecyclerView);

        FloatingActionButton fbCam = view.findViewById(R.id.faCamera);
        fbCam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startShoot();
            }
        });


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

    public void startShoot(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM+"/Camera/");
        String pic = getPic();
        File img = new File(dir, pic);
        Uri picUri = Uri.fromFile(img);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, picUri);
        startActivityForResult(intent, CAMERA_REQUEST);
        refreshGallery(img);

        //startActivityForResult(intent, CAMERA_REQUEST );
    }

    private String getPic(){
        SimpleDateFormat sd = new SimpleDateFormat("hhssddmmyy");
        String stamp = sd.format(new Date());
        return "IMG-"+stamp+".jpg";
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            if (requestCode == CAMERA_REQUEST){

            }
        }

    }
    public void refreshGallery(File file){
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(Uri.fromFile(file));
        main.sendBroadcast(intent);
    }
}