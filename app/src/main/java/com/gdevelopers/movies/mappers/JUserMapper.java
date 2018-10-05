package com.gdevelopers.movies.mappers;

import com.gdevelopers.movies.helpers.MovieDB;
import com.gdevelopers.movies.model.JObjectMapper;
import com.gdevelopers.movies.model.KObject;
import com.gdevelopers.movies.model.Server;
import com.gdevelopers.movies.objects.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;


public class JUserMapper extends JObjectMapper {

    public JUserMapper(Server server) {
        super(server);
    }

    public List<KObject> getUserDetails(String sessionId) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("api_key", MovieDB.API_KEY);
        hashMap.put("session_id", sessionId);

        return this.load("account", hashMap);

    }


    private KObject createObject(long id) {
        return new User(id);
    }

    @Override
    protected void setObjectAttribute(JSONObject jsonObject, JSONArray jArray, JSONObjectParser myParser) throws JSONException {
        KObject object = createObject(jsonObject.getInt("id"));
        User user = (User) object;
        user.setUsername(jsonObject.getString("username"));
        myParser.objects().add(user);
    }
}