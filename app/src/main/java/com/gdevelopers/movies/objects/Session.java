package com.gdevelopers.movies.objects;

import com.gdevelopers.movies.model.KObject;


public class Session extends KObject {
    private String session;

    public Session(long id) {
        super(id);
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }
}
