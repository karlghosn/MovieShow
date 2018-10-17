package com.gdevelopers.movies.activities;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;

import com.gdevelopers.movies.R;
import com.gdevelopers.movies.adapters.CommentsAdapter;
import com.gdevelopers.movies.adapters.ReviewsAdapter;
import com.gdevelopers.movies.model.ModelService;
import com.gdevelopers.movies.model.ServiceBinder;
import com.gdevelopers.movies.objects.Comment;
import com.gdevelopers.movies.objects.Review;
import com.gdevelopers.movies.rest.services.CommentService;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import okhttp3.Headers;

public class ReviewsActivity extends AppCompatActivity implements ServiceConnection, CommentsAdapter.OnLoadMoreListener {
    private ModelService service;
    private String title;
    private List<Comment> commentList;
    private boolean loadMore = false;
    private CommentsAdapter commentsAdapter;
    private RecyclerView reviewsRv;
    private List<Comment> mItems;
    private final CommentsAdapter.OnLoadMoreListener onLoadMoreListener = this;
    private String id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(view -> ReviewsActivity.this.onBackPressed());

        reviewsRv = findViewById(R.id.reviews_list);
        reviewsRv.setLayoutManager(new LinearLayoutManager(ReviewsActivity.this));

        Bundle bundle = getIntent().getExtras();
        ArrayList<Review> reviews;
        if (bundle != null) {
            title = bundle.getString("title");
            getSupportActionBar().setTitle(title);
        }

        if (bundle != null && bundle.getBoolean("isTMDB")) {
            reviews = bundle.getParcelableArrayList("reviews");

            ReviewsAdapter adapter = new ReviewsAdapter(reviews, true);
            reviewsRv.setAdapter(adapter);

            adapter.setOnItemClickListener(review -> {
                Intent intent = new Intent(ReviewsActivity.this, WebViewActivity.class);
                intent.putExtra("isHomePage", true);
                intent.putExtra("homePage", review.getUrl());
                intent.putExtra("title", title);
                startActivity(intent);
            });
        } else {
            assert bundle != null;
            id = bundle.getString("traktId");

            fetchComments(1);
        }
    }

    private void fetchComments(final int page) {
        CommentService.getInstance().getComments(id, loadMore, page, response -> {
            if (!loadMore) {
                commentList = new ArrayList<>();
                commentsAdapter = new CommentsAdapter(ReviewsActivity.this, commentList);
                reviewsRv.setAdapter(commentsAdapter);
            } else mItems.remove(mItems.size() - 1);

            Headers headers = response.headers();
            String currentPage = headers.get("x-pagination-page");
            String totalPages = headers.get("x-pagination-page-count");
            commentsAdapter.setCurrentPage(Integer.parseInt(currentPage));
            commentsAdapter.setTotalPages(Integer.parseInt(totalPages));
            commentList.addAll(response.body());

            commentsAdapter.notifyDataChanged();
            commentsAdapter.setLoadMoreListener(onLoadMoreListener);
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

    @Override
    public void onLoadMore(CommentsAdapter adapter) {
        loadMore(adapter);
    }

    private void loadMore(final CommentsAdapter adapter) {
        mItems = adapter.getmItems();
        final Comment comment = new Comment();
        comment.setType("load");
        //add loading progress view
        mItems.add(comment);
        adapter.notifyItemInserted(mItems.size() - 1);

        loadMore = true;

        final int current_page = adapter.getCurrentPage();

        final Handler handler = new Handler();
        handler.postDelayed(() -> fetchComments(current_page + 1), 500);
    }
}
