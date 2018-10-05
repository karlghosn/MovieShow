package com.gdevelopers.movies.objects;

import com.gdevelopers.movies.model.KObject;

import java.util.ArrayList;
import java.util.List;


public class Collection extends KObject {
    private String name;
    private String backdrop;
    private List<Movie> movieList;
    private String overview;

    public Collection(long id) {
        super(id);
    }

    public List<Movie> getMovieList() {
        if (movieList == null)
            movieList = new ArrayList<>();
        return movieList;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBackdrop() {
        return backdrop;
    }

    public void setBackdrop(String backdrop) {
        this.backdrop = backdrop;
    }
}
