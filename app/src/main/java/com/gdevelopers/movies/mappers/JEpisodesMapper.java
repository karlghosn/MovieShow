package com.gdevelopers.movies.mappers;

import android.content.Context;

import com.gdevelopers.movies.R;
import com.gdevelopers.movies.helpers.MovieDB;
import com.gdevelopers.movies.helpers.PreferencesHelper;
import com.gdevelopers.movies.model.JObjectMapper;
import com.gdevelopers.movies.model.KObject;
import com.gdevelopers.movies.model.Server;
import com.gdevelopers.movies.objects.Cast;
import com.gdevelopers.movies.objects.Episode;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class JEpisodesMapper extends JObjectMapper {
    private final Context context;

    public JEpisodesMapper(Context context, Server server) {
        super(server);
        this.context = context;
    }

    public List<KObject> getEpisodes(String id, String number, boolean reload) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("api_key", MovieDB.API_KEY);
        hashMap.put("language", PreferencesHelper.getLanguage(context));

        if (_loadedObjects.isEmpty() || reload) {
            return this.load("tv/" + id + "/season/" + number, hashMap);
        }

        return new ArrayList<>(_loadedObjects.values());
    }

    private KObject createObject(long id) {
        return new Episode(id);
    }

    @Override
    protected void setObjectAttribute(JSONObject jsonObject, JSONArray jArray, JSONObjectParser myParser) throws JSONException {
        JSONArray episodesArray = (JSONArray) jsonObject.get("episodes");
        int episodesLength = episodesArray.length();

        for (int i = 0; i < episodesLength; i++) {
            JSONObject episodesJSON = (JSONObject) episodesArray.get(i);
            Episode episode = (Episode) createObject(episodesJSON.getInt("id"));
            episode.setAirDate(episodesJSON.getString("air_date"));
            episode.setNumber(episodesJSON.getString("episode_number"));
            episode.setName(episodesJSON.getString("name"));
            episode.setOverview(episodesJSON.getString("overview"));
            episode.setSeason_number(episodesJSON.getString("season_number"));
            episode.setStillPath(episodesJSON.getString("still_path"));
            episode.setVoteAverage((float) episodesJSON.getInt("vote_average"));
            episode.setVoteCount(episodesJSON.getString("vote_count"));

            JSONArray castsArray = (JSONArray) episodesJSON.get("guest_stars");
            int castsLength = castsArray.length();
            for (int j = 0; j < castsLength; j++) {
                JSONObject castJSON = (JSONObject) castsArray.get(j);
                Cast cast = new Cast();
                cast.setCharacter(castJSON.getString("character"));
                cast.setCredit_id(castJSON.getString("credit_id"));
                cast.setName(castJSON.getString("name"));
                cast.setId(castJSON.getString("id"));
                cast.setOrder(castJSON.getString("order"));
                cast.setProfile_path(MovieDB.IMAGE_URL + context.getResources().getString(R.string.imageSize) + castJSON.getString("profile_path"));
                episode.getCastList().add(cast);
            }

            myParser.objects().add(episode);
        }
    }
}
