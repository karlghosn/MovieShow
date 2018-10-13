package com.gdevelopers.movies.activities;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.gdevelopers.movies.R;
import com.gdevelopers.movies.helpers.AdBuilder;
import com.gdevelopers.movies.model.ModelService;
import com.gdevelopers.movies.model.ServiceBinder;
import com.gdevelopers.movies.adapters.GalleryAdapter;

import java.util.ArrayList;

public class GalleryActivity extends AppCompatActivity implements ServiceConnection {
    private ModelService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_extended_details);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        AdBuilder.buildAd(this);
        GridView galleryGv = findViewById(R.id.gridView);

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle bundle = getIntent().getExtras();
        String title = bundle != null ? bundle.getString("title") : null;
        getSupportActionBar().setTitle(title);

        if (bundle != null) {
            final ArrayList<String> images = bundle.getStringArrayList("images");
            GalleryAdapter adapter = new GalleryAdapter(GalleryActivity.this, images);
            galleryGv.setAdapter(adapter);
            galleryGv.setOnItemClickListener((adapterView, view, i, l) -> {
                Intent intent = new Intent(GalleryActivity.this, GalleryPreviewActivity.class);
                intent.putStringArrayListExtra("urls", images);
                intent.putExtra("position", i);
                startActivity(intent);
            });
        }

        toolbar.setNavigationOnClickListener(view -> GalleryActivity.this.onBackPressed());
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
        this.service.setContext(GalleryActivity.this);
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        this.service = null;
    }

}
