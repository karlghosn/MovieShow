package com.gdevelopers.movies.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.gdevelopers.movies.activities.MainActivity;
import com.gdevelopers.movies.R;
import com.gdevelopers.movies.helpers.Connection;
import com.gdevelopers.movies.helpers.Constants;
import com.gdevelopers.movies.model.KFragment;
import com.gdevelopers.movies.model.KObject;
import com.gdevelopers.movies.model.ModelService;
import com.gdevelopers.movies.adapters.MoviesPageAdapter;
import com.gdevelopers.movies.objects.Movie;
import com.gdevelopers.movies.objects.MovieService;
import com.gdevelopers.movies.objects.MoviesWrapper;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


@SuppressWarnings("WeakerAccess")
public class FragmentMovies extends KFragment implements MoviesPageAdapter.OnLoadMoreListener {
    private MainActivity activity;
    private ModelService service;
    @BindView(R.id.upcoming_list)
    RecyclerView upComingRv;
    @BindView(R.id.now_playing_list)
    RecyclerView nowPlayingRv;
    @BindView(R.id.popular_list)
    RecyclerView popularRv;
    @BindView(R.id.top_rated_list)
    RecyclerView topRatedRv;
    private MoviesPageAdapter.OnLoadMoreListener onLoadMoreListener = this;
    private MoviesPageAdapter upComingAdapter;
    private MoviesPageAdapter nowPlayingAdapter;
    private MoviesPageAdapter popularAdapter;
    private MoviesPageAdapter topRatedAdapter;
    private List<Movie> upComingList;
    private List<Movie> nowPlayingList;
    private List<Movie> popularList;
    private List<Movie> topRatedList;
    private boolean loadMore = false;
    private List<Movie> mItems;
    @BindView(R.id.progressBar1)
    ProgressBar progressBar1;
    @BindView(R.id.progressBar2)
    ProgressBar progressBar2;
    @BindView(R.id.progressBar3)
    ProgressBar progressBar3;
    @BindView(R.id.progressBar4)
    ProgressBar progressBar4;
    private Context context;
    @BindView(R.id.offline_layout)
    LinearLayout offlineLayout;
    @BindView(R.id.container_layout)
    LinearLayout containerLayout;
    private Unbinder unbinder;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movies, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        context = getContext();
        activity = (MainActivity) getActivity();
        if (activity != null) {
            service = activity.getService();
            activity.setVisibleFragment(this);
        }

        upComingRv.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        upComingRv.setNestedScrollingEnabled(false);
        nowPlayingRv.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        nowPlayingRv.setNestedScrollingEnabled(false);
        popularRv.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        popularRv.setNestedScrollingEnabled(false);
        topRatedRv.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        topRatedRv.setNestedScrollingEnabled(false);

        fetchUpComing(1);

        fetchNowPlaying(1);

        fetchPopular(1);

        fetchTopRated(1);


