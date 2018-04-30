package com.example.dungit.gallery.presentation.entities;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
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
    private long dateTakenNumber;
    private static final Uri EXTERNAL_URI = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    private File file;
    private String nameImg;
    private String sizeImg;
    private String resoluImg;
    private String pathImg;


    public Photo(long idImg, String dateTaken, long albumId, String albumName) {
        this.idImg = idImg;
        this.dateTaken = dateTaken;
        this.albumId = albumId;
        this.albumName = albumName;
        this.pathUrl = Uri.withAppendedPath(EXTERNAL_URI, Long.toString(idImg)).toString();

    }

    public Photo(long idImg, String dateTaken, long albumId, String albumName, File file, long dataTakenNumber) {
        this(idImg, dateTaken, albumId, albumName);
        this.file = file;
        this.dateTakenNumber = dataTakenNumber;
    }

    public Photo(long idImg, String dateTaken, long albumId, String albumName, File file, long dataTakenNumber,String nameImg, String sizeImg , String resoluImg ,String pathImg) {
        this(idImg, dateTaken, albumId, albumName,file,dataTakenNumber);
        this.nameImg = nameImg;
        this.sizeImg = sizeImg;
        this.resoluImg = resoluImg;
        this.pathImg = pathImg;
    }


    protected Photo(Parcel in) {
        idImg = in.readLong();
        pathUrl = in.readString();
        dateTaken = in.readString();
        albumId = in.readLong();
        albumName = in.readString();
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

    public long getDateTakenNumber() {
        return dateTakenNumber;
    }

    public String getNameImg() {
        return nameImg;
    }

    public String getSizeImg() {
        return sizeImg;
    }

    public String getResoluImg() {
        return resoluImg;
    }

    public String getPathImg() {
        return pathImg;
    }

}
