package com.gdevelopers.movies.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gdevelopers.movies.R;
import com.gdevelopers.movies.activities.MainActivity;
import com.gdevelopers.movies.adapters.UserMoviesAdapter;
import com.gdevelopers.movies.helpers.ChangeMovieState;
import com.gdevelopers.movies.helpers.Connection;
import com.gdevelopers.movies.helpers.Constants;
import com.gdevelopers.movies.helpers.OfflineLayout;
import com.gdevelopers.movies.helpers.OnClickHelper;
import com.gdevelopers.movies.helpers.PreferencesHelper;
import com.gdevelopers.movies.model.KFragment;
import com.gdevelopers.movies.model.KObject;
import com.gdevelopers.movies.model.ModelService;
import com.gdevelopers.movies.objects.Movie;
import com.gdevelopers.movies.objects.Section;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


@SuppressWarnings("WeakerAccess")
public class FragmentWatchlist extends KFragment implements UserMoviesAdapter.OnLoadMoreListener, ChangeMovieState.OnCallBackListener {
    private MainActivity activity;
    private ModelService service;
    private boolean loadMore = false;
    private List<Movie> mItems;
    private List<Movie> movieList;
    private UserMoviesAdapter adapter;
    @BindView(R.id.favourite_list)
    RecyclerView favouritesRv;
    private int currentPage;
    private int totalPages;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    private String sortBy;
    private int pos = 1;
    private final ChangeMovieState.OnCallBackListener onCallBackListener = this;
    private int deletedPos;
    private Context context;
    @BindView(R.id.empty_layout)
    CardView emptyLayout;
    private View rootView;
    @BindView(R.id.offline_layout)
    LinearLayout offlineLayout;
    private Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_user_movies, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        activity = (MainActivity) getActivity();
        setHasOptionsMenu(true);
        context = getContext();
        activity.setVisibleFragment(this);
        service = activity.getService();
        favouritesRv.setLayoutManager(new GridLayoutManager(context, calculateNoOfColumns(context)));

        TextView addWatchlistTv = rootView.findViewById(R.id.add_watchlist);
        TextView browseTv = rootView.findViewById(R.id.browse_text);
        browseTv.setText(R.string.browsing_lists_watch);
        String src = getString(R.string.tap_watchlist);
        SpannableString str = new SpannableString(src);
        int index = src.indexOf('@');
        Drawable d = ContextCompat.getDrawable(context, R.drawable.ic_action_watchlist);
        assert d != null;
        d.setBounds(0, 0, 60, 60);
        ImageSpan span = new ImageSpan(d, ImageSpan.ALIGN_BOTTOM);
        str.setSpan(span, index, index + 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        addWatchlistTv.setText(str);
        RelativeLayout tryLayout = rootView.findViewById(R.id.try_layout);
        tryLayout.setVisibility(View.VISIBLE);
        final ImageView watchlistIv = rootView.findViewById(R.id.watchlist_icon);
        watchlistIv.setOnClickListener(view -> {
            Drawable.ConstantState state = watchlistIv.getDrawable().getConstantState();
            assert state != null;
            Drawable drawable = ContextCompat.getDrawable(context,
                    R.drawable.ic_action_watchlist);
            assert drawable != null;
            if (state.equals(drawable.getConstantState())) {
                watchlistIv.setImageResource(R.drawable.ic_action_watchlist_filled);
            } else watchlistIv.setImageResource(R.drawable.ic_action_watchlist);
        });


        this.update(service, false);
        return rootView;
    }

