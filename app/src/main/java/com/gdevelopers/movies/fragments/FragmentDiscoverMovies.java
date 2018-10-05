package com.gdevelopers.movies.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.gdevelopers.movies.activities.MainActivity;
import com.gdevelopers.movies.R;
import com.gdevelopers.movies.adapters.GenreMovieAdapter;
import com.gdevelopers.movies.helpers.Connection;
import com.gdevelopers.movies.helpers.Constants;
import com.gdevelopers.movies.helpers.DialogHelper;
import com.gdevelopers.movies.helpers.OnClickHelper;
import com.gdevelopers.movies.model.KFragment;
import com.gdevelopers.movies.model.KObject;
import com.gdevelopers.movies.model.ModelService;
import com.gdevelopers.movies.objects.Movie;
import com.gdevelopers.movies.objects.Section;

import java.util.ArrayList;
import java.util.List;


@SuppressWarnings("WeakerAccess")
public class FragmentDiscoverMovies extends KFragment implements GenreMovieAdapter.OnLoadMoreListener {

    private MainActivity activity;
    private ModelService service;
    @BindView(R.id.discover_movies_list)
    RecyclerView moviesRv;
    private String genreId;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    private String title;
    private int currentPage, totalPages;
    private boolean loadMore = false;
    private List<Movie> mItems;
    private List<Movie> movieList;
    private GenreMovieAdapter adapter;
    private Context context;
    private Unbinder unbinder;

    void setGenreId(String genreId) {
        this.genreId = genreId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_discover_movies, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        activity = (MainActivity) getActivity();
        context = getContext();
        if (activity != null) {
            activity.setVisibleFragment(this);
            service = activity.getService();
        }

        moviesRv.setLayoutManager(new LinearLayoutManager(getContext()));

        this.update(service, true);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (activity.getSupportActionBar() != null)
            activity.getSupportActionBar().setTitle(title);
        activity.getNavigationView().setCheckedItem(R.id.nav_discover);
        activity.getAddFab().hide();
    }

    @Override
    public void serviceResponse(int responseID, List<KObject> objects) {
        progressBar.setVisibility(View.GONE);
        if (responseID == Constants.DISCOVER_MOVIES && objects != null) {
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

            adapter.setLoadMoreListener(this);
            adapter.setOnItemClickListener(new GenreMovieAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(int position, ImageView imageView) {
                    Movie movie = (Movie) adapter.getItem(position);
                    OnClickHelper.movieClicked(getContext(), movie.getTitle(), movie.getPosterPath(),
                            String.valueOf(movie.id()), imageView);
                }
            });
        }
    }


    @Override
    public void update(ModelService service, boolean reload) {
        boolean hasConnection = Connection.isNetworkAvailable(context);
        if (hasConnection && service != null)
            service.getDiscoverMovies(genreId, "1", false, reload);
        else {
            progressBar.setVisibility(View.GONE);
            DialogHelper.noConnectionDialog(getContext());
        }

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
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                boolean hasConnection = Connection.isNetworkAvailable(context);
                if (hasConnection && service != null) {
                    service.getDiscoverMovies(genreId, String.valueOf(currentPage + 1), true, true);
                } else DialogHelper.noConnectionDialog(getContext());
            }
        }, 500);
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
