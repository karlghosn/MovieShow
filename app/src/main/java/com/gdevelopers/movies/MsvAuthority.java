package com.gdevelopers.movies;

/**
 * Created by mauker on 02/05/16.
 *
 * Convenience class used to give the correct authority to the Content Provider.
 * Note: On your AndroidManifest.xml, inside the provider tag, use the String below as the provider authority.
 *
 */
class MsvAuthority {
    @SuppressWarnings("unused")
    public static final String CONTENT_AUTHORITY = "com.gdevelopers.movies.searchhistorydatabase";
}
