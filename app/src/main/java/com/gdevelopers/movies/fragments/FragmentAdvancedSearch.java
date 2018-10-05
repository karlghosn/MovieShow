package com.gdevelopers.movies.fragments;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.gdevelopers.movies.R;
import com.gdevelopers.movies.activities.MainActivity;
import com.gdevelopers.movies.adapters.GenreAdapter;
import com.gdevelopers.movies.helpers.AutoCompleteAdapter;
import com.gdevelopers.movies.helpers.Connection;
import com.gdevelopers.movies.helpers.Constants;
import com.gdevelopers.movies.helpers.GlideApp;
import com.gdevelopers.movies.helpers.LetterTileProvider;
import com.gdevelopers.movies.model.KFragment;
import com.gdevelopers.movies.model.KObject;
import com.gdevelopers.movies.model.ModelService;
import com.gdevelopers.movies.objects.Genre;
import com.gdevelopers.movies.objects.Search;
import com.google.android.material.chip.Chip;
import com.nex3z.flowlayout.FlowLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import butterknife.BindArray;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


@SuppressWarnings("WeakerAccess")
public class FragmentAdvancedSearch extends KFragment {
    private MainActivity activity;
    private ModelService service;
    @BindView(R.id.genre_spinner)
    Spinner genreSpn;
    @BindView(R.id.from_spinner)
    Spinner fromSpn;
    @BindView(R.id.to_spinner)
    Spinner toSpn;
    @BindView(R.id.rating_spinner)
    Spinner ratingSpn;
    @BindView(R.id.sort_spinner)
    Spinner sortSpn;
    @BindView(R.id.type_spinner)
    Spinner typeSpn;
    private List<Genre> genreList;
    @BindView(R.id.toLayout)
    LinearLayout toLayout;
    private GenreAdapter genreAdapter;
    private AutoCompleteTextView editText;
    private final List<String> actorsIds = new ArrayList<>();
    private Context context;
    @BindView(R.id.flow_layout)
    FlowLayout flowLayout;
    private AutoCompleteAdapter autoCompleteAdapter;
    private final List<Search> searchList = new ArrayList<>();
    @BindArray(R.array.rating)
    String[] rating;
    @BindArray(R.array.years)
    String[] from;
    private Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_advanced_search, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        activity = (MainActivity) getActivity();
        context = getContext();
        if (activity != null) {
            activity.setVisibleFragment(this);
            service = activity.getService();
        }
        setHasOptionsMenu(true);

