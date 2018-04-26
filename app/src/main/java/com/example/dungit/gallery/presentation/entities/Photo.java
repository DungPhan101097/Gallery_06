package com.example.dungit.gallery.presentation.entities;

import android.net.Uri;
import android.provider.MediaStore;

import java.io.File;
import java.io.Serializable;

/**
 * Created by DUNGIT on 4/18/2018.
 */

public class Photo implements Serializable {
    private long idImg;
    private String pathUrl;
    private String dateTaken;
    private long albumId;
    private String albumName;
    private static final Uri EXTERNAL_URI = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    private File file;


    public Photo(long idImg, String dateTaken, long albumId, String albumName) {
        this.idImg = idImg;
        this.dateTaken = dateTaken;
        this.albumId = albumId;
        this.albumName = albumName;
        this.pathUrl = Uri.withAppendedPath(EXTERNAL_URI, Long.toString(idImg)).toString();
    }

    public Photo(long idImg, String dateTaken, long albumId, String albumName, File file) {
        this(idImg, dateTaken, albumId, albumName);
        this.file = file;
    }

    public long getIdImg() {
        return idImg;
    }

    public void setIdImg(long idImg) {
        this.idImg = idImg;
    }

    public String getDateTaken() {
        return dateTaken;
    }

    public void setDateTaken(String dateTaken) {
        this.dateTaken = dateTaken;
    }

    public long getAlbumId() {
        return albumId;
    }

    public void setAlbumId(long albumId) {
        this.albumId = albumId;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public String getUrl() {
        return this.pathUrl;
    }


    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
}
