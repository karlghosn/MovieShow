package com.gdevelopers.movies.model;

import android.util.Log;

import com.gdevelopers.movies.helpers.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


@SuppressWarnings("unused")
public abstract class JObjectMapper {
    protected final LinkedHashMap<String, KObject> _loadedObjects;
    private final Server server;


    protected JObjectMapper(Server server) {
        _loadedObjects = new LinkedHashMap<>();
        this.server = server;
    }

    protected abstract void setObjectAttribute(JSONObject jsonObject, JSONArray jArray, JSONObjectParser myParser) throws JSONException;


    protected class JSONObjectParser {
        private final ArrayList<KObject> _objects;
        private final JObjectMapper objectMapper;
        private final String command;

        JSONObjectParser(JObjectMapper mapper, String command) {
            this.objectMapper = mapper;
            _objects = new ArrayList<>();
            this.command = command;
        }

        public List<KObject> objects() {
            return _objects;
        }

        public String getCommand() {
            return command;
        }

        void parseElement(InputStream response) throws IOException, JSONException {
            String resp = convertStreamToString(response);
            JSONObject jsonObject = null;
            JSONArray jsonArray = null;
            Object json = new JSONTokener(resp).nextValue();
            if (json instanceof JSONObject)
                jsonObject = new JSONObject(resp);
            else if (json instanceof JSONArray)
                jsonArray = new JSONArray(resp);

           /* String responseCode = null;
            if (jsonObject != null) {
                responseCode = jsonObject.has("status_code") ? jsonObject.getString("status_code") : null;
            }
            if (responseCode != null && !responseCode.equals("34")) {
                handleError();
            } else {*/
            objectMapper.setObjectAttribute(jsonObject, jsonArray, this);
//            }
        }

        private String convertStreamToString(InputStream inputStream) throws IOException {
            BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder total = new StringBuilder();
            String line;
            while ((line = r.readLine()) != null) {
                total.append(line);
            }
            return total.toString();
        }
    }

    private JSONObjectParser createParser(String command) {
        return new JSONObjectParser(this, command);
    }

    protected List<KObject> load(String command, Map<String, String> params) {
        DataLoader loader = this.server.getDataLoader();
        InputStream in;
        try {
            in = loader.load(command, params);
            JSONObjectParser parser = createParser(command);
            parser.parseElement(in);
            List<KObject> ret = parser.objects();
            synchronized (_loadedObjects) {
                for (KObject b : ret) {
                    _loadedObjects.put(String.valueOf(b.id()), b);
                }
            }
            return ret;
        } catch (IOException | JSONException e) {
            Log.d(Constants.STRINGS.EXCEPTION, e.getMessage());
        }
        return new ArrayList<>();
    }

    protected List<KObject> loadBaseUrl(String baseUrl, String command, Map<String, String> params) {
        DataLoader loader = this.server.getDataLoader();
        InputStream in;
        try {
            in = loader.loadBaseUrl(baseUrl, command, params);
            JSONObjectParser parser = createParser(command);
            parser.parseElement(in);
            List<KObject> ret = parser.objects();
            synchronized (_loadedObjects) {
                for (KObject b : ret) {
                    _loadedObjects.put(String.valueOf(b.id()), b);
                }
            }
            return ret;
        } catch (IOException | JSONException e) {
            Log.d(Constants.STRINGS.EXCEPTION, e.getMessage());
        }
        return new ArrayList<>();
    }
}
