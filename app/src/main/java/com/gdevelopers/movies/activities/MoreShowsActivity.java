package com.gdevelopers.movies.activities;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.gdevelopers.movies.fragments.FragmentMoreShows;
import com.google.android.material.tabs.TabLayout;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.View;

import com.gdevelopers.movies.R;
import com.gdevelopers.movies.model.ModelService;
import com.gdevelopers.movies.model.ServiceBinder;
import com.gdevelopers.movies.objects.Show;

import java.util.List;

public class MoreShowsActivity extends AppCompatActivity implements ServiceConnection {
    private ModelService service;
    private List<Show> movieList;
    private List<Show> tvList;
    private boolean isCrew;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_shows);

        Bundle extra = getIntent().getExtras();
        if (extra != null) {
            movieList = extra.getParcelableArrayList("movie_list");
            tvList = extra.getParcelableArrayList("tv_list");
        }

        if (extra != null) {
            isCrew = extra.getBoolean("isCrew");
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (extra != null) {
            ActionBar actionBar = getSupportActionBar();
            assert actionBar != null;
            actionBar.setTitle(extra.getString("title"));
        }
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        ViewPager mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        toolbar.setNavigationOnClickListener(view -> MoreShowsActivity.this.onBackPressed());
    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    class SectionsPagerAdapter extends FragmentPagerAdapter {

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    FragmentMoreShows fragmentMoreMovies = new FragmentMoreShows();
                    fragmentMoreMovies.setShowList(movieList);
                    fragmentMoreMovies.setCrew(isCrew);
                    return fragmentMoreMovies;
                case 1:
                    FragmentMoreShows fragmentMoreShows = new FragmentMoreShows();
                    fragmentMoreShows.setShowList(tvList);
                    fragmentMoreShows.setCrew(isCrew);
                    return fragmentMoreShows;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Movies (" + movieList.size() + ")";
                case 1:
                    return "TV Shows (" + tvList.size() + ")";
                default:
                    return "";
            }
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
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        ServiceBinder binder = (ServiceBinder) iBinder;
        this.service = binder.getService();
        this.service.setContext(MoreShowsActivity.this);
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        this.service = null;
    }
}
