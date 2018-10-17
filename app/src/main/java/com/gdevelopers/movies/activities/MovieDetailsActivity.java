package com.gdevelopers.movies.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gdevelopers.movies.R;
import com.gdevelopers.movies.adapters.CastAdapter;
import com.gdevelopers.movies.adapters.HomeAdapter;
import com.gdevelopers.movies.adapters.TrailerAdapter;
import com.gdevelopers.movies.adapters.ViewPagerAdapter;
import com.gdevelopers.movies.helpers.AdBuilder;
import com.gdevelopers.movies.helpers.ChangeMovieState;
import com.gdevelopers.movies.helpers.Connection;
import com.gdevelopers.movies.helpers.Constants;
import com.gdevelopers.movies.helpers.DateHelper;
import com.gdevelopers.movies.helpers.DeleteRatingTask;
import com.gdevelopers.movies.helpers.DialogHelper;
import com.gdevelopers.movies.helpers.GlideApp;
import com.gdevelopers.movies.helpers.MovieDB;
import com.gdevelopers.movies.helpers.PostRetrofit;
import com.gdevelopers.movies.helpers.PreferencesHelper;
import com.gdevelopers.movies.helpers.Response;
import com.gdevelopers.movies.model.KObject;
import com.gdevelopers.movies.model.ModelService;
import com.gdevelopers.movies.model.ServiceBinder;
import com.gdevelopers.movies.objects.Cast;
import com.gdevelopers.movies.objects.Collection;
import com.gdevelopers.movies.objects.Company;
import com.gdevelopers.movies.objects.Country;
import com.gdevelopers.movies.objects.Crew;
import com.gdevelopers.movies.objects.Movie;
import com.gdevelopers.movies.objects.MovieState;
import com.gdevelopers.movies.objects.OMDBRating;
import com.gdevelopers.movies.objects.OMDb;
import com.gdevelopers.movies.objects.Review;
import com.gdevelopers.movies.objects.SearchTrakt;
import com.gdevelopers.movies.objects.Section;
import com.gdevelopers.movies.objects.Session;
import com.gdevelopers.movies.objects.Token;
import com.gdevelopers.movies.objects.Trailer;
import com.gdevelopers.movies.objects.TraktRating;
import com.gdevelopers.movies.objects.User;
import com.gdevelopers.movies.objects.UserList;
import com.gdevelopers.movies.rest.ApiClient;
import com.gdevelopers.movies.rest.ApiInterface;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.chip.Chip;
import com.google.android.material.snackbar.Snackbar;
import com.nex3z.flowlayout.FlowLayout;
import com.tolstykh.textviewrichdrawable.TextViewRichDrawable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindColor;
import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;

