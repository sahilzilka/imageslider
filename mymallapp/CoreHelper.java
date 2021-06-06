package com.example.mymallapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.OpenableColumns;
import android.view.View;

import androidx.annotation.RequiresApi;

import com.google.android.material.snackbar.Snackbar;

public class CoreHelper {
    Context context;

    public CoreHelper(Context context) {
        this.context = context;
    }

    public void createSnackBar(View view,String message, String actionText,View.OnClickListener actionClickListener,int time){
        Snackbar.make(view, message,time).setAction(actionText,actionClickListener).show();

    }

    public void createAlert(String aletrTitle, String alertmesaage, String positiveButtonText,
                            String negativeButoonText, DialogInterface.OnClickListener positiveButtonListener,
                            DialogInterface.OnClickListener negativeButtonListener,DialogInterface.OnDismissListener dismissListener){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(aletrTitle)
                .setMessage(alertmesaage)
                .setPositiveButton(positiveButtonText,positiveButtonListener)
                .setNegativeButton(negativeButoonText,negativeButtonListener)
                .setOnDismissListener(dismissListener)
                .create().show();

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String getfileNameFromUri (Uri uri){
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = context.getContentResolver().query(uri,null,null,null);
            try {
                if (cursor !=null && cursor.moveToFirst()){result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));}


            }finally{
                cursor.close();
            }
        }
        if(result == null){
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if(cut != -1){
                result = result.substring(cut+1);
            }
        }
        return result;
    }
}
