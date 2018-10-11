package com.gdevelopers.movies.helpers;

import android.app.Application;

import com.gdevelopers.movies.database.DatabaseHandler;

public class MovieDB extends Application {
    public static final String url = "https://api.themoviedb.org/3";
    public static final String API_KEY = "b63da4f71c5d1ed9a1829e9946a62488";
    public static final String TRAKT_TV_API_KEY = "9cf765f32b50661a17477137b364373699e86747a3471976e3d25366d0a9991a";
    public static final String OMDB_KEY = "512d9024";
    public static final String IMAGE_URL = "https://image.tmdb.org/t/p/";
    public static final String TRAILER_IMAGE_URL = "https://i1.ytimg.com/vi/";
    public static final String youtube = "https://www.youtube.com/watch?v=";
    public static final String PROFILE_IMAGE = "https://secure.gravatar.com/avatar/";
    public final static String TRAKT_BASE_URL = "https://api.trakt.tv";

    private static MovieDB instance = null;
    private DatabaseHandler databaseHandler;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        databaseHandler = new DatabaseHandler(this);
    }

    public static MovieDB getAppContext() {
        return instance;
    }

    public DatabaseHandler getDatabaseHandler() {
        return databaseHandler;
    }
}