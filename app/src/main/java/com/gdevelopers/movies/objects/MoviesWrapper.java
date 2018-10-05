package com.gdevelopers.movies.objects;

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

    public void setMovies(List<Movie> movies) {
        this.movies = movies;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }
}
