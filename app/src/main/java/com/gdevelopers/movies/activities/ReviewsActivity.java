package com.gdevelopers.movies.activities;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.View;

import com.gdevelopers.movies.R;
import com.gdevelopers.movies.model.ModelService;
import com.gdevelopers.movies.model.ServiceBinder;
import com.gdevelopers.movies.adapters.ReviewsAdapter;
import com.gdevelopers.movies.objects.Review;

import java.util.ArrayList;

public class ReviewsActivity extends AppCompatActivity implements ServiceConnection {
    private ModelService service;
    private String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ReviewsActivity.this.onBackPressed();
            }
        });

        Bundle bundle = getIntent().getExtras();
        ArrayList<Review> reviews = null;
        if (bundle != null) {
            title = bundle.getString("title");
            getSupportActionBar().setTitle(title);
            reviews = bundle.getParcelableArrayList("reviews");
        }

        RecyclerView reviewsRv = findViewById(R.id.reviews_list);
        reviewsRv.setLayoutManager(new LinearLayoutManager(ReviewsActivity.this));

        ReviewsAdapter adapter = new ReviewsAdapter(reviews, true);
        reviewsRv.setAdapter(adapter);

        adapter.setOnItemClickListener(new ReviewsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Review review) {
                Intent intent = new Intent(ReviewsActivity.this, WebViewActivity.class);
                intent.putExtra("isHomePage", true);
                intent.putExtra("homePage", review.getUrl());
                intent.putExtra("title", title);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        ServiceBinder binder = (ServiceBinder) iBinder;
        this.service = binder.getService();
        this.service.setContext(ReviewsActivity.this);
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        this.service = null;
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
}
