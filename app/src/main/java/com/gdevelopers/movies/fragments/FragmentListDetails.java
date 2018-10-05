package com.gdevelopers.movies.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.gdevelopers.movies.adapters.MovieListAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;

import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.gdevelopers.movies.activities.MainActivity;
import com.gdevelopers.movies.R;
import com.gdevelopers.movies.helpers.Connection;
import com.gdevelopers.movies.helpers.Constants;
import com.gdevelopers.movies.helpers.DialogHelper;
import com.gdevelopers.movies.helpers.OnClickHelper;
import com.gdevelopers.movies.helpers.PostRetrofit;
import com.gdevelopers.movies.helpers.PreferencesHelper;
import com.gdevelopers.movies.helpers.Response;
import com.gdevelopers.movies.helpers.SimpleItemTouchHelperCallback;
import com.gdevelopers.movies.model.KFragment;
import com.gdevelopers.movies.model.KObject;
import com.gdevelopers.movies.model.ModelService;
import com.gdevelopers.movies.objects.Movie;
import com.gdevelopers.movies.rest.ApiClient;
import com.gdevelopers.movies.rest.ApiInterface;
import com.gdevelopers.movies.objects.Search;
import com.gdevelopers.movies.adapters.SearchAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;


@SuppressWarnings("WeakerAccess")
public class FragmentListDetails extends KFragment implements MovieListAdapter.OnItemDeleted {
    private String listId;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.list_details)
    RecyclerView moviesRv;
    @BindView(R.id.empty_layout)
    CardView emptyLayout;
    private ViewGroup container;
    private ModelService service;
    private RecyclerView searchRv;
    private MovieListAdapter adapter;
    private List<Movie> movieList;
    private MainActivity activity;
    private boolean clicked = false;
    private String title;
    private Context context;
    private AlertDialog alertDialog;
    private Unbinder unbinder;

    void setListId(String listId) {
        this.listId = listId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_list_details, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        this.container = container;
        context = getContext();
        activity = (MainActivity) getActivity();
        if (activity != null) {
            service = activity.getService();
            activity.setVisibleFragment(this);
        }
        setHasOptionsMenu(true);

        moviesRv.setLayoutManager(new LinearLayoutManager(context));

        TextView addListTv = rootView.findViewById(R.id.add_watchlist);
        String src = getString(R.string.tap_add_list);
        SpannableString str = new SpannableString(src);
        int index = src.indexOf('@');
        Drawable d = ContextCompat.getDrawable(context, R.drawable.ic_action_list_add);
        assert d != null;
        d.setBounds(0, 0, 60, 60);
        ImageSpan span = new ImageSpan(d, ImageSpan.ALIGN_BOTTOM);
        str.setSpan(span, index, index + 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        addListTv.setText(str);


        this.update(service, true);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        FloatingActionButton fab = activity.getAddFab();
        fab.show();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Add Movie");
                View alertView = LayoutInflater.from(context).inflate(R.layout.search_dialog, container, false);
                builder.setView(alertView);
                TextInputLayout searchTl = alertView.findViewById(R.id.search_text_layout);
                searchRv = alertView.findViewById(R.id.movies_list);
                searchRv.setLayoutManager(new LinearLayoutManager(context));
                EditText editText = searchTl.getEditText();
                if (editText != null) {
                    editText.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                            //No Need
                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                            final String finalQuery = charSequence.toString();
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    if (service != null) {
                                        service.getSearch("movie", finalQuery);
                                    }
                                }
                            }).start();
                        }

                        @Override
                        public void afterTextChanged(Editable editable) {
                            //No Need
                        }
                    });
                }
                builder.setPositiveButton("Cancel", null);
                alertDialog = builder.create();
                alertDialog.show();
            }
        });
        if (activity.getSupportActionBar() != null)
            activity.getSupportActionBar().setTitle(title);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_list_details, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.clear_list) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(getString(R.string.clear_list, title));
            builder.setMessage("You are about to clear all movies from this list. Do you want to continue?");
            builder.setPositiveButton("Clear", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (movieList.isEmpty())
                        Snackbar.make(activity.findViewById(R.id.adView), "Cannot clear empty list",
                                Snackbar.LENGTH_SHORT).show();
                    else {
                        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
                        Call<Response> call = apiService.clearList(Integer.parseInt(listId), PreferencesHelper.getSessionId(context));
                        call.enqueue(new Callback<Response>() {
                            @Override
                            public void onResponse(@NonNull Call<Response> call, @NonNull retrofit2.Response<Response> response) {
                                int statusCode = response.code();
                                if (statusCode == 201) {
                                    update(service, true);
                                }
                                //noinspection ConstantConditions
                                Snackbar.make(activity.findViewById(R.id.adView), response.body().getMessage(),
                                        Snackbar.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure(@NonNull Call<Response> call, @NonNull Throwable t) {
                                // Log error here since request failed
                                Log.e("Failure", t.toString());
                            }
                        });
                    }

                }
            });
            builder.setNegativeButton("Cancel", null);
            AlertDialog dialog = builder.create();
            dialog.show();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void serviceResponse(int responseID, List<KObject> objects) {
        progressBar.setVisibility(View.GONE);

        if (responseID == Constants.GET_LIST_DETAILS && objects != null) {
            moviesRv.setVisibility(objects.isEmpty() ? View.GONE : View.VISIBLE);
            emptyLayout.setVisibility(objects.isEmpty() ? View.VISIBLE : View.GONE);
            movieList = new ArrayList<>();
            for (KObject object : objects) {
                Movie movie = (Movie) object;
                movieList.add(movie);
            }

            adapter = new MovieListAdapter(context, movieList);
            adapter.setOnItemDeleted(this);
            moviesRv.setAdapter(adapter);

            ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adapter);
            ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(callback);
            mItemTouchHelper.attachToRecyclerView(moviesRv);

            adapter.setOnItemClickListener(new MovieListAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(int position, ImageView imageView) {
                    Movie movie = (Movie) adapter.getItem(position);
                    OnClickHelper.movieClicked(context, movie.getTitle(), movie.getPosterPath(),
                            String.valueOf(movie.id()), imageView);
                }
            });

        }

        if (responseID == Constants.SEARCH && objects != null && !objects.isEmpty()) {
            final List<Search> searchList = new ArrayList<>();
            for (KObject kObject : objects) {
                Search search = (Search) kObject;
                searchList.add(search);
            }

            final SearchAdapter searchAdapter = new SearchAdapter(context, searchList, R.layout.search_dialog_row_layout);
            searchRv.setAdapter(searchAdapter);

            searchAdapter.setOnItemClickListener(new SearchAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(int position, ImageView imageView) {
                    Search search = searchList.get(position);
                    PostRetrofit postRetrofit = new PostRetrofit();
                    postRetrofit.setId((int) search.id());
                    ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
                    Call<Response> call = apiService.addMovie(Integer.parseInt(listId), PreferencesHelper.getSessionId(context), postRetrofit);
                    call.enqueue(new Callback<Response>() {
                        @Override
                        public void onResponse(@NonNull Call<Response> call, @NonNull retrofit2.Response<Response> response) {
                            int statusCode = response.code();
                            if (statusCode == 201) {
                                update(service, true);
                                alertDialog.cancel();
                            }
                            //noinspection ConstantConditions
                            Snackbar.make(activity.findViewById(R.id.adView), response.body().getMessage(),
                                    Snackbar.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(@NonNull Call<Response> call, @NonNull Throwable t) {
                            // Log error here since request failed
                            Log.e("Failure", t.toString());
                        }
                    });
                }
            });
        }
    }

    @Override
    public void update(ModelService service, boolean reload) {
        boolean hasConnection = Connection.isNetworkAvailable(context);
        if (hasConnection && service != null) service.getListDetails(listId);
        else DialogHelper.noConnectionDialog(context);
    }


    @Override
    public void onItemDeleted(final Movie movie, final int position) {
        // showing snack bar with Undo option
        Snackbar snackbar = Snackbar
                .make(activity.findViewById(R.id.ads_layout), movie.getTitle() + " removed from list", Snackbar.LENGTH_LONG);
        snackbar.setAction("UNDO", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // undo is selected, restore the deleted item
                clicked = true;
                adapter.restoreItem(movie, position);
            }
        });

        snackbar.addCallback(new Snackbar.Callback() {
            @Override
            public void onDismissed(Snackbar transientBottomBar, int event) {
                if (!clicked) {
                    ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
                    PostRetrofit postRetrofit = new PostRetrofit();
                    postRetrofit.setId((int) movie.id());
                    Call<Response> call = apiService.removeMovie(Integer.parseInt(listId),
                            PreferencesHelper.getSessionId(context), postRetrofit);
                    call.enqueue(new Callback<Response>() {
                        @Override
                        public void onResponse(@NonNull Call<Response> call, @NonNull retrofit2.Response<Response> response) {
                            int statusCode = response.code();
                            Log.d("Status Code", String.valueOf(statusCode));

                            //noinspection ConstantConditions
                            Snackbar.make(activity.findViewById(R.id.adView), response.body().getMessage(),
                                    Snackbar.LENGTH_SHORT).show();

                            update(service, true);
                        }

                        @Override
                        public void onFailure(@NonNull Call<Response> call, @NonNull Throwable t) {
                            // Log error here since request failed
                            Log.e("Failure", t.toString());
                        }
                    });
                }

                clicked = false;
                super.onDismissed(transientBottomBar, event);
            }
        });
        snackbar.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
