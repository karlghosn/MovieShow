package com.gdevelopers.movies.objects;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class OMDb {
    @SerializedName("Genre")
    private String genre;
    @SerializedName("Ratings")
    private List<OMDBRating> ratingList;

    public List<OMDBRating> getRatingList() {
        if (ratingList == null)
            ratingList = new ArrayList<>();
        return ratingList;
    }

    public String getGenre() {
        return genre;
    }

    @SuppressWarnings("unused")
    public void setGenre(String genre) {
        this.genre = genre;
    }
}
