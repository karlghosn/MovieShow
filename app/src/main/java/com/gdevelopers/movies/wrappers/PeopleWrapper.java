package com.gdevelopers.movies.wrappers;

import com.gdevelopers.movies.objects.Actor;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PeopleWrapper {
    @SerializedName("results")
    private List<Actor> actorList;
    @SerializedName("page")
    private int page;
    @SerializedName("total_pages")
    private int totalPages;

    public List<Actor> getActorList() {
        return actorList;
    }

    public int getPage() {
        return page;
    }

    public int getTotalPages() {
        return totalPages;
    }
}
