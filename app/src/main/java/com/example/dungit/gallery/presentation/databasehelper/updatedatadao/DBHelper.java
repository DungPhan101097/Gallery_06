package com.example.dungit.gallery.presentation.databasehelper.updatedatadao;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import com.example.dungit.gallery.presentation.Utils.EmptyFolderFileFilter;
import com.example.dungit.gallery.presentation.databasehelper.PhotoDatabaseHelper;
import com.example.dungit.gallery.presentation.entities.Album;
import com.example.dungit.gallery.presentation.entities.ListPhotoSameDate;
import com.example.dungit.gallery.presentation.entities.Photo;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;

/**
 * Created by DUNGIT on 4/28/2018.
 */

public class DBHelper extends Observable {
    private static final Uri EXTERNAL_URI = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    private static final String ID = MediaStore.Images.ImageColumns._ID;
    private static final String DATE_TAKEN = MediaStore.Images.ImageColumns.DATE_TAKEN;
    private static final String BUCKET_ID = MediaStore.Images.Media.BUCKET_ID;
    private static final String BUCKET_NAME = MediaStore.Images.Media.BUCKET_DISPLAY_NAME;
    private static final String DISPLAY_NAME = MediaStore.Images.Media.DISPLAY_NAME;
    private static final String SIZE = MediaStore.Images.Media.SIZE;
    private static final String DESCRIPTION = MediaStore.Images.Media.DESCRIPTION;
    private static final String DATA = MediaStore.Images.Media.DATA;
    private static final String[] IMAGE_PROJECTION_ALBUM =
            new String[]{
                    ID, DATE_TAKEN, BUCKET_NAME,BUCKET_ID,DISPLAY_NAME,SIZE,DESCRIPTION, DATA
            };
    private static final String USER_ALBUM_FLODER
            = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Albums06/";
    private static final String USER_ALBUM_LOVE_FLODER
            = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Albums06/Love/";

    private ArrayList<ListPhotoSameDate> listPhotoByDate = new ArrayList<>();
    private ArrayList<Photo> listPhoto = new ArrayList<>();

    private LinkedList<Album> listAlbum = new LinkedList<>();
    private LinkedList<Album> listHiddenAlbum = new LinkedList<>();

    private Context context;

    public DBHelper(Context context) {
        this.context = context;
    }

    public ArrayList<ListPhotoSameDate> getListPhotoByDate() {
        return listPhotoByDate;
    }

    public ArrayList<Photo> getListPhoto() {
        return listPhoto;
    }

    public LinkedList<Album> getListAlbum() {
        return listAlbum;
    }

    public void loadData() {
        listPhoto.clear();

        // Cursor for query images.
        Cursor imgCursor = null;
        String SORT_ORDER = " DESC";

        imgCursor = context.getContentResolver().query(EXTERNAL_URI,
                IMAGE_PROJECTION_ALBUM, null, null, DATE_TAKEN + SORT_ORDER);

        final int idIndex = imgCursor.getColumnIndex(ID);
        final int dateIndex = imgCursor.getColumnIndex(DATE_TAKEN);
        final int albumNameIndex = imgCursor.getColumnIndex(BUCKET_NAME);
        final int albumIdIndex = imgCursor.getColumnIndex(BUCKET_ID);
        final int dataIndex = imgCursor.getColumnIndex(DATA);

        ListPhotoSameDate lstPhoto = null;
        String date = null;

        PhotoDatabaseHelper databaseHelper = new PhotoDatabaseHelper(context);
        HashMap<Long,String> descriptionMap =databaseHelper.getPhotoDescriptionMap();
        HashMap<Long, LinkedList<Photo>> albumMap = new HashMap<>();

        while (imgCursor.moveToNext()) {
            final long id = imgCursor.getLong(idIndex);
            final long dateTaken = imgCursor.getLong(dateIndex);
            final String albumName = imgCursor.getString(albumNameIndex);
            final long albumId = imgCursor.getLong(albumIdIndex);
            final String filePath = imgCursor.getString(dataIndex);
            String descriptImg = null;

            //Lay kich thuoc anh resolution
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(filePath, options);
            int width = options.outWidth;
            int height = options.outHeight;

            if(descriptionMap.containsKey(id)){
                descriptImg = descriptionMap.get(id);
            }

            Photo curPhoto = new Photo(id, dateTaken, albumId, albumName, new File(filePath),width,height,descriptImg);
            listPhoto.add(curPhoto);

            if (albumMap.containsKey(albumId)) {
                albumMap.get(albumId).add(curPhoto);
            } else {
                LinkedList<Photo> photos = new LinkedList<>();
                photos.add(curPhoto);
                albumMap.put(albumId, photos);
            }
        }
        imgCursor.close();
        loadAlbums(albumMap);
        filterListPhoto(listHiddenAlbum);
    }

    public void convertListPhoto2ListPhotoSameDate(ArrayList<Photo> listPhoto) {
        listPhotoByDate.clear();
        for (Photo photo : listPhoto) {
            ListPhotoSameDate curListPhotoByDate = checkDate(listPhotoByDate, photo.getDateTaken());
            if (curListPhotoByDate == null) {
                curListPhotoByDate = new ListPhotoSameDate(photo.getDateTaken());
                curListPhotoByDate.addPhoto(photo);
                listPhotoByDate.add(curListPhotoByDate);
            } else {
                curListPhotoByDate.addPhoto(photo);
            }
        }
    }

    private void filterListPhoto(List<Album> rejectedList) {
        for (Album album : rejectedList) {
            listPhoto.removeAll(album.getArraylistPhoto());
        }
        convertListPhoto2ListPhotoSameDate(listPhoto);
    }

