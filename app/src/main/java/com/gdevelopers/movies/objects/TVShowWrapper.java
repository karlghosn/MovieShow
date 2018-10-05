package com.gdevelopers.movies.objects;

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

    public void setTvShows(List<TVShow> tvShows) {
        this.tvShows = tvShows;
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
