package com.gdevelopers.movies.mappers;

import android.content.Context;

import com.gdevelopers.movies.helpers.MovieDB;
import com.gdevelopers.movies.helpers.PreferencesHelper;
import com.gdevelopers.movies.model.JObjectMapper;
import com.gdevelopers.movies.model.KObject;
import com.gdevelopers.movies.model.Server;
import com.gdevelopers.movies.objects.Genre;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class JGenreMapper extends JObjectMapper {


    public JGenreMapper(Server server) {
        super(server);
    }

    public List<KObject> getGenres(Context context, boolean reload) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("api_key", MovieDB.API_KEY);
        hashMap.put("language", PreferencesHelper.getLanguage(context));

        if (_loadedObjects.isEmpty() || reload) {
            return this.load("genre/movie/list", hashMap);
        }

        return new ArrayList<>(_loadedObjects.values());
    }

    private KObject createObject(long id) {
        return new Genre(id);
    }

    @Override
    protected void setObjectAttribute(JSONObject jsonObject, JSONArray jArray, JSONObjectParser myParser) throws JSONException {
        JSONArray genresArray = (JSONArray) jsonObject.get("genres");
        int genresLength = genresArray.length();

        for (int i = 0; i < genresLength; i++) {
            JSONObject object = (JSONObject) genresArray.get(i);
            Genre genre = (Genre) createObject(object.getInt("id"));
            genre.setName(object.getString("name"));
            myParser.objects().add(genre);
        }
    }
}
