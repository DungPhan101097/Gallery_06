package com.hcmus.minhtoan.takepicturecamera;

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

    private static final int CAMERA_REQUEST=1888;

    ImageView imageView;
    Button btnCamera;
    Button btnShare;
    Button btnSave;
    Intent shareIntent;
    String shareBody = "This is a great app!!!";
    AlertDialog dialog;


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
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                imageView.setImageBitmap(bitmap);
            }
        }

    }

    @Override
    public void onClick(View v){
        int id = v.getId();
        switch (id){
            case R.id.btnCamera:{
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File picDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyymmsshhmmss");
                String date = simpleDateFormat.format(new Date());
                String name = "IMAGE"+ date+".jpg";
                File imgageFile = new File(picDir, name);
                Uri picUri = Uri.fromFile(imgageFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, picUri);
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
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/jpeg");
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        File file = new File(Environment.getExternalStorageDirectory()+ File.separator+"ImageDemo.jpg");
        try {
            file.createNewFile();
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(byteArrayOutputStream.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:///sdcard/ImageDemo.jpg"));
        startActivity(Intent.createChooser(shareIntent, "Share Image"));
    }
    public void startSave(){
        FileOutputStream fileOutputStream = null;
        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yymmdd");
        String date = simpleDateFormat.format(new Date());
        String name = "IMG-"+date+".jpg";
        File newfile = new File(dir, name);
        try {
            fileOutputStream = new FileOutputStream(newfile);
            Bitmap bitmap = viewToBitmap(imageView, imageView.getWidth(), imageView.getHeight());
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
            fileOutputStream.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        refreshGallery(newfile);
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
