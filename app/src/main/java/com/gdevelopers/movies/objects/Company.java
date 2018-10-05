package com.gdevelopers.movies.objects;

import com.gdevelopers.movies.model.KObject;

public class Company extends KObject {
    private String name;
    private String headquarters;

    public Company(long id) {
        super(id);
    }

    public String getHeadquarters() {
        return headquarters;
    }

    public void setHeadquarters(String headquarters) {
        this.headquarters = headquarters;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
