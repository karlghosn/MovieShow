package com.gdevelopers.movies.objects;

import com.gdevelopers.movies.model.KObject;


public class Genre extends KObject {
    private String name;

    public Genre(long id) {
        super(id);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
