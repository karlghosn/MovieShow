package com.gdevelopers.movies.mappers;

import android.content.Context;

import com.gdevelopers.movies.R;
import com.gdevelopers.movies.helpers.MovieDB;
import com.gdevelopers.movies.helpers.PreferencesHelper;
import com.gdevelopers.movies.model.JObjectMapper;
import com.gdevelopers.movies.model.KObject;
import com.gdevelopers.movies.model.Server;
import com.gdevelopers.movies.objects.Collection;
import com.gdevelopers.movies.objects.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class JCollectionMapper extends JObjectMapper {
    private final Context context;

    public JCollectionMapper(Context context, Server server) {
        super(server);
        this.context = context;
    }

    public List<KObject> getMovieCollection(String id) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("api_key", MovieDB.API_KEY);
        hashMap.put("language", PreferencesHelper.getLanguage(context));
        if (_loadedObjects.isEmpty() || !_loadedObjects.containsKey(id))
            return this.load("collection/" + id, hashMap);

        List<KObject> ret = new ArrayList<>();
        ret.add(_loadedObjects.get(id));
        return ret;
    }


    private KObject createObject(long id) {
        return new Collection(id);
    }

    @Override
    protected void setObjectAttribute(JSONObject jsonObject, JSONArray jArray, JSONObjectParser myParser) throws JSONException {
        Collection collection = (Collection) createObject(jsonObject.getInt("id"));
        collection.setName(jsonObject.getString("name"));
        collection.setOverview(jsonObject.getString("overview"));

        JSONArray jsonArray = (JSONArray) jsonObject.get("parts");
        int length = jsonArray.length();
        for (int i = 0; i < length; i++) {
            JSONObject movieJSON = (JSONObject) jsonArray.get(i);
            Movie movie = new Movie(movieJSON.getInt("id"));
            movie.setTitle(movieJSON.getString("title"));
            movie.setType("movie");
            movie.setReleaseDate(movieJSON.getString("release_date"));
            movie.setVoteAverage(movieJSON.getDouble("vote_average"));
            movie.setPosterPath(MovieDB.IMAGE_URL + context.getResources().getString(R.string.imageSize) + movieJSON.getString("poster_path"));
            movie.setBackdropPath(MovieDB.IMAGE_URL + context.getResources().getString(R.string.galleryImgSize) + movieJSON.getString("backdrop_path"));
            collection.getMovieList().add(movie);
        }
        myParser.objects().add(collection);
    }
}