    private void addListPhotoForReshowAlbum(Album album) {
        listPhoto.addAll(album.getArraylistPhoto());
        Collections.sort(listPhoto, new Comparator<Photo>() {
            @Override
            public int compare(Photo photo, Photo t1) {
                long result = (t1.getDateTakenNumber() - photo.getDateTakenNumber());
                return result > 0 ? 1 : result < 0 ? -1 : 0;
            }
        });
        convertListPhoto2ListPhotoSameDate(listPhoto);
    }

    void loadAlbums(HashMap<Long, LinkedList<Photo>> albumMap) {
        Iterator<Long> it = albumMap.keySet().iterator();
        PhotoDatabaseHelper databaseHelper = new PhotoDatabaseHelper(context);
        HashMap<Long, String> albumNames = databaseHelper.getAlbumNamesMap();
        HashSet<Long> hideids = databaseHelper.getHideBucketId();
        while (it.hasNext()) {
            long id = it.next();
            LinkedList<Photo> imgs = albumMap.get(id);
            String name = albumNames.get(id);
            if (name == null || name.isEmpty()) {
                name = imgs.get(0).getAlbumName();
            }
            Album album = new Album(id, name);
            album.setFile(imgs.get(0).getFile().getParentFile());
            album.setPhotos(imgs);
            if (hideids.contains(Long.valueOf(id))) {
                listHiddenAlbum.add(album);
            } else {
                listAlbum.add(album);
            }
        }
        scanUserAlbums(albumNames, hideids);
    }

    private ListPhotoSameDate checkDate(ArrayList<ListPhotoSameDate> lstPhoto, String date) {
        for (ListPhotoSameDate lstPhotoItem : lstPhoto) {
            if (lstPhotoItem.getDate().equals(date)) {
                return lstPhotoItem;
            }
        }
        return null;
    }

    void scanUserAlbums(HashMap<Long, String> albumNames, HashSet<Long> hideids) {
        File albumFile = new File(USER_ALBUM_FLODER);
        File loveFile = new File(USER_ALBUM_LOVE_FLODER);
        File[] files = albumFile.listFiles(new EmptyFolderFileFilter());
        if (files == null) return;
        for (File file : files) {
            long id = file.getAbsolutePath().hashCode();

            String name = albumNames.get(id);
            if (name == null || name.isEmpty()) {
                name = file.getName();
            }
            Album album = new Album(id, name);
            album.setFile(file);
            if (hideids.contains(Long.valueOf(id))) {
                listHiddenAlbum.add(album);
            } else {
                listAlbum.add(album);
            }
        }
    }

    public void hideAlbum(Album album, PhotoDatabaseHelper databaseHelper) {
        databaseHelper.insertHideAlbum(album.getId());
        listHiddenAlbum.add(album);
        listAlbum.remove(album);
        filterListPhoto(listHiddenAlbum);

        setChanged();
        notifyObservers();
    }

    public void deleteAlbum(Album album) {
        if (album.isEmpty()) {
            File file = album.getFile();
            file.delete();
        } else {
            String idStr = String.valueOf(album.getId());
            context.getContentResolver().delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    MediaStore.Images.ImageColumns.BUCKET_ID + " = ?",
                    new String[]{idStr});
        }

        listAlbum.remove(album);
        List<Album> tmpList = new ArrayList<>();
        tmpList.add(album);
        filterListPhoto(tmpList);

        setChanged();
        notifyObservers();
    }

    public void reShowAlbum(List<Album> unhideAlbums_, PhotoDatabaseHelper databaseHelper) {
        for (Album unhideAlbum : unhideAlbums_) {
            listAlbum.add(unhideAlbum);
            listHiddenAlbum.remove(unhideAlbum);
            addListPhotoForReshowAlbum(unhideAlbum);
            databaseHelper.removeHideAlbum(unhideAlbum.getId());
        }
        setChanged();
        notifyObservers();
    }


    public void refresh(Uri uri){
        Intent scanFileIntent = new Intent(
                Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri);
        context.sendBroadcast(scanFileIntent);

        setChanged();
        notifyObservers();
    }

    public void reshowImg()
    {
//        String idStr = String.valueOf(photo.getIdImg());
//        context.getContentResolver().delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//                MediaStore.Images.ImageColumns.BUCKET_ID + " = ?",
//                new String[]{idStr});
        setChanged();
        notifyObservers();
    }

    public void addNewAlbum(Album album) {
        listAlbum.add(album);
        setChanged();
        notifyObservers();
    }

    public void renameAlbum(Album album, String output) {
        album.setName(output);
        setChanged();
        notifyObservers();
    }

    public void moveAlbum(Album movedALbum, Album desAlbum) {
        File nAlbumFile = desAlbum.getFile();

        LinkedList<Photo> photos = movedALbum.getPhotos();

        for (Photo photo : photos) {
            File filePhoto = photo.getFile();
            File newFile = new File(nAlbumFile, filePhoto.getName());
            if (filePhoto.renameTo(newFile)) {
                Intent scanFileIntent = new Intent(
                        Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(newFile));
                context.sendBroadcast(scanFileIntent);
                photo.setFile(newFile);
                desAlbum.addPhoto(photo);
            }
        }
        context.getContentResolver().delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                MediaStore.Images.ImageColumns.BUCKET_ID + " = ?",
                new String[]{String.valueOf(movedALbum.getId())});
        movedALbum.clearAlbum();

        setChanged();
        notifyObservers();
    }

    public List<Album> getListHiddenAlbums() {
        return listHiddenAlbum;
    }
}
