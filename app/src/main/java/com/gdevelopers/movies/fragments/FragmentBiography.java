package com.gdevelopers.movies.fragments;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.gdevelopers.movies.R;
import com.gdevelopers.movies.activities.GalleryActivity;
import com.gdevelopers.movies.activities.GalleryPreviewActivity;
import com.gdevelopers.movies.adapters.ImagesAdapter;
import com.gdevelopers.movies.helpers.DateHelper;
import com.gdevelopers.movies.objects.Actor;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

@SuppressWarnings("WeakerAccess")
public class FragmentBiography extends Fragment {
    private Actor actor;
    private boolean isTextViewClicked = false;
    private final int lineCount = 4;
    private Unbinder unbinder;
    @BindView(R.id.view_all_images)
    Button imagesBt;
    @BindView(R.id.images_list)
    RecyclerView imagesRv;
    @BindView(R.id.actor_birthday)
    TextView birthdayTv;
    @BindView(R.id.actor_deathday)
    TextView deathdayTv;
    @BindView(R.id.actor_place_of_birth)
    TextView homeTv;
    @BindView(R.id.actor_overview)
    TextView bioTv;
    @BindView(R.id.actor_aka)
    TextView akaTv;

    public void setActor(Actor actor) {
        this.actor = actor;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_biography, container, false);
        unbinder = ButterKnife.bind(this, rootView);


        final List<String> imagesList = actor.getImages();
        imagesBt.setVisibility(imagesList.size() > 10 ? View.VISIBLE : View.GONE);

        imagesBt.setOnClickListener(view -> {
            Intent intent = new Intent(getContext(), GalleryActivity.class);
            intent.putStringArrayListExtra("images", (ArrayList<String>) imagesList);
            intent.putExtra("title", actor.getName());
            startActivity(intent);
        });

        imagesRv.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        imagesRv.setNestedScrollingEnabled(false);

        ImagesAdapter imagesAdapter = new ImagesAdapter(getContext(), imagesList, true);
        imagesRv.setAdapter(imagesAdapter);

        imagesAdapter.setOnItemClickListener(position -> {
            Intent intent = new Intent(getContext(), GalleryPreviewActivity.class);
            intent.putStringArrayListExtra("urls", (ArrayList<String>) imagesList);
            intent.putExtra("position", position);
            startActivity(intent);
        });

        String birthDay = actor.getBirthday();
        String deathDay = actor.getDeathday();
        String home = actor.getPlaceOfBirth();
        String aka = actor.getAka();

        if (birthDay == null || birthDay.isEmpty())
            birthdayTv.setVisibility(View.GONE);
        else {
            birthdayTv.setVisibility(View.VISIBLE);
            String birthDate = DateHelper.formatDate(birthDay);
            birthdayTv.setText(getSpannedText(getString(R.string.born_on, birthDate)));
        }

        if (deathDay == null || deathDay.isEmpty())
            deathdayTv.setVisibility(View.GONE);
        else {
            deathdayTv.setVisibility(View.VISIBLE);
            String deathDate = DateHelper.formatDate(deathDay);
            deathdayTv.setText(getSpannedText(getString(R.string.died_on, deathDate)));
        }

        if (home == null || home.isEmpty())
            homeTv.setVisibility(View.GONE);
        else {
            homeTv.setVisibility(View.VISIBLE);
            homeTv.setText(getSpannedText(getString(R.string.from_place_of_birth, home)));
        }


        if (aka == null || aka.isEmpty())
            akaTv.setVisibility(View.GONE);
        else {
            akaTv.setVisibility(View.VISIBLE);
            akaTv.setText(getSpannedText(getString(R.string.aka, aka)));
        }

        bioTv.setText(actor.getBiography().equals("") ? getString(R.string.empty_text) : actor.getBiography());

        bioTv.setOnClickListener(view -> {
            if (isTextViewClicked) {
                bioTv.setMaxLines(lineCount);
                isTextViewClicked = false;
            } else {
                bioTv.setMaxLines(Integer.MAX_VALUE);
                isTextViewClicked = true;
            }
        });

        bioTv.post(() -> {
            if (bioTv.getLineCount() > lineCount)
                bioTv.setMaxLines(lineCount);
        });
        return rootView;
    }

    @SuppressWarnings("deprecation")
    private Spanned getSpannedText(String text) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(text, Html.FROM_HTML_MODE_COMPACT);
        } else {
            return Html.fromHtml(text);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
