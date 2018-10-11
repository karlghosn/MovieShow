package com.gdevelopers.movies.rest.services;

import com.gdevelopers.movies.rest.ApiClient;
import com.gdevelopers.movies.rest.ApiInterface;
import com.gdevelopers.movies.wrappers.PeopleWrapper;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PeopleService {
    private static PeopleService peopleService;
    private final ApiInterface apiInterface;
    private PeopleWrapper popularWrapper;

    private PeopleService() {
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
    }

    public static PeopleService getInstance() {
        if (peopleService == null)
            peopleService = new PeopleService();
        return peopleService;
    }

    public void getPopularPeople(final boolean loadMore, int page, final ServiceCallBack serviceCallBack) {
        if (popularWrapper == null || loadMore) {
            final Call<PeopleWrapper> upComingCall = apiInterface.getPopularPeople(page);
            upComingCall.enqueue(new Callback<PeopleWrapper>() {
                @Override
                public void onResponse(Call<PeopleWrapper> call, Response<PeopleWrapper> response) {
                    if (!loadMore)
                        popularWrapper = response.body();
                    serviceCallBack.successful(response.body());
                }

                @Override
                public void onFailure(Call<PeopleWrapper> call, Throwable t) {
                }
            });
        } else serviceCallBack.successful(popularWrapper);
    }

    public interface ServiceCallBack {
        void successful(PeopleWrapper response);
    }
}
