package com.gdevelopers.movies.mappers;

import android.content.Context;

import com.gdevelopers.movies.R;
import com.gdevelopers.movies.helpers.MovieDB;
import com.gdevelopers.movies.helpers.PreferencesHelper;
import com.gdevelopers.movies.model.JObjectMapper;
import com.gdevelopers.movies.model.KObject;
import com.gdevelopers.movies.model.Server;
import com.gdevelopers.movies.objects.Search;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;


public class JSearchMapper extends JObjectMapper {

    private final Context context;

    public JSearchMapper(Context context, Server server) {
        super(server);
        this.context = context;
    }

    public List<KObject> getSearch(String type, String query) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("query", query);
        hashMap.put("api_key", MovieDB.API_KEY);
        hashMap.put("language", PreferencesHelper.getLanguage(context));

        return this.load("search/" + type, hashMap);
    }

    public List<KObject> getActors(String query) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("query", query);
        hashMap.put("api_key", MovieDB.API_KEY);
        hashMap.put("language", PreferencesHelper.getLanguage(context));

        return this.load("search/person", hashMap);
    }


    private KObject createObject(long id) {
        return new Search(id);
    }

    @Override
    protected void setObjectAttribute(JSONObject jsonObject, JSONArray jArray, JSONObjectParser myParser) throws JSONException {
        JSONArray jsonArray = (JSONArray) jsonObject.get("results");

        int length = jsonArray.length();
        for (int i = 0; i < length; i++) {
            JSONObject movieJSON = (JSONObject) jsonArray.get(i);
            KObject object = createObject((long) movieJSON.getInt("id"));
            Search search = (Search) object;
            if (movieJSON.has("media_type"))
                search.setMediaType(movieJSON.getString("media_type"));

            if (movieJSON.has("name"))
                search.setTitle(movieJSON.getString("name"));
            else if (movieJSON.has("original_name"))
                search.setTitle(movieJSON.getString("original_name"));
            else if (movieJSON.has("original_title"))
                search.setTitle(movieJSON.getString("original_title"));

            if (movieJSON.has("poster_path"))
                search.setPosterPath(MovieDB.IMAGE_URL + context.getResources().getString(R.string.imageSize) + movieJSON.getString("poster_path"));
            else if (movieJSON.has("profile_path"))
                search.setPosterPath(MovieDB.IMAGE_URL + context.getResources().getString(R.string.imageSize) + movieJSON.getString("profile_path"));
            else if (movieJSON.has("logo_path"))
                search.setPosterPath(MovieDB.IMAGE_URL + context.getResources().getString(R.string.backDropImgSize) + movieJSON.getString("logo_path"));

            if (movieJSON.has("vote_average"))
                search.setVoteAverage((float) movieJSON.getInt("vote_average"));

            if (movieJSON.has("first_air_date"))
                search.setReleaseDate(movieJSON.getString("first_air_date"));
            else if (movieJSON.has("release_date"))
                search.setReleaseDate(movieJSON.getString("release_date"));
            else search.setReleaseDate("");


            myParser.objects().add(search);
        }
    }
}
