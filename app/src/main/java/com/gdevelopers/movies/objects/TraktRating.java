package com.gdevelopers.movies.objects;

import android.util.SparseIntArray;

import com.gdevelopers.movies.model.KObject;

public class TraktRating extends KObject {
    private String overallRating;
    private String votes;
    private SparseIntArray ratingsMap;

    public TraktRating(long id) {
        super(id);
    }

    public SparseIntArray getRatingsMap() {
        if (ratingsMap == null)
            ratingsMap = new SparseIntArray();
        return ratingsMap;
    }

    public String getOverallRating() {
        return overallRating;
    }

    public void setOverallRating(String overallRating) {
        this.overallRating = overallRating;
    }

    public String getVotes() {
        return votes;
    }

    public void setVotes(String votes) {
        this.votes = votes;
    }
}
