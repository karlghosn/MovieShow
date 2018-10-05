package com.gdevelopers.movies.helpers;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class PostRetrofit {
    @SerializedName("media_id")
    @Expose
    private Integer id;
    @SerializedName("value")
    @Expose
    private Float value;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("language")
    @Expose
    private String language;

    @SuppressWarnings("unused")
    public Float getValue() {
        return value;
    }

    public void setValue(Float value) {
        this.value = value;
    }

    @SuppressWarnings("unused")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @SuppressWarnings("unused")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @SuppressWarnings("unused")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @SuppressWarnings("unused")
    public String getLanguage() {
        return language;
    }

    public void setLanguage() {
        this.language = "en";
    }
}
