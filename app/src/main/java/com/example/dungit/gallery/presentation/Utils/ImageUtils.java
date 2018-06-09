package com.example.dungit.gallery.presentation.Utils;

import android.app.AlertDialog;
import android.app.WallpaperManager;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

import com.example.dungit.gallery.presentation.entities.Photo;
import com.example.dungit.gallery.presentation.uis.activities.PreviewPhotoActivity;
import com.example.dungit.gallery.presentation.uis.dialog.ConfirmDialog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ImageUtils {
    public static int calculateImageSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

    public static void setImageAsWallpaper(DisplayMetrics displayMetrics, Photo photo, Context context) {
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels << 1;
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photo.getPathImg(), options);
        options.inSampleSize = ImageUtils.calculateImageSize(options, width, height);
        options.inJustDecodeBounds = false;
        Bitmap decodedSampleBitmap = BitmapFactory.decodeFile(photo.getPathImg(), options);
        WallpaperManager wm = WallpaperManager.getInstance(context);
        try {
            wm.setBitmap(decodedSampleBitmap);
            Toast.makeText(context, "Đặt hình nền thành công", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void showInfoDialog(Context context, Photo photo) {
        String desc = photo.getDescriptImg();
        AlertDialog.Builder builder_1 = new AlertDialog.Builder(context);
        builder_1.setTitle("Thông Tin Ảnh");
        String html = "<b>Tên ảnh</b>: " + photo.getNameImg() +
                "<br/><br/><b>Đường dẫn</b>: " + photo.getPathImg() +
                "<br/><br/><b>Kích cỡ</b>: " + photo.getSizeImg() +
                "<br/><br/><b>Kích thước</b>: " + photo.getResoluImg() +
                "<br/><br/><b>Ngày tạo</b>: " + photo.getDetailDateTaken() +
                "<br/><br/><b>Chú thích</b>: " + (desc == null ? "" : desc);
        builder_1.setMessage(Html.fromHtml(html));
        builder_1.setCancelable(false);
        builder_1.setNegativeButton("Đóng", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();

            }
        });
        AlertDialog alertDialog = builder_1.create();
        alertDialog.show();
    }

    private static SimpleDateFormat DATE_FMT_DETAILS
            = new SimpleDateFormat("dd/MM/yyyy hh:mm");
    private static SimpleDateFormat DATE_FMT = new SimpleDateFormat("dd/MM/yyyy");

    public static String getDate(long time) {
        Date date = new Date(time);
        return DATE_FMT.format(date);
    }

    public static String getDetailDate(long time) {
        Date date = new Date(time);
        return DATE_FMT_DETAILS.format(date);
    }

    public static void deletePhoto(Context context, Photo photo) {
        context.getContentResolver().delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                MediaStore.Images.ImageColumns.DATA + " = ? OR " + MediaStore.Images.ImageColumns._ID + "= ?",
                new String[]{photo.getPathImg(), String.valueOf(photo.getIdImg())});

        Intent scanFileIntent = new Intent(
                Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(photo.getFile()));
        context.sendBroadcast(scanFileIntent);
    }

    public static void sendPhoto(Context context, Photo photo) {
        File fileShare = photo.getFile();
        fileShare.setReadable(true, false);
        final Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(fileShare));
        intent.setType("image/jpg");
        context.startActivity(Intent.createChooser(intent, "Share image via"));
    }

    public static long getIDImgFromURL(String url) {
        int idx = url.lastIndexOf("/");
        return Long.valueOf(url.substring(idx + 1, url.length()));
    }

    public static void insertNewPhoto(Context context, Photo newPhoto, File file) {
        if (!file.exists()) return;
        MediaScannerConnection.scanFile(context,
                new String[]{file.getAbsolutePath()},
                null,
                null);

        String url = "";
        try {
            url = MediaStore.Images.Media.insertImage(context.getContentResolver()
                    , file.getAbsolutePath()
                    , file.getName(), "");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        long id = getIDImgFromURL(url);
        newPhoto.setIdImg(id);
        newPhoto.setPathUrl(url);
        newPhoto.setFile(file);
    }

    public static boolean insertPhoto(Context context, Photo photo, File file) {
        if (!file.exists()) return false;
        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DATA, file.getAbsolutePath());
        return context.getContentResolver().update(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values,
        MediaStore.MediaColumns.DATA + "='" + photo.getPathImg() + "'", null) == 1;
    }



    // Inspired by:
