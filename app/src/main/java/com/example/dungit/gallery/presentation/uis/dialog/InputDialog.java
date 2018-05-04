package com.example.dungit.gallery.presentation.uis.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.example.dungit.gallery.R;

abstract public class InputDialog {

    private String title;
    private String positiveButton;
    private String negativeButton;
    private Context context;
    private String defaultText;

    public InputDialog(Context context, String title, String positiveButton
            , String negativeButton,String defaultText) {
        this.context = context;
        this.title = title;
        this.positiveButton = positiveButton;
        this.negativeButton = negativeButton;
        this.defaultText = defaultText;
    }

    public void showDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View alertView = inflater.inflate(R.layout.alert_dialog, null);
        builder.setView(alertView);
        final EditText input = alertView.findViewById(R.id.edtAlbum);
        input.setText(defaultText);
        input.setSelection(0,defaultText.length());

        builder.setPositiveButton(positiveButton,null);
        builder.setNegativeButton(negativeButton, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        final AlertDialog inputDialog = builder.create();
        inputDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button btnPostive = inputDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                btnPostive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String ouput = input.getText().toString().trim();
                        onPositiveButtonClick(inputDialog,ouput);
                    }
                });
            }
        });

        inputDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        inputDialog.show();
    }

    abstract public void onPositiveButtonClick(AlertDialog inputDialog,String output);
}
