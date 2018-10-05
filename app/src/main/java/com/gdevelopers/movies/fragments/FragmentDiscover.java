package com.gdevelopers.movies.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindArray;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.gdevelopers.movies.activities.MainActivity;
import com.gdevelopers.movies.R;
import com.gdevelopers.movies.adapters.CastAdapter;
import com.gdevelopers.movies.adapters.DiscoverGenreAdapter;
import com.gdevelopers.movies.helpers.Connection;
import com.gdevelopers.movies.helpers.Constants;
import com.gdevelopers.movies.helpers.OfflineLayout;
import com.gdevelopers.movies.model.KFragment;
import com.gdevelopers.movies.model.KObject;
import com.gdevelopers.movies.model.ModelService;
import com.gdevelopers.movies.objects.Genre;

import java.util.ArrayList;
import java.util.List;


@SuppressWarnings("WeakerAccess")
public class FragmentDiscover extends KFragment {
    private MainActivity activity;

    @BindView(R.id.discover_list)
    RecyclerView discoverRv;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    private ModelService service;
    private Context context;
    private View rootView;
    @BindView(R.id.offline_layout)
    LinearLayout offlineLayout;
    @BindArray(R.array.genres_images)
    String[] images;
    private Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_discover, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        activity = (MainActivity) getActivity();
        context = getContext();
        if (activity != null) {
            activity.setVisibleFragment(this);
            service = activity.getService();
        }

        discoverRv.setLayoutManager(new GridLayoutManager(context, 3));

        this.update(service, false);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (activity.getSupportActionBar() != null)
            activity.getSupportActionBar().setTitle(R.string.discover);
        activity.getNavigationView().setCheckedItem(R.id.nav_discover);
        activity.getAddFab().hide();
    }

    @Override
    public void serviceResponse(int responseID, List<KObject> objects) {
        progressBar.setVisibility(View.GONE);
        List<Genre> genreList = new ArrayList<>();
        if (responseID == Constants.GENRES && objects != null) {
            for (KObject object : objects) {
                Genre genre = (Genre) object;
                genreList.add(genre);
            }

            final DiscoverGenreAdapter adapter = new DiscoverGenreAdapter(context, genreList, images);
            discoverRv.setAdapter(adapter);

            adapter.setOnItemClickListener(new CastAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(int position, ImageView imageView) {
                    Genre genre = (Genre) adapter.getItem(position);
                    FragmentDiscoverMovies discoverMovies = new FragmentDiscoverMovies();
                    discoverMovies.setGenreId(String.valueOf(genre.id()));
                    discoverMovies.setTitle(genre.getName());
                    activity.getSupportFragmentManager().beginTransaction()
                            .replace(R.id.content_main, discoverMovies)
                            .addToBackStack(null)
                            .commit();
                }
            });
        }
    }

    @Override
    public void update(ModelService service, boolean reload) {
        boolean hasConnection = Connection.isNetworkAvailable(context);
        isOnline(hasConnection);
        if (hasConnection && service != null) service.getGenres(context, reload);
        else OfflineLayout.init(context, rootView, this, service);
    }


    private void isOnline(boolean isOnline) {
        progressBar.setVisibility(isOnline ? View.VISIBLE : View.GONE);
        discoverRv.setVisibility(isOnline ? View.VISIBLE : View.GONE);
        offlineLayout.setVisibility(isOnline ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
