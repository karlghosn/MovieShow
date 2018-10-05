package com.gdevelopers.movies.activities;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.util.Log;
import android.view.View;

import com.gdevelopers.movies.R;
import com.gdevelopers.movies.helpers.Constants;
import com.gdevelopers.movies.model.KFragment;
import com.gdevelopers.movies.model.KObject;
import com.gdevelopers.movies.model.ModelService;
import com.gdevelopers.movies.model.ServiceBinder;
import com.gdevelopers.movies.fragments.FragmentEpisodes;

import java.util.List;

public class EpisodesActivity extends AppCompatActivity implements ServiceConnection, ModelService.ResponseListener {
    private ModelService service = null;
    private KFragment visibleFragment;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    private Bundle bundle;

    public Toolbar getToolbar() {
        return toolbar;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_episodes);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        bundle = getIntent().getExtras();

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EpisodesActivity.this.onBackPressed();
            }
        });

        getSupportActionBar().setTitle(bundle.getString("title"));
    }

    public void setVisibleFragment(KFragment visibleFragment) {
        this.visibleFragment = visibleFragment;
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        ServiceBinder binder = (ServiceBinder) iBinder;
        this.service = binder.getService();
        this.service.setContext(EpisodesActivity.this);
        this.service.setOnResponseListener(this);

        if (visibleFragment == null || visibleFragment instanceof FragmentEpisodes) {
            FragmentEpisodes fragmentEpisodes = new FragmentEpisodes();
            fragmentEpisodes.setId(bundle.getString("id"));
            fragmentEpisodes.setNumber(bundle.getString("number"));
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_episodes, fragmentEpisodes)
                    .commit();
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
        try {
            unbindService(this);
        } catch (Exception e) {
            Log.d(Constants.STRINGS.EXCEPTION, e.getMessage());
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        service = null;
    }

    public ModelService getService() {
        return service;
    }

    @Override
    public void onResponseListener(int responseID) {
        List<KObject> objects = service.getResponseFor(responseID);
        visibleFragment.serviceResponse(responseID, objects);
    }
}
