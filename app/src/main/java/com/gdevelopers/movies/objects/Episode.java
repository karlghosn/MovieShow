package com.gdevelopers.movies.objects;

import com.gdevelopers.movies.model.KObject;
import com.gdevelopers.movies.objects.Cast;

import java.util.ArrayList;
import java.util.List;


public class Episode extends KObject {
    private String name;
    private String overview;
    private String season_number;
    private String stillPath;
    private Float voteAverage;
    private String voteCount;
    private String number;
    private List<Cast> castList;
    private String airDate;
    private List<String> images;

    public Episode(long id) {
        super(id);
    }

    public List<String> getImages() {
        if (images == null)
            images = new ArrayList<>();
        return images;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getSeason_number() {
        return season_number;
    }

    public void setSeason_number(String season_number) {
        this.season_number = season_number;
    }

    public String getStillPath() {
        return stillPath;
    }

    public void setStillPath(String stillPath) {
        this.stillPath = stillPath;
    }

    public Float getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(Float voteAverage) {
        this.voteAverage = voteAverage;
    }

    public String getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(String voteCount) {
        this.voteCount = voteCount;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public List<Cast> getCastList() {
        if (castList == null)
            castList = new ArrayList<>();
        return castList;
    }


    public String getAirDate() {
        return airDate;
    }

    public void setAirDate(String airDate) {
        this.airDate = airDate;
    }
}
