package com.gdevelopers.movies.activities;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.gdevelopers.movies.R;
import com.gdevelopers.movies.adapters.CastAdapter;
import com.gdevelopers.movies.adapters.SeasonAdapter;
import com.gdevelopers.movies.adapters.TVPageAdapter;
import com.gdevelopers.movies.adapters.TrailerAdapter;
import com.gdevelopers.movies.helpers.AdBuilder;
import com.gdevelopers.movies.helpers.Connection;
import com.gdevelopers.movies.helpers.Constants;
import com.gdevelopers.movies.helpers.DateHelper;
import com.gdevelopers.movies.helpers.DialogHelper;
import com.gdevelopers.movies.helpers.GlideApp;
import com.gdevelopers.movies.helpers.MovieDB;
import com.gdevelopers.movies.model.KObject;
import com.gdevelopers.movies.model.ModelService;
import com.gdevelopers.movies.model.ServiceBinder;
import com.gdevelopers.movies.objects.Cast;
import com.gdevelopers.movies.objects.Company;
import com.gdevelopers.movies.objects.Genre;
import com.gdevelopers.movies.objects.Season;
import com.gdevelopers.movies.objects.TVShow;
import com.gdevelopers.movies.objects.Trailer;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.nineoldandroids.view.ViewHelper;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindColor;
import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;

