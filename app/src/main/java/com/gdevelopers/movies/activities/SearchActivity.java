package com.gdevelopers.movies.activities;

import android.app.Service;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

import com.gdevelopers.movies.objects.Search;
import com.gdevelopers.movies.adapters.SearchAdapter;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.gdevelopers.movies.R;
import com.gdevelopers.movies.helpers.Constants;
import com.gdevelopers.movies.helpers.DialogHelper;
import com.gdevelopers.movies.helpers.OnClickHelper;
import com.gdevelopers.movies.model.KObject;
import com.gdevelopers.movies.model.ModelService;
import com.gdevelopers.movies.model.ServiceBinder;

import java.util.ArrayList;
import java.util.List;

import br.com.mauker.materialsearchview.MaterialSearchView;
import butterknife.BindView;
import butterknife.ButterKnife;

@SuppressWarnings("WeakerAccess")
public class SearchActivity extends AppCompatActivity implements ServiceConnection, ModelService.ResponseListener, View.OnClickListener {
    private ModelService service;
    @BindView(R.id.search_list)
    RecyclerView searchRv;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    private String selectedType = Constants.STRINGS.MULTI;
    @BindView(R.id.empty_layout)
    LinearLayout emptyLayout;
    private SearchAdapter adapter;
    @BindView(R.id.search_view)
    MaterialSearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(view -> SearchActivity.this.onBackPressed());

        searchRv.setLayoutManager(new LinearLayoutManager(SearchActivity.this));

        searchView.openSearch();

        final ChipGroup chipGroup = findViewById(R.id.search_type_container);

        Chip companiesChip = findViewById(R.id.companiesChip);
        Chip collectionsChip = findViewById(R.id.collectionsChip);
        Chip keywordsChip = findViewById(R.id.keywordsChip);

        companiesChip.setOnClickListener(this);
        collectionsChip.setOnClickListener(this);
        keywordsChip.setOnClickListener(this);

        chipGroup.setOnCheckedChangeListener((chipGroup1, i) -> {
            Chip selectedChip = chipGroup1.findViewById(chipGroup1.getCheckedChipId());
            if (selectedChip != null) {
                String tag = (String) selectedChip.getTag();
                String type = getType(Integer.valueOf(tag));
                if (!selectedType.equals(type)) {
                    String query = searchView.getCurrentQuery();
                    selectedType = type;

                    if (!query.equals("")) {
                        service.getSearch(type, query);
                        showProgress();
                    }
                }
            }

        });

        searchView.setOnQueryTextListener(query -> {
            Chip selectedChip = chipGroup.findViewById(chipGroup.getCheckedChipId());
            if (selectedChip != null) {
                String tag = (String) selectedChip.getTag();
                selectedType = getType(Integer.valueOf(tag));
                final String newQuery = query.replaceAll("[\\s%\"^#<>{}\\\\|`]", "%20");
                if (newQuery.length() > 0) {
                    new Thread(() -> {
                        if (service != null) {
                            service.getSearch(selectedType, newQuery);
                        }
                    }).start();
                }
            } else
                Toast.makeText(SearchActivity.this, "Please choose a filter", Toast.LENGTH_SHORT).show();

            return false;
        });


        searchView.setOnItemClickListener((adapterView, view, i, l) -> {
            String suggestion = searchView.getSuggestionAtPosition(i);
            searchView.setQuery(suggestion);
            searchView.dismissSuggestions();
        });

        searchView.setOnItemLongClickListener((adapterView, view, i, l) -> {
            final String suggestion = searchView.getSuggestionAtPosition(i);
            AlertDialog.Builder builder;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                builder = new AlertDialog.Builder(SearchActivity.this, android.R.style.ThemeOverlay_Material_Dialog);
            } else
                builder = new AlertDialog.Builder(SearchActivity.this);
            builder.setTitle(suggestion);
            builder.setMessage(R.string.remove_from_search);
            builder.setPositiveButton(R.string.remove, (dialogInterface, i1) -> searchView.removeHistory(suggestion));
            builder.setNegativeButton(R.string.cancel, null);
            builder.create().show();
            return true;
        });

        searchView.setOnBackClickListener(SearchActivity.this::onBackPressed);
    }


    private void showProgress() {
        progressBar.setVisibility(View.GONE);
    }

    private String getType(int tag) {
        switch (tag) {
            case 0:
                return "multi";
            case 1:
                return "movie";
            case 2:
                return "tv";
            case 3:
                return "person";
            default:
                return "";
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle toolbar item clicks here. It'll
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_search)
            searchView.openSearch();

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        ServiceBinder binder = (ServiceBinder) iBinder;
        this.service = binder.getService();
        this.service.setContext(SearchActivity.this);
        this.service.setOnResponseListener(this);
    }

    @Override
    public void onBackPressed() {
        if (searchView.isSuggestionVisible()) {
            // Close the search on the back button press.
            searchView.dismissSuggestions();
        } else super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        searchView.clearSuggestions();
    }

    @Override
    protected void onResume() {
        super.onResume();
        searchView.activityResumed();
        searchView.addSuggestions(searchView.getSuggestionsList());
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        service = null;
    }

    @Override
    public void onResponseListener(int responseID) {
        showProgress();
        List<KObject> objects = service.getResponseFor(responseID);
        emptyLayout.setVisibility(objects != null && objects.isEmpty() ? View.VISIBLE : View.GONE);
        if (responseID == Constants.SEARCH) {
            List<Search> searchList = new ArrayList<>();
            assert objects != null;
            for (KObject kObject : objects) {
                Search search = (Search) kObject;
                searchList.add(search);
            }
            adapter = new SearchAdapter(SearchActivity.this, searchList, R.layout.search_row_layout);
            searchRv.setAdapter(adapter);

            adapter.setOnItemClickListener((position, imageView) -> {
                Search search = (Search) adapter.getItem(position);
                String mediaType = search.getMediaType() == null ? selectedType : search.getMediaType();
                switch (mediaType) {
                    case "movie":
                        OnClickHelper.movieClicked(SearchActivity.this, search.getTitle(), search.getPosterPath(),
                                String.valueOf(search.id()), imageView);
                        break;
                    case "person":
                        Intent intent = new Intent(SearchActivity.this, ActorDetailsActivity.class);
                        intent.putExtra("id", String.valueOf(search.id()));
                        intent.putExtra("title", search.getTitle());
                        intent.putExtra("image", search.getPosterPath());
                        startActivity(intent);
                        break;
                    case "tv":
                        OnClickHelper.tvClicked(SearchActivity.this, search.getTitle(), search.getPosterPath(),
                                String.valueOf(search.id()), imageView);
                        break;
                    default:
                        break;
                }

            });
        }
    }

    @Override
    public void onClick(View v) {
        DialogHelper.proVersionDialog(SearchActivity.this);
    }
}
