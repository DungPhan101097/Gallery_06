package com.example.dungit.gallery.presentation.uis.activities;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.example.dungit.gallery.R;
import com.example.dungit.gallery.presentation.databasehelper.updatedatadao.DBHelper;

import java.io.File;

import ly.img.android.PESDK;
import ly.img.android.sdk.models.constant.Directory;
import ly.img.android.sdk.models.state.CameraSettings;
import ly.img.android.sdk.models.state.EditorLoadSettings;
import ly.img.android.sdk.models.state.EditorSaveSettings;
import ly.img.android.sdk.models.state.manager.SettingsList;
import ly.img.android.ui.activities.CameraPreviewBuilder;
import ly.img.android.ui.activities.ImgLyActivity;
import ly.img.android.ui.activities.ImgLyIntent;
import ly.img.android.ui.activities.PhotoEditorBuilder;
import ly.img.android.ui.utilities.PermissionRequest;

//Create by TuNguyen

public class EditPhotoActivity extends Activity implements PermissionRequest.Response {

    private static final String FOLDER = "Edited";
    public static int CAMERA_PREVIEW_RESULT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        setContentView(R.layout.activity_edit_photo);

        SettingsList settingsList = new SettingsList();
        String myPicture = intent.getStringExtra("filePath");
        settingsList
                .getSettingsModel(EditorLoadSettings.class)
                .setImageSourcePath(myPicture,true) // Load with delete protection true!
                .getSettingsModel(EditorSaveSettings.class)
                .setExportDir(Directory.DCIM, FOLDER)
                .setExportPrefix("result_")

                .setSavePolicy(
                        EditorSaveSettings.SavePolicy.KEEP_SOURCE_AND_CREATE_ALWAYS_OUTPUT
                );
        new PhotoEditorBuilder(this)
                .setSettingsList(settingsList)
                .startActivityForResult(this, CAMERA_PREVIEW_RESULT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, android.content.Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == CAMERA_PREVIEW_RESULT) {

            String resultPath = data.getStringExtra(ImgLyIntent.RESULT_IMAGE_PATH);
            String sourcePath = data.getStringExtra(ImgLyIntent.SOURCE_IMAGE_PATH);


            if (resultPath != null) {
                // Scan result file
                File file = new File(resultPath);
                if (file.exists()) {
//                    MediaScannerConnection.scanFile(this,
//                            new String[]{file.getAbsolutePath()},
//                            null,
//                            null);
                    DBHelper.getInstance().addEdittedPhoto(file);
                }
            }


            Toast.makeText(this, "Image Save on: " + resultPath, Toast.LENGTH_LONG).show();

        }

        onBackPressed();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionRequest.onRequestPermissionsResult(requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void permissionGranted() {

    }

    @Override
    public void permissionDenied() {
        // The Permission was rejected by the user. The Editor was not opened, as it could not save the result image.
        // TODO for you: Show a Hint to the User
    }



}

