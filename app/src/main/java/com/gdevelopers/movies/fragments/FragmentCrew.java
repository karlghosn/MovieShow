package com.gdevelopers.movies.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.gdevelopers.movies.R;
import com.gdevelopers.movies.activities.ExtendedDetailsActivity;
import com.gdevelopers.movies.adapters.AllCrewAdapter;
import com.gdevelopers.movies.adapters.DepartmentAdapter;
import com.gdevelopers.movies.objects.Crew;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


@SuppressWarnings("WeakerAccess")
public class FragmentCrew extends Fragment {
    private List<Crew> crewList;
    private AllCrewAdapter allCrewAdapter;
    private String selectedDepartment = "All";
    private Unbinder unbinder;
    @BindView(R.id.crew_list)
    RecyclerView crewRv;
    @BindView(R.id.departments_spinner)
    Spinner departmentSpn;

    public void setCrewList(List<Crew> crewList) {
        this.crewList = crewList;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_crew, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        ExtendedDetailsActivity activity = (ExtendedDetailsActivity) getActivity();
        setHasOptionsMenu(true);

        assert activity != null;
        crewRv.setLayoutManager(new GridLayoutManager(getContext(), calculateNoOfColumns(activity)));

        final HashMap<String, List<Crew>> hashMap = new HashMap<>();
        hashMap.put("All", new ArrayList<Crew>());
        List<String> departmentList = new ArrayList<>();
        departmentList.add("All");

        if (crewList != null)
            for (Crew crew : crewList) {
                String department = crew.getDepartment();
                hashMap.get("All").add(crew);
                if (hashMap.containsKey(department))
                    hashMap.get(department).add(crew);
                else {
                    hashMap.put(department, new ArrayList<Crew>());
                    hashMap.get(department).add(crew);
                    departmentList.add(department);
                }
            }

        allCrewAdapter = new AllCrewAdapter(getContext(), hashMap.get(selectedDepartment));
        crewRv.setAdapter(allCrewAdapter);

        DepartmentAdapter departmentAdapter = new DepartmentAdapter(getContext(), departmentList);
        departmentSpn.setAdapter(departmentAdapter);


        departmentSpn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedDepartment = (String) adapterView.getItemAtPosition(i);
                allCrewAdapter = new AllCrewAdapter(getContext(), hashMap.get(selectedDepartment));
                crewRv.setAdapter(allCrewAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        return rootView;
    }

    private int calculateNoOfColumns(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int width = (int) (getResources().getDimension(R.dimen.show_row_width) / getResources().getDisplayMetrics().density);
        return (int) (dpWidth / width);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_more, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (allCrewAdapter != null)
                    allCrewAdapter.getFilter().filter(newText);
                return false;
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
