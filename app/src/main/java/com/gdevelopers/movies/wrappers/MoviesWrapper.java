package com.gdevelopers.movies.wrappers;

import com.gdevelopers.movies.objects.Movie;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MoviesWrapper {
    @SerializedName("results")
    private List<Movie> movies;
    @SerializedName("page")
    private int page;
    @SerializedName("total_pages")
    private int totalPages;

    public List<Movie> getMovies() {
        return movies;
    }

    public int getPage() {
        return page;
    }

    public int getTotalPages() {
        return totalPages;
    }

}