    @Override
    public void serviceResponse(int responseID, List<KObject> objects) {
        progressBar.setVisibility(View.GONE);
        if (responseID == Constants.GET_WATCHLIST_MOVIES && objects != null && !objects.isEmpty()) {
            if (!loadMore)
                movieList = new ArrayList<>();
            else mItems.remove(mItems.size() - 1);

            Section section = (Section) objects.get(0);
            boolean hasObjects = section.getMovieList().isEmpty();
            emptyLayout.setVisibility(hasObjects ? View.VISIBLE : View.GONE);
            favouritesRv.setVisibility(hasObjects ? View.GONE : View.VISIBLE);
            currentPage = (int) section.id();
            totalPages = section.getTotalPages();
            movieList.addAll(section.getMovieList());

            if (!loadMore) {
                adapter = new UserMoviesAdapter(context, movieList);
                favouritesRv.setAdapter(adapter);
            } else adapter.notifyDataChanged();

            adapter.setLoadMoreListener(this);
            adapter.setOnItemClickListener((position, imageView) -> {
                Movie movie = (Movie) adapter.getItem(position);
                OnClickHelper.movieClicked(context, movie.getTitle(), movie.getPosterPath(),
                        String.valueOf(movie.id()), imageView);
            });

            adapter.setOnRemoveListener((movie, pos) -> {
                deletedPos = pos;
                ChangeMovieState changeMovieState = new ChangeMovieState(context, "watchlist", String.valueOf(movie.id()),
                        service, fillJSON(movie), true);
                changeMovieState.setOnCallBackListener(onCallBackListener);
                changeMovieState.execute();
            });
        }
    }

    private JSONObject fillJSON(Movie movie) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("media_type", "movie");
            jsonObject.put("media_id", movie.id());
            jsonObject.put("watchlist", false);
        } catch (JSONException e) {
            Log.d(Constants.STRINGS.EXCEPTION, e.getMessage());
        }
        return jsonObject;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_sort, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.sort) {
            new AlertDialog.Builder(context)
                    .setTitle("Sort By")
                    .setSingleChoiceItems(new String[]{"Date created ascending", "Date created descending"}, pos, (dialog, whichButton) -> {
                        boolean isSimilar = pos == whichButton;
                        pos = whichButton;
                        sortBy = whichButton == 0 ? "created_at.asc" : "created_at.desc";
                        if (!isSimilar) {
                            service.getWatchlistMovies(PreferencesHelper.getAccountId(context), PreferencesHelper.getSessionId(context)
                                    , "1", false, sortBy, true);
                            progressBar.setVisibility(View.VISIBLE);
                        }
                        dialog.cancel();
                    })
                    .setPositiveButton("Cancel", (dialog, whichButton) -> dialog.cancel()).create().show();
        }
        if (id == R.id.sync) {
            loadMore = false;
            this.update(service, true);
        }
        return super.onOptionsItemSelected(item);
    }

    private int calculateNoOfColumns(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int width = (int) (getResources().getDimension(R.dimen.show_row_width) / getResources().getDisplayMetrics().density);
        return (int) (dpWidth / width);
    }

    @Override
    public void update(ModelService service, boolean reload) {
        boolean hasConnection = Connection.isNetworkAvailable(context);
        isOnline(hasConnection);
        if (hasConnection && service != null) {
            sortBy = "created_at.desc";
            service.getWatchlistMovies(PreferencesHelper.getAccountId(context), PreferencesHelper.getSessionId(context)
                    , "1", false, sortBy, reload);
        } else OfflineLayout.init(context, rootView, this, service);
    }

    private void isOnline(boolean isOnline) {
        progressBar.setVisibility(isOnline ? View.VISIBLE : View.GONE);
        favouritesRv.setVisibility(isOnline ? View.VISIBLE : View.GONE);
        offlineLayout.setVisibility(isOnline ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (activity.getSupportActionBar() != null)
            activity.getSupportActionBar().setTitle(R.string.watchlist);
        activity.getNavigationView().setCheckedItem(R.id.nav_watchlist);
        activity.getAddFab().hide();
    }

    private void loadMore(final UserMoviesAdapter adapter) {
        mItems = adapter.getmItems();
        final Movie movie = new Movie(0);
        movie.setType("load");
        //add loading progress view
        mItems.add(movie);
        adapter.notifyItemInserted(mItems.size() - 1);

        loadMore = true;

        final Handler handler = new Handler();
        handler.postDelayed(() -> service.getWatchlistMovies(PreferencesHelper.getAccountId(context), PreferencesHelper.getSessionId(context)
                , String.valueOf(currentPage + 1), true, sortBy, true), 500);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                boolean refresh = data.getBooleanExtra("refresh", false);
                if (refresh)
                    this.update(service, true);
            }
        }
    }

    @Override
    public void onLoadMore(UserMoviesAdapter adapter) {
        if (currentPage == totalPages)
            return;

        loadMore(adapter);
    }

    @Override
    public void onCallBack() {
        adapter.removeAt(deletedPos);
        this.update(service, true);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}