package com.gdevelopers.movies.helpers;

import android.content.Context;
import android.content.SharedPreferences;


public class PreferencesHelper {

    public static String getSessionId(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("my_prefs", Context.MODE_PRIVATE);
        return preferences.getString("session_id", "");
    }

    public static boolean hasSessionId(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("my_prefs", Context.MODE_PRIVATE);
        return preferences.contains("session_id");
    }

    public static void clear(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("my_prefs", Context.MODE_PRIVATE);
        preferences.edit().clear().apply();
    }

    public static void putSessionId(String sessionId, Context context) {
        SharedPreferences preferences = context.getSharedPreferences("my_prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("session_id", sessionId);
        editor.apply();
    }

    public static void putAccountId(String id, Context context) {
        SharedPreferences preferences = context.getSharedPreferences("my_prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("account_id", id);
        editor.apply();
    }

    public static String getAccountId(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("my_prefs", Context.MODE_PRIVATE);
        return preferences.getString("account_id", "");
    }

    public static void putLanguage(String id, Context context) {
        SharedPreferences preferences = context.getSharedPreferences("my_prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("language", id);
        editor.apply();
    }

    public static String getLanguage(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("my_prefs", Context.MODE_PRIVATE);
        return preferences.getString("language", "");
    }
}
