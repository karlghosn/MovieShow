package com.gdevelopers.movies.adapters;

import android.content.Context;
import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.gdevelopers.movies.R;
import com.gdevelopers.movies.objects.Genre;

import java.util.List;


public class GenreAdapter extends ArrayAdapter<Genre> {
    private final List<Genre> objects;
    private final Context context;

    public GenreAdapter(@NonNull Context context, @NonNull List<Genre> objects) {
        super(context, R.layout.spinner_item, objects);
        this.objects = objects;
        this.context = context;
    }

    static class ViewHolder {
        TextView textView;
    }

    @Override
    public void add(@Nullable Genre object) {
        super.add(object);
        this.objects.add(object);
    }

    @Override
    public int getCount() {
        return this.objects.size();
    }

    @Nullable
    @Override
    public Genre getItem(int position) {
        return this.objects.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View row = convertView;
        ViewHolder holder;

        if (convertView == null) {
            row = LayoutInflater.from(context).inflate(R.layout.spinner_item, parent, false);
            holder = new ViewHolder();
            holder.textView = row.findViewById(R.id.textView);
            row.setTag(holder);
        } else holder = (ViewHolder) row.getTag();

        Genre genre = this.getItem(position);
        holder.textView.setText(genre != null ? genre.getName() : null);
        return row;
    }

    @Override
    public boolean isEnabled(int position) {
        return position != 0;
    }


    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View row = convertView;
        ViewHolder holder;

        if (convertView == null) {
            row = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, null);
            holder = new ViewHolder();
            holder.textView = row.findViewById(android.R.id.text1);
            row.setTag(holder);
        } else holder = (ViewHolder) row.getTag();

        Genre genre = this.getItem(position);
        holder.textView.setText(genre != null ? genre.getName() : null);
        holder.textView.setTextColor(position == 0 ? Color.GRAY : Color.BLACK);
        return row;
    }
}
