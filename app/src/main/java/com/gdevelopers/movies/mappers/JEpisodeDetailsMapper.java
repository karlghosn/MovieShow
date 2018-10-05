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


public class JEpisodeDetailsMapper extends JObjectMapper {
    private final Context context;

    public JEpisodeDetailsMapper(Context context, Server server) {
        super(server);
        this.context = context;
    }

    public List<KObject> getEpisodeDetails(String id, String number, String episodeNumber, boolean reload) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("api_key", MovieDB.API_KEY);
        hashMap.put("append_to_response", "images,videos");
        hashMap.put("language", PreferencesHelper.getLanguage(context));

        if (_loadedObjects.isEmpty() || reload) {
            return this.load("tv/" + id + "/season/" + number + "/episode/" + episodeNumber, hashMap);
        }

        return new ArrayList<>(_loadedObjects.values());
    }

    private KObject createObject(long id) {
        return new Episode(id);
    }

    @Override
    protected void setObjectAttribute(JSONObject jsonObject, JSONArray jArray, JSONObjectParser myParser) throws JSONException {
        Episode episode = (Episode) createObject(jsonObject.getInt("id"));
        episode.setNumber(jsonObject.getString("episode_number"));
        episode.setAirDate(jsonObject.getString("air_date"));
        episode.setName(jsonObject.getString("name"));
        episode.setOverview(jsonObject.getString("overview"));
        episode.setSeason_number(jsonObject.getString("season_number"));
        episode.setStillPath(jsonObject.getString("still_path"));
        episode.setVoteAverage((float) jsonObject.getDouble("vote_average"));
        episode.setVoteCount(jsonObject.getString("vote_count"));

        JSONObject imagesJSON = (JSONObject) jsonObject.get("images");
        JSONArray stillsArray = (JSONArray) imagesJSON.get("stills");
        int stillsLength = stillsArray.length();

        for (int i = 0; i < stillsLength; i++) {
            JSONObject object = (JSONObject) stillsArray.get(i);
            episode.getImages().add(object.getString("file_path"));
        }

        JSONArray castsArray = (JSONArray) jsonObject.get("guest_stars");
        int castsLength = castsArray.length();
        for (int i = 0; i < castsLength; i++) {
            JSONObject castJSON = (JSONObject) castsArray.get(i);
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
