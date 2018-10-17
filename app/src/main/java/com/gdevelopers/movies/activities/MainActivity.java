package com.gdevelopers.movies.activities;

import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.gdevelopers.movies.R;
import com.gdevelopers.movies.fragments.FragmentAbout;
import com.gdevelopers.movies.fragments.FragmentAdvancedSearch;
import com.gdevelopers.movies.fragments.FragmentDiscover;
import com.gdevelopers.movies.fragments.FragmentFavourites;
import com.gdevelopers.movies.fragments.FragmentHome;
import com.gdevelopers.movies.fragments.FragmentLists;
import com.gdevelopers.movies.fragments.FragmentMovies;
import com.gdevelopers.movies.fragments.FragmentPopular;
import com.gdevelopers.movies.fragments.FragmentRatedMovies;
import com.gdevelopers.movies.fragments.FragmentTvShows;
import com.gdevelopers.movies.fragments.FragmentWatchlist;
import com.gdevelopers.movies.helpers.Connection;
import com.gdevelopers.movies.helpers.Constants;
import com.gdevelopers.movies.helpers.MovieDB;
import com.gdevelopers.movies.helpers.PreferencesHelper;
import com.gdevelopers.movies.model.KFragment;
import com.gdevelopers.movies.model.KObject;
import com.gdevelopers.movies.model.ModelService;
import com.gdevelopers.movies.model.ServiceBinder;
import com.gdevelopers.movies.objects.Session;
import com.gdevelopers.movies.objects.Token;
import com.gdevelopers.movies.objects.User;
import com.gdevelopers.movies.rest.ApiClient;
import com.gdevelopers.movies.rest.ApiInterface;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.kobakei.ratethisapp.RateThisApp;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

