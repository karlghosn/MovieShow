package com.gdevelopers.movies.objects;

import android.content.Context;

import com.gdevelopers.movies.helpers.PreferencesHelper;
import com.gdevelopers.movies.rest.ApiClient;
import com.gdevelopers.movies.rest.ApiInterface;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieService {
    private static MovieService movieService;
    private final ApiInterface apiInterface;
    private MoviesWrapper upComingWrapper;
    private MoviesWrapper nowPlayingWrapper;
    private MoviesWrapper popularWrapper;
    private MoviesWrapper topRatedWrapper;

    private MovieService() {
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
    }

    public static MovieService getInstance() {
        if (movieService == null)
            movieService = new MovieService();
        return movieService;
    }

    public void getUpComingMovies(Context context, final boolean loadMore, int page, final ServiceCallBack serviceCallBack) {
        if (upComingWrapper == null || loadMore) {
            final Call<MoviesWrapper> upComingCall = apiInterface.getMovies("upcoming", PreferencesHelper.getLanguage(context), "US", page);
            upComingCall.enqueue(new Callback<MoviesWrapper>() {
                @Override
                public void onResponse(Call<MoviesWrapper> call, Response<MoviesWrapper> response) {
                    if (!loadMore)
                        upComingWrapper = response.body();
                    serviceCallBack.successful(response.body());
                }

                @Override
                public void onFailure(Call<MoviesWrapper> call, Throwable t) {
                }
            });
        } else serviceCallBack.successful(upComingWrapper);
    }

    public void getNowPlayingMovies(Context context, final boolean loadMore, int page, final ServiceCallBack serviceCallBack) {
        if (nowPlayingWrapper == null || loadMore) {
            final Call<MoviesWrapper> upComingCall = apiInterface.getMovies("now_playing", PreferencesHelper.getLanguage(context), "US", page);
            upComingCall.enqueue(new Callback<MoviesWrapper>() {
                @Override
                public void onResponse(Call<MoviesWrapper> call, Response<MoviesWrapper> response) {
                    if (!loadMore)
                        nowPlayingWrapper = response.body();
                    serviceCallBack.successful(response.body());
                }

                @Override
                public void onFailure(Call<MoviesWrapper> call, Throwable t) {
                }
            });
        } else serviceCallBack.successful(nowPlayingWrapper);
    }

    public void getPopularMovies(Context context, final boolean loadMore, int page, final ServiceCallBack serviceCallBack) {
        if (popularWrapper == null || loadMore) {
            final Call<MoviesWrapper> upComingCall = apiInterface.getMovies("popular", PreferencesHelper.getLanguage(context), "US", page);
            upComingCall.enqueue(new Callback<MoviesWrapper>() {
                @Override
                public void onResponse(Call<MoviesWrapper> call, Response<MoviesWrapper> response) {
                    if (!loadMore)
                        popularWrapper = response.body();
                    serviceCallBack.successful(response.body());
                }

                @Override
                public void onFailure(Call<MoviesWrapper> call, Throwable t) {
                }
            });
        } else serviceCallBack.successful(popularWrapper);
    }

    public void getTopRatedMovies(Context context, final boolean loadMore, int page, final ServiceCallBack serviceCallBack) {
        if (topRatedWrapper == null || loadMore) {
            final Call<MoviesWrapper> upComingCall = apiInterface.getMovies("top_rated", PreferencesHelper.getLanguage(context), "US", page);
            upComingCall.enqueue(new Callback<MoviesWrapper>() {
                @Override
                public void onResponse(Call<MoviesWrapper> call, Response<MoviesWrapper> response) {
                    if (!loadMore)
                        topRatedWrapper = response.body();
                    serviceCallBack.successful(response.body());
                }

                @Override
                public void onFailure(Call<MoviesWrapper> call, Throwable t) {
                }
            });
        } else serviceCallBack.successful(topRatedWrapper);
    }

    public interface ServiceCallBack {
        void successful(MoviesWrapper response);
    }
}
