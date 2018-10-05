package com.gdevelopers.movies.objects;

import com.gdevelopers.movies.helpers.MovieDB;
import com.gdevelopers.movies.model.KObject;
import com.google.gson.annotations.SerializedName;


public class User extends KObject {
    @SerializedName("avatar")
    private Avatar imageUrl;

    @SerializedName("username")
    private String username;

    public User(long id) {
        super(id);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImageUrl() {
        return MovieDB.PROFILE_IMAGE + imageUrl.hash.imageUrl + ".jpg?s=80";
    }

    private class Avatar {
        @SerializedName("gravatar")
        private Hash hash;
    }

    private class Hash {
        @SerializedName("hash")
        private String imageUrl;
    }
}
