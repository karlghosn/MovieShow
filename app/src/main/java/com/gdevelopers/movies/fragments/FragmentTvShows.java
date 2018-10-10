package com.gdevelopers.movies.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.gdevelopers.movies.R;
import com.gdevelopers.movies.activities.MainActivity;
import com.gdevelopers.movies.activities.TVDetailsActivity;
import com.gdevelopers.movies.adapters.TVPageAdapter;
import com.gdevelopers.movies.helpers.Connection;
import com.gdevelopers.movies.helpers.Constants;
import com.gdevelopers.movies.helpers.DialogHelper;
import com.gdevelopers.movies.model.KFragment;
import com.gdevelopers.movies.model.KObject;
import com.gdevelopers.movies.model.ModelService;
import com.gdevelopers.movies.objects.Section;
import com.gdevelopers.movies.objects.TVShow;
import com.gdevelopers.movies.objects.TVShowService;
import com.gdevelopers.movies.objects.TVShowWrapper;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


@SuppressWarnings("WeakerAccess")
public class FragmentTvShows extends KFragment implements TVPageAdapter.OnLoadMoreListener, TVPageAdapter.OnItemClickListener {
    private MainActivity activity;
    @BindView(R.id.on_air_list)
    RecyclerView onAirRv;
    @BindView(R.id.airing_today_list)
    RecyclerView airingTodayRv;
    @BindView(R.id.popular_list)
    RecyclerView popularRv;
    @BindView(R.id.top_rated_list)
    RecyclerView topRatedRv;
    private TVPageAdapter.OnLoadMoreListener onLoadMoreListener = this;
    private TVPageAdapter.OnItemClickListener onItemClickListener = this;
    private TVPageAdapter onAirAdapter;
    private TVPageAdapter airingTodayAdapter;
    private TVPageAdapter popularAdapter;
    private TVPageAdapter topRatedAdapter;
    private List<TVShow> onAirList;
    private List<TVShow> airingTodayList;
    private List<TVShow> popularList;
    private List<TVShow> topRatedList;
    private boolean loadMore = false;
    private List<TVShow> mItems;
    private Context context;
    @BindView(R.id.progressBar1)
    ProgressBar progressBar1;
    @BindView(R.id.progressBar2)
    ProgressBar progressBar2;
    @BindView(R.id.progressBar3)
    ProgressBar progressBar3;
    @BindView(R.id.progressBar4)
    ProgressBar progressBar4;
    @BindView(R.id.container_layout)
    LinearLayout containerLayout;
    @BindView(R.id.offline_layout)
    LinearLayout offlineLayout;
    private Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tv_shows, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        activity = (MainActivity) getActivity();
        context = getContext();
        assert activity != null;
        activity.setVisibleFragment(this);
        ModelService service = activity.getService();

        onAirRv.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        onAirRv.setNestedScrollingEnabled(false);
        airingTodayRv.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        airingTodayRv.setNestedScrollingEnabled(false);
        popularRv.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        popularRv.setNestedScrollingEnabled(false);
        topRatedRv.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        topRatedRv.setNestedScrollingEnabled(false);

        fetchOnAir(1);

        fetchAiringToday(1);

        fetchPopular(1);

        fetchTopRated(1);

