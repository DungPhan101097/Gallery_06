package com.example.dungit.gallery.presentation.entities;


import com.example.dungit.gallery.presentation.Utils.ImageFileFilter;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by thaib on 17/04/2018.
 */


/**
 * Created by thaib on 17/04/2018.
 */

public class Album {
    private long id;
    private String name;
    private LinkedList<Photo> photos;
    private File file;
    private static FileFilter imageFilter = new ImageFileFilter();


    public Album(long id, String name) {
        this.id = id;
        this.name = name;
        photos=new LinkedList<>();
    }



    public LinkedList<Photo> getPhotos() {
        return photos;
    }

    public ArrayList<Photo> getArraylistPhoto(){
        ArrayList<Photo> arrayList = new ArrayList<>();
        arrayList.addAll(photos);
        return arrayList;

    }

    public String getName() {
        return name;
    }

    public int getSize() {
        if (photos != null)
            return photos.size();
        return 0;
    }

    public Photo getPhotoAt(int index) {
        return photos.get(index);
    }

    public Photo getLastestPhotos() {
        if(photos.size() > 0)
            return photos.get(0);
        return null;
    }

    public void addPhoto(Photo img){
        photos.add(img);
    }

    public void addPhotoAtHead(Photo photo){
        photos.addFirst(photo);
    }

    public void clearAlbum(){
        this.photos.clear();
    }

    public void setPhotos(LinkedList<Photo> images) {
        this.photos = images;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public boolean isEmpty(){
        return photos.size() < 1;
    }
}
