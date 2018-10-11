package com.gdevelopers.movies.helpers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.ActivityOptionsCompat;

import android.widget.ImageView;

import com.gdevelopers.movies.R;
import com.gdevelopers.movies.activities.MovieDetailsActivity;
import com.gdevelopers.movies.activities.TVDetailsActivity;


public class OnClickHelper {
    public static void movieClicked(Context context, String title, String image, String id, ImageView imageView) {
        Intent intent = new Intent(context, MovieDetailsActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("image", image);
        intent.putExtra("id", id);
        if (imageView != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            imageView.setTransitionName(context.getString(R.string.movie_poster));
            Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) context, imageView, context.getString(R.string.movie_poster)).toBundle();
            context.startActivity(intent, bundle);
        } else context.startActivity(intent);
    }

    public static void tvClicked(Context context, String title, String image, String id, ImageView imageView) {
        Intent intent = new Intent(context, TVDetailsActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("image", image);
        intent.putExtra("id", id);
        if (imageView != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            imageView.setTransitionName(context.getString(R.string.movie_poster));
            Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) context, imageView, context.getString(R.string.movie_poster)).toBundle();
            context.startActivity(intent, bundle);
        } else context.startActivity(intent);
    }
}
