package com.gdevelopers.movies.helpers;

import androidx.appcompat.app.AppCompatActivity;

import com.gdevelopers.movies.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

public class AdBuilder {

    public static void buildAd (AppCompatActivity appCompatActivity) {
        MobileAds.initialize(appCompatActivity.getApplicationContext(),
                "ca-app-pub-8319784661284062/7719954035");

        AdView mAdView = appCompatActivity.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        mAdView.loadAd(adRequest);
    }
}
