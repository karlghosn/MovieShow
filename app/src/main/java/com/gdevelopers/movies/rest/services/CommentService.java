package com.gdevelopers.movies.rest.services;

import com.gdevelopers.movies.helpers.MovieDB;
import com.gdevelopers.movies.objects.Comment;
import com.gdevelopers.movies.rest.ApiClient;
import com.gdevelopers.movies.rest.ApiInterface;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommentService {
    private static CommentService commentService;
    private final ApiInterface apiInterface;
    private Response<List<Comment>> listResponse;

    private CommentService() {
        apiInterface = ApiClient.getTraktClient().create(ApiInterface.class);
    }

    public static CommentService getInstance() {
        if (commentService == null)
            commentService = new CommentService();
        return commentService;
    }

    public void getComments(String id, final boolean loadMore, int page, final ServiceCallBack serviceCallBack) {
        if (listResponse == null || loadMore) {
            Call<List<Comment>> call = apiInterface.getTraktComments(id, MovieDB.CONTENT_TYPE, "2", MovieDB.TRAKT_TV_API_KEY, page);
            call.enqueue(new Callback<List<Comment>>() {
                @Override
                public void onResponse(Call<List<Comment>> call, Response<List<Comment>> response) {
                    if (!loadMore)
                        listResponse = response;

                    serviceCallBack.successful(response);
                }

                @Override
                public void onFailure(Call<List<Comment>> call, Throwable t) {

                }
            });
        } else serviceCallBack.successful(listResponse);
    }

    public interface ServiceCallBack {
        void successful(Response<List<Comment>> response);
    }
}
