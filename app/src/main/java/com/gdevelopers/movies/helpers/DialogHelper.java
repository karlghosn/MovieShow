package com.gdevelopers.movies.helpers;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.widget.ListView;

import androidx.appcompat.app.AlertDialog;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.content.ContextCompat;

import com.gdevelopers.movies.R;
import com.gdevelopers.movies.adapters.FeaturesAdapter;
import com.gdevelopers.movies.objects.Feature;

import java.util.ArrayList;
import java.util.List;


public class DialogHelper {

    public static void proVersionDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Feature is locked");
        ListView listView = (ListView) LayoutInflater.from(context).inflate(R.layout.features_dialog_layout, null);
        String[] titles = context.getResources().getStringArray(R.array.feature_titles);
        String[] descriptions = context.getResources().getStringArray(R.array.feature_descriptions);
        int[] icons = {R.mipmap.ic_no_ads, R.mipmap.ic_statistics, R.mipmap.ic_comment, R.mipmap.ic_write_comment, R.mipmap.ic_search, R.mipmap.ic_more};

        List<Feature> featureList = new ArrayList<>();
        for (int i = 0; i < titles.length; i++) {
            Feature feature = new Feature();
            feature.setIcon(icons[i]);
            feature.setTitle(titles[i]);
            feature.setDescription(descriptions[i]);
            featureList.add(feature);
        }

        FeaturesAdapter featuresAdapter = new FeaturesAdapter(context, R.layout.features_row_layout, featureList);
        listView.setAdapter(featuresAdapter);
        builder.setView(listView);
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
