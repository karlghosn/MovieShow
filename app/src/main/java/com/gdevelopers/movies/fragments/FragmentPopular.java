package com.gdevelopers.movies.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.gdevelopers.movies.R;
import com.gdevelopers.movies.activities.MainActivity;
import com.gdevelopers.movies.adapters.PopularActorsAdapter;
import com.gdevelopers.movies.helpers.Connection;
import com.gdevelopers.movies.helpers.OfflineLayout;
import com.gdevelopers.movies.helpers.OnClickHelper;
import com.gdevelopers.movies.model.KFragment;
import com.gdevelopers.movies.model.KObject;
import com.gdevelopers.movies.model.ModelService;
import com.gdevelopers.movies.objects.Actor;
import com.gdevelopers.movies.rest.services.PeopleService;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


@SuppressWarnings("WeakerAccess")
public class FragmentPopular extends KFragment implements PopularActorsAdapter.OnItemClickListener, PopularActorsAdapter.OnLoadMoreListener {
    private MainActivity activity;
    @BindView(R.id.popular_people)
    RecyclerView peopleRv;
    private List<Actor> actorList;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    private List<Actor> mItems;
    private boolean loadMore = false;
    private PopularActorsAdapter adapter;
    private Context context;
    private View rootView;
    @BindView(R.id.offline_layout)
    LinearLayout offlineLayout;
    private Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_popular, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        activity = (MainActivity) getActivity();
        context = getContext();
        if (activity != null) {
            activity.setVisibleFragment(this);
        }

        peopleRv.setLayoutManager(new GridLayoutManager(getContext(), 3));

        fetchActors(1);

        return rootView;
    }


    private void fetchActors(int page) {
        PeopleService.getInstance().getPopularPeople(loadMore, page, response -> {
            if (!loadMore) {
                actorList = new ArrayList<>();
                adapter = new PopularActorsAdapter(getContext(), actorList, false);
                peopleRv.setAdapter(adapter);
            } else mItems.remove(mItems.size() - 1);

            adapter.setCurrentPage(response.getPage());
            adapter.setTotalPages(response.getTotalPages());
            actorList.addAll(response.getActorList());

            adapter.notifyDataChanged();

            adapter.setLoadMoreListener(this);
            adapter.setOnItemClickListener(this);
            progressBar.setVisibility(View.GONE);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        activity.getNavigationView().setCheckedItem(R.id.nav_popular);
        if (activity.getSupportActionBar() != null)
            activity.getSupportActionBar().setTitle(R.string.popular_people);
        activity.getAddFab().hide();
    }

    @Override
    public void serviceResponse(int responseID, List<KObject> objects) {
    }

    @Override
    public void update(ModelService service, boolean reload) {
        boolean hasConnection = Connection.isNetworkAvailable(context);
        isOnline(hasConnection);
        if (hasConnection && service != null) service.getPopularPeople("1", false, reload);
        else OfflineLayout.init(context, rootView, this, service);

    }

    private void isOnline(boolean isOnline) {
        progressBar.setVisibility(isOnline ? View.VISIBLE : View.GONE);
        peopleRv.setVisibility(isOnline ? View.VISIBLE : View.GONE);
        offlineLayout.setVisibility(isOnline ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onItemClick(Actor actor, ImageView imageView) {
        OnClickHelper.actorClicked(context, actor, imageView);
    }

    private void loadMore(final PopularActorsAdapter adapter) {
        mItems = adapter.getmItems();
        final Actor actor = new Actor(0);
        actor.setType("load");
        //add loading progress view
        mItems.add(actor);
        adapter.notifyItemInserted(mItems.size() - 1);
        int page = adapter.getCurrentPage() + 1;
        loadMore = true;

        final Handler handler = new Handler();
        handler.postDelayed(() -> fetchActors(page), 500);
    }

    @Override
    public void onLoadMore(PopularActorsAdapter adapter) {
        loadMore(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