@SuppressWarnings("ALL")
public class MovieDetailsActivity extends AppCompatActivity implements ServiceConnection, ModelService.ResponseListener,
        View.OnClickListener, ObservableScrollViewCallbacks, DeleteRatingTask.OnDeleteCallBackListener, ViewPager.OnPageChangeListener {
    @BindDrawable(R.drawable.nonselecteditem_dot)
    Drawable nonSelectedDot;
    @BindDrawable(R.drawable.selecteditem_dot)
    Drawable selectedDot;
    @BindColor(android.R.color.white)
    int colorWhite;
    @BindColor(R.color.colorPrimary)
    int colorPrimary;
    @BindView(R.id.tagLine)
    TextView tagLineTv;
    @BindView(R.id.revenue)
    TextView revenueTv;
    @BindView(R.id.releaseDate)
    TextView dateRuntimeTv;
    @BindView(R.id.status)
    TextView statusTv;
    @BindView(R.id.title)
    TextView titleTv;
    @BindView(R.id.movie_overview)
    TextView overviewTv;
    @BindView(R.id.countries)
    TextView countriesTv;
    @BindView(R.id.genre)
    TextView genreTv;
    @BindView(R.id.budget)
    TextView budgetTv;
    @BindView(R.id.rating_text)
    TextView ratingTv;
    @BindView(R.id.vote_average)
    TextView voteAverageTv;
    @BindView(R.id.metacritic)
    TextView metacriticTv;
    @BindView(R.id.rotten_tomatoes)
    TextView rottenTv;
    @BindView(R.id.imdb)
    TextView imdbTv;
    @BindView(R.id.collection_name)
    TextView collectionTv;
    @BindView(R.id.voteCount)
    TextView voteCountTv;
    @BindView(R.id.tag_line_layout)
    LinearLayout tagLineLayout;
    @BindView(R.id.budget_layout)
    LinearLayout budgetLayout;
    @BindView(R.id.countries_layout)
    LinearLayout countriesLayout;
    @BindView(R.id.related_movies_layout)
    LinearLayout relatedLayout;
    @BindView(R.id.collection_layout)
    RelativeLayout collectionLayout;
    @BindView(R.id.revenue_layout)
    LinearLayout revenueLayout;
    @BindView(R.id.see_all)
    Button seeAllBt;
    @BindView(R.id.go_to_review)
    Button addReviewBt;
    @BindView(R.id.cast_list)
    RecyclerView castRv;
    @BindView(R.id.related_movies_list)
    RecyclerView relatedRv;
    @BindView(R.id.trailers_list)
    RecyclerView trailersRv;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.scroll)
    ObservableScrollView mScrollView;
    @BindView(R.id.companies_layout)
    CardView companiesCv;
    @BindView(R.id.list_action)
    FloatingActionButton listFab;
    @BindView(R.id.favourite_action)
    FloatingActionButton favouriteFab;
    @BindView(R.id.watchlist_action)
    FloatingActionButton watchlistFab;
    @BindView(R.id.menu_red)
    FloatingActionMenu fabMenu;
    @BindView(R.id.collection_image)
    ImageView collectionIv;
    @BindView(R.id.pager_introduction)
    ViewPager introImages;
    @BindView(R.id.viewPagerCountDots)
    LinearLayout pagerIndicator;
    @BindView(R.id.view_collection)
    Button viewCollectionBt;
    @BindView(R.id.reviews_layout)
    CardView reviewsCv;
    @BindView(R.id.view_all_images)
    Button imagesBt;
    @BindView(R.id.flow_layout)
    FlowLayout flowLayout;
    @BindView(R.id.videos_layout)
    CardView videosCv;
    @BindViews({R.id.image1, R.id.image2, R.id.image3, R.id.image4, R.id.image5})
    List<ImageView> imageViewList;
    @BindView(R.id.chart1)
    BarChart mChart;
    @BindView(R.id.trakt_rating)
    TextView traktRatingTv;
    @BindView(R.id.trakt_votes)
    TextView traktVotesTv;
    @BindView(R.id.trakt)
    TextView traktTv;
    private String traktId;
    @BindView(R.id.tmdb_reviews)
    TextViewRichDrawable tmdbReviews;
    private boolean isTextViewClicked = false;
    private ModelService service;
    private final DeleteRatingTask.OnDeleteCallBackListener deleteCallBackListener = this;
    private String requestToken;
    private String title;
    private String movieId;
    private Bundle extra;
    private Movie currentMovie;
    private int currentPosition = 0;
    private int dotsCount;
    private ImageView[] dots;
    private ViewPagerAdapter mAdapter;
    private final HashMap<String, String> ratingMap = new HashMap<>();
    private int mParallaxImageHeight;
    private MovieState currentState;
    private boolean isRefresh = false;
    private List<UserList> userLists;
    private String[] names;
    private UserList selectedList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        ButterKnife.bind(this);
        toolbar.setTitleTextColor(ScrollUtils.getColorWithAlpha(0, colorWhite));
        setSupportActionBar(toolbar);

        fabMenu.setClosedOnTouchOutside(true);

        fillHashMap();

        AdBuilder.buildAd(this);

        extra = getIntent().getExtras();

        if (extra != null) {
            title = extra.getString(Constants.STRINGS.TITLE);
            movieId = extra.getString("id");
        }
        initViews();

        mChart.setDrawBarShadow(false);
        mChart.setDrawValueAboveBar(true);

        mChart.getDescription().setEnabled(false);

        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        mChart.setScaleEnabled(false);
        mChart.setTouchEnabled(false);
        mChart.setDrawGridBackground(false);
        mChart.getAxisLeft().setEnabled(false);
        mChart.getAxisRight().setEnabled(false);
        mChart.getLegend().setEnabled(false);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setLabelCount(10);
        xAxis.setTextColor(colorWhite);
        xAxis.setAxisLineColor(android.R.color.transparent);

        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(title);

        toolbar.setBackgroundColor(ScrollUtils.getColorWithAlpha(0, colorPrimary));

        mScrollView.setScrollViewCallbacks(this);
        mParallaxImageHeight = getResources().getDimensionPixelSize(R.dimen.parallax_image_height);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MovieDetailsActivity.super.onBackPressed();
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        seeAllBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentMovie != null)
                    goToCrewCast(currentMovie);
            }
        });

        ratingTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!PreferencesHelper.hasSessionId(MovieDetailsActivity.this))
                    service.getToken();
                else {
                    if (!ratingTv.getText().toString().equals(getString(R.string.rate))) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MovieDetailsActivity.this);
                        builder.setTitle(R.string.change_rating);
                        builder.setMessage(R.string.add_delete_rating);
                        builder.setNegativeButton(R.string.delete, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                DeleteRatingTask deleteRatingTask = new DeleteRatingTask(MovieDetailsActivity.this, String.valueOf(currentMovie.id()));
                                deleteRatingTask.setOnCallBackListener(deleteCallBackListener);
                                deleteRatingTask.execute();
                                dialogInterface.cancel();
                            }
                        });

                        builder.setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                                ratingDialog();
                            }
                        });

                        builder.create().show();
                    } else ratingDialog();
                }
            }
        });

        TextViewRichDrawable traktReviews = findViewById(R.id.trakt_reviews);
        traktReviews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MovieDetailsActivity.this, ReviewsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("traktId", traktId);
                bundle.putString("title", title);
                bundle.putBoolean("isTMDB", false);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        addReviewBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogHelper.proVersionDialog(MovieDetailsActivity.this);
            }
        });
    }

    private void setUiPageViewController() {
        dotsCount = mAdapter.getCount();
        dots = new ImageView[dotsCount];

        for (int i = 0; i < dotsCount; i++) {
            dots[i] = new ImageView(this);
            dots[i].setImageDrawable(nonSelectedDot);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

            params.setMargins(10, 0, 10, 0);

            pagerIndicator.addView(dots[i], params);
        }

        if (dots.length > 0)
            dots[0].setImageDrawable(selectedDot);
    }

    private void goToGalleryPreview(int position) {
        Intent intent = new Intent(MovieDetailsActivity.this, GalleryPreviewActivity.class);
        intent.putStringArrayListExtra("urls", (ArrayList<String>) currentMovie.getImagesList());
        intent.putExtra("position", position);
        startActivity(intent);
    }

    private void fillHashMap() {
        String[] array = getResources().getStringArray(R.array.rating_text);
        for (int i = 0; i < array.length; i++) {
            ratingMap.put(String.valueOf(i + 1), array[i]);
        }
    }

    private void goToCrewCast(Movie movie) {
        Intent intent = new Intent(MovieDetailsActivity.this, ExtendedDetailsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("casts", (ArrayList<Cast>) movie.getCastList());
        bundle.putParcelableArrayList("crew", (ArrayList<Crew>) movie.getCrewList());
        bundle.putString(Constants.STRINGS.TITLE, title);
        bundle.putBoolean(Constants.STRINGS.GALLERY, false);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("refresh", isRefresh);
        setResult(RESULT_OK, intent);
        super.onBackPressed();
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


    private void setData(SparseIntArray hashMap) {
        ArrayList<BarEntry> yVals1 = new ArrayList<>();

        for (int i = 0; i < hashMap.size(); i++) {
            int key = hashMap.keyAt(i);
            int value = hashMap.get(key);
            BarEntry barEntry = new BarEntry(key, value);
            yVals1.add(barEntry);
        }

        BarDataSet set1;

        if (mChart.getData() != null &&
                mChart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) mChart.getData().getDataSetByIndex(0);
            set1.setValueTextColor(colorWhite);
            set1.setValues(yVals1);
            mChart.getData().notifyDataChanged();
            mChart.notifyDataSetChanged();
        } else {
            set1 = new BarDataSet(yVals1, "");
            set1.setDrawIcons(false);
            set1.setColors(ColorTemplate.MATERIAL_COLORS);
            set1.setValueTextColor(colorWhite);

            ArrayList<IBarDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);

            BarData data = new BarData(dataSets);
            data.setValueTextSize(10f);
            data.setValueTextColor(colorWhite);
            mChart.setData(data);
        }
        mChart.invalidate();
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        if (this.service == null) {
            ServiceBinder binder = (ServiceBinder) iBinder;
            this.service = binder.getService();
            this.service.setContext(MovieDetailsActivity.this);
            this.service.setOnResponseListener(this);

            boolean hasConnection = Connection.isNetworkAvailable(MovieDetailsActivity.this);

            service.getTraktMovieId(movieId);
            if (hasConnection && PreferencesHelper.hasSessionId(this))
                service.getMovieState(movieId, PreferencesHelper.getSessionId(this));

            if (hasConnection && service != null) service.getMovieDetails(movieId);
            else DialogHelper.noConnectionDialog(MovieDetailsActivity.this);
        }
    }

    private void initViews() {
        ImageView posterIv = findViewById(R.id.movie_poster);

        trailersRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        trailersRv.setNestedScrollingEnabled(false);

        relatedRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        relatedRv.setNestedScrollingEnabled(false);

        castRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        castRv.setNestedScrollingEnabled(false);

        String image = extra.getString("image");
        GlideApp.with(MovieDetailsActivity.this)
                .asBitmap()
                .load(MovieDB.IMAGE_URL + getResources().getString(R.string.imageSize) + image)
                .error(R.drawable.placeholder)
                .into(posterIv);

        posterIv.setOnClickListener(this);
        overviewTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isTextViewClicked) {
                    overviewTv.setMaxLines(3);
                    isTextViewClicked = false;
                } else {
                    overviewTv.setMaxLines(Integer.MAX_VALUE);
                    isTextViewClicked = true;
                }
            }
        });
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        service = null;
    }

    @Override
    public void onResponseListener(final int responseID) {
        List<KObject> objects = service.getResponseFor(responseID);
        if (responseID == Constants.MOVIE_DETAILS && objects != null && !objects.isEmpty()) {
            final Movie movie = (Movie) objects.get(0);
            this.currentMovie = movie;

            ApiInterface apiService = ApiClient.getOMDBClient().create(ApiInterface.class);

            List<String> backdropList = movie.getBackdropList();
            mAdapter = new ViewPagerAdapter(MovieDetailsActivity.this, backdropList);
            introImages.setAdapter(mAdapter);
            introImages.setCurrentItem(0);
            introImages.addOnPageChangeListener(this);
            setUiPageViewController();

            final List<String> imagesList = movie.getImagesList();
            int size = imagesList.size();

            int loopSize = size <= 5 ? size : 5;
            for (int i = 0; i < loopSize; i++) {
                ImageView imageView = imageViewList.get(i);
                imageView.setOnClickListener(this);
                GlideApp.with(MovieDetailsActivity.this).load(MovieDB.IMAGE_URL + getString(R.string.galleryImgSize) +
                        imagesList.get(i)).error(R.drawable.placeholder).into(imageViewList.get(i));
            }

            imagesBt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MovieDetailsActivity.this, GalleryActivity.class);
                    intent.putStringArrayListExtra("images", (ArrayList<String>) imagesList);
                    intent.putExtra("title", title);
                    startActivity(intent);
                }
            });

            Call<OMDb> call = apiService.getMovieRatings(movie.getImdbId());
            call.enqueue(new Callback<OMDb>() {
                @Override
                public void onResponse(@NonNull Call<OMDb> call, @NonNull retrofit2.Response<OMDb> response) {
                    HashMap<String, String> hashMap = new HashMap<>();
                    OMDb omDb = response.body();
                    assert omDb != null;
                    String genre = omDb.getGenre();
                    genreTv.setText(genre == null || genre.equals("") ? getString(R.string.empty_text) : genre);
                    List<OMDBRating> ratingList = omDb.getRatingList();
                    for (OMDBRating rating : ratingList) {
                        hashMap.put(rating.getSource(), rating.getValue());
                    }

                    String imdb = hashMap.get("Internet Movie Database");
                    imdbTv.setText(imdb == null || imdb.equals("") ? getString(R.string.empty_text) : imdb);

                    String rotten = hashMap.get("Rotten Tomatoes");
                    rottenTv.setText(rotten == null || rotten.equals("") ? getString(R.string.empty_text) : rotten);

                    String metacritic = hashMap.get("Metacritic");
                    metacriticTv.setText(metacritic == null || metacritic.equals("") ? getString(R.string.empty_text) : metacritic);

                }

                @Override
                public void onFailure(@NonNull Call<OMDb> call, @NonNull Throwable t) {
                    // Log error here since request failed
                    Log.e(Constants.STRINGS.FAILURE, t.toString());
                }
            });

            final Collection collection = movie.getCollection();
            if (collection != null) {
                GlideApp.with(this).load(collection.getBackdrop())
                        .centerCrop()
                        .into(collectionIv);
                collectionTv.setText(getString(R.string.part_of_the_collection, collection.getName()));

                viewCollectionBt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(MovieDetailsActivity.this, CollectionActivity.class);
                        intent.putExtra(Constants.STRINGS.TITLE, collection.getName());
                        intent.putExtra("id", String.valueOf(collection.id()));
                        intent.putExtra("image", collection.getBackdrop());
                        startActivity(intent);
                    }
                });
            } else collectionLayout.setVisibility(View.GONE);

            relatedLayout.setVisibility(movie.getRelatedMovies().isEmpty() ? View.GONE : View.VISIBLE);

            voteCountTv.setText(getString(R.string.number_of_votes, movie.getVoteCount()));
            titleTv.setText(title);
            voteAverageTv.setText(movie.getVoteAverage() == 0.0 ? getString(R.string.empty_text) : getString(R.string.vote_average_over_ten, movie.getVoteAverage()));
            statusTv.setText(movie.getStatus());


            dateRuntimeTv.setText(getString(R.string.release_date_runtime, DateHelper.formatDate(movie.getReleaseDate()), movie.getRunTime()));
            if (movie.getTagLine().equals(""))
                tagLineLayout.setVisibility(View.GONE);
            else {
                tagLineLayout.setVisibility(View.VISIBLE);
                tagLineTv.setText(movie.getTagLine());
            }

            final String overview = movie.getOverview();
            overviewTv.setText(overview);
            overviewTv.post(new Runnable() {
                @Override
                public void run() {
                    if (overviewTv.getLineCount() > 3)
                        overviewTv.setMaxLines(3);
                }
            });


            if (movie.getBudget().equals("0 $"))
                budgetLayout.setVisibility(View.GONE);
            else {
                budgetLayout.setVisibility(View.VISIBLE);
                budgetTv.setText(movie.getBudget());
            }

            if (movie.getRevenue().equals("0 $"))
                revenueLayout.setVisibility(View.GONE);
            else {
                revenueLayout.setVisibility(View.VISIBLE);
                revenueTv.setText(movie.getRevenue());
            }

            seeAllBt.setVisibility(movie.getCastList().size() < 8 ? View.INVISIBLE : View.VISIBLE);

            List<Company> companyList = movie.getCompanyList();

            for (final Company company : companyList) {
                @SuppressLint("InflateParams") Chip companyTv = (Chip) LayoutInflater.from(MovieDetailsActivity.this)
                        .inflate(R.layout.company_text_layout, null);
                companyTv.setText(company.getName());

                companyTv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(MovieDetailsActivity.this, CompanyActivity.class);
                        intent.putExtra(Constants.STRINGS.TITLE, company.getName());
                        intent.putExtra("id", String.valueOf(company.id()));
                        startActivity(intent);
                    }
                });
                flowLayout.addView(companyTv);
            }
            companiesCv.setVisibility(companyList.isEmpty() ? View.GONE : View.VISIBLE);

            List<Country> countryList = movie.getCountryList();
            if (countryList.isEmpty())
                countriesLayout.setVisibility(View.GONE);
            else {
                countriesLayout.setVisibility(View.VISIBLE);
                StringBuilder companiesBuilder = new StringBuilder();
                for (int i = 0; i < countryList.size(); i++) {
                    if (i < countryList.size() - 1)
                        companiesBuilder.append(countryList.get(i).getName()).append(", ");
                    else companiesBuilder.append(countryList.get(i).getName());
                }
                countriesTv.setText(companiesBuilder.toString());
            }

            final List<Cast> actorList = movie.getCastList();
            final CastAdapter castAdapter = new CastAdapter(this, actorList);
            castRv.setAdapter(castAdapter);


            castAdapter.setOnItemClickListener(new CastAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(int position, ImageView imageView) {
                    Cast cast = (Cast) castAdapter.getItem(position);
                    personClicked(cast.getId(), cast.getName(), cast.getProfile_path(), imageView);
                }
            });

            HomeAdapter adapter = new HomeAdapter(MovieDetailsActivity.this, movie.getRelatedMovies(), null, true);
            relatedRv.setAdapter(adapter);

            List<Trailer> trailerList = movie.getTrailerList();
            TrailerAdapter trailerAdapter = new TrailerAdapter(MovieDetailsActivity.this, trailerList);
            trailersRv.setAdapter(trailerAdapter);

            videosCv.setVisibility(trailerList.isEmpty() ? View.GONE : View.VISIBLE);

            trailerAdapter.setOnItemClickListener(new TrailerAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(Trailer trailer) {
                    String str = null;
                    if (trailer != null) {
                        str = MovieDB.youtube + trailer.getSource();
                    }
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(str)));
                }
            });

            final List<Review> reviewList = movie.getReviewList();

            tmdbReviews.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (reviewList.isEmpty())
                        Toast.makeText(MovieDetailsActivity.this, R.string.no_reviews_available, Toast.LENGTH_SHORT).show();
                    else {
                        Intent intent = new Intent(MovieDetailsActivity.this, ReviewsActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putParcelableArrayList("reviews", (ArrayList<Review>) movie.getReviewList());
                        bundle.putString("id", extra.getString("id"));
                        bundle.putString(Constants.STRINGS.TITLE, title);
                        bundle.putBoolean("isTMDB", true);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                }
            });
        } else if (responseID == Constants.GET_TOKEN && objects != null) {
            Token token = (Token) objects.get(0);
            Intent intent = new Intent(MovieDetailsActivity.this, WebViewActivity.class);
            requestToken = token.getRequestToken();
            intent.putExtra("token", requestToken);
            startActivityForResult(intent, 1);
        } else if (responseID == Constants.CHECK_ITEM_STATUS && objects != null) {
            Response response = (Response) objects.get(0);
            boolean exists = response.isResponse();

            if (exists) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MovieDetailsActivity.this);
                builder.setTitle("Error");
                builder.setMessage(title + getString(R.string.already_exists) + selectedList.getName());
                builder.setPositiveButton("Ok", null);
                builder.create().show();
            } else {
                PostRetrofit postRetrofit = new PostRetrofit();
                postRetrofit.setId((int) currentMovie.id());

                ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

                Call<Response> call = apiService.addMovieToList((int) selectedList.id(),
                        PreferencesHelper.getSessionId(MovieDetailsActivity.this), postRetrofit);
                call.enqueue(new Callback<Response>() {
                    @Override
                    public void onResponse(@NonNull Call<Response> call, @NonNull retrofit2.Response<Response> response) {
                        //noinspection ConstantConditions
                        @SuppressWarnings("ConstantConditions") String message = response.body().getMessage();
                        Toast.makeText(MovieDetailsActivity.this, message, Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onFailure(@NonNull Call<Response> call, @NonNull Throwable t) {
                        // Log error here since request failed
                        Log.e(Constants.STRINGS.FAILURE, t.toString());
                    }
                });
            }
        } else if (responseID == Constants.GET_SESSION_ID && objects != null) {
            Session session = (Session) objects.get(0);
            PreferencesHelper.putSessionId(session.getSession(), this);
            service.getAccountDetails(PreferencesHelper.getSessionId(this));
        } else if (responseID == Constants.SEARCH_TRAKT && objects != null && !objects.isEmpty()) {
            SearchTrakt searchTrakt = (SearchTrakt) objects.get(0);
            traktId = searchTrakt.getMovieId();
            service.getTraktRatings(searchTrakt.getMovieId());
        } else if (responseID == Constants.TRAKT_RATINGS && objects != null) {
            TraktRating traktRating = (TraktRating) objects.get(0);
            String rating = getString(R.string.vote_average_over_ten, Float.valueOf(traktRating.getOverallRating()));
            traktRatingTv.setText(rating);
            traktTv.setText(rating);
            traktVotesTv.setText(getString(R.string.number_of_votes_enclosed, traktRating.getVotes()));
            setData(traktRating.getRatingsMap());
        } else if (responseID == Constants.GET_ACCOUNT_DETAILS && objects != null) {
            User user = (User) objects.get(0);
            PreferencesHelper.putAccountId(String.valueOf(user.id()), this);
            service.getMovieState(movieId, PreferencesHelper.getSessionId(this));
        } else if (responseID == Constants.GET_CREATED_LISTS && objects != null) {
            userLists = new ArrayList<>();
            Section section = (Section) objects.get(0);
            userLists.addAll(section.getUserLists());
            names = new String[section.getUserLists().size()];
            for (int i = 0; i < section.getUserLists().size(); i++) {
                UserList userList = section.getUserLists().get(i);
                names[i] = userList.getName();
            }
            if (userLists.isEmpty())
                noListDialog();
            else listDialog();
        } else if (responseID == Constants.GET_MOVIE_STATE && objects != null) {
            MovieState state = (MovieState) objects.get(0);
            ratingTv.setText(state.isRated() ? String.valueOf(state.getRating()) : getString(R.string.rate));
            setUpFabs(state);
            if (currentState != null)
                isRefresh = currentState != state;

            currentState = state;
        }
    }

    private void noListDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.no_created_lists);
        builder.setPositiveButton("Ok", null);
        builder.create().show();
    }


    private void setUpFabs(MovieState state) {
        favouriteFab.setColorNormalResId(state.isFavorite() ? R.color.colorAccent : R.color.fab_color);
        favouriteFab.setColorPressedResId(state.isFavorite() ? R.color.colorAccent : R.color.fab_color);
        favouriteFab.setLabelText(state.isFavorite() ? getString(R.string.remove_favourite) : getString(R.string.add_favourites));
        watchlistFab.setColorNormalResId(state.isWatchlist() ? R.color.colorAccent : R.color.fab_color);
        watchlistFab.setColorPressedResId(state.isWatchlist() ? R.color.colorAccent : R.color.fab_color);
        watchlistFab.setLabelText(state.isWatchlist() ? getString(R.string.delete_from_watchlist) : getString(R.string.add_to_watchlist));
        favouriteFab.setOnClickListener(this);
        watchlistFab.setOnClickListener(this);
        listFab.setOnClickListener(this);
    }

    private void personClicked(String id, String name, String profilePath, ImageView imageView) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            imageView.setTransitionName(getString(R.string.cast_image));
        }
        Intent intent = new Intent(MovieDetailsActivity.this, ActorDetailsActivity.class);
        intent.putExtra("id", id);
        intent.putExtra(Constants.STRINGS.TITLE, name);
        intent.putExtra(Constants.STRINGS.IMAGE, profilePath);
        startActivity(intent);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.favourite_action:
                fabMenu.toggle(true);
                if (!PreferencesHelper.hasSessionId(this))
                    service.getToken();
                else
                    new ChangeMovieState(MovieDetailsActivity.this, Constants.STRINGS.FAVORITE, String.valueOf(currentMovie.id()),
                            service, fillJSON(Constants.STRINGS.FAVORITE), false).execute();
                break;
            case R.id.watchlist_action:
                fabMenu.toggle(true);
                if (!PreferencesHelper.hasSessionId(this))
                    service.getToken();
                else
                    new ChangeMovieState(MovieDetailsActivity.this, Constants.STRINGS.WATCHLIST, String.valueOf(currentMovie.id()),
                            service, fillJSON(Constants.STRINGS.WATCHLIST), false).execute();
                break;
            case R.id.list_action:
                fabMenu.toggle(true);
                if (!PreferencesHelper.hasSessionId(this))
                    service.getToken();
                else
                    service.getCreatedLists(PreferencesHelper.getAccountId(this), PreferencesHelper.getSessionId(this)
                            , false);
                break;
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

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        onScrollChanged(mScrollView.getCurrentScrollY(), false, false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_movie_details, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.reminder)
            DialogHelper.proVersionDialog(MovieDetailsActivity.this);
        if (id == R.id.homePage) {
            if (!currentMovie.getHomePage().equals("")) {
                goToWebView(currentMovie.getHomePage());
            } else
                Snackbar.make(findViewById(R.id.adView), "No homepage available for this movie", Snackbar.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }

    private void goToWebView(String url) {
        Intent intent = new Intent(MovieDetailsActivity.this, WebViewActivity.class);
        intent.putExtra("isHomePage", true);
        intent.putExtra("homePage", url);
        intent.putExtra(Constants.STRINGS.TITLE, title);
        startActivity(intent);
    }

    private void listDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MovieDetailsActivity.this);
        builder.setTitle(getString(R.string.add_movie_to_list, title));
        builder.setSingleChoiceItems(names, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                selectedList = userLists.get(i);
            }
        });


        builder.setNegativeButton(R.string.cancel, null);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                boolean hasConnection = Connection.isNetworkAvailable(MovieDetailsActivity.this);
                if (hasConnection && service != null)
                    service.checkItemStatus(String.valueOf(selectedList.id()), String.valueOf(currentMovie.id()));
                else DialogHelper.noConnectionDialog(MovieDetailsActivity.this);

            }
        });

        builder.create().show();
    }

    private void ratingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MovieDetailsActivity.this);
        @SuppressLint("InflateParams") View alertView = getLayoutInflater().inflate(R.layout.rating_dialog, null);
        final RatingBar bar = alertView.findViewById(R.id.ratingBar);
        Button cancelBt = alertView.findViewById(R.id.cancel);
        final Button rateBt = alertView.findViewById(R.id.rate_movie);
        final TextView ratingDialogTv = alertView.findViewById(R.id.rating_text);
        final TextView ratingDescriptionTv = alertView.findViewById(R.id.rating_description);
        ratingDialogTv.setText(R.string.ten);
        ratingDescriptionTv.setText(ratingMap.get("10"));
        builder.setView(alertView);

        final AlertDialog dialog = builder.create();
        bar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                String voteAverage = String.format(Locale.getDefault(), "%.0f", v);
                String description = ratingMap.get(voteAverage);
                ratingDialogTv.setText(voteAverage);
                ratingDescriptionTv.setText(description);
            }
        });
        cancelBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });

        rateBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PostRetrofit postRetrofit = new PostRetrofit();
                postRetrofit.setValue(bar.getRating());
                ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
                Call<Response> call = apiService.rateMovie((int) currentMovie.id(),
                        PreferencesHelper.getSessionId(MovieDetailsActivity.this), postRetrofit);
                call.enqueue(new Callback<Response>() {
                    @Override
                    public void onResponse(@NonNull Call<Response> call, @NonNull retrofit2.Response<Response> response) {
                        int statusCode = response.code();
                        Log.d("Status Code", String.valueOf(statusCode));
                        //noinspection ConstantConditions
                        @SuppressWarnings("ConstantConditions") String message = response.body().getMessage();
                        Toast.makeText(MovieDetailsActivity.this, message, Toast.LENGTH_SHORT).show();
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                service.getMovieState(movieId, PreferencesHelper.getSessionId(MovieDetailsActivity.this));
                            }
                        }, 1500);
                    }

                    @Override
                    public void onFailure(@NonNull Call<Response> call, @NonNull Throwable t) {
                        // Log error here since request failed
                        Log.e("Failure", t.toString());
                    }
                });

                dialog.cancel();
            }
        });

        dialog.show();
    }


    private JSONObject fillJSON(String type) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("media_type", "movie");
            jsonObject.put("media_id", currentMovie.id());
            if (type.equals("watchlist"))
                jsonObject.put("watchlist", !currentState.isWatchlist());
            else if (type.equals("favorite"))
                jsonObject.put("favorite", !currentState.isFavorite());

        } catch (JSONException e) {
            Log.d(Constants.STRINGS.EXCEPTION, e.getMessage());
        }
        return jsonObject;
    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        float alpha = Math.min(1, (float) scrollY / mParallaxImageHeight);
        toolbar.setBackgroundColor(ScrollUtils.getColorWithAlpha(alpha, colorPrimary));
        toolbar.setTitleTextColor(ScrollUtils.getColorWithAlpha(alpha, colorWhite));

        if (scrollY > 500 && scrollY > currentPosition)
            fabMenu.hideMenuButton(true);
        if (scrollY < currentPosition)
            fabMenu.showMenuButton(true);


        currentPosition = scrollY;
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
    public void onDeleteCallBack() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                service.getMovieState(movieId, PreferencesHelper.getSessionId(MovieDetailsActivity.this));
            }
        }, 1000);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            Boolean returnValue = data.getBooleanExtra("allow", false);
            if (returnValue) {
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        service.getSessionId(requestToken);
                    }
                }, 1000);
            }
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        //No Need
    }

    @Override
    public void onPageSelected(int position) {
        for (int i = 0; i < dotsCount; i++) {
            dots[i].setImageDrawable(nonSelectedDot);
        }

        dots[position].setImageDrawable(selectedDot);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        //No Need
    }
}
