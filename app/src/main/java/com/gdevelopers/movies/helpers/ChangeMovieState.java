package com.gdevelopers.movies.helpers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.gdevelopers.movies.model.ModelService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;

@SuppressLint("StaticFieldLeak")
public class ChangeMovieState extends AsyncTask<JSONObject, Void, String> {
    private final Context context;
    private final String type;
    private final String movieId;
    private final ModelService service;
    private final boolean callBack;
    private OnCallBackListener onCallBackListener;
    private final JSONObject jsonObject;

    public void setOnCallBackListener(OnCallBackListener onCallBackListener) {
        this.onCallBackListener = onCallBackListener;
    }

    public ChangeMovieState(Context context, String type, String movieId, ModelService service, JSONObject jsonObject, boolean callBack) {
        this.context = context;
        this.type = type;
        this.movieId = movieId;
        this.service = service;
        this.callBack = callBack;
        this.jsonObject = jsonObject;
    }

    @Override
    protected String doInBackground(JSONObject... jsonObjects) {
        String JsonResponse;
        String JsonDATA = jsonObject.toString();
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        try {
            URL url = new URL("https://api.themoviedb.org/3/account/" + PreferencesHelper.getAccountId(context)
                    + "/" + type + "?api_key=" + MovieDB.API_KEY + "&session_id=" + PreferencesHelper.getSessionId(context));
            Log.d("Url", url.toString());
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoOutput(true);
            // is output buffer writter
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json;charset=utf-8");
            //set headers and method
            Writer writer = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream(), "UTF-8"));
            writer.write(JsonDATA);
            // json data
            writer.close();
            InputStream inputStream = urlConnection.getInputStream();
            //input stream
            StringBuilder buffer = new StringBuilder();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String inputLine;
            while ((inputLine = reader.readLine()) != null)
                buffer.append(inputLine).append("\n");
            if (buffer.length() == 0) {
                // Stream was empty. No point in parsing.
                return null;
            }
            JsonResponse = buffer.toString();
            //response data
            Log.i("Response", JsonResponse);
            //send to post execute
            return JsonResponse;

        } catch (IOException e) {
            Log.d(Constants.STRINGS.EXCEPTION,e.getMessage());
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e("Error", "Error closing stream", e);
                }
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            Toast.makeText(context, jsonObject.getString("status_message"), Toast.LENGTH_SHORT).show();
        } catch (JSONException e) {
            Log.d(Constants.STRINGS.EXCEPTION,e.getMessage());
        }

        if (callBack)
            onCallBackListener.onCallBack();
        else
            service.getMovieState(movieId, PreferencesHelper.getSessionId(context));
    }

    public interface OnCallBackListener {
        void onCallBack();
    }
}