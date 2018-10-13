package com.gdevelopers.movies.objects;

import com.gdevelopers.movies.model.KObject;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Actor extends KObject {
    @SerializedName("id")
    private int id;
    private String biography;
    private String birthday;
    private String deathday;
    private String aka;
    @SerializedName("name")
    private String name;
    private String type;
    private String placeOfBirth;
    @SerializedName("profile_path")
    private String profilePath;
    private List<String> images;
    private List<Show> crewList;
    private List<Show> movieList;
    private List<Show> tvList;
    private List<String> taggedImages;

    public Actor(long id) {
        super(id);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getTaggedImages() {
        if (taggedImages == null)
            taggedImages = new ArrayList<>();
        return taggedImages;
    }

    public String getAka() {
        return aka;
    }

    public void setAka(String aka) {
        this.aka = aka;
    }

    public List<Show> getMovieList() {
        if (movieList == null)
            movieList = new ArrayList<>();
        return movieList;
    }

    public int getId() {
        return id;
    }

    public List<Show> getTvList() {
        if (tvList == null)
            tvList = new ArrayList<>();
        return tvList;
    }

    public List<Show> getCrewList() {
        if (crewList == null)
            crewList = new ArrayList<>();
        return crewList;
    }

    public List<String> getImages() {
        if (images == null)
            images = new ArrayList<>();

        return images;
    }

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getDeathday() {
        return deathday;
    }

    public void setDeathday(String deathday) {
        this.deathday = deathday;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlaceOfBirth() {
        return placeOfBirth;
    }

    public void setPlaceOfBirth(String placeOfBirth) {
        this.placeOfBirth = placeOfBirth;
    }

    public String getProfilePath() {
        return profilePath;
    }

    public void setProfilePath(String profilePath) {
        this.profilePath = profilePath;
    }
}
