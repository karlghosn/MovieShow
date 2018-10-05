package com.gdevelopers.movies.mappers;

import android.content.Context;

import com.gdevelopers.movies.R;
import com.gdevelopers.movies.helpers.MovieDB;
import com.gdevelopers.movies.helpers.PreferencesHelper;
import com.gdevelopers.movies.model.JObjectMapper;
import com.gdevelopers.movies.model.KObject;
import com.gdevelopers.movies.model.Server;
import com.gdevelopers.movies.objects.Cast;
import com.gdevelopers.movies.objects.Collection;
import com.gdevelopers.movies.objects.Company;
import com.gdevelopers.movies.objects.Country;
import com.gdevelopers.movies.objects.Crew;
import com.gdevelopers.movies.objects.Movie;
import com.gdevelopers.movies.objects.Review;
import com.gdevelopers.movies.objects.Trailer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;


public class JMovieDetailsMapper extends JObjectMapper {

    private final Context context;

    public JMovieDetailsMapper(Context context, Server server) {
        super(server);
        this.context = context;
    }

    public List<KObject> getMovieDetails(String id) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("api_key", MovieDB.API_KEY);
        hashMap.put("language", PreferencesHelper.getLanguage(context));
        hashMap.put("append_to_response", "trailers%2Ccasts%2Cimages%2Csimilar%2Creviews%2Crecommendations&include_image_language=en,null");

        if (_loadedObjects.isEmpty() || !_loadedObjects.containsKey(id)) {
            return this.load("movie/" + id, hashMap);
        }

        List<KObject> ret = new ArrayList<>();
        ret.add(_loadedObjects.get(id));

