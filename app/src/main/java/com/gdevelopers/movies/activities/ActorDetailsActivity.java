package com.gdevelopers.movies.activities;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.gdevelopers.movies.R;
import com.gdevelopers.movies.adapters.ViewPagerAdapter;
import com.gdevelopers.movies.fragments.FragmentBiography;
import com.gdevelopers.movies.fragments.FragmentMoreShows;
import com.gdevelopers.movies.helpers.Connection;
import com.gdevelopers.movies.helpers.Constants;
import com.gdevelopers.movies.helpers.DialogHelper;
import com.gdevelopers.movies.helpers.MovieDB;
import com.gdevelopers.movies.model.KObject;
import com.gdevelopers.movies.model.ModelService;
import com.gdevelopers.movies.model.ServiceBinder;
import com.gdevelopers.movies.objects.Actor;
import com.gdevelopers.movies.objects.Show;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

@SuppressWarnings("WeakerAccess")
public class ActorDetailsActivity extends AppCompatActivity implements ServiceConnection, ModelService.ResponseListener, ViewPager.OnPageChangeListener {
    private SectionsPagerAdapter mSectionsPagerAdapter;
    @BindView(R.id.container)
    ViewPager mViewPager;
    private ModelService service;
    private Bundle extra;
    private Actor currentActor;
    private List<Show> movieList;
    private List<Show> tvList;
    private int dotsCount;
    private ImageView[] dots;
    private ViewPagerAdapter mAdapter;
    @BindView(R.id.pager_introduction)
    ViewPager introImages;
    @BindView(R.id.viewPagerCountDots)
    LinearLayout pagerIndicator;
    private List<Show> crewList;
    @BindView(R.id.r1)
    RelativeLayout imagesContainer;
    @BindView(R.id.no_image)
    ImageView errorIv;
    @BindDrawable(R.drawable.nonselecteditem_dot)
    Drawable nonSelectedDot;
    @BindDrawable(R.drawable.selecteditem_dot)
    Drawable selectedDot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actor_details);
        ButterKnife.bind(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.collapsingToolbar);
        collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));

        CircleImageView posterIv = findViewById(R.id.actor_image);
        TextView nameTv = findViewById(R.id.actor_name);

        extra = getIntent().getExtras();
        assert extra != null;
        String actorName = extra.getString(Constants.STRINGS.TITLE);
        nameTv.setText(actorName);

        String image = extra.getString(Constants.STRINGS.IMAGE);

        Glide.with(ActorDetailsActivity.this).load(MovieDB.IMAGE_URL + getString(R.string.imageSize) + image)
                .apply(RequestOptions.circleCropTransform())
                .into(posterIv);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        collapsingToolbarLayout.setTitle(actorName);

        toolbar.setNavigationOnClickListener(view -> ActorDetailsActivity.this.finish());
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
            Log.d(Constants.STRINGS.EXCEPTION, e.getMessage());
        }
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        if (this.service == null) {
            ServiceBinder binder = (ServiceBinder) iBinder;
            this.service = binder.getService();
            this.service.setContext(ActorDetailsActivity.this);
            this.service.setOnResponseListener(this);

            String id = extra.getString("id");

            boolean hasConnection = Connection.isNetworkAvailable(ActorDetailsActivity.this);
            if (hasConnection && service != null) service.getActorDetails(id);
            else DialogHelper.noConnectionDialog(ActorDetailsActivity.this);
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        this.service = null;
    }

    @Override
    public void onResponseListener(int responseID) {
        List<KObject> objects = service.getResponseFor(responseID);
        if (responseID == Constants.ACTOR_DETAILS && objects != null && !objects.isEmpty()) {
            movieList = new ArrayList<>();
            tvList = new ArrayList<>();
            crewList = new ArrayList<>();
            final Actor actor = (Actor) objects.get(0);
            currentActor = actor;

            List<String> backdropList = actor.getTaggedImages();
            boolean isEmpty = backdropList.isEmpty();
            errorIv.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
            imagesContainer.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
            if (!isEmpty) {
                mAdapter = new ViewPagerAdapter(ActorDetailsActivity.this, backdropList);
                introImages.setAdapter(mAdapter);
                introImages.setCurrentItem(0);
                introImages.addOnPageChangeListener(this);
                setUiPageViewController();
            }


            crewList.addAll(actor.getCrewList());
            movieList.addAll(actor.getMovieList());
            tvList.addAll(actor.getTvList());

            mViewPager.setAdapter(mSectionsPagerAdapter);
        }
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

    class SectionsPagerAdapter extends FragmentPagerAdapter {

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    FragmentBiography fragmentBiography = new FragmentBiography();
                    fragmentBiography.setActor(currentActor);
                    return fragmentBiography;
                case 1:
                    FragmentMoreShows fragmentMoreMovies = new FragmentMoreShows();
                    fragmentMoreMovies.setShowList(movieList);
                    fragmentMoreMovies.setCrew(false);
                    return fragmentMoreMovies;
                case 2:
                    FragmentMoreShows fragmentMoreShows = new FragmentMoreShows();
                    fragmentMoreShows.setShowList(tvList);
                    fragmentMoreShows.setCrew(false);
                    return fragmentMoreShows;
                case 3:
                    FragmentMoreShows fragmentMoreCrew = new FragmentMoreShows();
                    fragmentMoreCrew.setShowList(crewList);
                    fragmentMoreCrew.setCrew(false);
                    return fragmentMoreCrew;
                default:
                    return null;
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.info);
                case 1:
                    return "Movies (" + movieList.size() + ")";
                case 2:
                    return "TV Shows (" + tvList.size() + ")";
                case 3:
                    return "As Crew (" + crewList.size() + ")";
                default:
                    return "";
            }
        }

        @Override
        public int getCount() {
            return 4;
        }
    }
}