        return rootView;
    }

    private void fetchOnAir(int page) {
        TVShowService.getInstance().getOnTV(context, loadMore, page, new TVShowService.ServiceCallBack() {
            @Override
            public void successful(TVShowWrapper response) {
                if (!loadMore) {
                    onAirList = new ArrayList<>();
                    onAirAdapter = new TVPageAdapter(getContext(), onAirList, Constants.STRINGS.ON_AIR);
                    onAirRv.setAdapter(onAirAdapter);
                } else mItems.remove(mItems.size() - 1);


                onAirAdapter.setCurrentPage(response.getPage());
                onAirAdapter.setTotalPages(response.getTotalPages());
                onAirList.addAll(response.getTvShows());

                onAirAdapter.notifyDataChanged();

                onAirAdapter.setLoadMoreListener(onLoadMoreListener);
                onAirAdapter.setOnItemClickListener(onItemClickListener);
                progressBar1.setVisibility(View.GONE);
            }
        });
    }

    private void fetchAiringToday(int page) {
        TVShowService.getInstance().getAiringToday(context, loadMore, page, new TVShowService.ServiceCallBack() {
            @Override
            public void successful(TVShowWrapper response) {
                if (!loadMore) {
                    airingTodayList = new ArrayList<>();
                    airingTodayAdapter = new TVPageAdapter(getContext(), airingTodayList, Constants.STRINGS.AIRING_TODAY);
                    airingTodayRv.setAdapter(airingTodayAdapter);
                } else mItems.remove(mItems.size() - 1);


                airingTodayAdapter.setCurrentPage(response.getPage());
                airingTodayAdapter.setTotalPages(response.getTotalPages());
                airingTodayList.addAll(response.getTvShows());


                airingTodayAdapter.notifyDataChanged();

                airingTodayAdapter.setLoadMoreListener(onLoadMoreListener);
                airingTodayAdapter.setOnItemClickListener(onItemClickListener);
                progressBar2.setVisibility(View.GONE);
            }
        });
    }

    private void fetchPopular(int page) {
        TVShowService.getInstance().getPopular(context, loadMore, page, new TVShowService.ServiceCallBack() {
            @Override
            public void successful(TVShowWrapper response) {
                if (!loadMore) {
                    popularList = new ArrayList<>();
                    popularAdapter = new TVPageAdapter(getContext(), popularList, Constants.STRINGS.POPULAR);
                    popularRv.setAdapter(popularAdapter);
                } else mItems.remove(mItems.size() - 1);

                popularAdapter.setCurrentPage(response.getPage());
                popularAdapter.setTotalPages(response.getTotalPages());
                popularList.addAll(response.getTvShows());

                popularAdapter.notifyDataChanged();

                popularAdapter.setLoadMoreListener(onLoadMoreListener);
                popularAdapter.setOnItemClickListener(onItemClickListener);
                progressBar3.setVisibility(View.GONE);
            }
        });
    }

    private void fetchTopRated(int page) {
        TVShowService.getInstance().getTopRated(context, loadMore, page, new TVShowService.ServiceCallBack() {
            @Override
            public void successful(TVShowWrapper response) {
                if (!loadMore) {
                    topRatedList = new ArrayList<>();
                    topRatedAdapter = new TVPageAdapter(getContext(), topRatedList, Constants.STRINGS.TOP_RATED);
                    topRatedRv.setAdapter(topRatedAdapter);
                } else mItems.remove(mItems.size() - 1);

                topRatedAdapter.setCurrentPage(response.getPage());
                topRatedAdapter.setTotalPages(response.getTotalPages());
                topRatedList.addAll(response.getTvShows());

                topRatedAdapter.notifyDataChanged();

                topRatedAdapter.setLoadMoreListener(onLoadMoreListener);
                topRatedAdapter.setOnItemClickListener(onItemClickListener);
                progressBar4.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void serviceResponse(int responseID, List<KObject> objects) {
    }

    @Override
    public void update(ModelService service, boolean reload) {
    }

    @Override
    public void onItemClick(TVShow tvShow, View v) {
        ImageView imageView = v.findViewById(R.id.movie_image);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            imageView.setTransitionName(getString(R.string.movie_poster));
        }
        Intent intent = new Intent(getContext(), TVDetailsActivity.class);
        intent.putExtra("title", tvShow.getName());
        intent.putExtra("image", tvShow.getPosterPath());
        intent.putExtra("id", String.valueOf(tvShow.getId()));
        Bundle bundle = null;
        if (getContext() != null) {
            bundle = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) getContext(), imageView, getString(R.string.movie_poster)).toBundle();
        }
        startActivity(intent, bundle);
    }

    @Override
    public void onLoadMore(TVPageAdapter adapter) {
        loadMore(adapter);
    }

    private void isOffline() {
        containerLayout.setVisibility(View.GONE);
        offlineLayout.setVisibility(View.VISIBLE);
    }

    private void loadMore(final TVPageAdapter adapter) {
        mItems = adapter.getmItems();
        final TVShow tvShow = new TVShow(0);
        tvShow.setType("load");
        //add loading progress view
        mItems.add(tvShow);
        adapter.notifyItemInserted(mItems.size() - 1);

        loadMore = true;
        final String type = adapter.getType();
        final int current_page = adapter.getCurrentPage();
        Log.d("Current Page", String.valueOf(current_page));

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                switch (type) {
                    case Constants.STRINGS.ON_AIR:
                        fetchOnAir(current_page + 1);
                        break;
                    case Constants.STRINGS.AIRING_TODAY:
                        fetchAiringToday(current_page + 1);
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
    public void onResume() {
        super.onResume();
        if (activity.getSupportActionBar() != null)
            activity.getSupportActionBar().setTitle("TV Shows");

        activity.getNavigationView().setCheckedItem(R.id.nav_tv_shows);
        activity.getAddFab().hide();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
