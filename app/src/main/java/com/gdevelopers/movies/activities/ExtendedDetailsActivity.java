package com.gdevelopers.movies.activities;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;

import com.gdevelopers.movies.R;
import com.gdevelopers.movies.fragments.FragmentCast;
import com.gdevelopers.movies.fragments.FragmentCrew;
import com.gdevelopers.movies.model.ModelService;
import com.gdevelopers.movies.model.ServiceBinder;
import com.gdevelopers.movies.objects.Cast;
import com.gdevelopers.movies.objects.Crew;

import java.util.List;

public class ExtendedDetailsActivity extends AppCompatActivity implements ServiceConnection {
    private ModelService service;
    private List<Cast> castList;
    private List<Crew> crewList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_shows);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle extras = getIntent().getExtras();
        String title = extras != null ? extras.getString("title") : null;
        getSupportActionBar().setTitle(title);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        ViewPager mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ExtendedDetailsActivity.this.onBackPressed();
            }
        });

        Bundle bundle = getIntent().getExtras();
        castList = bundle.getParcelableArrayList("casts");
        crewList = bundle.getParcelableArrayList("crew");
    }

    class SectionsPagerAdapter extends FragmentPagerAdapter {

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                FragmentCast fragmentCast = new FragmentCast();
                fragmentCast.setCastList(castList);
                return fragmentCast;
            } else if (position == 1) {
                FragmentCrew fragmentCrew = new FragmentCrew();
                fragmentCrew.setCrewList(crewList);
                return fragmentCrew;
            }
            return null;
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return position == 0 ? "Cast" : "Crew";
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
        this.service.setContext(ExtendedDetailsActivity.this);
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        this.service = null;
    }

}
