package com.gdevelopers.movies.mappers;

import android.content.Context;

import com.gdevelopers.movies.helpers.MovieDB;
import com.gdevelopers.movies.helpers.PreferencesHelper;
import com.gdevelopers.movies.model.JObjectMapper;
import com.gdevelopers.movies.model.KObject;
import com.gdevelopers.movies.model.Server;
import com.gdevelopers.movies.objects.Company;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class JCompanyMapper extends JObjectMapper {

    public JCompanyMapper(Server server) {
        super(server);
    }

    public List<KObject> getCompanyDetails(Context context, String id) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("api_key", MovieDB.API_KEY);
        hashMap.put("language", PreferencesHelper.getLanguage(context));

        if (_loadedObjects.isEmpty() || !_loadedObjects.containsKey(id)) {
            return this.load("company/" + id, hashMap);
        }

        List<KObject> ret = new ArrayList<>();
        ret.add(_loadedObjects.get(id));

        return ret;
    }

    private KObject createObject(long id) {
        return new Company(id);
    }

    @Override
    protected void setObjectAttribute(JSONObject jsonObject, JSONArray jArray, JSONObjectParser myParser) throws JSONException {
        Company company = (Company) createObject(jsonObject.getInt("id"));
        company.setName(jsonObject.getString("name"));
        company.setHeadquarters(jsonObject.getString("headquarters"));
        myParser.objects().add(company);
    }
}