        editText = new AutoCompleteTextView(context);
        editText.setHint(R.string.search_actors);
        editText.setThreshold(1);
        editText.setHintTextColor(ContextCompat.getColor(context, android.R.color.white));
        editText.setTextColor(ContextCompat.getColor(context, android.R.color.white));
        editText.setLayoutParams(new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        editText.setBackgroundResource(android.R.color.transparent);
        autoCompleteAdapter = new AutoCompleteAdapter(context, searchList, R.layout.autocomplete_layout);
        editText.setAdapter(autoCompleteAdapter);

        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //You can identify which key pressed buy checking keyCode value with KeyEvent.KEYCODE_
                if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN && TextUtils.isEmpty(editText.getText()) &&
                        flowLayout.getChildCount() >= 2) {
                    //this is for backspace
                    View view = flowLayout.getChildAt(flowLayout.getChildCount() - 2);
                    actorsIds.remove(String.valueOf((long) view.getTag()));
                    flowLayout.removeViewAt(flowLayout.getChildCount() - 2);
                    autoCompleteAdapter.clear();
                }
                return false;
            }
        });

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String[] arr = charSequence.toString().split(", ");
                String queryStr = arr[arr.length - 1];
                queryStr = queryStr.replaceAll("[\\s%\"^#<>{}\\\\|`]", "%20");
                if (queryStr.length() > 0) {
                    final String finalQuery = queryStr;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            service.getActors(finalQuery);
                        }
                    }).start();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        flowLayout.addView(editText);

        //noinspection ConstantConditions
        ArrayAdapter<CharSequence> typeAdapter = ArrayAdapter.createFromResource(context,
                R.array.advanced_search_types, R.layout.spinner_item);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        typeSpn.setAdapter(typeAdapter);
        final LinearLayout actorsLayout = rootView.findViewById(R.id.actors_layout);

        typeSpn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                actorsLayout.setVisibility(i == 0 ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //No Need
            }
        });


        final List<String> ratingList = new ArrayList<>(Arrays.asList(rating));
        ArrayAdapter<String> ratingAdapter = createAdapter(ratingList);
        ratingAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);

        ArrayAdapter<CharSequence> sortAdapter = ArrayAdapter.createFromResource(context,
                R.array.sort, R.layout.spinner_item);
        sortAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);

        final List<String> fromList = new ArrayList<>(Arrays.asList(from));
        fromList.add(0, getString(R.string.from));
        // Initializing an ArrayAdapter
        final ArrayAdapter<String> fromAdapter = createAdapter(fromList);
        fromAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        fromSpn.setAdapter(fromAdapter);

        final List<String> to = new ArrayList<>();
        final ArrayAdapter<String> toAdapter = new ArrayAdapter<>(context, R.layout.spinner_item, to);
        to.add(0, getString(R.string.to));
        toAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        toSpn.setAdapter(toAdapter);
        fromSpn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                toLayout.setVisibility(i <= 1 ? View.GONE : View.VISIBLE);
                if (i > 0) {
                    to.clear();
                    to.addAll(new ArrayList<>(fromList.subList(1, i)));
                    toAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //No Need
            }
        });

        ratingSpn.setAdapter(ratingAdapter);
        sortSpn.setAdapter(sortAdapter);

        if (genreList == null || genreList.isEmpty())
            genreList = new ArrayList<>();

        genreAdapter = new GenreAdapter(context, genreList);
        genreAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        genreSpn.setAdapter(genreAdapter);

        this.update(service, false);
        return rootView;
    }


    private ArrayAdapter<String> createAdapter(List<String> stringList) {
        return new ArrayAdapter<String>(
                context, R.layout.spinner_item, stringList) {
            @Override
            public boolean isEnabled(int position) {
                return position != 0;
            }

            @Override
            public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                // Set the disable item text color
                tv.setTextColor(position == 0 ? Color.GRAY : Color.BLACK);
                return view;
            }
        };
    }

    @Override
    public void onResume() {
        super.onResume();
        if (activity.getSupportActionBar() != null)
            activity.getSupportActionBar().setTitle(R.string.advanced_search);

        activity.getNavigationView().setCheckedItem(R.id.nav_advanced_search);
        activity.getAddFab().hide();
    }

    @Override
    public void onPause() {
        super.onPause();
        actorsIds.clear();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_send, menu);
        super.onCreateOptionsMenu(menu, inflater);
        MenuItem sendItem = menu.findItem(R.id.send);
        boolean hasConnection = Connection.isNetworkAvailable(context);
        sendItem.setEnabled(hasConnection);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.send) {
            Drawable icon = ContextCompat.getDrawable(context, R.drawable.ic_error_outline_white_24dp);
            if (icon != null) {
                icon.setBounds(0, 0,
                        icon.getIntrinsicWidth(),
                        icon.getIntrinsicHeight());
            }
            if (genreSpn.getSelectedItemPosition() == 0)
                ((TextView) genreSpn.getSelectedView()).setError("", icon);
            if (fromSpn.getSelectedItemPosition() == 0)
                ((TextView) fromSpn.getSelectedView()).setError("", icon);
            if (ratingSpn.getSelectedItemPosition() == 0)
                ((TextView) ratingSpn.getSelectedView()).setError("", icon);

            if (genreSpn.getSelectedItemPosition() > 0 && fromSpn.getSelectedItemPosition() > 0 &&
                    ratingSpn.getSelectedItemPosition() > 0) {
                HashMap<String, String> hashMap = new HashMap<>();
                Genre genre = (Genre) genreSpn.getSelectedItem();
                if (!genre.getName().equals(getString(R.string.all)))
                    hashMap.put("with_genres", String.valueOf(genre.id()));

                String fromYear = (String) fromSpn.getSelectedItem();
                String toYear = (String) toSpn.getSelectedItem();
                String voteAverage = (String) ratingSpn.getSelectedItem();
                int sort = sortSpn.getSelectedItemPosition();

                if (!actorsIds.isEmpty()) {
                    StringBuilder idBuilder = new StringBuilder();

                    for (String textStr : actorsIds) {
                        idBuilder.append(textStr).append(", ");
                    }
                    hashMap.put("with_cast", idBuilder.toString());
                }

                if (!fromYear.equals(getString(R.string.unknown)))
                    hashMap.put(typeSpn.getSelectedItemPosition() == 0 ? "release_date.gte" : "air_date.gte", fromYear);


                if (toLayout.getVisibility() == View.VISIBLE && toYear != null && !toYear.equals("Unknown"))
                    hashMap.put(typeSpn.getSelectedItemPosition() == 0 ? "release_date.lte" : "air_date.lte", toYear);

                if (ratingSpn.getSelectedItemPosition() > 2)
                    hashMap.put("vote_average.gte", voteAverage.split(" ")[2]);

                hashMap.put("sort_by", getSort(sort));

                FragmentSearchResults fragmentSearchResults = new FragmentSearchResults();
                fragmentSearchResults.setHashMap(hashMap);
                fragmentSearchResults.setType(typeSpn.getSelectedItemPosition() == 0 ? "movie" : "tv");
                activity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.content_main, fragmentSearchResults)
                        .addToBackStack(null)
                        .commit();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void serviceResponse(int responseID, List<KObject> objects) {
        if (genreList.isEmpty()) {
            Genre g = new Genre(0);
            g.setName(getString(R.string.all));
            Genre g1 = new Genre(1);
            g1.setName(getString(R.string.genre));
            genreList.add(g1);
            genreList.add(g);
            if (responseID == Constants.GENRES && objects != null) {
                for (KObject object : objects) {
                    Genre genre = (Genre) object;
                    genreList.add(genre);
                }
            }
            genreAdapter.notifyDataSetChanged();
        }
        if (responseID == Constants.SEARCH_ACTORS && objects != null) {
            searchList.clear();
            for (KObject object : objects) {
                Search search = (Search) object;
                searchList.add(search);
            }

            autoCompleteAdapter.notifyDataSetChanged();

            editText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    final Search search = autoCompleteAdapter.getItem(position);
                    assert search != null;
                    if (actorsIds.contains(String.valueOf(search.id())))
                        Toast.makeText(context, "Actor already selected", Toast.LENGTH_SHORT).show();
                    else {
                        actorsIds.add(String.valueOf(search.id()));
                        editText.setText("");

                        final Chip chip = new Chip(context);
                        chip.setTextAppearance(R.style.TextAppearance_MaterialComponents_Body1);
                        chip.setCloseIconVisible(true);
                        chip.setChipIconVisible(true);
                        chip.setTag(search.id());

                        chip.setOnCloseIconClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                flowLayout.removeView(chip);
                                actorsIds.remove(String.valueOf(search.id()));
                            }
                        });

                        if (search.getPosterPath().endsWith("null")) {
                            LetterTileProvider letterTileProvider = new LetterTileProvider(context);
                            Drawable drawable = new BitmapDrawable(getResources(), letterTileProvider.getCircularLetterTile(search.getTitle()));
                            chip.setChipIcon(drawable);
                        } else
                            GlideApp.with(context).load(search.getPosterPath()).apply(RequestOptions.circleCropTransform()).into(new SimpleTarget<Drawable>() {
                                @Override
                                public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                    chip.setChipIcon(resource);
                                }
                            });

                        chip.setText(search.getTitle());

                        flowLayout.addView(chip, flowLayout.getChildCount() - 1);
                        autoCompleteAdapter.clear();
                    }
                }
            });
        }
    }


    @Override
    public void update(ModelService service, boolean reload) {
        boolean hasConnection = Connection.isNetworkAvailable(context);
        if (hasConnection && service != null)
            service.getGenres(context, reload);
    }


    private String getSort(int position) {
        switch (position) {
            case 0:
                return "popularity.asc";
            case 1:
                return "popularity.desc";
            case 2:
                return "original_title.asc";
            case 3:
                return "original_title.desc";
            case 4:
                return "release_date.asc";
            case 5:
                return "release_date.desc";
            case 6:
                return "vote_average.asc";
            case 7:
                return "vote_average.desc";
            default:
                return "";
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
