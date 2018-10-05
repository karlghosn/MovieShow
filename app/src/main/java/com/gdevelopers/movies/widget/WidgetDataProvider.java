package com.gdevelopers.movies.widget;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.gdevelopers.movies.R;
import com.gdevelopers.movies.database.DatabaseHandler;
import com.gdevelopers.movies.helpers.Constants;
import com.gdevelopers.movies.helpers.DateHelper;
import com.gdevelopers.movies.helpers.MyRemoteView;
import com.gdevelopers.movies.objects.Movie;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


class WidgetDataProvider implements RemoteViewsService.RemoteViewsFactory {
    private List<Movie> mCollections = new ArrayList<>();
    private final Context mContext;
    private DatabaseHandler databaseHandler;

    WidgetDataProvider(Context context) {
        mContext = context;
    }

    @Override
    public int getCount() {
        return mCollections.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public RemoteViews getViewAt(int position) {
        final Movie movie = mCollections.get(position);
        final MyRemoteView mView = new MyRemoteView(mContext.getPackageName());
        mView.setId(position);
        mView.setTextViewText(R.id.movie_name, movie.getTitle());
        Log.d("Movie Name", movie.getTitle());
        mView.setTextViewText(R.id.movie_date, DateHelper.formatDate(movie.getReleaseDate()));
        String vote = String.valueOf(movie.getVoteAverage());
        mView.setTextViewText(R.id.movie_rating, vote.equals("0.0") ? mContext.getString(R.string.empty_text) : vote);

        final Intent fillInIntent = new Intent();
        fillInIntent.setAction(NewAppWidget.ACTION_TOAST);
        final Bundle bundle = new Bundle();
        bundle.putString(NewAppWidget.EXTRA_TITLE, movie.getTitle());
        bundle.putString(NewAppWidget.EXTRA_IMAGE, movie.getPosterPath());
        bundle.putString(NewAppWidget.EXTRA_ID, movie.getMovieId());
        fillInIntent.putExtras(bundle);
        mView.setOnClickFillInIntent(R.id.container_layout, fillInIntent);


        Bitmap weatherArtImage = null;

        String posterImage = movie.getPosterPath();
        if (!posterImage.endsWith("null")) {
            try {
                weatherArtImage = Picasso.with(mContext)
                        .load(posterImage)
                        .resize(80, 80)
                        .get();
            } catch (IOException e) {
                Log.d(Constants.STRINGS.EXCEPTION, e.getMessage());
            }
        }


        if (weatherArtImage != null) mView.setImageViewBitmap(R.id.movie_image, weatherArtImage);
        else mView.setImageViewResource(R.id.movie_image, R.drawable.placeholder);

        return mView;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public void onCreate() {
        databaseHandler = new DatabaseHandler(mContext);
        initData();
    }

    @Override
    public void onDataSetChanged() {
        initData();
    }

    private void initData() {
        mCollections = databaseHandler.getAllMovies();
    }


    @Override
    public void onDestroy() {
    }
}