        return ret;
    }

    private KObject createObject(long id) {
        return new Movie(id);
    }

    @Override
    protected void setObjectAttribute(JSONObject jsonObject, JSONArray jArray, JSONObjectParser myParser) throws JSONException {
        KObject object = createObject((long) jsonObject.getInt("id"));
        Movie movie = (Movie) object;
        movie.setBackdropPath(MovieDB.IMAGE_URL + context.getResources().getString(R.string.backDropImgSize) + jsonObject.getString("backdrop_path"));
        String budget = String.format(Locale.getDefault(), "%,d", jsonObject.getInt("budget")).replace(",", " ");
        movie.setBudget(budget + " $");

        String revenue = String.format(Locale.getDefault(), "%,d", jsonObject.getInt("revenue")).replace(",", " ");
        movie.setRevenue(revenue + " $");

        if (!jsonObject.isNull("belongs_to_collection")) {
            JSONObject collectionJSON = (JSONObject) jsonObject.get("belongs_to_collection");
            Collection collection = new Collection(collectionJSON.getInt("id"));
            collection.setName(collectionJSON.getString("name"));
            collection.setBackdrop(MovieDB.IMAGE_URL + context.getResources().getString(R.string.galleryImgSize) +
                    collectionJSON.getString("backdrop_path"));
            movie.setCollection(collection);
        }

        movie.setImdbId(jsonObject.getString("imdb_id"));
        movie.setOverview(jsonObject.getString("overview"));

        //Get Production Companies
        JSONArray companiesArray = (JSONArray) jsonObject.get("production_companies");
        int companiesLength = companiesArray.length();
        for (int i = 0; i < companiesLength; i++) {
            JSONObject companyJSON = (JSONObject) companiesArray.get(i);
            Company company = new Company(companyJSON.getInt("id"));
            company.setName(companyJSON.getString("name"));
            movie.getCompanyList().add(company);
        }

        //Get Production Countries
        JSONArray countriesArray = (JSONArray) jsonObject.get("production_countries");
        int countriesLength = countriesArray.length();
        for (int i = 0; i < countriesLength; i++) {
            JSONObject countryJSON = (JSONObject) countriesArray.get(i);
            Country country = new Country(i);
            country.setName(countryJSON.getString("name"));
            movie.getCountryList().add(country);
        }

        movie.setReleaseDate(jsonObject.getString("release_date"));
        movie.setRunTime(jsonObject.getString("runtime") + " mins");
        movie.setStatus(jsonObject.getString("status"));
        movie.setTagLine(jsonObject.getString("tagline"));
        movie.setVoteAverage(jsonObject.getDouble("vote_average"));
        movie.setVoteCount(jsonObject.getString("vote_count"));
        movie.setHomePage(jsonObject.getString("homepage"));


        JSONObject castsJSON = (JSONObject) jsonObject.get("casts");
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
            movie.getCastList().add(cast);
        }

        JSONArray crewArray = (JSONArray) castsJSON.get("crew");
        int crewLength = crewArray.length();
        for (int i = 0; i < crewLength; i++) {
            JSONObject crewJSON = (JSONObject) crewArray.get(i);
            Crew crew = new Crew();
            crew.setDepartment(crewJSON.getString("department"));
            crew.setCreditId(crewJSON.getString("credit_id"));
            crew.setName(crewJSON.getString("name"));
            crew.setId(crewJSON.getString("id"));
            crew.setJob(crewJSON.getString("job"));
            crew.setProfilePath(MovieDB.IMAGE_URL + context.getResources().getString(R.string.imageSize) + crewJSON.getString("profile_path"));
            movie.getCrewList().add(crew);
        }


        JSONObject imagesJSON = (JSONObject) jsonObject.get("images");
        JSONArray backdropsArray = (JSONArray) imagesJSON.get("backdrops");
        int backdropsLength = backdropsArray.length();
        for (int i = 0; i < backdropsLength; i++) {
            JSONObject backdropJSON = (JSONObject) backdropsArray.get(i);
            movie.getImagesList().add(backdropJSON.getString("file_path"));
            movie.getBackdropList().add(MovieDB.IMAGE_URL + context.getResources().getString(R.string.backDropImgSize) +
                    backdropJSON.getString("file_path"));
        }

        JSONArray postersArray = (JSONArray) imagesJSON.get("posters");
        int postersLength = postersArray.length();
        for (int i = 0; i < postersLength; i++) {
            JSONObject postersJSON = (JSONObject) postersArray.get(i);
            movie.getImagesList().add(postersJSON.getString("file_path"));
        }

        JSONObject trailersJSON = (JSONObject) jsonObject.get("trailers");
        JSONArray youtubeArray = (JSONArray) trailersJSON.get("youtube");
        int youtubeArrayLength = youtubeArray.length();
        for (int i = 0; i < youtubeArrayLength; i++) {
            JSONObject youtubeJSON = (JSONObject) youtubeArray.get(i);
            Trailer trailer = new Trailer();
            trailer.setName(youtubeJSON.getString("name"));
            trailer.setSource(youtubeJSON.getString("source"));
            movie.getTrailerList().add(trailer);
        }

        JSONObject reviewsJSON = (JSONObject) jsonObject.get("reviews");
        JSONArray resultsArray = (JSONArray) reviewsJSON.get("results");
        int resultsLength = resultsArray.length();
        for (int i = 0; i < resultsLength; i++) {
            JSONObject object1 = (JSONObject) resultsArray.get(i);
            Review review = new Review();
            review.setId(object1.getString("id"));
            review.setAuthor(object1.getString("author"));
            review.setUrl(object1.getString("url"));
            review.setContent(object1.getString("content"));
            movie.getReviewList().add(review);
        }


        JSONObject similarJSON = (JSONObject) jsonObject.get("similar");
        JSONArray jsonArray = (JSONArray) similarJSON.get("results");
        int length = jsonArray.length();
        for (int i = 0; i < length; i++) {
            JSONObject movieJSON = (JSONObject) jsonArray.get(i);
            Movie movie1 = new Movie(movieJSON.getInt("id"));
            movie1.setTitle(movieJSON.getString("title"));
            movie1.setType("movie");
            movie1.setReleaseDate(movieJSON.getString("release_date"));
            movie1.setVoteAverage(movieJSON.getDouble("vote_average"));
            movie1.setPosterPath(MovieDB.IMAGE_URL + context.getResources().getString(R.string.imageSize) + movieJSON.getString("poster_path"));
            movie1.setBackdropPath(MovieDB.IMAGE_URL + context.getResources().getString(R.string.galleryImgSize) + movieJSON.getString("backdrop_path"));
            movie.getRelatedMovies().add(movie1);
        }

        myParser.objects().add(movie);

    }
}

