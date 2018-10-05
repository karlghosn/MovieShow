package com.gdevelopers.movies.model;

public class KObject {
    private final long _id;

    protected KObject(long id){
        _id= id;
    }

    public long id(){
        return _id;
    }

}
