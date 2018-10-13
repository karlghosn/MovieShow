package com.gdevelopers.movies.fragments;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.gdevelopers.movies.activities.MainActivity;
import com.gdevelopers.movies.R;
import com.gdevelopers.movies.helpers.Constants;
import com.gdevelopers.movies.model.KFragment;
import com.gdevelopers.movies.model.KObject;
import com.gdevelopers.movies.model.ModelService;
import com.mikepenz.aboutlibraries.Libs;
import com.mikepenz.aboutlibraries.LibsBuilder;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;


@SuppressWarnings("WeakerAccess")
public class FragmentAbout extends KFragment {
    private MainActivity activity;
    private Unbinder unbinder;
    @BindView(R.id.about_container)
    LinearLayout aboutContainer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_about, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        activity = (MainActivity) getActivity();
        Context context = getContext();
        //noinspection ConstantConditions
        activity.setVisibleFragment(this);
        PackageInfo pInfo;
        Element versionElement = new Element();
        try {
            //noinspection ConstantConditions
            pInfo = getContext().getPackageManager().getPackageInfo(getContext().getPackageName(), 0);
            String version = getString(R.string.version_number, pInfo.versionName);
            versionElement.setTitle(version);
        } catch (PackageManager.NameNotFoundException e) {
            Log.d(Constants.STRINGS.EXCEPTION, e.getMessage());
        }

        Element librariesElement = new Element();
        librariesElement.setTitle(getString(R.string.open_source_licenses));
        librariesElement.setOnClickListener(view -> new LibsBuilder()
                .withActivityStyle(Libs.ActivityStyle.LIGHT_DARK_TOOLBAR)
                .withActivityTitle(getString(R.string.libraries))
                .start(getContext()));

        assert context != null;
        aboutContainer.addView(new AboutPage(getContext())
                .isRTL(false)
                .setImage(R.mipmap.ic_logo)
                .setDescription(getString(R.string.about_description))
                .addItem(versionElement)
                .addItem(librariesElement)
                .addGroup(getString(R.string.connect_with_us))
                .addWebsite("http://www.movieshow-app.com")
                .addPlayStore(context.getPackageName())
                .addFacebook("MovieShowApp")
                .addInstagram("movieshowapp")
                .create());


        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        activity.getNavigationView().setCheckedItem(R.id.nav_about);
        if (activity.getSupportActionBar() != null)
            activity.getSupportActionBar().setTitle(R.string.about);
    }

    @Override
    public void serviceResponse(int responseID, List<KObject> objects) {
        //No Need
    }

    @Override
    public void update(ModelService service, boolean reload) {
        //No Need
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
