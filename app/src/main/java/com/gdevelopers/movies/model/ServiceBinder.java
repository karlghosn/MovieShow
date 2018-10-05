package com.gdevelopers.movies.model;

import android.os.Binder;


public class ServiceBinder extends Binder {
    private final ModelService service;

    ServiceBinder(ModelService s){
        service=s;
    }

    public ModelService getService(){
        return service;
    }
}
