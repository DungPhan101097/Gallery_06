package com.example.dungit.gallery.presentation.uis.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.dungit.gallery.R;
import com.google.vr.sdk.widgets.pano.VrPanoramaView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class VRViewActivity extends Activity {
    private VrPanoramaView vrView;
    private VrPanoramaView.Options options;
    public static final String IMG_PATH = "IMG_PATH";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        setContentView(R.layout.activy_vrview);
        vrView = findViewById(R.id.vrView);
        options = new VrPanoramaView.Options();
        String path = intent.getStringExtra(IMG_PATH);
        Log.e("RROR",path);
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(path);
            options.inputType = VrPanoramaView.Options.TYPE_MONO;
            vrView.loadImageFromBitmap(BitmapFactory.decodeStream(inputStream), options);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        vrView.pauseRendering();
    }

    @Override
    protected void onResume() {
        super.onResume();
        vrView.resumeRendering();
    }

    @Override
    protected void onDestroy() {
        vrView.shutdown();
        super.onDestroy();
    }

}
