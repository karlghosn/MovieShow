package com.gdevelopers.movies.mappers;

import com.gdevelopers.movies.helpers.MovieDB;
import com.gdevelopers.movies.model.JObjectMapper;
import com.gdevelopers.movies.model.KObject;
import com.gdevelopers.movies.model.Server;
import com.gdevelopers.movies.objects.Token;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;


public class JTokenMapper extends JObjectMapper {

    public JTokenMapper(Server server) {
        super(server);
    }

    public List<KObject> getToken() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("api_key", MovieDB.API_KEY);

        return this.load("authentication/token/new", hashMap);

    }

    private KObject createObject() {
        return new Token((long) 0);
    }

    @Override
    protected void setObjectAttribute(JSONObject jsonObject, JSONArray jArray, JSONObjectParser myParser) throws JSONException {
        KObject object = createObject();
        Token token = (Token) object;
        token.setRequestToken(jsonObject.getString("request_token"));

        myParser.objects().add(token);
    }
}
