package com.gdevelopers.movies.mappers;

import android.content.Context;

import com.gdevelopers.movies.R;
import com.gdevelopers.movies.helpers.MovieDB;
import com.gdevelopers.movies.model.JObjectMapper;
import com.gdevelopers.movies.model.KObject;
import com.gdevelopers.movies.model.Server;
import com.gdevelopers.movies.objects.Actor;
import com.gdevelopers.movies.objects.Section;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class JPopularActors extends JObjectMapper {
    private final Context context;

    public JPopularActors(Context context, Server server) {
        super(server);
        this.context = context;
    }

    public List<KObject> getPopularActors(String page, boolean more, boolean reload) {
        HashMap<String, String> hashMap = new HashMap<>();
        if (more)
            hashMap.put("page", page);

        hashMap.put("api_key", MovieDB.API_KEY);

        if (_loadedObjects.isEmpty() || reload) {
            return this.load("person/popular", hashMap);
        }
        return new ArrayList<>(_loadedObjects.values());
    }


    private KObject createObject(long id) {
        return new Section(id);
    }

    @Override
    protected void setObjectAttribute(JSONObject jsonObject, JSONArray jArray, JSONObjectParser myParser) throws JSONException {
        Section section = (Section) createObject(jsonObject.getInt("page"));
        section.setTotalPages(jsonObject.getInt("total_pages"));
        JSONArray jsonArray = (JSONArray) jsonObject.get("results");

        int length = jsonArray.length();
        for (int i = 0; i < length; i++) {
            JSONObject castJSON = (JSONObject) jsonArray.get(i);
            Actor actor = new Actor(castJSON.getInt("id"));
            actor.setName(castJSON.getString("name"));
            /*String popularity = new DecimalFormat("##.##").format(castJSON.getDouble("popularity"));
            actor.setPopularity(popularity);*/
            actor.setType("movie");
            actor.setProfilePath(MovieDB.IMAGE_URL + context.getResources().getString(R.string.imageSize) + castJSON.getString("profile_path"));
            section.getActorList().add(actor);
        }
        myParser.objects().add(section);

    }
}