        this.update(service, false);
        return rootView;
    }

    private void fetchUpComing(int page) {
        MovieService.getInstance().getUpComingMovies(context, loadMore, page, new MovieService.ServiceCallBack() {
            @Override
            public void successful(MoviesWrapper response) {
                if (!loadMore) {
                    upComingList = new ArrayList<>();
                    upComingAdapter = new MoviesPageAdapter(getContext(), R.layout.home_list_item_layout, R.layout.horizontal_row_load, upComingList, Constants.STRINGS.UPCOMING);
                    upComingRv.setAdapter(upComingAdapter);
                } else mItems.remove(mItems.size() - 1);

                upComingAdapter.setCurrentPage(response.getPage());
                upComingAdapter.setTotalPages(response.getTotalPages());

                upComingList.addAll(response.getMovies());

                upComingAdapter.notifyDataChanged();

                upComingAdapter.setLoadMoreListener(onLoadMoreListener);
                progressBar1.setVisibility(View.GONE);
            }
        });
    }

    private void fetchNowPlaying(int page) {
        MovieService.getInstance().getNowPlayingMovies(context, loadMore, page, new MovieService.ServiceCallBack() {
            @Override
            public void successful(MoviesWrapper response) {
                if (!loadMore) {
                    nowPlayingList = new ArrayList<>();
                    nowPlayingAdapter = new MoviesPageAdapter(getContext(), R.layout.home_list_item_layout, R.layout.horizontal_row_load, nowPlayingList, Constants.STRINGS.NOW_PLAYING);
                    nowPlayingRv.setAdapter(nowPlayingAdapter);
                } else mItems.remove(mItems.size() - 1);

                nowPlayingAdapter.setCurrentPage(response.getPage());
                nowPlayingAdapter.setTotalPages(response.getTotalPages());
                nowPlayingList.addAll(response.getMovies());

                nowPlayingAdapter.notifyDataChanged();

                nowPlayingAdapter.setLoadMoreListener(onLoadMoreListener);
                progressBar2.setVisibility(View.GONE);
            }
        });
    }

    private void fetchPopular(int page) {
        MovieService.getInstance().getPopularMovies(context, loadMore, page, new MovieService.ServiceCallBack() {
            @Override
            public void successful(MoviesWrapper response) {
                if (!loadMore) {
                    popularList = new ArrayList<>();
                    popularAdapter = new MoviesPageAdapter(getContext(), R.layout.home_list_item_layout, R.layout.horizontal_row_load, popularList, Constants.STRINGS.POPULAR);
                    popularRv.setAdapter(popularAdapter);
                } else mItems.remove(mItems.size() - 1);

                popularAdapter.setCurrentPage(response.getPage());
                popularAdapter.setTotalPages(response.getTotalPages());
                popularList.addAll(response.getMovies());

                popularAdapter.notifyDataChanged();

                popularAdapter.setLoadMoreListener(onLoadMoreListener);
                progressBar3.setVisibility(View.GONE);
            }
        });
    }

    private void fetchTopRated(final int page) {
        MovieService.getInstance().getTopRatedMovies(context, loadMore, page, new MovieService.ServiceCallBack() {
            @Override
            public void successful(MoviesWrapper response) {
                if (!loadMore) {
                    topRatedList = new ArrayList<>();
                    topRatedAdapter = new MoviesPageAdapter(getContext(), R.layout.home_list_item_layout, R.layout.horizontal_row_load, topRatedList, Constants.STRINGS.TOP_RATED);
                    topRatedRv.setAdapter(topRatedAdapter);
                } else mItems.remove(mItems.size() - 1);

                topRatedAdapter.setCurrentPage(response.getPage());
                topRatedAdapter.setTotalPages(response.getTotalPages());
                topRatedList.addAll(response.getMovies());

                topRatedAdapter.notifyDataChanged();
                topRatedAdapter.setLoadMoreListener(onLoadMoreListener);
                progressBar4.setVisibility(View.GONE);
            }
        });

    }

    @Override
    public void serviceResponse(int responseID, List<KObject> objects) {
    }

    @Override
    public void update(ModelService service, boolean reload) {
        boolean hasConnection = Connection.isNetworkAvailable(context);
        if (hasConnection && service != null) {
//            service.getMovies(Constants.STRINGS.UPCOMING, "", false, Constants.UPCOMING, reload);
            /*service.getMovies(Constants.STRINGS.NOW_PLAYING, "", false, Constants.NOW_PLAYING, reload);
            service.getMovies(Constants.STRINGS.POPULAR, "", false, Constants.POPULAR, reload);
            service.getMovies(Constants.STRINGS.TOP_RATED, "", false, Constants.TOP_RATED, reload);*/
        } else isOffline();
    }

    private void loadMore(final MoviesPageAdapter adapter) {
        mItems = adapter.getmItems();
        final Movie movie = new Movie(0);
        movie.setType("load");
        //add loading progress view
        mItems.add(movie);
        adapter.notifyItemInserted(mItems.size() - 1);

        loadMore = true;

        final String type = adapter.getType();
        final int current_page = adapter.getCurrentPage();

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                switch (type) {
                    case Constants.STRINGS.UPCOMING:
                        fetchUpComing(current_page + 1);
                        break;
                    case Constants.STRINGS.NOW_PLAYING:
                        fetchNowPlaying(current_page + 1);
                        break;
                    case Constants.STRINGS.POPULAR:
                        fetchPopular(current_page + 1);
                        break;
                    case Constants.STRINGS.TOP_RATED:
                        fetchTopRated(current_page + 1);
                        break;
                }
            }
        }, 500);
    }

    @Override
    public void onLoadMore(MoviesPageAdapter adapter) {
        loadMore(adapter);
    }

    private void isOffline() {
        containerLayout.setVisibility(View.GONE);
        offlineLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (activity.getSupportActionBar() != null)
            activity.getSupportActionBar().setTitle(R.string.movies);

        activity.getNavigationView().setCheckedItem(R.id.nav_movies);
        activity.getAddFab().hide();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}


