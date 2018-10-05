package com.gdevelopers.movies.objects;

import com.gdevelopers.movies.model.KObject;

public class SearchTrakt extends KObject {
    private String movieId;

    public SearchTrakt(long id) {
        super(id);
    }

    public String getMovieId() {
        return movieId;
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }
}
