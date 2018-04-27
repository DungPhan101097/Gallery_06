package com.example.dungit.gallery.presentation.uis.callbacks;

import com.example.dungit.gallery.presentation.entities.Album;

/**
 * Created by DUNGIT on 4/26/2018.
 */

public interface MainCallback {
    void onUpdateListPhotoWhenDelOrHideAlbum(Album... albums);
    void onUpdateListPhotoWhenReshowAlbum(Album... albums);
}
