package com.gdevelopers.movies.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
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
import android.widget.ImageView;

import com.gdevelopers.movies.R;
import com.gdevelopers.movies.activities.ActorDetailsActivity;
import com.gdevelopers.movies.activities.ExtendedDetailsActivity;
import com.gdevelopers.movies.adapters.AllCastAdapter;
import com.gdevelopers.movies.objects.Cast;

import java.util.List;


@SuppressWarnings("WeakerAccess")
public class FragmentCast extends Fragment {
    private List<Cast> castList;
    private AllCastAdapter adapter;
    private Unbinder unbinder;
    @BindView(R.id.cast_list)
    RecyclerView castRv;

    public void setCastList(List<Cast> castList) {
        this.castList = castList;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_cast, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        ExtendedDetailsActivity activity = (ExtendedDetailsActivity) getActivity();
        setHasOptionsMenu(true);

        assert activity != null;
        castRv.setLayoutManager(new GridLayoutManager(getContext(), calculateNoOfColumns(activity)));
        adapter = new AllCastAdapter(getContext(), castList);
        castRv.setAdapter(adapter);

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
                if (adapter != null)
                    adapter.getFilter().filter(newText);
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