// http://stackoverflow.com/questions/8560501/android-save-image-into-gallery/8722494#8722494
// https://gist.github.com/samkirton/0242ba81d7ca00b475b9

    public static Uri saveImageToGallery(ContentResolver cr, File file) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, file.getName());
        values.put(MediaStore.Images.Media.DISPLAY_NAME, file.getName());
        values.put(MediaStore.Images.Media.DESCRIPTION, "");
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        // Add the date meta data to ensure the image is added at the front of the gallery
        long millis = System.currentTimeMillis();
        values.put(MediaStore.Images.Media.DATE_ADDED, millis / 1000L);
        values.put(MediaStore.Images.Media.DATE_MODIFIED, millis / 1000L);
        values.put(MediaStore.Images.Media.DATE_TAKEN, millis);

        Uri url = null;

        try {
            url = cr.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

            if (file.exists()) {
                final int BUFFER_SIZE = 1024;

                FileInputStream fileStream = new FileInputStream(file);
                try {
                    OutputStream imageOut = cr.openOutputStream(url);
                    try {
                        byte[] buffer = new byte[BUFFER_SIZE];
                        while (true) {
                            int numBytesRead = fileStream.read(buffer);
                            if (numBytesRead <= 0) {
                                break;
                            }
                            imageOut.write(buffer, 0, numBytesRead);
                        }
                    } finally {
                        imageOut.close();
                    }
                } finally {
                    fileStream.close();
                }

                long id = ContentUris.parseId(url);
                // Wait until MINI_KIND thumbnail is generated.
                Bitmap miniThumb = MediaStore.Images.Thumbnails.getThumbnail(cr, id, MediaStore.Images.Thumbnails.MINI_KIND, null);
                // This is for backward compatibility.
                storeThumbnail(cr, miniThumb, id, 50F, 50F, MediaStore.Images.Thumbnails.MICRO_KIND);
            } else {
                cr.delete(url, null, null);
            }
        } catch (Exception e) {
            if (url != null) {
                cr.delete(url, null, null);
            }
        }
        return url;
    }

    /**
     * A copy of the Android internals StoreThumbnail method, it used with the insertImage to
     * populate the android.provider.MediaStore.Images.Media#insertImage with all the correct
     * meta data. The StoreThumbnail method is private so it must be duplicated here.
     * @see android.provider.MediaStore.Images.Media (StoreThumbnail private method)
     */
    private static Bitmap storeThumbnail(
            ContentResolver cr,
            Bitmap source,
            long id,
            float width,
            float height,
            int kind) {

        // create the matrix to scale it
        Matrix matrix = new Matrix();

        float scaleX = width / source.getWidth();
        float scaleY = height / source.getHeight();

        matrix.setScale(scaleX, scaleY);

        Bitmap thumb = Bitmap.createBitmap(source, 0, 0,
                source.getWidth(),
                source.getHeight(), matrix,
                true
        );

        ContentValues values = new ContentValues(4);
        values.put(MediaStore.Images.Thumbnails.KIND, kind);
        values.put(MediaStore.Images.Thumbnails.IMAGE_ID, (int) id);
        values.put(MediaStore.Images.Thumbnails.HEIGHT, thumb.getHeight());
        values.put(MediaStore.Images.Thumbnails.WIDTH, thumb.getWidth());

        Uri url = cr.insert(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, values);

        try {
            OutputStream thumbOut = cr.openOutputStream(url);
            thumb.compress(Bitmap.CompressFormat.JPEG, 100, thumbOut);
            thumbOut.close();
            return thumb;
        } catch (FileNotFoundException ex) {
            return null;
        } catch (IOException ex) {
            return null;
        }
    }

}

