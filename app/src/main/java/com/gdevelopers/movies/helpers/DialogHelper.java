package com.gdevelopers.movies.helpers;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;

import androidx.appcompat.app.AlertDialog;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.content.ContextCompat;

import com.gdevelopers.movies.R;


public class DialogHelper {

    public static void proVersionDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Feature is locked");
        builder.setIcon(R.drawable.ic_action_locked);
        builder.setMessage(R.string.unlock_pro);
        builder.setPositiveButton("Get Pro", (dialog, which) -> {
            final String appPackageName = "com.gdevelopers.movies_pro"; // getPackageName() from Context or Activity object
            try {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
            } catch (android.content.ActivityNotFoundException exception) {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
            }
        });
        builder.create().show();
    }

    public static void traktSignInDialog(Context context) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setTitle("Sign In");
        alertBuilder.setMessage(R.string.unlock_pro);
        alertBuilder.setNegativeButton(R.string.cancel, null);
        alertBuilder.setPositiveButton("Sign In", (dialog, which) -> {

            CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
            builder.setShowTitle(true);
            builder.setToolbarColor(ContextCompat.getColor(context, R.color.colorPrimary));
            CustomTabsIntent customTabsIntent = builder.build();
            String buffer = "https://api.trakt.tv/oauth/authorize?" +
                    "response_type=code" +
                    "&client_id=" + MovieDB.TRAKT_TV_API_KEY +
                    "&redirect_uri=" + MovieDB.REDIRECT_URI +
                    "&state=" + "110";
            customTabsIntent.launchUrl(context, Uri.parse(buffer));
        });
        alertBuilder.create().show();
    }

    public static void noConnectionDialog(final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.you_are_offline);
        builder.setMessage(R.string.no_connection);
        builder.setPositiveButton("Go to Settings", (dialogInterface, i) -> context.startActivity(new Intent(Settings.ACTION_SETTINGS)));
        builder.setNegativeButton("Close", null);
        builder.create().show();
    }
}
