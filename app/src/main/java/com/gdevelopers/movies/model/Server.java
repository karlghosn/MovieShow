package com.gdevelopers.movies.model;


import com.gdevelopers.movies.helpers.MovieDB;

abstract public class Server {
    final String _server;

    Server(){
        _server = MovieDB.url;
    }

    public abstract DataLoader getDataLoader();
}
