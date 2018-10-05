package com.gdevelopers.movies.objects;

import com.gdevelopers.movies.model.KObject;


public class Token extends KObject {
    private String requestToken;

    public Token(long id) {
        super(id);
    }

    public String getRequestToken() {
        return requestToken;
    }

    public void setRequestToken(String requestToken) {
        this.requestToken = requestToken;
    }
}
