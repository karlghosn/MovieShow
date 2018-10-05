package com.gdevelopers.movies.mappers;

import com.gdevelopers.movies.helpers.MovieDB;
import com.gdevelopers.movies.model.JObjectMapper;
import com.gdevelopers.movies.model.KObject;
import com.gdevelopers.movies.model.Server;
import com.gdevelopers.movies.objects.MovieState;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;


public class JUserMovieStateMapper extends JObjectMapper {

    public JUserMovieStateMapper(Server server) {
        super(server);
    }

    public List<KObject> getMovieState(String movieId, String sessionId) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("api_key", MovieDB.API_KEY);
        hashMap.put("session_id", sessionId);

        return this.load("movie/" + movieId + "/account_states", hashMap);


    }

    private KObject createObject(long id) {
        return new MovieState(id);
    }

    @Override
    protected void setObjectAttribute(JSONObject jsonObject, JSONArray jArray, JSONObjectParser myParser) throws JSONException {
        KObject object = createObject(jsonObject.getInt("id"));
        MovieState movieState = (MovieState) object;
        movieState.setFavorite(jsonObject.getBoolean("favorite"));
        if (jsonObject.get("rated") instanceof Boolean)
            movieState.setRated(jsonObject.getBoolean("rated"));
        else {
            movieState.setRated(true);
            JSONObject ratingJSON = (JSONObject) jsonObject.get("rated");
            float rating = (float) ratingJSON.getDouble("value");
            movieState.setRating(rating);
        }
        movieState.setWatchlist(jsonObject.getBoolean("watchlist"));
        myParser.objects().add(movieState);
    }
}
