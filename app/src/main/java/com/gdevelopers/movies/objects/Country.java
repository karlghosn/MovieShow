package com.gdevelopers.movies.objects;

import com.gdevelopers.movies.model.KObject;

public class Country extends KObject {
    private String name;

    public Country(long id) {
        super(id);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
