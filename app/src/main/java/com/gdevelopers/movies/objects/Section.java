package com.gdevelopers.movies.objects;

import com.gdevelopers.movies.model.KObject;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;


public class Section extends KObject {
    private List<TVShow> tvShowList;
    private List<Actor> actorList;
    private List<UserList> userLists;
    @SerializedName("results")
    private List<Movie> movieList;
    private int page;
    private int totalPages;

    public Section(long id) {
        super(id);
    }

    @SuppressWarnings("unused")
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

    public List<UserList> getUserLists() {
        if (userLists == null)
            userLists = new ArrayList<>();
        return userLists;
    }

    public List<Actor> getActorList() {
        if (actorList == null)
            actorList = new ArrayList<>();
        return actorList;
    }

    public List<TVShow> getTvShowList() {
        if (tvShowList == null)
            tvShowList = new ArrayList<>();
        return tvShowList;
    }

    public List<Movie> getMovieList() {
        if (movieList == null)
            movieList = new ArrayList<>();
        return movieList;
    }
}