@SuppressWarnings("WeakerAccess")
public class TVDetailsActivity extends AppCompatActivity implements ServiceConnection, ModelService.ResponseListener,
        ObservableScrollViewCallbacks, View.OnClickListener {
    private String title;
    private Bundle extra;
    private boolean isTextViewClicked = false;
    private ModelService service;
    @BindView(R.id.movie_details_image)
    ImageView detailsIv;
    @BindView(R.id.movie_overview)
    TextView overviewTv;
    @BindView(R.id.created_by)
    TextView createdByTv;
    @BindView(R.id.title)
    TextView titleTv;
    @BindView(R.id.status)
    TextView statusTv;
    @BindView(R.id.releaseDate)
    TextView dateRuntimeTv;
    @BindView(R.id.tagLine)
    TextView tagLineTv;
    @BindView(R.id.voteCount)
    TextView voteCountTv;
    @BindView(R.id.budget)
    TextView budgetTv;
    @BindView(R.id.companies)
    TextView companiesTv;
    @BindView(R.id.genre)
    TextView genreTv;
    @BindView(R.id.vote_average)
    TextView voteAverageTv;
    @BindView(R.id.ratingBar)
    RatingBar ratingBar;
    @BindView(R.id.see_all)
    Button seeAllBt;
    @BindView(R.id.cast_list)
    RecyclerView castRv;
    @BindView(R.id.seasons_list)
    RecyclerView seasonsRv;
    private TVShow currentTvShow;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.scroll)
    ObservableScrollView mScrollView;
    private int mParallaxImageHeight;
    @BindView(R.id.related_movies_list)
    RecyclerView relatedRv;
    @BindView(R.id.trailers_list)
    RecyclerView trailersRv;
    @BindView(R.id.view_all_images)
    Button imagesBt;
    @BindView(R.id.videos_layout)
    CardView videosCv;
    @BindViews({R.id.image1, R.id.image2, R.id.image3, R.id.image4, R.id.image5})
    List<ImageView> imageViewList;
    @BindColor(android.R.color.white)
    int colorWhite;
    @BindColor(R.color.colorPrimary)
    int colorPrimary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tv_details);
        ButterKnife.bind(this);
        toolbar.setTitleTextColor(ScrollUtils.getColorWithAlpha(0, colorWhite));
        setSupportActionBar(toolbar);

        AdBuilder.buildAd(this);

        extra = getIntent().getExtras();
        if (extra != null) {
            title = extra.getString(Constants.STRINGS.TITLE);
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(title);
        }

        toolbar.setBackgroundColor(ScrollUtils.getColorWithAlpha(0, colorPrimary));

        mScrollView.setScrollViewCallbacks(this);

        mParallaxImageHeight = getResources().getDimensionPixelSize(R.dimen.parallax_image_height);

        toolbar.setNavigationOnClickListener(view -> TVDetailsActivity.super.onBackPressed());

        initViews();

        seeAllBt.setOnClickListener(view -> {
            if (currentTvShow != null) {
                Intent intent = new Intent(TVDetailsActivity.this, ExtendedDetailsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("casts", (ArrayList<Cast>) currentTvShow.getCastList());
                bundle.putString(Constants.STRINGS.TITLE, title);
                bundle.putBoolean(Constants.STRINGS.GALLERY, false);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    private void initViews() {
        ImageView posterIv = findViewById(R.id.movie_poster);

        relatedRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        relatedRv.setNestedScrollingEnabled(false);

        seasonsRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        seasonsRv.setNestedScrollingEnabled(false);

        castRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        castRv.setNestedScrollingEnabled(false);

        String image = extra.getString(Constants.STRINGS.IMAGE);
        Glide.with(TVDetailsActivity.this).load(image).into(posterIv);

        overviewTv.setOnClickListener(view -> {
            if (isTextViewClicked) {
                overviewTv.setMaxLines(3);
                isTextViewClicked = false;
            } else {
                overviewTv.setMaxLines(Integer.MAX_VALUE);
                isTextViewClicked = true;
            }
        });

        trailersRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        trailersRv.setNestedScrollingEnabled(false);
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
        try {
            unbindService(this);
        } catch (Exception e) {
            Log.d("Exception", e.getMessage());
        }
    }


    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        if (this.service == null) {
            ServiceBinder binder = (ServiceBinder) iBinder;
            this.service = binder.getService();
            this.service.setContext(TVDetailsActivity.this);
            this.service.setOnResponseListener(this);

            String id = extra.getString("id");
            boolean hasConnection = Connection.isNetworkAvailable(TVDetailsActivity.this);
            if (hasConnection && service != null) service.getTVShowDetails(id);
            else DialogHelper.noConnectionDialog(TVDetailsActivity.this);

        }
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        service = null;
    }

    @Override
    public void onResponseListener(int responseID) {
        List<KObject> objects = service.getResponseFor(responseID);
        if (responseID == Constants.TV_SHOW_DETAILS && objects != null && !objects.isEmpty()) {
            TVShow tvShow = (TVShow) objects.get(0);
            currentTvShow = tvShow;
            Glide.with(TVDetailsActivity.this).load(tvShow.getBackdropPath()).listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    detailsIv.setVisibility(View.GONE);
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    return false;
                }
            }).into(detailsIv);

            ratingBar.setRating(tvShow.getVoteAverage() / 2);
            createdByTv.setVisibility(tvShow.getCreatedBy().equals("Created by ") ? View.GONE : View.VISIBLE);
            createdByTv.setText(tvShow.getCreatedBy());

            final List<String> imagesList = tvShow.getImages();
            int size = imagesList.size();

            int loopSize = size <= 5 ? size : 5;
            for (int i = 0; i < loopSize; i++) {
                ImageView imageView = imageViewList.get(i);
                imageView.setOnClickListener(this);
                GlideApp.with(TVDetailsActivity.this).load(MovieDB.IMAGE_URL + getString(R.string.galleryImgSize) +
                        imagesList.get(i)).error(R.drawable.placeholder).into(imageView);
            }

            imagesBt.setOnClickListener(view -> {
                Intent intent = new Intent(TVDetailsActivity.this, GalleryActivity.class);
                intent.putStringArrayListExtra("images", (ArrayList<String>) imagesList);
                intent.putExtra("title", title);
                startActivity(intent);
            });

            voteCountTv.setText(getString(R.string.number_of_votes, tvShow.getVoteCount()));
            titleTv.setText(title);
            voteAverageTv.setText(getString(R.string.vote_average_over_ten, tvShow.getVoteAverage()));
            statusTv.setText(tvShow.getStatus());
            dateRuntimeTv.setText(getString(R.string.number_of_seasons, tvShow.getNumberOfSeasons()));
            final String overview = tvShow.getOverview();
            overviewTv.setText(overview);
            overviewTv.post(() -> {
                if (overviewTv.getLineCount() > 3)
                    overviewTv.setMaxLines(3);
            });


            tagLineTv.setText(DateHelper.formatDate(tvShow.getFirstAirDate()));
            budgetTv.setText(DateHelper.formatDate(tvShow.getLastAirDate()));
            seeAllBt.setVisibility(tvShow.getCastList().size() < 9 ? View.INVISIBLE : View.VISIBLE);

            StringBuilder companyBuilder = new StringBuilder();
            List<Company> companyList = tvShow.getCompanies();
            for (int i = 0; i < companyList.size(); i++) {
                if (i < companyList.size() - 1)
                    companyBuilder.append(companyList.get(i).getName()).append("\n");
                else companyBuilder.append(companyList.get(i).getName());
            }
            companiesTv.setText(companyBuilder.toString());

            StringBuilder genreBuilder = new StringBuilder();
            List<Genre> genreList = tvShow.getGenreList();
            for (int i = 0; i < genreList.size(); i++) {
                if (i < genreList.size() - 1)
                    genreBuilder.append(genreList.get(i).getName()).append(", ");
                else genreBuilder.append(genreList.get(i).getName());
            }
            genreTv.setText(genreBuilder.toString());

            List<Season> seasonList = tvShow.getSeasonList();
            final SeasonAdapter seasonAdapter = new SeasonAdapter(this, seasonList);
            seasonsRv.setAdapter(seasonAdapter);

            seasonAdapter.setOnItemClickListener(position -> {
                Season season = (Season) seasonAdapter.getItem(position);
                Intent intent = new Intent(TVDetailsActivity.this, EpisodesActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString(Constants.STRINGS.IMAGE, "Season " + season.getNumber());
                bundle.putString("id", extra.getString("id"));
                bundle.putString("number", season.getNumber());
                intent.putExtras(bundle);
                startActivity(intent);
            });


            List<Trailer> trailerList = tvShow.getTrailerList();
            TrailerAdapter trailerAdapter = new TrailerAdapter(TVDetailsActivity.this, trailerList);
            trailersRv.setAdapter(trailerAdapter);

            videosCv.setVisibility(trailerList.isEmpty() ? View.GONE : View.VISIBLE);

            trailerAdapter.setOnItemClickListener(trailer -> {
                String str = null;
                if (trailer != null) {
                    str = MovieDB.youtube + trailer.getSource();
                }
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(str)));
            });

            final List<Cast> actorList = tvShow.getCastList();
            final CastAdapter castAdapter = new CastAdapter(this, actorList);
            castRv.setAdapter(castAdapter);

            castAdapter.setOnItemClickListener((position, imageView) -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    imageView.setTransitionName(getString(R.string.cast_image));
                }
                Cast cast = (Cast) castAdapter.getItem(position);
                Intent intent = new Intent(TVDetailsActivity.this, ActorDetailsActivity.class);
                intent.putExtra("id", cast.getId());
                intent.putExtra("title", cast.getName());
                intent.putExtra("image", cast.getProfile_path());
                startActivity(intent);
            });

            TVPageAdapter adapter = new TVPageAdapter(TVDetailsActivity.this, tvShow.getRelatedShows(), "tv");
            relatedRv.setAdapter(adapter);

            adapter.setOnItemClickListener((tvShow1, v) -> {
                ImageView imageView = v.findViewById(R.id.movie_image);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    imageView.setTransitionName(getString(R.string.movie_poster));
                }
                Intent intent = new Intent(TVDetailsActivity.this, TVDetailsActivity.class);
                intent.putExtra("title", tvShow1.getName());
                intent.putExtra("image", tvShow1.getPosterPath());
                intent.putExtra("id", String.valueOf(tvShow1.id()));
                Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(TVDetailsActivity.this, imageView, getString(R.string.movie_poster)).toBundle();
                startActivity(intent, bundle);
            });
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
        ViewHelper.setTranslationY(detailsIv, (float) scrollY / 2);
    }

    @Override
    public void onDownMotionEvent() {
        //No need
    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
        //No need
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.image1:
                goToGalleryPreview(0);
                break;
            case R.id.image2:
                goToGalleryPreview(1);
                break;
            case R.id.image3:
                goToGalleryPreview(2);
                break;
            case R.id.image4:
                goToGalleryPreview(3);
                break;
            case R.id.image5:
                goToGalleryPreview(4);
                break;
        }
    }

    private void goToGalleryPreview(int position) {
        Intent intent = new Intent(TVDetailsActivity.this, GalleryPreviewActivity.class);
        intent.putStringArrayListExtra("urls", (ArrayList<String>) currentTvShow.getImages());
        intent.putExtra("position", position);
        startActivity(intent);
    }
}
