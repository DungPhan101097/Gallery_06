package com.example.dungit.gallery.presentation.Utils;

import android.app.AlertDialog;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.Html;
import android.util.DisplayMetrics;
import android.widget.Toast;

import com.example.dungit.gallery.presentation.entities.Photo;
import com.example.dungit.gallery.presentation.uis.activities.PreviewPhotoActivity;
import com.example.dungit.gallery.presentation.uis.dialog.ConfirmDialog;

import java.io.File;
import java.io.IOException;
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

    public static void setImageAsWallpaper(DisplayMetrics displayMetrics, Photo photo, Context context){
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

    public static void showInfoDialog(Context context,Photo photo){
        String desc =photo.getDescriptImg();
        AlertDialog.Builder builder_1 = new AlertDialog.Builder(context);
        builder_1.setTitle("Thông Tin Ảnh");
        String html = "<b>Tên ảnh</b>: " + photo.getNameImg() +
                "<br/><br/><b>Đường dẫn</b>: " + photo.getPathImg() +
                "<br/><br/><b>Kích cỡ</b>: " + photo.getSizeImg() +
                "<br/><br/><b>Kích thước</b>: " + photo.getResoluImg() +
                "<br/><br/><b>Ngày tạo</b>: " + photo.getDetailDateTaken() +
                "<br/><br/><b>Chú thích</b>: " + (desc == null? "" : desc);
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

    public static String getDate(long time){
        Date date = new Date(time);
        return DATE_FMT.format(date);
    }

    public static String getDetailDate(long time){
        Date date = new Date(time);
        return DATE_FMT_DETAILS.format(date);
    }

    public static void deletePhoto(Context context, Photo photo){
        context.getContentResolver().delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                MediaStore.Images.ImageColumns._ID + " = ?",
                new String[]{String.valueOf(photo.getIdImg())});

    }

    public static void sendPhoto(Context context,Photo photo){
        File fileShare = photo.getFile();
        fileShare.setReadable(true, false);
        final Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(fileShare));
        intent.setType("image/jpg");
        context.startActivity(Intent.createChooser(intent, "Share image via"));

    }
}
