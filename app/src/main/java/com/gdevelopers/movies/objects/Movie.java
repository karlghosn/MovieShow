package com.gdevelopers.movies.objects;

import com.gdevelopers.movies.model.KObject;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;


public class Movie extends KObject {
    @SerializedName("title")
    private String title;
    @SerializedName("poster_path")
    private String posterPath;
    private String type;
    private Double voteAverage;
    private String backdropPath;
    private String budget;
    private String imdbId;
    private String movieId;
    private String overview;
    private List<Company> companyList;
    private List<Country> countryList;
    @SerializedName("release_date")
    private String releaseDate;
    private String runTime;
    private String status;
    private String tagLine;
    private String homePage;
    private String voteCount;
    private List<Trailer> trailerList;
    private List<Cast> castList;
    private List<String> imagesList;
    private List<String> backdropList;
    private List<Movie> relatedMovies;
    private List<Review> reviewList;
    private List<Crew> crewList;
    private String revenue;
    private Collection collection;
    @SerializedName("id")
    private int id;

    public Movie(long id) {
        super(id);
    }

    public int getId() {
        return id;
    }

    public Collection getCollection() {
        return collection;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public void setCollection(Collection collection) {
        this.collection = collection;
    }

    public String getHomePage() {
        return homePage;
    }

    public String getMovieId() {
        return movieId;
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }

    public List<String> getBackdropList() {
        if (backdropList == null)
            backdropList = new ArrayList<>();
        return backdropList;
    }

    public void setHomePage(String homePage) {
        this.homePage = homePage;
    }

    public String getRevenue() {
        return revenue;
    }

    public void setRevenue(String revenue) {
        this.revenue = revenue;
    }

    public List<Crew> getCrewList() {
        if (crewList == null)
            crewList = new ArrayList<>();
        return crewList;
    }

    public List<Movie> getRelatedMovies() {
        if (relatedMovies == null)
            relatedMovies = new ArrayList<>();

        return relatedMovies;
    }

    public List<Review> getReviewList() {
        if (reviewList == null)
            reviewList = new ArrayList<>();
        return reviewList;
    }

    public List<String> getImagesList() {
        if (imagesList == null)
            imagesList = new ArrayList<>();

        return imagesList;
    }

    public List<Cast> getCastList() {
        if (castList == null)
            castList = new ArrayList<>();

        return castList;
    }

    public List<Trailer> getTrailerList() {
        if (trailerList == null)
            trailerList = new ArrayList<>();

        return trailerList;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTagLine() {
        return tagLine;
    }

    public void setTagLine(String tagLine) {
        this.tagLine = tagLine;
    }

    public String getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(String voteCount) {
        this.voteCount = voteCount;
    }


    public String getRunTime() {
        return runTime;
    }

    public void setRunTime(String runTime) {
        this.runTime = runTime;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }


    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }

    public String getBudget() {
        return budget;
    }


    public String getImdbId() {
        return imdbId;
    }

    public void setImdbId(String imdbId) {
        this.imdbId = imdbId;
    }

    public List<Country> getCountryList() {
        if (countryList == null)
            countryList = new ArrayList<>();
        return countryList;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public void setBudget(String budget) {
        this.budget = budget;
    }


    public List<Company> getCompanyList() {
        if (companyList == null)
            companyList = new ArrayList<>();
        return companyList;
    }

    public Double getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(Double voteAverage) {
        this.voteAverage = voteAverage;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }
}
