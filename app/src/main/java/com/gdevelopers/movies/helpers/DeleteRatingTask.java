package com.gdevelopers.movies.helpers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@SuppressLint("StaticFieldLeak")
public class DeleteRatingTask extends AsyncTask<Void, Void, String> {
    private final Context context;
    private final String movieId;
    private OnDeleteCallBackListener onDeleteCallBackListener;

    public void setOnCallBackListener(OnDeleteCallBackListener onCallBackListener) {
        this.onDeleteCallBackListener = onCallBackListener;
    }

    public DeleteRatingTask(Context context, String movieId) {
        this.context = context;
        this.movieId = movieId;
    }

    @Override
    protected String doInBackground(Void... voids) {
        String JsonResponse;
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        try {
            URL url = new URL("https://api.themoviedb.org/3/movie/" + movieId
                    + "/rating" + "?api_key=" + MovieDB.API_KEY + "&session_id=" + PreferencesHelper.getSessionId(context));
            Log.d("Url", url.toString());
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoOutput(true);
            // is output buffer writter
            urlConnection.setRequestMethod("DELETE");
            urlConnection.setRequestProperty("Content-Type", "application/json;charset=utf-8");
            //set headers and method

            // json data
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
//        dialog.cancel();
        try {
            JSONObject jsonObject = new JSONObject(result);
            Toast.makeText(context, jsonObject.getString("status_message"), Toast.LENGTH_SHORT).show();
        } catch (JSONException e) {
            Log.d(Constants.STRINGS.EXCEPTION,e.getMessage());
        }
        onDeleteCallBackListener.onDeleteCallBack();

    }

    public interface OnDeleteCallBackListener {
        void onDeleteCallBack();
    }
}

