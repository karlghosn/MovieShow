package com.gdevelopers.movies.objects;

import android.content.Context;

import com.gdevelopers.movies.helpers.PreferencesHelper;
import com.gdevelopers.movies.rest.ApiClient;
import com.gdevelopers.movies.rest.ApiInterface;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TVShowService {
    private static TVShowService tvShowService;
    private final ApiInterface apiInterface;
    private TVShowWrapper onTVWrapper;
    private TVShowWrapper airingTodayWrapper;
    private TVShowWrapper popularWrapper;
    private TVShowWrapper topRatedWrapper;

    private TVShowService() {
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
    }

    public static TVShowService getInstance() {
        if (tvShowService == null)
            tvShowService = new TVShowService();
        return tvShowService;
    }

    public void getOnTV(Context context, final boolean loadMore, int page, final ServiceCallBack serviceCallBack) {
        if (onTVWrapper == null || loadMore) {
            final Call<TVShowWrapper> upComingCall = apiInterface.getTVShows("on_the_air", PreferencesHelper.getLanguage(context), page);
            upComingCall.enqueue(new Callback<TVShowWrapper>() {
                @Override
                public void onResponse(Call<TVShowWrapper> call, Response<TVShowWrapper> response) {
                    if (!loadMore)
                        onTVWrapper = response.body();
                    serviceCallBack.successful(response.body());
                }

                @Override
                public void onFailure(Call<TVShowWrapper> call, Throwable t) {
                }
            });
        } else serviceCallBack.successful(onTVWrapper);
    }

    public void getAiringToday(Context context, final boolean loadMore, int page, final ServiceCallBack serviceCallBack) {
        if (airingTodayWrapper == null || loadMore) {
            final Call<TVShowWrapper> upComingCall = apiInterface.getTVShows("airing_today", PreferencesHelper.getLanguage(context), page);
            upComingCall.enqueue(new Callback<TVShowWrapper>() {
                @Override
                public void onResponse(Call<TVShowWrapper> call, Response<TVShowWrapper> response) {
                    if (!loadMore)
                        airingTodayWrapper = response.body();
                    serviceCallBack.successful(response.body());
                }

                @Override
                public void onFailure(Call<TVShowWrapper> call, Throwable t) {
                }
            });
        } else serviceCallBack.successful(airingTodayWrapper);
    }

    public void getPopular(Context context, final boolean loadMore, int page, final ServiceCallBack serviceCallBack) {
        if (popularWrapper == null || loadMore) {
            final Call<TVShowWrapper> upComingCall = apiInterface.getTVShows("popular", PreferencesHelper.getLanguage(context), page);
            upComingCall.enqueue(new Callback<TVShowWrapper>() {
                @Override
                public void onResponse(Call<TVShowWrapper> call, Response<TVShowWrapper> response) {
                    if (!loadMore)
                        popularWrapper = response.body();
                    serviceCallBack.successful(response.body());
                }

                @Override
                public void onFailure(Call<TVShowWrapper> call, Throwable t) {
                }
            });
        } else serviceCallBack.successful(popularWrapper);
    }

    public void getTopRated(Context context, final boolean loadMore, int page, final ServiceCallBack serviceCallBack) {
        if (topRatedWrapper == null || loadMore) {
            final Call<TVShowWrapper> upComingCall = apiInterface.getTVShows("top_rated", PreferencesHelper.getLanguage(context), page);
            upComingCall.enqueue(new Callback<TVShowWrapper>() {
                @Override
                public void onResponse(Call<TVShowWrapper> call, Response<TVShowWrapper> response) {
                    if (!loadMore)
                        topRatedWrapper = response.body();
                    serviceCallBack.successful(response.body());
                }

                @Override
                public void onFailure(Call<TVShowWrapper> call, Throwable t) {
                }
            });
        } else serviceCallBack.successful(topRatedWrapper);
    }

    public interface ServiceCallBack {
        void successful(TVShowWrapper response);
    }
}
