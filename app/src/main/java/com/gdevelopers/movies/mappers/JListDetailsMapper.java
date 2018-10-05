package com.gdevelopers.movies.mappers;

import android.content.Context;

import com.gdevelopers.movies.R;
import com.gdevelopers.movies.helpers.MovieDB;
import com.gdevelopers.movies.helpers.PreferencesHelper;
import com.gdevelopers.movies.helpers.Response;
import com.gdevelopers.movies.model.JObjectMapper;
import com.gdevelopers.movies.model.KObject;
import com.gdevelopers.movies.model.Server;
import com.gdevelopers.movies.objects.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class JListDetailsMapper extends JObjectMapper {

    private final Context context;

    public JListDetailsMapper(Context context, Server server) {
        super(server);
        this.context = context;
    }

    public List<KObject> getListDetails(String listId) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("api_key", MovieDB.API_KEY);
        hashMap.put("language", PreferencesHelper.getLanguage(context));

        if (_loadedObjects.isEmpty() || !_loadedObjects.containsKey(listId)) {
            return this.load("list/" + listId, hashMap);
        }
        List<KObject> ret = new ArrayList<>();
        ret.add(_loadedObjects.get(listId));
        return ret;
    }

    public List<KObject> checkItemStatus(String listId, String movieId) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("api_key", MovieDB.API_KEY);
        hashMap.put("movie_id", movieId);

        return this.load("list/" + listId + "/item_status", hashMap);
    }

    private KObject createObject(long id) {
        return new Movie(id);
    }

    @Override
    protected void setObjectAttribute(JSONObject jsonObject, JSONArray jArray, JSONObjectParser myParser) throws JSONException {
        if (myParser.getCommand().contains("item_status")) {
            Response response = new Response();
            response.setResponse(jsonObject.getBoolean("item_present"));
            myParser.objects().add(response);
        } else {
            JSONArray jsonArray = (JSONArray) jsonObject.get("items");
            int length = jsonArray.length();
            for (int i = 0; i < length; i++) {
                JSONObject movieJSON = (JSONObject) jsonArray.get(i);
                KObject object = createObject((long) movieJSON.getInt("id"));
                Movie movie = (Movie) object;
                if (movieJSON.has("title"))
                    movie.setTitle(movieJSON.getString("title"));
                movie.setType("movie");
                movie.setOverview(movieJSON.getString("overview"));
                if (movieJSON.has("release_date"))
                    movie.setReleaseDate(movieJSON.getString("release_date"));
                movie.setVoteAverage(movieJSON.getDouble("vote_average"));
                movie.setPosterPath(MovieDB.IMAGE_URL + context.getResources().getString(R.string.galleryImgSize) + movieJSON.getString("poster_path"));
                movie.setBackdropPath(MovieDB.IMAGE_URL + context.getResources().getString(R.string.galleryImgSize) + movieJSON.getString("backdrop_path"));
                myParser.objects().add(movie);
            }
        }

    }
}
