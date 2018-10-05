package com.gdevelopers.movies.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.gdevelopers.movies.R;
import com.gdevelopers.movies.activities.EpisodesActivity;
import com.gdevelopers.movies.helpers.Connection;
import com.gdevelopers.movies.helpers.Constants;
import com.gdevelopers.movies.helpers.DialogHelper;
import com.gdevelopers.movies.model.KFragment;
import com.gdevelopers.movies.model.KObject;
import com.gdevelopers.movies.model.ModelService;
import com.gdevelopers.movies.objects.Episode;
import com.gdevelopers.movies.adapters.EpisodesAdapter;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;

import java.util.ArrayList;
import java.util.List;


@SuppressWarnings("WeakerAccess")
public class FragmentEpisodes extends KFragment {
    private EpisodesActivity activity;
    private String id;
    private String number;
    @BindView(R.id.episodes_list)
    RecyclerView episodesRv;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    private ModelService service;
    private Context context;
    private Unbinder unbinder;

    public void setId(String id) {
        this.id = id;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_episodes, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        activity = (EpisodesActivity) getActivity();
        context = getContext();
        if (activity != null) {
            activity.setVisibleFragment(this);
            service = activity.getService();
        }

        episodesRv.setLayoutManager(new LinearLayoutManager(getContext()));

        this.update(service, true);
        return rootView;
    }

    @Override
    public void serviceResponse(int responseID, List<KObject> objects) {
        List<Episode> episodeList = new ArrayList<>();
        progressBar.setVisibility(View.GONE);
        if (responseID == Constants.EPISODES && objects != null) {
            for (KObject object : objects) {
                Episode episode = (Episode) object;
                episodeList.add(episode);
            }
            final EpisodesAdapter adapter = new EpisodesAdapter(getContext(), episodeList);
            episodesRv.setAdapter(adapter);

            adapter.setOnItemClickListener(new EpisodesAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    Episode episode = (Episode) adapter.getItem(position);
                    FragmentEpisodeDetails fragmentEpisodeDetails = new FragmentEpisodeDetails();
                    fragmentEpisodeDetails.setTvId(id);
                    fragmentEpisodeDetails.setEpisode(episode);
                    activity.getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.left_to_right, R.anim.right_to_left)
                            .replace(R.id.content_episodes, fragmentEpisodeDetails)
                            .addToBackStack(null)
                            .commit();
                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        activity.getToolbar().setBackgroundColor(ScrollUtils.getColorWithAlpha(1, ContextCompat.getColor(context, R.color.colorPrimary)));
        if (activity.getSupportActionBar() != null)
            activity.getSupportActionBar().setTitle(getString(R.string.season_number, number));
    }

    @Override
    public void update(ModelService service, boolean reload) {
        progressBar.setVisibility(View.VISIBLE);
        boolean hasConnection = Connection.isNetworkAvailable(context);
        if (hasConnection && service != null) service.getEpisodes(id, number, reload);
        else {
            progressBar.setVisibility(View.GONE);
            DialogHelper.noConnectionDialog(getContext());
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

}
