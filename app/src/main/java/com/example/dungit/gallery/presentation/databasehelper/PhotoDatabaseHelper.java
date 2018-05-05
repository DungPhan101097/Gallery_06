package com.example.dungit.gallery.presentation.databasehelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.HashMap;
import java.util.HashSet;

public class PhotoDatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "SQLite";
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Album_Grallery_06";

    private static final String TABLE_BUCKET_NAME = "bucket_name";
    private static final String BUCKET_ID = "bucket_id";
    private static final String BUCKET_DISPLAY_NAME = "bucket_display_name";

    private static final String TABLE_HIDE_ALBUM = "hide_bucket_id";

    private static final String TABLE_PHOTO_DESCRIPTION = "photo_description_table";
    private static final String PHOTO_ID = "photo_id";
    private static final String PHOTO_DESCR = "photo_descr";



    public PhotoDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String script = "CREATE TABLE " + TABLE_BUCKET_NAME + "("
                + BUCKET_ID + " INTEGER PRIMARY KEY," + BUCKET_DISPLAY_NAME + " TEXT)";
        db.execSQL(script);

        script = "CREATE TABLE " + TABLE_HIDE_ALBUM + "("
                + BUCKET_ID + " INTEGER PRIMARY KEY )";
        db.execSQL(script);

        script = "CREATE TABLE " + TABLE_PHOTO_DESCRIPTION + "("
                + PHOTO_ID + " INTEGER PRIMARY KEY," + PHOTO_DESCR + " TEXT)";
        db.execSQL(script);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BUCKET_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BUCKET_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PHOTO_DESCRIPTION);
        onCreate(db);
    }

    public void insertBucketName(long id,String name){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(BUCKET_ID, id);
        values.put(BUCKET_DISPLAY_NAME, name);
        db.insert(TABLE_BUCKET_NAME, null, values);
        db.close();
    }

    public HashMap<Long,String> getAlbumNamesMap(){
        HashMap<Long,String> map =new HashMap<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor =db.rawQuery("select * from " + TABLE_BUCKET_NAME,null);
        if (cursor != null && cursor.moveToFirst()) {
            map.put(cursor.getLong(0),cursor.getString(1));
            while (cursor.moveToNext()){
                map.put(cursor.getLong(0),cursor.getString(1));
            }
        }
        cursor.close();
        return map;
    }

    public String getBucketName(long id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_BUCKET_NAME, new String[] {
                        BUCKET_DISPLAY_NAME }, BUCKET_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            return cursor.getString(0);
        }
        return null;
    }

    public int updateBucketName(long id,String name){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(BUCKET_DISPLAY_NAME, name);

        // updating row
        return db.update(TABLE_BUCKET_NAME, values, BUCKET_ID + " = ?",
                new String[]{String.valueOf(id)});
    }

    public HashSet<Long> getHideBucketId(){
        HashSet<Long> set= new HashSet<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor =db.rawQuery("select * from " + TABLE_HIDE_ALBUM,null);
        if (cursor != null && cursor.moveToFirst()) {
            set.add(cursor.getLong(0));
            while (cursor.moveToNext()){
                set.add(cursor.getLong(0));
            }
        }
        cursor.close();
        return set;
    }

    public void insertHideAlbum(long bucket_id){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(BUCKET_ID, bucket_id);
        try {
            db.insertOrThrow(TABLE_HIDE_ALBUM, null, values);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        db.close();
    }

    public void removeHideAlbum(long bucket_id){
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.delete(TABLE_HIDE_ALBUM, BUCKET_ID + " = ? "
                    ,new String[]{String.valueOf(bucket_id)});
        }catch (Exception ex){
            ex.printStackTrace();
        }
        db.close();
    }

    public HashMap<Long,String> getPhotoDescriptionMap(){
        HashMap<Long,String> map =new HashMap<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor =db.rawQuery("select * from " + TABLE_PHOTO_DESCRIPTION,null);
        if (cursor != null && cursor.moveToFirst()) {
            map.put(cursor.getLong(0),cursor.getString(1));
            while (cursor.moveToNext()){
                map.put(cursor.getLong(0),cursor.getString(1));
            }
        }
        cursor.close();
        return map;
    }

    public void insertPhotoDescription(long id,String description){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PHOTO_DESCRIPTION,PHOTO_ID + "= ? ",new String[]{
                String.valueOf(id)
        });
        ContentValues values = new ContentValues();
        values.put(PHOTO_ID, id);
        values.put(PHOTO_DESCR, description);
        db.insert(TABLE_PHOTO_DESCRIPTION, null, values);
        db.close();
    }
}
