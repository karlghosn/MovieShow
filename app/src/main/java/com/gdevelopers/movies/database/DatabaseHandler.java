package com.gdevelopers.movies.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.gdevelopers.movies.objects.Movie;

import java.util.ArrayList;
import java.util.List;


public class DatabaseHandler extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "moviesManager";

    // Contacts table name
    private static final String TABLE_MOVIES = "movies";

    // Contacts Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_MOVIE_ID = "movieId";
    private static final String KEY_TITLE = "title";
    private static final String KEY_POSTER = "poster";
    private static final String KEY_RATING = "rating";
    private static final String KEY_RELEASE_DATE = "releaseDate";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_MOVIES + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_TITLE + " TEXT," + KEY_MOVIE_ID + " TEXT,"
                + KEY_POSTER + " TEXT," + KEY_RELEASE_DATE + " TEXT," + KEY_RATING + " TEXT"
                + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MOVIES);

        // Create tables again
        onCreate(db);
    }


    public void addMovie(Movie movie) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, movie.getTitle());
        values.put(KEY_MOVIE_ID, String.valueOf(movie.id()));
        values.put(KEY_POSTER, movie.getPosterPath());
        values.put(KEY_RELEASE_DATE, movie.getReleaseDate());
        values.put(KEY_RATING, movie.getVoteAverage());

        // Inserting Row
        db.insert(TABLE_MOVIES, null, values);
        db.close(); // Closing database connection
    }

    /*public Movie getMovie(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_MOVIES, new String[]{KEY_ID,
                        KEY_TITLE, KEY_MOVIE_ID, KEY_POSTER, KEY_RELEASE_DATE, KEY_RATING}, KEY_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        assert cursor != null;
        Movie movie = new Movie(Integer.parseInt(cursor.getString(0)));
        movie.setTitle(cursor.getString(1));
        movie.setPosterPath(cursor.getString(2));
        movie.setReleaseDate(cursor.getString(3));
        movie.setVoteAverage(Double.valueOf(cursor.getString(4)));
        cursor.close();

        return movie;
    }*/

    public void clearMovies() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_MOVIES, null, null);
    }

    public long getCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        long count = DatabaseUtils.queryNumEntries(db, TABLE_MOVIES);
        db.close();
        return count;
    }

    public List<Movie> getAllMovies() {
        List<Movie> movieList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_MOVIES;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Movie movie = new Movie(Integer.parseInt(cursor.getString(0)));
                movie.setTitle(cursor.getString(1));
                movie.setMovieId(cursor.getString(2));
                movie.setPosterPath(cursor.getString(3));
                movie.setReleaseDate(cursor.getString(4));
                movie.setVoteAverage(Double.valueOf(cursor.getString(5)));
                // Adding contact to list
                movieList.add(movie);
            } while (cursor.moveToNext());
        }

        cursor.close();

        // return contact list
        return movieList;
    }
}
