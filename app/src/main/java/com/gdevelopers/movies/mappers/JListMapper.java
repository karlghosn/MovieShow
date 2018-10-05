package com.gdevelopers.movies.mappers;

import android.content.Context;

import com.gdevelopers.movies.helpers.MovieDB;
import com.gdevelopers.movies.helpers.PreferencesHelper;
import com.gdevelopers.movies.model.JObjectMapper;
import com.gdevelopers.movies.model.KObject;
import com.gdevelopers.movies.model.Server;
import com.gdevelopers.movies.objects.UserList;
import com.gdevelopers.movies.objects.Section;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class JListMapper extends JObjectMapper {
    private final Context context;

    public JListMapper(Server server, Context context) {
        super(server);
        this.context = context;
    }

    public List<KObject> getCreatedLists(String accountId, String sessionId, boolean reload) {
        HashMap<String, String> hashMap = new HashMap<>();

        hashMap.put("api_key", MovieDB.API_KEY);
        hashMap.put("session_id", sessionId);
        hashMap.put("language", PreferencesHelper.getLanguage(context));

        if (_loadedObjects.isEmpty() || reload) {
            return this.load("account/" + accountId + "/lists", hashMap);
        }
        return new ArrayList<>(_loadedObjects.values());
    }


    private KObject createObject(long id) {
        return new Section(id);
    }

    @Override
    protected void setObjectAttribute(JSONObject jsonObject, JSONArray jArray, JSONObjectParser myParser) throws JSONException {
        Section section = (Section) createObject(jsonObject.getInt("page"));
        section.setTotalPages(jsonObject.getInt("total_pages"));
        section.setPage(jsonObject.getInt("page"));
        JSONArray jsonArray = (JSONArray) jsonObject.get("results");

        int length = jsonArray.length();
        for (int i = 0; i < length; i++) {
            JSONObject listJSON = (JSONObject) jsonArray.get(i);
            UserList list = new UserList(listJSON.getInt("id"));
            list.setName(listJSON.getString("name"));
            list.setDescription(listJSON.getString("description"));
            list.setItemCount(listJSON.getString("item_count"));
            section.getUserLists().add(list);
        }
        myParser.objects().add(section);
    }


}
