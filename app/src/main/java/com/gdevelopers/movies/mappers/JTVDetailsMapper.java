package com.gdevelopers.movies.mappers;

import android.content.Context;

import com.gdevelopers.movies.R;
import com.gdevelopers.movies.helpers.MovieDB;
import com.gdevelopers.movies.helpers.PreferencesHelper;
import com.gdevelopers.movies.model.JObjectMapper;
import com.gdevelopers.movies.model.KObject;
import com.gdevelopers.movies.model.Server;
import com.gdevelopers.movies.objects.Cast;
import com.gdevelopers.movies.objects.Company;
import com.gdevelopers.movies.objects.Genre;
import com.gdevelopers.movies.objects.Season;
import com.gdevelopers.movies.objects.Trailer;
import com.gdevelopers.movies.objects.TVShow;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class JTVDetailsMapper extends JObjectMapper {
    private final Context context;

    public JTVDetailsMapper(Context context, Server server) {
        super(server);
        this.context = context;
    }

    public List<KObject> getTVShowDetails(String id) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("api_key", MovieDB.API_KEY);
        hashMap.put("append_to_response", "images,videos,credits,similar,external_ids&include_image_language=en,null");
        hashMap.put("language", PreferencesHelper.getLanguage(context));

        if (_loadedObjects.isEmpty() || !_loadedObjects.containsKey(id)) {
            return this.load("tv/" + id, hashMap);
        }

        List<KObject> ret = new ArrayList<>();
        ret.add(_loadedObjects.get(id));

        return ret;
    }

    private KObject createObject(long id) {
        return new TVShow(id);
    }

    @Override
    protected void setObjectAttribute(JSONObject jsonObject, JSONArray jArray, JSONObjectParser myParser) throws JSONException {
        TVShow tvShow = (TVShow) createObject(jsonObject.getInt("id"));
        tvShow.setBackdropPath(MovieDB.IMAGE_URL + context.getString(R.string.backDropImgSize) + jsonObject.getString("backdrop_path"));
        tvShow.setFirstAirDate(jsonObject.getString("first_air_date"));

        JSONArray createdByArray = (JSONArray) jsonObject.get("created_by");
        int createdByLength = createdByArray.length();

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < createdByLength; i++) {
            JSONObject createdByJSON = (JSONObject) createdByArray.get(i);
            builder.append(createdByJSON.getString("name"));
            if (i < createdByLength - 1)
                builder.append(", ");
        }
        tvShow.setCreatedBy("Created by " + builder.toString());

        JSONArray genresArray = (JSONArray) jsonObject.get("genres");
        int genresLength = genresArray.length();

        for (int i = 0; i < genresLength; i++) {
            JSONObject genreJSON = (JSONObject) genresArray.get(i);
            Genre genre = new Genre(genreJSON.getInt("id"));
            genre.setName(genreJSON.getString("name"));
            tvShow.getGenreList().add(genre);
        }

        tvShow.setLastAirDate(jsonObject.getString("last_air_date"));
        tvShow.setName(jsonObject.getString("name"));

        JSONArray networksArray = (JSONArray) jsonObject.get("networks");
        int networksLength = networksArray.length();

        for (int i = 0; i < networksLength; i++) {
            JSONObject networkJSON = (JSONObject) networksArray.get(i);
            tvShow.getNetworks().add(networkJSON.getString("name"));
        }

        tvShow.setNumberOfSeasons(jsonObject.getString("number_of_seasons"));
        tvShow.setOverview(jsonObject.getString("overview"));
        tvShow.setPosterPath(MovieDB.IMAGE_URL + context.getString(R.string.imageSize) + jsonObject.getString("poster_path"));

        JSONArray companiesArray = (JSONArray) jsonObject.get("production_companies");
        int companiesLength = companiesArray.length();

        for (int i = 0; i < companiesLength; i++) {
            JSONObject companyJSON = (JSONObject) companiesArray.get(i);
            Company company = new Company(companyJSON.getInt("id"));
            company.setName(companyJSON.getString("name"));
            tvShow.getCompanies().add(company);
        }

        JSONArray seasonsArray = (JSONArray) jsonObject.get("seasons");
        int seasonsLength = seasonsArray.length();

        for (int i = 0; i < seasonsLength; i++) {
            JSONObject seasonJSON = (JSONObject) seasonsArray.get(i);
            if (seasonJSON.getInt("season_number") != 0) {
                Season season = new Season();
                season.setId(seasonJSON.getInt("id"));
                season.setNumber(seasonJSON.getString("season_number"));
                season.setAirDate(seasonJSON.getString("air_date"));
                season.setPosterPath(MovieDB.IMAGE_URL + context.getString(R.string.imageSize) + seasonJSON.getString("poster_path"));
                season.setEpisodeCount(seasonJSON.getString("episode_count"));
                tvShow.getSeasonList().add(season);
            }

        }

        tvShow.setStatus(jsonObject.getString("status"));
        tvShow.setVoteAverage((float) jsonObject.getDouble("vote_average"));
        tvShow.setVoteCount(jsonObject.getString("vote_count"));

        JSONObject imagesJSON = (JSONObject) jsonObject.get("images");
        JSONArray backdropsArray = (JSONArray) imagesJSON.get("backdrops");
        int backdropsLength = backdropsArray.length();
        for (int i = 0; i < backdropsLength; i++) {
            JSONObject backdropJSON = (JSONObject) backdropsArray.get(i);
            tvShow.getImages().add(backdropJSON.getString("file_path"));
        }

        JSONArray postersArray = (JSONArray) imagesJSON.get("posters");
        int postersLength = postersArray.length();
        for (int i = 0; i < postersLength; i++) {
            JSONObject postersJSON = (JSONObject) postersArray.get(i);
            tvShow.getImages().add(postersJSON.getString("file_path"));
        }

        JSONObject trailersJSON = (JSONObject) jsonObject.get("videos");
        JSONArray youtubeArray = (JSONArray) trailersJSON.get("results");
        int youtubeArrayLength = youtubeArray.length();
        for (int i = 0; i < youtubeArrayLength; i++) {
            JSONObject youtubeJSON = (JSONObject) youtubeArray.get(i);
            Trailer trailer = new Trailer();
            trailer.setName(youtubeJSON.getString("name"));
            trailer.setSource(youtubeJSON.getString("key"));
            tvShow.getTrailerList().add(trailer);
        }

        JSONObject castsJSON = (JSONObject) jsonObject.get("credits");
        JSONArray castsArray = (JSONArray) castsJSON.get("cast");
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
            tvShow.getCastList().add(cast);
        }

        JSONObject similarJSON = (JSONObject) jsonObject.get("similar");
        JSONArray jsonArray = (JSONArray) similarJSON.get("results");
        int length = jsonArray.length();
        for (int i = 0; i < length; i++) {
            JSONObject movieJSON = (JSONObject) jsonArray.get(i);
            TVShow tvShow1 = new TVShow(movieJSON.getInt("id"));
            tvShow1.setName(movieJSON.getString("name"));
            tvShow1.setVoteAverage((float) movieJSON.getInt("vote_average"));
            tvShow1.setPosterPath(movieJSON.getString("poster_path"));
            tvShow.getRelatedShows().add(tvShow1);
        }

        myParser.objects().add(tvShow);
    }
}
