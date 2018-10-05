package com.gdevelopers.movies.model;

import android.util.Log;

import com.gdevelopers.movies.helpers.MovieDB;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;


class HttpDataLoader implements DataLoader {

    private final String server;


    HttpDataLoader(String s) {
        server = s;
    }

    private String urlGetBuilder(String commandName, Map<String, String> params) {

        if (params == null || params.isEmpty()) return commandName;
        StringBuilder builder = new StringBuilder();
        builder.append(commandName).append("?");
        Iterator<String> iterator = params.keySet().iterator();
        boolean first = true;

        while (iterator.hasNext()) {
            String k = iterator.next();
            String v = params.get(k);
            if (!first)
                builder.append("&");
            builder.append(k).append("=").append(v);
            first = false;
        }
        String url = builder.toString();
        url = url.replaceAll(" +", "%20");
        return url;
    }

    /*private String urlBuilder(Map<String, String> params) {
        if (params == null || params.isEmpty()) return "";
        StringBuilder builder = new StringBuilder();

        Iterator<String> iterator = params.keySet().iterator();
        boolean first = true;
        while (iterator.hasNext()) {
            String k = iterator.next();
            String v = params.get(k);
            if (!first)
                builder.append("&");
            builder.append(k).append("=").append(v);
            first = false;
        }
        String url = builder.toString();
        url = url.replaceAll(" +", "%20");
        return url;
    }*/


    @Override
    public InputStream load(String command, Map<String, String> params) throws IOException {
        String url_string = server + "/" + urlGetBuilder(command, params);
        URL url = new URL(url_string);
        HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
        Log.d("url", url_string);
        int timeout = 30 * 1000;
        urlConnection.setConnectTimeout(timeout);
        urlConnection.setReadTimeout(timeout);
        urlConnection.connect();
        int responseCode = urlConnection.getResponseCode();
        if (responseCode == 403) {
            return new BufferedInputStream(urlConnection.getErrorStream());
        } else if (responseCode != 200) {
            String response = "Error from server: " + responseCode;
            return new ByteArrayInputStream(response.getBytes());
        }
        return new BufferedInputStream(urlConnection.getInputStream());
    }

    @Override
    public InputStream loadBaseUrl(String baseUrl, String command, Map<String, String> params) throws IOException {
        String url_string = baseUrl + "/" + urlGetBuilder(command, params);
        URL url = new URL(url_string);
        HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
        Log.d("url", url_string);
        int timeout = 30 * 1000;
        urlConnection.setConnectTimeout(timeout);
        urlConnection.setReadTimeout(timeout);
        urlConnection.setRequestProperty("Content-Type", "application/json");
        urlConnection.setRequestProperty("trakt-api-version", "2");
        urlConnection.setRequestProperty("trakt-api-key", MovieDB.TRAKT_TV_API_KEY);
        urlConnection.connect();
        int responseCode = urlConnection.getResponseCode();
        if (responseCode == 403) {
            return new BufferedInputStream(urlConnection.getErrorStream());
        } else if (responseCode != 200) {
            String response = "Error from server: " + responseCode;
            return new ByteArrayInputStream(response.getBytes());
        }
        return new BufferedInputStream(urlConnection.getInputStream());
    }

    /*@Override
    public InputStream loadDelete(String command, Map<String, String> params) throws IOException {
        String url_string = server + "/" + urlGetBuilder(command, params);
        url url = new url(url_string);
        HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
        Log.d("url", url_string);
        urlConnection.setRequestMethod("DELETE");
        urlConnection.setConnectTimeout(timeout);
        urlConnection.setReadTimeout(timeout);
        urlConnection.connect();
        int responseCode = urlConnection.getResponseCode();
        if (responseCode == 403) {
            return new BufferedInputStream(urlConnection.getErrorStream());
        } else if (responseCode != 200) {
            String response = "Error from server: " + responseCode;
            return new ByteArrayInputStream(response.getBytes());
        }
        return new BufferedInputStream(urlConnection.getInputStream());
    }*/


    /*@Override
    public InputStream loadPostRequest(String command, Map<String, String> params) throws IOException {
        String url_string = server + "/" + command;
        url url = new url(url_string);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        Log.d("url", url_string);
        urlConnection.setRequestMethod("POST");
        urlConnection.setDoOutput(true);
        urlConnection.setConnectTimeout(timeout);
        urlConnection.setReadTimeout(timeout);
        DataOutputStream wr = new DataOutputStream(
                urlConnection.getOutputStream());
        wr.writeBytes(urlBuilder(params));
        wr.flush();
        wr.close();
        urlConnection.connect();
        int responseCode = urlConnection.getResponseCode();
        if (responseCode == 403) {
            return new BufferedInputStream(urlConnection.getErrorStream());
        } else if (responseCode != 200) {
            String response = "Error from server: " + responseCode;
            return new ByteArrayInputStream(response.getBytes());
        }
        return new BufferedInputStream(urlConnection.getInputStream());
    }*/


    /*@Override
    public InputStream loadWithImageMultiPart(String command, Map<String, String> params, Map<String, String> filesParams) throws IOException {
        String url_string = server + "/" + command;
        url url = new url(url_string);

        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        Log.d("url", url_string);
        // Allow Inputs
        urlConnection.setDoInput(true);
        // Allow Outputs
        urlConnection.setDoOutput(true);
        // Don't use a cached copy.
        urlConnection.setUseCaches(false);
        // Use a post method.
        urlConnection.setRequestMethod("POST");
        urlConnection.setRequestProperty("Connection", "Keep-Alive");
        urlConnection.setRequestProperty("Content-Type", "multipart/form-data;boundary=*****");

        *//*if (wsse) {
            urlConnection.setRequestProperty("x-wsse", getWsseToken(u));
        }*//*

        DataOutputStream dos = new DataOutputStream(
                urlConnection.getOutputStream());

        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1024 * 1024;
        StringBuilder builder = new StringBuilder();
        for (Map.Entry entry : filesParams.entrySet()) {
            File sourceFile = new File(filesParams.get(entry.getKey()));
            FileInputStream fileInputStream = new FileInputStream(sourceFile);

            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"" + entry.getKey() + "\";filename=\""
                    + sourceFile.getName() + "\"" + lineEnd);
            dos.writeBytes("Content-Type: " + URLConnection.guessContentTypeFromName(sourceFile.getName()) + lineEnd);
            dos.writeBytes(lineEnd);

            // create a buffer of  maximum size
            bytesAvailable = fileInputStream.available();

            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];

            // read file and write it into form...
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);

            while (bytesRead > 0) {

                dos.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

            }
            dos.writeBytes(lineEnd);
        }

        for (Map.Entry entry : params.entrySet()) {
            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"" + lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes((String) entry.getValue());
            dos.writeBytes(lineEnd);
        }
        builder.append(twoHyphens).append(boundary).append(twoHyphens).append(lineEnd);
        dos.flush();
        dos.close();
        urlConnection.connect();
        int responseCode = urlConnection.getResponseCode();
        if (responseCode == 403) {
            return new BufferedInputStream(urlConnection.getErrorStream());
        } else if (responseCode != 200) {
            String response = "Error from server: " + responseCode;
            return new ByteArrayInputStream(response.getBytes());
        }
        return new BufferedInputStream(urlConnection.getInputStream());
    }*/

}
