package com.gdevelopers.movies.helpers;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.request.RequestOptions;
import com.gdevelopers.movies.R;
import com.gdevelopers.movies.objects.Search;

import java.util.ArrayList;
import java.util.List;

public class AutoCompleteAdapter extends ArrayAdapter<Search> implements Filterable {
    private List<Search> mData;
    private final List<Search> filterList;
    private final Context context;

    public AutoCompleteAdapter(Context context, List<Search> mData, int textViewResourceId) {
        super(context, textViewResourceId);
        this.context = context;
        this.mData = mData;
        this.filterList = mData;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Search getItem(int index) {
        return mData.get(index);
    }

    static class ViewHolder {
        TextView nameTv;
        ImageView actorIv;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View row = convertView;
        ViewHolder holder;

        if (convertView == null) {
            row = LayoutInflater.from(context).inflate(R.layout.autocomplete_layout, parent, false);
            holder = new ViewHolder();
            holder.nameTv = row.findViewById(R.id.actor_name);
            holder.actorIv = row.findViewById(R.id.actor_image);
            row.setTag(holder);
        } else holder = (ViewHolder) row.getTag();

        Search search = mData.get(position);
        holder.nameTv.setText(search.getTitle());

        if (search.getPosterPath().endsWith("null")) {
            LetterTileProvider letterTileProvider = new LetterTileProvider(context);
            holder.actorIv.setImageBitmap(letterTileProvider.getCircularLetterTile(search.getTitle()));
        } else
            GlideApp.with(context)
                    .load(search.getPosterPath())
                    .apply(RequestOptions.circleCropTransform())
                    .into(holder.actorIv);

        return row;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                if (constraint != null && constraint.length() > 0) {
                    List<Search> list = new ArrayList<>();
                    for (int i = 0; i < filterList.size(); i++) {
                        if ((filterList.get(i).getTitle().toUpperCase())
                                .contains(constraint.toString().toUpperCase())) {
                            Search actor = filterList.get(i);
                            list.add(actor);
                        }
                    }
                    results.count = list.size();
                    results.values = list;
                } else {
                    results.count = filterList.size();
                    results.values = filterList;
                }
                return results;

            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint,
                                          FilterResults results) {
                mData = (List<Search>) results.values;
                notifyDataSetChanged();
            }
        };
    }
}