package com.gdevelopers.movies.model;

import java.net.CookieHandler;
import java.net.CookieManager;

class HttpServer extends Server {

    HttpServer() {
        super();
        CookieManager cookieManager = new CookieManager();
        CookieHandler.setDefault(cookieManager);
    }

    @Override
    public DataLoader getDataLoader() {
        return new HttpDataLoader(_server);
    }

}
