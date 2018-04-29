package com.example.dungit.gallery.presentation.uis.fragments;


import android.content.Intent;
import android.graphics.Bitmap;
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
import android.widget.Toast;

import com.example.dungit.gallery.R;
import com.example.dungit.gallery.presentation.entities.EMODE;
import com.example.dungit.gallery.presentation.entities.ListPhotoSameDate;
import com.example.dungit.gallery.presentation.entities.Photo;
import com.example.dungit.gallery.presentation.uis.activities.MainActivity;
import com.example.dungit.gallery.presentation.uis.adapters.AdapterInnerRecyclerView;
import com.example.dungit.gallery.presentation.uis.adapters.AdapterRecyclerView;
import com.example.dungit.gallery.presentation.uis.callbacks.PictureFragCallback;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static android.app.Activity.RESULT_OK;

/**
 * Created by DUNGIT on 4/22/2018.
 */
public class PictureFragment extends Fragment implements PictureFragCallback {
    private static final int CAMERA_REQUEST= 1;
    private static final String KEY_LIST_PHOTO = "list_photo";
    private static final String KEY_MODE = "mode_preview_photo";
    private ArrayList<ListPhotoSameDate> lstPhotoSameDate;
    private ArrayList<Photo> lstPhoto;
    private EMODE mode;

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
        rvWrapper.setAdapter(new AdapterRecyclerView(main, lstPhotoSameDate));

        FloatingActionButton fbCam = view.findViewById(R.id.fabAlbum);
        fbCam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hhssddmmyy");
//                String date = simpleDateFormat.format(new Date());
//                String name = "IMG-"+date+".jpg";
//                File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM + "/Camera/")+ File.separator+ name);
//                Uri savePic = Uri.fromFile(file);
//                intent.putExtra(MediaStore.EXTRA_OUTPUT, savePic);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                intent.setType("image/jpg");
                startActivity(Intent.createChooser(intent, "Share image via"));
            }
        });

        return view;
    }

    @Override
    public void onChangeView(EMODE mode) {
        switch (mode) {
            case MODE_BY_DATE:
                rvWrapper.setLayoutManager(new LinearLayoutManager(main));
                rvWrapper.setAdapter(new AdapterRecyclerView(main, lstPhotoSameDate));
                break;
            case MODE_GRID:
                rvWrapper.setLayoutManager(new GridLayoutManager(main, 4));
                rvWrapper.setAdapter(new AdapterInnerRecyclerView(main, lstPhoto));
                break;
        }
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data){
//        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == RESULT_OK){
//            if (requestCode == CAMERA_REQUEST){
//                startSave();
//            }
//        }
//
//    }
//
//    private void startSave() {
//
//    }
}