@SuppressWarnings("WeakerAccess")
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, ServiceConnection, ModelService.ResponseListener {
    private ModelService service = null;
    private KFragment visibleFragment;
    @BindView(R.id.nav_view)
    NavigationView navigationView;
    private TextView usernameTv;
    private User currentUser;
    private static final String TAG = MainActivity.class.getSimpleName();
    private String requestToken;
    @BindView(R.id.fab_add)
    FloatingActionButton addFab;
    private CircleImageView profileIv;
    private ApiInterface apiService;
    private final CompositeDisposable disposable = new CompositeDisposable();


    public NavigationView getNavigationView() {
        return navigationView;
    }

    public FloatingActionButton getAddFab() {
        return addFab;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        FirebaseAnalytics.getInstance(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initShortcuts();
        MovieDB.getAppContext().getDatabaseHandler().clearMovies();
        apiService = ApiClient.getClient().create(ApiInterface.class);
        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
        usernameTv = navigationView.getHeaderView(0).findViewById(R.id.user_name);
        profileIv = navigationView.getHeaderView(0).findViewById(R.id.imageView);

//        AdBuilder.buildAd(this);

        // Monitor launch times and interval from installation
        RateThisApp.onCreate(this);
        // If the condition is satisfied, "Rate this app" dialog will be shown
        RateThisApp.showRateDialogIfNeeded(this);

        String language = Locale.getDefault().getLanguage();
        PreferencesHelper.putLanguage(language, MainActivity.this);

        boolean hasConnection = Connection.isNetworkAvailable(MainActivity.this);
        if (hasConnection && PreferencesHelper.hasSessionId(this) && currentUser == null)
            getAccountDetails();

    }


    public void setVisibleFragment(KFragment visibleFragment) {
        this.visibleFragment = visibleFragment;
    }

    @Override
    protected void onResume() {
        super.onResume();
        toggleSessions();
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        ServiceBinder binder = (ServiceBinder) iBinder;
        this.service = binder.getService();
        this.service.setContext(MainActivity.this);
        this.service.setOnResponseListener(this);


        Bundle extras = getIntent().getExtras();
        int position = 1;
        if (extras != null)
            position = extras.getInt(Constants.STRINGS.SHORTCUT);

        if (position == 3 && visibleFragment == null)
            replace(new FragmentTvShows());
        else if (position == 4 && visibleFragment == null)
            replace(new FragmentPopular());
        else if (position == 2 && visibleFragment == null) {
            replace(new FragmentMovies());
        } else if (position == 1 && visibleFragment == null)
            replace(new FragmentHome());
    }

    private void replace(KFragment kFragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_main, kFragment)
                .commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.search) {
            searchActivity();
        }
        return super.onOptionsItemSelected(item);
    }

    private void searchActivity() {
        Intent intent = new Intent(MainActivity.this, SearchActivity.class);
        startActivity(intent);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home && !visibleFragment.getClass().equals(FragmentHome.class)) {
            replaceFragment(new FragmentHome());
        } else if (id == R.id.nav_movies && !visibleFragment.getClass().equals(FragmentMovies.class)) {
            replaceFragment(new FragmentMovies());
        } else if (id == R.id.nav_tv_shows && !visibleFragment.getClass().equals(FragmentTvShows.class)) {
            replaceFragment(new FragmentTvShows());
        } else if (id == R.id.nav_advanced_search && !visibleFragment.getClass().equals(FragmentAdvancedSearch.class)) {
            replaceFragment(new FragmentAdvancedSearch());
        } else if (id == R.id.nav_discover && !visibleFragment.getClass().equals(FragmentDiscover.class)) {
            replaceFragment(new FragmentDiscover());
        } else if (id == R.id.nav_about && !visibleFragment.getClass().equals(FragmentAbout.class)) {
            replaceFragment(new FragmentAbout());
        } else if (id == R.id.nav_favourites && !visibleFragment.getClass().equals(FragmentFavourites.class)) {
            if (PreferencesHelper.hasSessionId(this)) {
                replaceFragment(new FragmentFavourites());
            } else signInDialog();
        } else if (id == R.id.nav_watchlist && !visibleFragment.getClass().equals(FragmentWatchlist.class)) {
            if (PreferencesHelper.hasSessionId(this)) {
                replaceFragment(new FragmentWatchlist());
            } else signInDialog();
        } else if (id == R.id.nav_rated && !visibleFragment.getClass().equals(FragmentRatedMovies.class)) {
            if (PreferencesHelper.hasSessionId(this)) {
                replaceFragment(new FragmentRatedMovies());
            } else signInDialog();
        } else if (id == R.id.nav_popular && !visibleFragment.getClass().equals(FragmentPopular.class)) {
            replaceFragment(new FragmentPopular());
        } else if (id == R.id.nav_lists && !visibleFragment.getClass().equals(FragmentLists.class)) {
            if (PreferencesHelper.hasSessionId(this)) {
                replaceFragment(new FragmentLists());
            } else signInDialog();

        } else if (id == R.id.nav_sign_in) {
            service.getToken();
        } else if (id == R.id.nav_sign_out) {
            PreferencesHelper.clear(this);
            toggleSessions();
        } else if (id == R.id.nav_contact_us) {
            sendFeedbackMail();
        } else if (id == R.id.nav_share) {
            shareApp();
        } else if (id == R.id.nav_apps) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("https://play.google.com/store/apps/dev?id=9114263730237446857"));
            startActivity(intent);
        } else if (id == R.id.nav_pro_version) {
            final String appPackageName = "com.gdevelopers.movies_pro"; // getPackageName() from Context or Activity object
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
            } catch (android.content.ActivityNotFoundException exception) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
            }
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void shareApp() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT,
                "https://play.google.com/store/apps/details?id=com.gdevelopers.movies");
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    public void replaceFragment(KFragment kFragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_main, kFragment)
                .addToBackStack(null)
                .commit();
    }

    private void signInDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setCancelable(true);
        builder.setTitle("Sign in");
        builder.setMessage("You need to sign in to access this feature");
        builder.setNegativeButton("Cancel", null);
        builder.setPositiveButton("Sign in", (dialogInterface, i) -> service.getToken());
        builder.create().show();
    }

    private void sendFeedbackMail() {
        String release = android.os.Build.VERSION.RELEASE;
        String device = android.os.Build.DEVICE;
        String model = android.os.Build.MODEL;
        String brand = android.os.Build.BRAND;
        String manufacturer = android.os.Build.MANUFACTURER;
        String version = null;
        StringBuilder builder = new StringBuilder();
        PackageInfo pInfo;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            version = pInfo.versionName;
            builder.append("App Version: ").append(version).append("\n");
        } catch (PackageManager.NameNotFoundException e) {
            Log.d(Constants.STRINGS.EXCEPTION, e.getMessage());
        }
        builder.append("Android Version: ").append(release).append("\n");
        builder.append("Brand: ").append(brand).append("\n");
        builder.append("Device: ").append(device).append("\n");
        builder.append("Manufacturer: ").append(manufacturer).append("\n");
        builder.append("Model: ").append(model).append("\n\n");
        builder.append("Feedback: ").append(" ");

        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL, new String[]{"dev.ghosn@gmail.com"});
        i.putExtra(Intent.EXTRA_SUBJECT, "Feedback: Movie Show " + version);
        i.putExtra(Intent.EXTRA_TEXT, builder.toString());
        try {
            startActivity(Intent.createChooser(i, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ignored) {
            Toast.makeText(MainActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }

    private void toggleSessions() {
        boolean hasSession = PreferencesHelper.hasSessionId(this);
        navigationView.getMenu().findItem(R.id.nav_sign_in).setVisible(!hasSession);
        navigationView.getMenu().findItem(R.id.nav_sign_out).setVisible(hasSession);
        usernameTv.setVisibility(hasSession ? View.VISIBLE : View.GONE);
    }


    @Override
    protected void onStart() {
        super.onStart();
        Intent bindIntent = new Intent(this, ModelService.class);
        bindIntent.putExtra("activity", "main");
        bindService(bindIntent, this, Service.BIND_AUTO_CREATE);
    }


    @Override
    protected void onStop() {
        super.onStop();
        unbindService(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            Boolean returnValue = data.getBooleanExtra("allow", false);
            if (returnValue) {
                final Handler handler = new Handler();
                handler.postDelayed(() -> service.getSessionId(requestToken), 1000);
            }
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

        if (responseID == Constants.GET_TOKEN) {
            Token token = (Token) objects.get(0);
            Intent intent = new Intent(MainActivity.this, WebViewActivity.class);
            requestToken = token.getRequestToken();
            intent.putExtra("token", requestToken);
            startActivityForResult(intent, 1);
        }

        if (responseID == Constants.GET_SESSION_ID) {
            Session session = (Session) objects.get(0);
            PreferencesHelper.putSessionId(session.getSession(), this);
            getAccountDetails();
            toggleSessions();
        } else visibleFragment.serviceResponse(responseID, objects);
    }

    private void getAccountDetails() {
        String sessionId = PreferencesHelper.getSessionId(this);
        disposable.add(
                apiService.getAccountDetails(sessionId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<User>() {
                            @Override
                            public void onSuccess(User user) {
                                currentUser = user;
                                PreferencesHelper.putAccountId(String.valueOf(user.id()), MainActivity.this);
                                Log.d("Username", user.getUsername());
                                usernameTv.setText(getString(R.string.welcome_user, user.getUsername()));
                                Glide.with(MainActivity.this).load(user.getImageUrl())
                                        .apply(RequestOptions.circleCropTransform())
                                        .into(profileIv);
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e(TAG, "onError: " + e.getMessage());
                            }
                        }));
    }

    private void initShortcuts() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N_MR1) {
            ShortcutManager shortcutManager = getSystemService(ShortcutManager.class);
            ShortcutInfo moviesShortcut = new ShortcutInfo.Builder(this, "movie_shortcut")
                    .setShortLabel(getString(R.string.movies))
                    .setLongLabel(getString(R.string.movies))
                    .setIcon(Icon.createWithResource(this, R.mipmap.ic_shortcut_movie))
                    .setIntents(new Intent[]{
                            new Intent(Intent.ACTION_MAIN, Uri.EMPTY, this, MainActivity.class)
                                    .putExtra(Constants.STRINGS.SHORTCUT, 2)
                                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)})
                    .build();

            ShortcutInfo tvShortcut = new ShortcutInfo.Builder(this, "tv_shortcut")
                    .setShortLabel(getString(R.string.tv_shows))
                    .setLongLabel(getString(R.string.tv_shows))
                    .setIcon(Icon.createWithResource(this, R.mipmap.ic_shortcut_tv_show))
                    .setIntents(new Intent[]{
                            new Intent(Intent.ACTION_MAIN, Uri.EMPTY, this, MainActivity.class)
                                    .putExtra(Constants.STRINGS.SHORTCUT, 3)
                                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)})
                    .build();

            ShortcutInfo searchShortcut = new ShortcutInfo.Builder(this, "search_shortcut")
                    .setShortLabel(getString(R.string.search))
                    .setLongLabel(getString(R.string.search))
                    .setIcon(Icon.createWithResource(this, R.mipmap.ic_shortcut_search))
                    .setIntents(new Intent[]{
                            new Intent(Intent.ACTION_MAIN, Uri.EMPTY, this, SearchActivity.class)
                                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)})
                    .build();

            ShortcutInfo peopleShortcut = new ShortcutInfo.Builder(this, "people_shortcut")
                    .setShortLabel(getString(R.string.popular_people))
                    .setLongLabel(getString(R.string.popular_people))
                    .setIcon(Icon.createWithResource(this, R.mipmap.ic_shortcut_people))
                    .setIntents(new Intent[]{
                            new Intent(Intent.ACTION_MAIN, Uri.EMPTY, this, MainActivity.class)
                                    .putExtra(Constants.STRINGS.SHORTCUT, 4)
                                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)})
                    .build();
            assert shortcutManager != null;
            shortcutManager.setDynamicShortcuts(Arrays.asList(searchShortcut, moviesShortcut, tvShortcut, peopleShortcut));
        }
    }

    @Override
    protected void onDestroy() {
        disposable.dispose();
        super.onDestroy();
    }
}
