package com.gdevelopers.movies.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.gdevelopers.movies.R;
import com.gdevelopers.movies.activities.ActorDetailsActivity;
import com.gdevelopers.movies.activities.EpisodesActivity;
import com.gdevelopers.movies.adapters.CastAdapter;
import com.gdevelopers.movies.helpers.Connection;
import com.gdevelopers.movies.helpers.Constants;
import com.gdevelopers.movies.helpers.DateHelper;
import com.gdevelopers.movies.helpers.DialogHelper;
import com.gdevelopers.movies.helpers.GlideApp;
import com.gdevelopers.movies.helpers.MovieDB;
import com.gdevelopers.movies.model.KFragment;
import com.gdevelopers.movies.model.KObject;
import com.gdevelopers.movies.model.ModelService;
import com.gdevelopers.movies.activities.GalleryActivity;
import com.gdevelopers.movies.activities.GalleryPreviewActivity;
import com.gdevelopers.movies.adapters.ImagesAdapter;
import com.gdevelopers.movies.objects.Cast;
import com.gdevelopers.movies.objects.Episode;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.nineoldandroids.view.ViewHelper;

import java.util.ArrayList;
import java.util.List;


@SuppressWarnings("WeakerAccess")
public class FragmentEpisodeDetails extends KFragment implements ObservableScrollViewCallbacks {
    private EpisodesActivity activity;
    private int mParallaxImageHeight;
    @BindView(R.id.episode_details_image)
    ImageView detailsIv;
    private String tvId;
    private Episode episode;
    private boolean isTextViewClicked = false;
    private ModelService service;
    private Context context;
    @BindView(R.id.images_list)
    RecyclerView imagesRv;
    @BindView(R.id.images_layout)
    CardView imagesCv;
    @BindView(R.id.view_all_images)
    Button imagesBt;
    private Unbinder unbinder;

    void setTvId(String tvId) {
        this.tvId = tvId;
    }

    void setEpisode(Episode episode) {
        this.episode = episode;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_episode_details, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        context = getContext();
        activity = (EpisodesActivity) getActivity();

        if (activity != null) {
            activity.setVisibleFragment(this);
            service = activity.getService();
            activity.getToolbar().setBackgroundColor(ScrollUtils.getColorWithAlpha(0, ContextCompat.getColor(context,
                    R.color.colorPrimary)));
        }

        ObservableScrollView mScrollView = rootView.findViewById(R.id.scroll);
        mScrollView.setScrollViewCallbacks(this);

        mParallaxImageHeight = getResources().getDimensionPixelSize(R.dimen.parallax_image_height);
        imagesRv.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        ImageView posterIv = rootView.findViewById(R.id.episode_poster);
        RecyclerView castRv = rootView.findViewById(R.id.cast_list);
        castRv.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        CardView guestCv = rootView.findViewById(R.id.guest_stars_card);
        guestCv.setVisibility(episode.getCastList().isEmpty() ? View.GONE : View.VISIBLE);
        final CastAdapter adapter = new CastAdapter(getContext(), episode.getCastList());
        castRv.setAdapter(adapter);
        GlideApp.with(context).load(MovieDB.IMAGE_URL + getString(R.string.galleryImgSize) + episode.getStillPath())
                .centerCrop()
                .error(R.drawable.placeholder)
                .into(posterIv);
        GlideApp.with(context).load(MovieDB.IMAGE_URL + getString(R.string.backDropImgSize) + episode.getStillPath())
                .into(detailsIv);

        RatingBar ratingBar = rootView.findViewById(R.id.ratingBar);
        ratingBar.setRating(episode.getVoteAverage() / 2);
        TextView voteTv = rootView.findViewById(R.id.voteCount);
        voteTv.setText(getString(R.string.number_of_votes, episode.getVoteCount()));
        TextView titleTv = rootView.findViewById(R.id.title);
        titleTv.setText(episode.getName());
        TextView seasonTv = rootView.findViewById(R.id.episode_season);
        seasonTv.setText(getString(R.string.season_number, episode.getSeason_number()));
        TextView dateTv = rootView.findViewById(R.id.releaseDate);
        dateTv.setText(DateHelper.formatDate(episode.getAirDate()));
        TextView voteAverageTv = rootView.findViewById(R.id.vote_average);
        voteAverageTv.setText(getString(R.string.vote_average_over_ten, episode.getVoteAverage()));
        TextView overviewTitleTv = rootView.findViewById(R.id.overview_title);
        overviewTitleTv.setText(episode.getName());
        final TextView overviewTv = rootView.findViewById(R.id.episode_overview);
        overviewTv.setText(episode.getOverview());

        overviewTv.post(() -> {
            if (overviewTv.getLineCount() > 3)
                overviewTv.setMaxLines(3);
        });

        overviewTv.setOnClickListener(view -> {
            if (isTextViewClicked) {
                overviewTv.setMaxLines(3);
                isTextViewClicked = false;
            } else {
                overviewTv.setMaxLines(Integer.MAX_VALUE);
                isTextViewClicked = true;
            }
        });

        adapter.setOnItemClickListener((position, imageView) -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                imageView.setTransitionName(getString(R.string.cast_image));
            }
            Cast cast = (Cast) adapter.getItem(position);
            Intent intent = new Intent(getContext(), ActorDetailsActivity.class);
            intent.putExtra("id", cast.getId());
            intent.putExtra("title", cast.getName());
            intent.putExtra("image", cast.getProfile_path());
            startActivity(intent);
        });

        this.update(service, true);
        return rootView;
    }

    @Override
    public void serviceResponse(int responseID, List<KObject> objects) {
        if (responseID == Constants.EPISODE_DETAILS && objects != null) {
            final Episode ep = (Episode) objects.get(0);
            final List<String> imagesList = ep.getImages();
            imagesCv.setVisibility(imagesList.isEmpty() ? View.GONE : View.VISIBLE);
            ImagesAdapter imagesAdapter = new ImagesAdapter(getContext(), imagesList, false);
            imagesRv.setAdapter(imagesAdapter);

            imagesAdapter.setOnItemClickListener(position -> {
                Intent intent = new Intent(getContext(), GalleryPreviewActivity.class);
                intent.putStringArrayListExtra("urls", (ArrayList<String>) imagesList);
                intent.putExtra("position", position);
                startActivity(intent);
            });

            imagesBt.setOnClickListener(view -> {
                Intent intent = new Intent(getContext(), GalleryActivity.class);
                intent.putStringArrayListExtra("images", (ArrayList<String>) imagesList);
                intent.putExtra("title", episode.getName());
                startActivity(intent);
            });
        }
    }

    @Override
    public void update(ModelService service, boolean reload) {
        boolean hasConnection = Connection.isNetworkAvailable(context);
        if (hasConnection && service != null)
            service.getEpisodeDetails(tvId, episode.getSeason_number(), episode.getNumber(), reload);
        else DialogHelper.noConnectionDialog(getContext());

    }

    @Override
    public void onResume() {
        super.onResume();
        if (activity.getSupportActionBar() != null)
            activity.getSupportActionBar().setTitle(episode.getName());
    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        int baseColor = ContextCompat.getColor(context, R.color.colorPrimary);
        float alpha = Math.min(1, (float) scrollY / mParallaxImageHeight);
        activity.getToolbar().setBackgroundColor(ScrollUtils.getColorWithAlpha(alpha, baseColor));
        ViewHelper.setTranslationY(detailsIv, (float) scrollY / 2);
    }

    @Override
    public void onDownMotionEvent() {
        //No Need
    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
        //No Need
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
