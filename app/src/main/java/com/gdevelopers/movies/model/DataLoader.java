package com.gdevelopers.movies.model;


import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

interface DataLoader {
    InputStream load(String command, Map<String, String> params) throws IOException;

    InputStream loadBaseUrl(String baseUrl, String command, Map<String, String> params) throws IOException;
//    InputStream loadDelete(String command, Map<String, String> params) throws IOException;
}
