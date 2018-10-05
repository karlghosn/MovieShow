package com.gdevelopers.movies.objects;

import com.google.gson.annotations.SerializedName;


public class OMDBRating {
    @SerializedName("Source")
    private String source;

    @SerializedName("Value")
    private String value;

    public String getSource() {
        return source;
    }

    public String getValue() {
        return value;
    }

    @SuppressWarnings("unused")
    public void setValue(String value) {
        this.value = value;
    }

    @SuppressWarnings("unused")
    public void setSource(String source) {
        this.source = source;
    }
}
