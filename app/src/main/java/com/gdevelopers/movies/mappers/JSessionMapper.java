package com.gdevelopers.movies.mappers;

import com.gdevelopers.movies.helpers.MovieDB;
import com.gdevelopers.movies.model.JObjectMapper;
import com.gdevelopers.movies.model.KObject;
import com.gdevelopers.movies.model.Server;
import com.gdevelopers.movies.objects.Session;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;


public class JSessionMapper extends JObjectMapper {

    public JSessionMapper(Server server) {
        super(server);
    }

    public List<KObject> getSessionId(String token) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("api_key", MovieDB.API_KEY);
        hashMap.put("request_token", token);

        return this.load("authentication/session/new", hashMap);

    }

    private KObject createObject() {
        return new Session((long) 0);
    }

    @Override
    protected void setObjectAttribute(JSONObject jsonObject, JSONArray jArray, JSONObjectParser myParser) throws JSONException {
        Session session = (Session) createObject();
        session.setSession(jsonObject.getString("session_id"));
        myParser.objects().add(session);
    }
}
