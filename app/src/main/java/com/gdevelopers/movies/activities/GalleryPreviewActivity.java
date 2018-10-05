package com.gdevelopers.movies.activities;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.IBinder;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.gdevelopers.movies.R;
import com.gdevelopers.movies.helpers.MovieDB;
import com.gdevelopers.movies.model.ModelService;
import com.gdevelopers.movies.model.ServiceBinder;

import java.util.ArrayList;
import java.util.List;

import uk.co.senab.photoview.PhotoView;

public class GalleryPreviewActivity extends AppCompatActivity implements ServiceConnection {
    private ModelService service;
    private int position = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_preview);

        ViewPager gallery = findViewById(R.id.gallery_vpager);
        Bundle bundle = getIntent().getExtras();
        List<String> imageUrls = new ArrayList<>();
        if (bundle != null) {
            imageUrls = bundle.getStringArrayList("urls");
            position = bundle.getInt("position");
        }

        GalleryPagerAdapter galleryPagerAdapter = new GalleryPagerAdapter(GalleryPreviewActivity.this, imageUrls);
        gallery.setAdapter(galleryPagerAdapter);
        gallery.setCurrentItem(position);
    }

    private class GalleryPagerAdapter extends PagerAdapter {
        private final Context context;
        private final List<String> imageUrls;

        GalleryPagerAdapter(Context context, List<String> imageUrls) {
            this.context = context;
            this.imageUrls = imageUrls;
        }

        @Override
        public int getCount() {
            return imageUrls.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == (object);
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup collection, int pos) {
            View view = getLayoutInflater().inflate(R.layout.gallery_preview, collection, false);
            final PhotoView imageView = view.findViewById(R.id.galleryPreviewImgHolder);
            final ProgressBar progressBar = view.findViewById(R.id.progressBar);

            (collection).addView(view);

            Glide.with(context).load(MovieDB.IMAGE_URL + context.getResources().getString(R.string.backDropImgSize) + imageUrls.get(pos)).into(new SimpleTarget<Drawable>() {
                @Override
                public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                    imageView.setImageDrawable(resource);
                    progressBar.setVisibility(View.GONE);
                }
            });
            return view;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup collection, int position, @NonNull Object view) {
            (collection).removeView((View) view);
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
        this.service.setContext(GalleryPreviewActivity.this);
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        this.service = null;
    }
}
