package com.gdevelopers.movies.wrappers;

import com.gdevelopers.movies.objects.TVShow;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TVShowWrapper {
    @SerializedName("results")
    private List<TVShow> tvShows;
    @SerializedName("page")
    private int page;
    @SerializedName("total_pages")
    private int totalPages;

    public List<TVShow> getTvShows() {
        return tvShows;
    }

    public int getPage() {
        return page;
    }

    public int getTotalPages() {
        return totalPages;
    }
}
