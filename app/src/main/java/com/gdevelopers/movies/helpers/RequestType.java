package com.gdevelopers.movies.helpers;


public class RequestType {

    public static int getRequestType(String type) {
        switch (type) {
            case "upcoming":
                return Constants.UPCOMING;
            case "now_playing":
                return Constants.NOW_PLAYING;
            case "popular":
                return Constants.POPULAR;
            case "top_rated":
                return Constants.TOP_RATED;
        }
        return Constants.UPCOMING;
    }

    public static int getTVRequestType(String type) {
        switch (type) {
            case "on_air":
                return Constants.ON_THE_AIR;
            case "airing_today":
                return Constants.AIRING_TODAY;
            case "popular":
                return Constants.TV_POPULAR;
            case "top_rated":
                return Constants.TV_TOP_RATED;
        }
        return Constants.ON_THE_AIR;
    }
}
