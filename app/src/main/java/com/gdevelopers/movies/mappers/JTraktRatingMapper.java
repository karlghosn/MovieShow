package com.gdevelopers.movies.mappers;

import com.gdevelopers.movies.helpers.MovieDB;
import com.gdevelopers.movies.model.JObjectMapper;
import com.gdevelopers.movies.model.KObject;
import com.gdevelopers.movies.model.Server;
import com.gdevelopers.movies.objects.TraktRating;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class JTraktRatingMapper extends JObjectMapper {

    public JTraktRatingMapper(Server server) {
        super(server);
    }

    public List<KObject> getTraktRatings(String id) {
        if (_loadedObjects.isEmpty() || !_loadedObjects.containsKey(id)) {
            return this.loadBaseUrl(MovieDB.TRAKT_BASE_URL, "movies/" + id + "/ratings", null);
        }

        List<KObject> ret = new ArrayList<>();
        ret.add(_loadedObjects.get(id));

        return ret;
    }

    private KObject createObject() {
        return new TraktRating((long) 0);
    }

    @Override
    protected void setObjectAttribute(JSONObject jsonObject, JSONArray jArray, JSONObjectParser myParser) throws JSONException {
        TraktRating traktRating = (TraktRating) createObject();
        String rating = jsonObject.getString("rating");
        traktRating.setOverallRating(rating);
        String votes = jsonObject.getString("votes");
        traktRating.setVotes(votes);

        JSONObject distributionJSON = (JSONObject) jsonObject.get("distribution");

        for (int i = 1; i <= 10; i++) {
            int rat = distributionJSON.getInt(String.valueOf(i));
            traktRating.getRatingsMap().put(i, rat);
        }
        myParser.objects().add(traktRating);
    }
}
