package com.gdevelopers.movies.activities;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gdevelopers.movies.R;
import com.gdevelopers.movies.adapters.MoviesPageAdapter;
import com.gdevelopers.movies.helpers.AdBuilder;
import com.gdevelopers.movies.helpers.Constants;
import com.gdevelopers.movies.helpers.GlideApp;
import com.gdevelopers.movies.model.KObject;
import com.gdevelopers.movies.model.ModelService;
import com.gdevelopers.movies.model.ServiceBinder;
import com.gdevelopers.movies.objects.Collection;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;

@SuppressWarnings("WeakerAccess")
public class CollectionActivity extends AppCompatActivity implements ServiceConnection, ModelService.ResponseListener,
        ObservableScrollViewCallbacks {
    private ModelService service;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.scroll)
    ObservableScrollView mScrollView;
    private int mParallaxImageHeight;
    private String id;
    private String title;
    @BindView(R.id.collection_overview)
    TextView overviewTv;
    @BindView(R.id.collection_movies)
    RecyclerView moviesRv;
    @BindColor(R.color.colorPrimary)
    int colorPrimary;
    @BindColor(android.R.color.white)
    int colorWhite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        AdBuilder.buildAd(this);

        TextView titleTv = findViewById(R.id.collection_title);
        ImageView backdropIv = findViewById(R.id.collection_image);
        moviesRv.setLayoutManager(new GridLayoutManager(this, 3));
        moviesRv.setNestedScrollingEnabled(false);


        Bundle extra = getIntent().getExtras();

        if (extra != null) {
            title = extra.getString(Constants.STRINGS.TITLE);
            id = extra.getString("id");
            GlideApp.with(this).load(extra.getString("image"))
                    .centerCrop()
                    .into(backdropIv);
            titleTv.setText(title);
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(title);
        }

        toolbar.setBackgroundColor(ScrollUtils.getColorWithAlpha(0, colorPrimary));

        mScrollView.setScrollViewCallbacks(this);
        mParallaxImageHeight = getResources().getDimensionPixelSize(R.dimen.parallax_image_height);

        toolbar.setNavigationOnClickListener(view -> CollectionActivity.super.onBackPressed());
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        if (this.service == null) {
            ServiceBinder binder = (ServiceBinder) iBinder;
            this.service = binder.getService();
            this.service.setContext(CollectionActivity.this);
            this.service.setOnResponseListener(this);

            service.getMovieCollection(id);
        }
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
    public void onServiceDisconnected(ComponentName componentName) {
        service = null;
    }

    @Override
    public void onResponseListener(int responseID) {
        List<KObject> objects = service.getResponseFor(responseID);
        if (responseID == Constants.GET_COLLECTION && objects != null && !objects.isEmpty()) {
            Collection collection = (Collection) objects.get(0);
            overviewTv.setText(collection.getOverview());

            MoviesPageAdapter adapter = new MoviesPageAdapter(this, R.layout.company_movie_layout, R.layout.vertical_row_layout, collection.getMovieList(), "movie");
            moviesRv.setAdapter(adapter);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        onScrollChanged(mScrollView.getCurrentScrollY(), false, false);
    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        float alpha = Math.min(1, (float) scrollY / mParallaxImageHeight);
        toolbar.setBackgroundColor(ScrollUtils.getColorWithAlpha(alpha, colorPrimary));
        toolbar.setTitleTextColor(ScrollUtils.getColorWithAlpha(alpha, colorWhite));
    }

    @Override
    public void onDownMotionEvent() {
        //No need
    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
        //No need
    }
}
