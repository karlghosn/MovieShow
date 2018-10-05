package com.gdevelopers.movies.objects;

import com.gdevelopers.movies.model.KObject;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;


public class TVShow extends KObject {
    private String backdropPath;
    private String firstAirDate;
    private String lastAirDate;
    private List<String> networks;
    private String numberOfSeasons;
    @SerializedName("name")
    private String name;
    private String overview;
    @SerializedName("poster_path")
    private String posterPath;
    private List<Company> companies;
    private List<Season> seasonList;
    private String status;
    private String type;
    private Float voteAverage;
    private String voteCount;
    private List<String> images;
    private List<Genre> genreList;
    private List<Cast> castList;
    private List<Trailer> trailerList;
    private List<TVShow> relatedShows;
    private String createdBy;
    @SerializedName("id")
    private int id;

    public TVShow(long id) {
        super(id);
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public int getId() {
        return id;
    }

    public List<TVShow> getRelatedShows() {
        if (relatedShows == null)
            relatedShows = new ArrayList<>();
        return relatedShows;
    }

    public List<Trailer> getTrailerList() {
        if (trailerList == null)
            trailerList = new ArrayList<>();
        return trailerList;
    }

    public List<Cast> getCastList() {
        if (castList == null)
            castList = new ArrayList<>();
        return castList;
    }

    public List<Genre> getGenreList() {
        if (genreList == null)
            genreList = new ArrayList<>();
        return genreList;
    }

    public List<String> getNetworks() {
        if (networks == null)
            networks = new ArrayList<>();
        return networks;
    }

    public List<Company> getCompanies() {
        if (companies == null)
            companies = new ArrayList<>();
        return companies;
    }

    public List<Season> getSeasonList() {
        if (seasonList == null)
            seasonList = new ArrayList<>();
        return seasonList;
    }

    public List<String> getImages() {
        if (images == null)
            images = new ArrayList<>();
        return images;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }

    public String getFirstAirDate() {
        return firstAirDate;
    }

    public void setFirstAirDate(String firstAirDate) {
        this.firstAirDate = firstAirDate;
    }

    public String getLastAirDate() {
        return lastAirDate;
    }

    public void setLastAirDate(String lastAirDate) {
        this.lastAirDate = lastAirDate;
    }

    public String getNumberOfSeasons() {
        return numberOfSeasons;
    }

    public void setNumberOfSeasons(String numberOfSeasons) {
        this.numberOfSeasons = numberOfSeasons;
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

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Float getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(Float vote_average) {
        this.voteAverage = vote_average;
    }

    public String getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(String vote_count) {
        this.voteCount = vote_count;
    }
}
