package com.example.dungit.gallery.presentation.uis.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public abstract class ConfirmDialog {
    Context context;
    String title;
    String positiveButton;
    String negativeButton ;
    public ConfirmDialog(Context context, String title, String positiveButton, String negativeButton){
        this.context=context;
        this.title=title;
        this.positiveButton=positiveButton;
        this.negativeButton=negativeButton;
    }

    public void showDialog(){
        final AlertDialog.Builder builder =new AlertDialog.Builder(context)
                .setTitle(title)
                .setCancelable(false)
                .setPositiveButton(positiveButton, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        onPositiveButtonClick();
                    }
                })
                .setNegativeButton(negativeButton, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog confirmDialog = builder.create();
        confirmDialog.show();
    }

    abstract public void onPositiveButtonClick();

}
