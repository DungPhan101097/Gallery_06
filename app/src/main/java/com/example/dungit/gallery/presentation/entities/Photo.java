package com.example.dungit.gallery.presentation.entities;

import android.net.Uri;
import android.os.Parcel;
import android.provider.MediaStore;

import com.example.dungit.gallery.presentation.Utils.FileUtils;
import com.example.dungit.gallery.presentation.Utils.ImageUtils;

import java.io.File;
import java.io.Serializable;

/**
 * Created by DUNGIT on 4/18/2018.
 */

public class Photo implements Serializable {
    private static final Uri EXTERNAL_URI = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

    private long idImg;
    private String pathUrl;
    private long albumId;
    private String albumName;
    private long dateTakenNumber;
    private File file;
    private int width;
    private int height;
    private String descriptImg;

    private boolean isLove;


    public Photo(long idImg, long dateTakenNumber, long albumId, String albumName, File file) {
        this.idImg = idImg;
        this.albumId = albumId;
        this.albumName = albumName;
        this.pathUrl = Uri.withAppendedPath(EXTERNAL_URI, Long.toString(idImg)).toString();
        this.dateTakenNumber = dateTakenNumber;
        this.file = file;
        this.isLove = false;
    }

    public Photo(long idImg, long dataTakenNumber, long albumId, String albumName
            , File file,int width ,int height, String descriptImg) {
        this(idImg, dataTakenNumber, albumId, albumName, file);
        this.width = width;
        this.height = height;
        this.descriptImg = descriptImg;
        this.isLove = false;
    }


    public long getIdImg() {
        return idImg;
    }

    public boolean getLoveImg(){return this.isLove; }

    public void setLove(boolean b_love){ this.isLove = b_love;}

    public void setIdImg(long idImg) {
        this.idImg = idImg;
    }

    public String getDateTaken() {
        return ImageUtils.getDate(dateTakenNumber);
    }

    public String getDetailDateTaken() {
        return ImageUtils.getDetailDate(dateTakenNumber);
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
        return file.getName();
    }

    public String getSizeImg() {
        return FileUtils.calculateSize(file.length());
    }

    public String getResoluImg() {
        return String.valueOf(width) + "x" + String.valueOf(height);
    }

    public String getPathImg() {
        return file.getAbsolutePath();
    }

    public String getDescriptImg() {
        return descriptImg;
    }

    public void setDescriptImg(String descriptImg) {
        this.descriptImg = descriptImg;
    }

}
