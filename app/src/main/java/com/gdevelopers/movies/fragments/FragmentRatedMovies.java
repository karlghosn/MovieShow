package com.gdevelopers.movies.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.DisplayMetrics;
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

import com.gdevelopers.movies.activities.MainActivity;
import com.gdevelopers.movies.R;
import com.gdevelopers.movies.helpers.Connection;
import com.gdevelopers.movies.helpers.Constants;
import com.gdevelopers.movies.helpers.DeleteRatingTask;
import com.gdevelopers.movies.helpers.OfflineLayout;
import com.gdevelopers.movies.helpers.PreferencesHelper;
import com.gdevelopers.movies.model.KFragment;
import com.gdevelopers.movies.model.KObject;
import com.gdevelopers.movies.model.ModelService;
import com.gdevelopers.movies.activities.MovieDetailsActivity;
import com.gdevelopers.movies.objects.Movie;
import com.gdevelopers.movies.objects.Section;
import com.gdevelopers.movies.adapters.UserMoviesAdapter;

import java.util.ArrayList;
import java.util.List;


@SuppressWarnings("WeakerAccess")
public class FragmentRatedMovies extends KFragment implements UserMoviesAdapter.OnLoadMoreListener, DeleteRatingTask.OnDeleteCallBackListener {
    private MainActivity activity;
    private ModelService service;
    private boolean loadMore = false;
    private List<Movie> mItems;
    private List<Movie> movieList;
    private UserMoviesAdapter adapter;
    @BindView(R.id.favourite_list)
    RecyclerView favouritesRv;
    private int currentPage, totalPages;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    private String sortBy;
    private int pos = 1;
    private final DeleteRatingTask.OnDeleteCallBackListener onDeleteCallBackListener = this;
    private int deletedPos;
    @BindView(R.id.empty_layout)
    CardView emptyLayout;
    private Context context;
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
        context = getContext();
        setHasOptionsMenu(true);
        activity.setVisibleFragment(this);
        service = activity.getService();
        favouritesRv.setLayoutManager(new GridLayoutManager(context, calculateNoOfColumns(context)));

        TextView addWatchlistTv = rootView.findViewById(R.id.add_watchlist);
        TextView browseTv = rootView.findViewById(R.id.browse_text);
        browseTv.setText(R.string.browsing_lists_rated);
        String src = getString(R.string.tap_rated);
        SpannableString str = new SpannableString(src);
        int index = src.indexOf("@");
        Drawable d = ContextCompat.getDrawable(context, R.drawable.star_rate_yellow);
        if (d != null) d.setBounds(0, 0, 61, 61);
        assert d != null;
        ImageSpan span = new ImageSpan(d, ImageSpan.ALIGN_BOTTOM);
        str.setSpan(span, index, index + 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        addWatchlistTv.setText(str);
        RelativeLayout tryLayout = rootView.findViewById(R.id.try_layout);
        tryLayout.setVisibility(View.GONE);

        this.update(service, false);
        return rootView;
    }

    @Override
    public void serviceResponse(int responseID, List<KObject> objects) {
        progressBar.setVisibility(View.GONE);

        if (responseID == Constants.GET_RATED_MOVIES && objects != null && !objects.isEmpty()) {
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
                Intent intent = new Intent(context, MovieDetailsActivity.class);
                intent.putExtra("title", movie.getTitle());
                intent.putExtra("image", movie.getPosterPath());
                intent.putExtra("id", String.valueOf(movie.id()));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    imageView.setTransitionName(getString(R.string.movie_poster));
                }
                Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, imageView,
                        getString(R.string.movie_poster)).toBundle();
                startActivityForResult(intent, 1, bundle);
            });

            adapter.setOnRemoveListener((movie, pos) -> {
                deletedPos = pos;
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Delete Rating");
                builder.setMessage("Are you sure you want to delete your rating for " + movie.getTitle());
                builder.setNegativeButton("Cancel", null);
                builder.setPositiveButton("Delete", (dialogInterface, i) -> {
                    DeleteRatingTask deleteRatingTask = new DeleteRatingTask(context, String.valueOf(movie.id()));
                    deleteRatingTask.setOnCallBackListener(onDeleteCallBackListener);
                    deleteRatingTask.execute();
                });
                builder.create().show();
            });
        }
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
                            service.getRatedMovies(PreferencesHelper.getAccountId(context), PreferencesHelper.getSessionId(context)
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
            service.getRatedMovies(PreferencesHelper.getAccountId(context), PreferencesHelper.getSessionId(context)
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
            activity.getSupportActionBar().setTitle(R.string.rated);
        activity.getNavigationView().setCheckedItem(R.id.nav_rated);
        activity.getAddFab().hide();
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

    private void loadMore(final UserMoviesAdapter adapter) {
        mItems = adapter.getmItems();
        final Movie movie = new Movie(0);
        movie.setType("load");
        //add loading progress view
        mItems.add(movie);
        adapter.notifyItemInserted(mItems.size() - 1);

        loadMore = true;

        final Handler handler = new Handler();
        handler.postDelayed(() -> service.getRatedMovies(PreferencesHelper.getAccountId(context), PreferencesHelper.getSessionId(context)
                , String.valueOf(currentPage + 1), true, sortBy, true), 500);
    }

    @Override
    public void onLoadMore(UserMoviesAdapter adapter) {
        if (currentPage == totalPages)
            return;

        loadMore(adapter);
    }


    @Override
    public void onDeleteCallBack() {
        adapter.removeAt(deletedPos);
        final Handler handler = new Handler();
        handler.postDelayed(() -> update(service, true), 1000);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}