package com.gdevelopers.movies.activities;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import androidx.core.app.ActivityOptionsCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.gdevelopers.movies.R;
import com.gdevelopers.movies.adapters.ShowAdapter;
import com.gdevelopers.movies.helpers.Constants;
import com.gdevelopers.movies.model.ModelService;
import com.gdevelopers.movies.model.ServiceBinder;
import com.gdevelopers.movies.objects.Show;

import java.util.ArrayList;
import java.util.List;

public class MoreActivity extends AppCompatActivity implements ServiceConnection {
    private ModelService service;
    private ShowAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MoreActivity.this.onBackPressed();
            }
        });

        Bundle extra = getIntent().getExtras();
        List<Show> showList = new ArrayList<>();
        if (extra != null) {
            getSupportActionBar().setTitle(extra.getString(Constants.STRINGS.TITLE));
            showList = extra.getParcelableArrayList("shows_list");
        }


        boolean isCrew = false;
        if (extra != null) {
            isCrew = extra.getBoolean("isCrew");
        }

        RecyclerView showsRv = findViewById(R.id.more_list);
        showsRv.setLayoutManager(new GridLayoutManager(this, calculateNoOfColumns(MoreActivity.this)));

        adapter = new ShowAdapter(MoreActivity.this, showList, isCrew);
        showsRv.setAdapter(adapter);

        adapter.setOnItemClickListener(new ShowAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Show show, View v) {
                ImageView imageView = v.findViewById(R.id.show_image);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    imageView.setTransitionName(getString(R.string.movie_poster));
                }
                if (show.getMediaType().equals("movie")) {
                    Intent intent = new Intent(MoreActivity.this, MovieDetailsActivity.class);
                    intent.putExtra(Constants.STRINGS.TITLE, show.getTitle());
                    intent.putExtra(Constants.STRINGS.IMAGE, show.getPosterPath());
                    intent.putExtra("id", String.valueOf(show.getId()));
                    Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(MoreActivity.this, imageView, getString(R.string.movie_poster)).toBundle();
                    startActivity(intent, bundle);
                } else if (show.getMediaType().equals("tv")) {
                    Intent intent = new Intent(MoreActivity.this, TVDetailsActivity.class);
                    intent.putExtra(Constants.STRINGS.TITLE, show.getTitle());
                    intent.putExtra(Constants.STRINGS.IMAGE, show.getPosterPath());
                    intent.putExtra("id", String.valueOf(show.getId()));
                    Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(MoreActivity.this, imageView, getString(R.string.movie_poster)).toBundle();
                    startActivity(intent, bundle);
                }
            }
        });
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
        ServiceBinder binder = (ServiceBinder) iBinder;
        this.service = binder.getService();
        this.service.setContext(MoreActivity.this);
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        this.service = null;
    }

    private int calculateNoOfColumns(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int width = (int) (getResources().getDimension(R.dimen.show_row_width) / getResources().getDisplayMetrics().density);
        return (int) (dpWidth / width);
    }
}
