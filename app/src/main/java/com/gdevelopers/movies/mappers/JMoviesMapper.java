package com.gdevelopers.movies.mappers;

import android.content.Context;

import com.gdevelopers.movies.R;
import com.gdevelopers.movies.helpers.MovieDB;
import com.gdevelopers.movies.helpers.PreferencesHelper;
import com.gdevelopers.movies.model.JObjectMapper;
import com.gdevelopers.movies.model.KObject;
import com.gdevelopers.movies.model.Server;
import com.gdevelopers.movies.objects.Movie;
import com.gdevelopers.movies.objects.Section;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class JMoviesMapper extends JObjectMapper {
    private final Context context;

    public JMoviesMapper(Context context, Server server) {
        super(server);
        this.context = context;
    }

    public List<KObject> getMovies(String genreId, String page, boolean more, boolean reload) {
        HashMap<String, String> hashMap = new HashMap<>();
        if (more)
            hashMap.put("page", page);

        hashMap.put("api_key", MovieDB.API_KEY);
        hashMap.put("language", PreferencesHelper.getLanguage(context));

        if (_loadedObjects.isEmpty() || reload) {
            return this.load("genre/" + genreId + "/movies", hashMap);
        }


        return new ArrayList<>(_loadedObjects.values());
    }


    private KObject createObject(long id) {
        return new Section(id);
    }

    @Override
    protected void setObjectAttribute(JSONObject jsonObject, JSONArray jArray, JSONObjectParser myParser) throws JSONException {
        JSONArray jsonArray;
        Section section;
        section = (Section) createObject(jsonObject.getInt("page"));
        section.setTotalPages(jsonObject.getInt("total_pages"));
        jsonArray = (JSONArray) jsonObject.get("results");
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
            section.getMovieList().add(movie);
        }
        myParser.objects().add(section);
    }
}
