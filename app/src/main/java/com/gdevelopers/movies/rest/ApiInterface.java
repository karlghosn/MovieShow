package com.gdevelopers.movies.rest;

import com.gdevelopers.movies.helpers.MovieDB;
import com.gdevelopers.movies.helpers.PostRetrofit;
import com.gdevelopers.movies.helpers.Response;
import com.gdevelopers.movies.wrappers.MoviesWrapper;
import com.gdevelopers.movies.objects.OMDb;
import com.gdevelopers.movies.wrappers.PeopleWrapper;
import com.gdevelopers.movies.wrappers.TVShowWrapper;
import com.gdevelopers.movies.objects.User;

import io.reactivex.Single;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;


public interface ApiInterface {

    @GET("/?apikey=" + MovieDB.OMDB_KEY)
    Call<OMDb> getMovieRatings(@Query("i") String movieId);

    @POST("list/{id}/add_item?api_key=" + MovieDB.API_KEY)
    Call<Response> addMovieToList(@Path("id") int id, @Query("session_id") String sessionId,
                                  @Body PostRetrofit post);

    @POST("movie/{id}/rating?api_key=" + MovieDB.API_KEY)
    Call<Response> rateMovie(@Path("id") int id, @Query("session_id") String sessionId,
                             @Body PostRetrofit post);

    @POST("list?api_key=" + MovieDB.API_KEY)
    Call<Response> createList(@Query("session_id") String sessionId, @Body PostRetrofit post);

    @DELETE("list/{id}?api_key=" + MovieDB.API_KEY)
    Call<Response> deleteList(@Path("id") int id, @Query("session_id") String sessionId);

    @POST("list/{id}/add_item?api_key=" + MovieDB.API_KEY)
    Call<Response> addMovie(@Path("id") int id, @Query("session_id") String sessionId, @Body PostRetrofit postRetrofit);

    @POST("list/{id}/clear?api_key=" + MovieDB.API_KEY + "&confirm=true")
    Call<Response> clearList(@Path("id") int id, @Query("session_id") String sessionId);

    @POST("list/{id}/remove_item?api_key=" + MovieDB.API_KEY)
    Call<Response> removeMovie(@Path("id") int id, @Query("session_id") String sessionId, @Body PostRetrofit postRetrofit);

    // Get Account Details
    @GET("account?api_key=" + MovieDB.API_KEY)
    Single<User> getAccountDetails(@Query("session_id") String sessionId);

    @GET("movie/{type}?api_key=" + MovieDB.API_KEY)
    Call<MoviesWrapper> getMovies(@Path("type") String type, @Query("language") String language,
                                  @Query("region") String region, @Query("page") int page);

    @GET("tv/{type}?api_key=" + MovieDB.API_KEY)
    Call<TVShowWrapper> getTVShows(@Path("type") String type, @Query("language") String language, @Query("page") int page);

    @GET("person/popular?api_key=" + MovieDB.API_KEY)
    Call<PeopleWrapper> getPopularPeople(@Query("page") int page);
}

