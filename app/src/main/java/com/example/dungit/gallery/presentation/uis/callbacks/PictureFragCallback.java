package com.example.dungit.gallery.presentation.uis.callbacks;

import com.example.dungit.gallery.presentation.entities.EMODE;
import com.example.dungit.gallery.presentation.entities.ListPhotoSameDate;
import com.example.dungit.gallery.presentation.entities.Photo;
import com.example.dungit.gallery.presentation.uis.adapters.AdapterRecyclerView;

import java.util.ArrayList;

/**
 * Created by DUNGIT on 4/23/2018.
 */

public interface PictureFragCallback {
    void onChangeView(EMODE mode);

    void onChangeDataView(ArrayList<ListPhotoSameDate> listPhotoByDate, ArrayList<Photo> lstPhoto);

}
