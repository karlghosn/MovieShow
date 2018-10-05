package com.gdevelopers.movies.mappers;

import com.gdevelopers.movies.helpers.MovieDB;
import com.gdevelopers.movies.model.JObjectMapper;
import com.gdevelopers.movies.model.KObject;
import com.gdevelopers.movies.model.Server;
import com.gdevelopers.movies.objects.SearchTrakt;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class JSearchTraktMapper extends JObjectMapper {

    public JSearchTraktMapper(Server server) {
        super(server);
    }

    public List<KObject> getTraktMovieId(String id) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("type", "movie");

        if (_loadedObjects.isEmpty() || !_loadedObjects.containsKey(id)) {
            return this.loadBaseUrl(MovieDB.TRAKT_BASE_URL, "search/tmdb/" + id, hashMap);
        }

        List<KObject> ret = new ArrayList<>();
        ret.add(_loadedObjects.get(id));

        return ret;
    }

    private KObject createObject() {
        return new SearchTrakt((long) 0);
    }

    @Override
    protected void setObjectAttribute(JSONObject jsonObject, JSONArray jArray, JSONObjectParser myParser) throws JSONException {
        SearchTrakt searchTrakt = (SearchTrakt) createObject();
        JSONObject containerJSON = (JSONObject) jArray.get(0);
        JSONObject movieJSON = (JSONObject) containerJSON.get("movie");
        JSONObject idJSON = (JSONObject) movieJSON.get("ids");
        searchTrakt.setMovieId(idJSON.getString("trakt"));
        myParser.objects().add(searchTrakt);
    }
}
