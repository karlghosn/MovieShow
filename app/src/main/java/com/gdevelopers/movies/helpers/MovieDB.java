package com.gdevelopers.movies.helpers;

import android.app.Application;

import com.gdevelopers.movies.database.DatabaseHandler;

public class MovieDB extends Application {
    public static final String url = "https://api.themoviedb.org/3";
    public static final String API_KEY = "your_key";
    public static final String TRAKT_TV_API_KEY = "your_key";
    public static final String TRAKT_TV_SECRET_KEY = "your_key";
    public static final String OMDB_KEY = "your_key";
    public static final String IMAGE_URL = "https://image.tmdb.org/t/p/";
    public static final String TRAILER_IMAGE_URL = "https://i1.ytimg.com/vi/";
    public static final String youtube = "https://www.youtube.com/watch?v=";
    public static final String PROFILE_IMAGE = "https://secure.gravatar.com/avatar/";
    public final static String TRAKT_BASE_URL = "https://api.trakt.tv";
    //    public static final String BASE_OAUTH_AUTHORIZE = "https://api.trakt.tv/oauth/authorize/";
    public static final String REDIRECT_URI = "your";
    public static final String CONTENT_TYPE = "application/json";

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