package com.hcmus.minhtoan.takepicturecamera;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int CAMERA_REQUEST= 1;
    private static final int PHOTO_SHARE= 2;

    ImageView imageView;
    Button btnCamera;
    Button btnShare;
    Button btnSave;
    AlertDialog dialog;
    Uri uri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnCamera = (Button) findViewById(R.id.btnCamera);
        btnShare = (Button) findViewById(R.id.btnShare);
        btnSave = (Button) findViewById(R.id.btnSave);
        imageView = (ImageView) findViewById(R.id.imageView);

        btnCamera.setOnClickListener(this);

        btnSave.setOnClickListener(this);
        btnShare.setOnClickListener(this);

//        btnShare.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
////                shareIntent = new Intent(Intent.ACTION_SEND);
////                shareIntent.setType("image/png");
////                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "MyApp");
////                shareIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
////                startActivity(Intent.createChooser(shareIntent,"Share"));
//
//
//            }
//        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            if (requestCode == CAMERA_REQUEST){
                Bundle extras = data.getExtras();
                Bitmap bitmap = (Bitmap) extras.get("data");
                imageView.setImageBitmap(bitmap);
            }
            if (requestCode == PHOTO_SHARE){
                uri = data.getData();
                imageView.setImageURI(uri);
            }
        }

    }

    @Override
    public void onClick(View v){
        int id = v.getId();
        switch (id){
            case R.id.btnCamera:{
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, CAMERA_REQUEST);
            }
            break;
            case R.id.btnSave:{
                dialog = new AlertDialog.Builder(this).create();
                dialog.setTitle("Save image");
                dialog.setMessage("You sure?");
                dialog.setButton("Yes", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startSave();
                    }
                });
                dialog.setButton2("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
                break;
            }
            case R.id.btnShare:{
                startShare();
                break;
            }
        }
    }

    public void startShare(){
        Bitmap bitmap = viewToBitmap(imageView, imageView.getWidth(), imageView.getHeight());

        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hhssddmmyy");
            String date = simpleDateFormat.format(new Date());
            String name = "IMG-"+date+".jpg";
            File file = new File(this.getExternalCacheDir(),name);
            FileOutputStream fOut = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
            fOut.close();
            file.setReadable(true, false);
            final Intent intent = new Intent(android.content.Intent.ACTION_SEND);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
            intent.setType("image/jpg");
            startActivity(Intent.createChooser(intent, "Share image via"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void startSave(){
        ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        Bitmap bitmap = viewToBitmap(imageView, imageView.getWidth(), imageView.getHeight());
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hhssddmmyy");
        String date = simpleDateFormat.format(new Date());
        String name = "IMG-"+date+".jpg";
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM + "/Camera/")+ File.separator+ name);
        try {
            file.createNewFile();
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(byteArrayOutputStream.toByteArray());
            Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            e.printStackTrace();
        }
        refreshGallery(file);
    }

    public  void refreshGallery(File file){
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(Uri.fromFile(file));
        sendBroadcast(intent);
    }

    private File getDisc(){
        File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        return new File(file, "Image Demo");
    }

    public  static Bitmap viewToBitmap(View view,int width, int height)
    {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }
}
