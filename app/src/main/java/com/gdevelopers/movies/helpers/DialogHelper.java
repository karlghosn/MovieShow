package com.gdevelopers.movies.helpers;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;
import androidx.appcompat.app.AlertDialog;

import com.gdevelopers.movies.R;


public class DialogHelper {

    public static void proVersionDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Feature is locked");
        builder.setIcon(R.drawable.ic_action_locked);
        builder.setMessage(R.string.unlock_pro);
        builder.setPositiveButton("OK", null);
        builder.create().show();
    }

    public static void noConnectionDialog(final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.you_are_offline);
        builder.setMessage(R.string.no_connection);
        builder.setPositiveButton("Go to Settings", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                context.startActivity(new Intent(Settings.ACTION_SETTINGS));
            }
        });
        builder.setNegativeButton("Close", null);
        builder.create().show();
    }
}
