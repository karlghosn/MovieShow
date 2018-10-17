package com.gdevelopers.movies.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gdevelopers.movies.R;
import com.gdevelopers.movies.objects.Feature;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class FeaturesAdapter extends ArrayAdapter<Feature> {
    private List<Feature> objects;
    private Context context;

    public FeaturesAdapter(@NonNull Context context, int resource, @NonNull List<Feature> objects) {
        super(context, resource, objects);
        this.objects = objects;
        this.context = context;
    }

    @Override
    public int getCount() {
        return this.objects.size();
    }

    static class ViewHolder {
        ImageView iconIv;
        TextView titleTv;
        TextView descriptionTv;
    }

    @Nullable
    @Override
    public Feature getItem(int position) {
        return this.objects.get(position);
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View row = convertView;
        ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();
            row = LayoutInflater.from(context).inflate(R.layout.features_row_layout, parent, false);
            holder.iconIv = row.findViewById(R.id.feature_icon);
            holder.titleTv = row.findViewById(R.id.feature_title);
            holder.descriptionTv = row.findViewById(R.id.feature_description);
            row.setTag(holder);
        } else holder = (ViewHolder) row.getTag();


        Feature feature = this.getItem(position);
        holder.iconIv.setImageResource(feature.getIcon());
        holder.titleTv.setText(feature.getTitle());
        holder.descriptionTv.setText(feature.getDescription());
        return row;
    }
}
