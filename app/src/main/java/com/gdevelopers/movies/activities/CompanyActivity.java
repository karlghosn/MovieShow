package com.gdevelopers.movies.activities;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.gdevelopers.movies.R;
import com.gdevelopers.movies.helpers.AdBuilder;
import com.gdevelopers.movies.helpers.Constants;
import com.gdevelopers.movies.model.KObject;
import com.gdevelopers.movies.model.ModelService;
import com.gdevelopers.movies.model.ServiceBinder;
import com.gdevelopers.movies.adapters.MoviesPageAdapter;
import com.gdevelopers.movies.objects.Company;
import com.gdevelopers.movies.objects.Movie;
import com.gdevelopers.movies.objects.Section;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings("WeakerAccess")
public class CompanyActivity extends AppCompatActivity implements ServiceConnection, ModelService.ResponseListener, MoviesPageAdapter.OnLoadMoreListener {
    private ModelService service;
    private String companyId;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.company_movies_list)
    RecyclerView moviesRv;
    private boolean loadMore = false;
    private List<Movie> movieList;
    private List<Movie> mItems;
    private MoviesPageAdapter adapter;
    private int currentPage;
    private HashMap<String, String> hashMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        Bundle extra = getIntent().getExtras();

        AdBuilder.buildAd(this);

        String title = extra != null ? extra.getString("title") : null;
        companyId = extra != null ? extra.getString("id") : null;
        moviesRv.setLayoutManager(new GridLayoutManager(this, 3));

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(view -> CompanyActivity.super.onBackPressed());


    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent bindIntent = new Intent(this, ModelService.class);
        bindService(bindIntent, this, Service.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(this);
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        if (this.service == null) {
            ServiceBinder binder = (ServiceBinder) iBinder;
            this.service = binder.getService();
            this.service.setContext(CompanyActivity.this);
            this.service.setOnResponseListener(this);

            service.getCompanyDetails(CompanyActivity.this, companyId);
            hashMap = new HashMap<>();
            hashMap.put("with_companies", companyId);
            hashMap.put("sort_by", "original_title.asc");
            service.getAdvancedSearch("movie", "1", hashMap, false, true);
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        service = null;
    }

    @Override
    public void onResponseListener(int responseID) {
        progressBar.setVisibility(View.GONE);
        List<KObject> objects = service.getResponseFor(responseID);
        if (responseID == Constants.COMPANY_DETAILS && objects != null && !objects.isEmpty()) {
            Company company = (Company) objects.get(0);
            if (!company.getHeadquarters().equals("null"))
                toolbar.setSubtitle(company.getHeadquarters());
        }

        if (responseID == Constants.ADVANCED_SEARCH && objects != null && !objects.isEmpty()) {
            if (!loadMore)
                movieList = new ArrayList<>();
            else mItems.remove(mItems.size() - 1);

            Section section = (Section) objects.get(0);
            currentPage = (int) section.id();
            int totalPages = section.getTotalPages();
            movieList.addAll(section.getMovieList());

            if (!loadMore) {
                adapter = new MoviesPageAdapter(this, R.layout.company_movie_layout, R.layout.vertical_row_layout, movieList, "movie");
                moviesRv.setAdapter(adapter);
            } else adapter.notifyDataChanged();

            adapter.setLoadMoreListener(currentPage == totalPages ? null : this);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_more, menu);

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
        return true;
    }

    @Override
    public void onLoadMore(MoviesPageAdapter adapter) {
        mItems = adapter.getmItems();
        final Movie movie = new Movie(0);
        movie.setType("load");
        //add loading progress view
        mItems.add(movie);
        adapter.notifyItemInserted(mItems.size() - 1);

        loadMore = true;

        final Handler handler = new Handler();
        handler.postDelayed(() -> service.getAdvancedSearch("movie", String.valueOf(currentPage + 1), hashMap, true, true), 500);
    }
}

