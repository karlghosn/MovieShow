package com.gdevelopers.movies.fragments;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.gdevelopers.movies.activities.ActorDetailsActivity;
import com.gdevelopers.movies.R;
import com.gdevelopers.movies.adapters.ShowAdapter;
import com.gdevelopers.movies.activities.MovieDetailsActivity;
import com.gdevelopers.movies.objects.Show;
import com.gdevelopers.movies.activities.TVDetailsActivity;

import java.util.List;


public class FragmentMoreShows extends Fragment {
    private List<Show> showList;
    private ActorDetailsActivity activity;
    private boolean isCrew;
    private ShowAdapter adapter;

    public void setShowList(List<Show> showList) {
        this.showList = showList;
    }

    public void setCrew(boolean crew) {
        isCrew = crew;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_more_shows, container, false);
        activity = (ActorDetailsActivity) getActivity();
        setHasOptionsMenu(true);
        RecyclerView showsRv = rootView.findViewById(R.id.more_list);
        showsRv.setLayoutManager(new GridLayoutManager(getContext(), 3));

        adapter = new ShowAdapter(getContext(), showList, isCrew);
        showsRv.setAdapter(adapter);

        adapter.setOnItemClickListener(new ShowAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Show show, View v) {
                ImageView imageView = v.findViewById(R.id.show_image);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    imageView.setTransitionName(getString(R.string.movie_poster));
                }
                if (show.getMediaType().equals("movie")) {
                    Intent intent = new Intent(getContext(), MovieDetailsActivity.class);
                    intent.putExtra("title", show.getTitle());
                    intent.putExtra("image", show.getPosterPath());
                    intent.putExtra("id", String.valueOf(show.getId()));
                    Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, imageView, getString(R.string.movie_poster)).toBundle();
                    startActivity(intent, bundle);
                } else if (show.getMediaType().equals("tv")) {
                    Intent intent = new Intent(getContext(), TVDetailsActivity.class);
                    intent.putExtra("title", show.getTitle());
                    intent.putExtra("image", show.getPosterPath());
                    intent.putExtra("id", String.valueOf(show.getId()));
                    Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, imageView, getString(R.string.movie_poster)).toBundle();
                    startActivity(intent, bundle);
                }
            }
        });
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_more, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (adapter != null)
                    adapter.getFilter().filter(newText);
                return false;
            }
        });
    }
}
