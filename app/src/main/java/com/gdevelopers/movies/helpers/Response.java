package com.gdevelopers.movies.helpers;

import com.gdevelopers.movies.model.KObject;
import com.google.gson.annotations.SerializedName;


public class Response extends KObject {
    private boolean response;
    @SerializedName("status_message")
    private String message;

    @SuppressWarnings("unused")
    public void setMessage(String message) {
        this.message = message;
    }

    public Response() {
        super((long) 0);
    }

    public String getMessage() {
        return message;
    }

    public boolean isResponse() {
        return response;
    }

    public void setResponse(boolean response) {
        this.response = response;
    }

}
