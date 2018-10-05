package com.gdevelopers.movies.mappers;

import android.content.Context;

import com.gdevelopers.movies.R;
import com.gdevelopers.movies.helpers.MovieDB;
import com.gdevelopers.movies.helpers.PreferencesHelper;
import com.gdevelopers.movies.model.JObjectMapper;
import com.gdevelopers.movies.model.KObject;
import com.gdevelopers.movies.model.Server;
import com.gdevelopers.movies.objects.Actor;
import com.gdevelopers.movies.objects.Show;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class JActorsMapper extends JObjectMapper {
    private final Context context;

    public JActorsMapper(Context context, Server server) {
        super(server);
        this.context = context;
    }

    public List<KObject> getActorDetails(String id) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("api_key", MovieDB.API_KEY);
        hashMap.put("language", PreferencesHelper.getLanguage(context));
        hashMap.put("append_to_response", "combined_credits%2Cimages%2Ctagged_images");

        if (_loadedObjects.isEmpty() || !_loadedObjects.containsKey(id)) {
            return this.load("person/" + id, hashMap);
        }

        List<KObject> ret = new ArrayList<>();
        ret.add(_loadedObjects.get(id));

        return ret;
    }

    private KObject createObject(long id) {
        return new Actor(id);
    }

    @Override
    protected void setObjectAttribute(JSONObject jsonObject, JSONArray jArray, JSONObjectParser myParser) throws JSONException {
        KObject object = createObject((long) jsonObject.getInt("id"));
        Actor actor = (Actor) object;

        actor.setBiography(jsonObject.getString("biography"));

        actor.setBirthday(jsonObject.isNull("birthday") ? "" : jsonObject.getString("birthday"));

        actor.setDeathday(jsonObject.isNull("deathday") ? "" : jsonObject.getString("deathday"));

        actor.setName(jsonObject.getString("name"));
        actor.setPlaceOfBirth(jsonObject.has("place_of_birth") ? jsonObject.getString("place_of_birth") : "");
        actor.setProfilePath(MovieDB.IMAGE_URL + context.getResources().getString(R.string.backDropImgSize) + jsonObject.getString("profile_path"));

        JSONObject creditsJSON = (JSONObject) jsonObject.get("combined_credits");
        JSONArray castArray = (JSONArray) creditsJSON.get("cast");
        int castLength = castArray.length();

        for (int i = 0; i < castLength; i++) {
            JSONObject movieJSON = (JSONObject) castArray.get(i);
            Show show = new Show();
            show.setId(movieJSON.getInt("id"));
            if (movieJSON.has("character"))
                show.setCharacter(movieJSON.getString("character"));

            if (movieJSON.has("title"))
                show.setTitle(movieJSON.getString("title"));
            else if (movieJSON.has("name"))
                show.setTitle(movieJSON.getString("name"));

            if (movieJSON.has("poster_path"))
                show.setPosterPath(MovieDB.IMAGE_URL + context.getResources().getString(R.string.imageSize) + movieJSON.getString("poster_path"));

            if (movieJSON.has("first_air_date"))
                show.setReleaseDate(movieJSON.getString("first_air_date"));
            else if (movieJSON.has("release_date"))
                show.setReleaseDate(movieJSON.getString("release_date"));

            String mediaType = movieJSON.getString("media_type");
            show.setMediaType(mediaType);
            if (mediaType.equals("tv"))
                actor.getTvList().add(show);
            else if (mediaType.equals("movie"))
                actor.getMovieList().add(show);
//            actor.getShowList().add(show);
        }

        JSONArray crewArray = (JSONArray) creditsJSON.get("crew");
        int crewLength = crewArray.length();

        for (int i = 0; i < crewLength; i++) {
            JSONObject movieJSON = (JSONObject) crewArray.get(i);
            Show show = new Show();
            show.setId(movieJSON.getInt("id"));
            show.setJob(movieJSON.getString("job"));
            show.setDepartment(movieJSON.getString("department"));

            if (movieJSON.has("title"))
                show.setTitle(movieJSON.getString("title"));
            else if (movieJSON.has("name"))
                show.setTitle(movieJSON.getString("name"));

            show.setPosterPath(MovieDB.IMAGE_URL + context.getResources().getString(R.string.imageSize) + movieJSON.getString("poster_path"));

            if (movieJSON.has("first_air_date"))
                show.setReleaseDate(movieJSON.getString("first_air_date"));
            else if (movieJSON.has("release_date"))
                show.setReleaseDate(movieJSON.getString("release_date"));

            show.setMediaType(movieJSON.getString("media_type"));
            actor.getCrewList().add(show);
        }

        JSONObject imagesJSON = (JSONObject) jsonObject.get("images");
        JSONArray imagesArray = (JSONArray) imagesJSON.get("profiles");
        int imagesLength = imagesArray.length();
        for (int i = 0; i < imagesLength; i++) {
            JSONObject profilesJSON = (JSONObject) imagesArray.get(i);
            actor.getImages().add(profilesJSON.getString("file_path"));
        }

        JSONObject taggedImagesJSON = (JSONObject) jsonObject.get("tagged_images");
        JSONArray taggedImagesArray = (JSONArray) taggedImagesJSON.get("results");
        int taggedImagesLength = taggedImagesArray.length();
        for (int i = 0; i < taggedImagesLength; i++) {
            JSONObject profilesJSON = (JSONObject) taggedImagesArray.get(i);
            actor.getTaggedImages().add(MovieDB.IMAGE_URL + context.getResources().getString(R.string.backDropImgSize) + profilesJSON.getString("file_path"));
        }

        JSONArray akaArray = (JSONArray) jsonObject.get("also_known_as");
        if (akaArray.length() > 0) {
            String akaStr = (String) akaArray.get(0);
            actor.setAka(akaStr);
        } else actor.setAka("");


        myParser.objects().add(actor);
    }
}
