package com.gdevelopers.movies.objects;

import com.gdevelopers.movies.model.KObject;


public class MovieState extends KObject {
    private boolean isFavorite;
    private boolean isRated;
    private boolean isWatchlist;
    private float rating;

    public MovieState(long id) {
        super(id);
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public boolean isRated() {
        return isRated;
    }

    public void setRated(boolean rated) {
        isRated = rated;
    }

    public boolean isWatchlist() {
        return isWatchlist;
    }

    public void setWatchlist(boolean watchlist) {
        isWatchlist = watchlist;
    }
}
