package com.gdevelopers.movies.fragments;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.gdevelopers.movies.activities.MainActivity;
import com.gdevelopers.movies.R;
import com.gdevelopers.movies.adapters.GenreMovieAdapter;
import com.gdevelopers.movies.helpers.Constants;
import com.gdevelopers.movies.helpers.OnClickHelper;
import com.gdevelopers.movies.model.KFragment;
import com.gdevelopers.movies.model.KObject;
import com.gdevelopers.movies.model.ModelService;
import com.gdevelopers.movies.objects.Movie;
import com.gdevelopers.movies.objects.Section;
import com.gdevelopers.movies.activities.TVDetailsActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


@SuppressWarnings("WeakerAccess")
public class FragmentSearchResults extends KFragment implements GenreMovieAdapter.OnLoadMoreListener {
    private HashMap<String, String> hashMap;
    private ModelService service;
    private List<Movie> movieList;
    private boolean loadMore = false;
    private int currentPage, totalPages;
    private List<Movie> mItems;
    private GenreMovieAdapter adapter;
    @BindView(R.id.search_results)
    RecyclerView moviesRv;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.empty_layout)
    TextView emptyLl;
    private String type;
    private MainActivity activity;
    private Unbinder unbinder;

    public void setType(String type) {
        this.type = type;
    }

    public void setHashMap(HashMap<String, String> hashMap) {
        this.hashMap = hashMap;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_search_results, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        activity = (MainActivity) getActivity();
        assert activity != null;
        activity.setVisibleFragment(this);
        service = activity.getService();

        moviesRv.setLayoutManager(new LinearLayoutManager(getContext()));

        this.update(service, true);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        activity.getAddFab().hide();
    }

    @Override
    public void serviceResponse(int responseID, List<KObject> objects) {
        progressBar.setVisibility(View.GONE);
        emptyLl.setVisibility(objects == null || objects.isEmpty() ? View.VISIBLE : View.GONE);
        moviesRv.setVisibility(objects == null || objects.isEmpty() ? View.GONE : View.VISIBLE);
        if (responseID == Constants.ADVANCED_SEARCH && (objects != null && !objects.isEmpty())) {
            if (!loadMore)
                movieList = new ArrayList<>();
            else mItems.remove(mItems.size() - 1);

            Section section = (Section) objects.get(0);
            currentPage = (int) section.id();
            totalPages = section.getTotalPages();
            movieList.addAll(section.getMovieList());

            if (!loadMore) {
                adapter = new GenreMovieAdapter(getContext(), movieList);
                moviesRv.setAdapter(adapter);
            } else adapter.notifyDataChanged();

            adapter.setLoadMoreListener(currentPage == totalPages ? null : this);
            adapter.setOnItemClickListener((position, imageView) -> {
                Movie movie = (Movie) adapter.getItem(position);
                if (type.equals("movie")) {
                    OnClickHelper.movieClicked(getContext(), movie.getTitle(), movie.getPosterPath(),
                            String.valueOf(movie.id()), imageView);
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        imageView.setTransitionName(getString(R.string.movie_poster));
                    }
                    Intent intent = new Intent(getContext(), TVDetailsActivity.class);
                    intent.putExtra("title", movie.getTitle());
                    intent.putExtra("image", movie.getPosterPath());
                    intent.putExtra("id", String.valueOf(movie.id()));
                    Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, imageView, getString(R.string.movie_poster)).toBundle();
                    startActivity(intent, bundle);
                }

            });
        }
    }


    @Override
    public void update(ModelService service, boolean reload) {
        progressBar.setVisibility(View.VISIBLE);
        service.getAdvancedSearch(type, "1", hashMap, false, reload);
    }

    private void loadMore(final GenreMovieAdapter adapter) {
        mItems = adapter.getmItems();
        final Movie movie = new Movie(0);
        movie.setType("load");
        //add loading progress view
        mItems.add(movie);
        adapter.notifyItemInserted(mItems.size() - 1);

        loadMore = true;

        final Handler handler = new Handler();
        handler.postDelayed(() -> service.getAdvancedSearch(type, String.valueOf(currentPage + 1), hashMap, true, true), 500);
    }

    @Override
    public void onLoadMore(GenreMovieAdapter adapter) {
        if (currentPage == totalPages)
            return;

        loadMore(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
