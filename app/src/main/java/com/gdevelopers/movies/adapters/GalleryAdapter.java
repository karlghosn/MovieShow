package com.gdevelopers.movies.adapters;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.gdevelopers.movies.R;
import com.gdevelopers.movies.helpers.GlideApp;
import com.gdevelopers.movies.helpers.MovieDB;

import java.util.List;


public class GalleryAdapter extends ArrayAdapter<String> {
    private final List<String> objects;
    private final Context context;

    public GalleryAdapter(@NonNull Context context, List<String> objects) {
        super(context, R.layout.gallery_view_row, objects);
        this.objects = objects;
        this.context = context;
    }

    private static class ViewHolder {
        ImageView imageView;
    }

    @Override
    public void add(@Nullable String object) {
        super.add(object);
        this.objects.add(object);
    }

    @Override
    public int getCount() {
        return this.objects.size();
    }

    @Nullable
    @Override
    public String getItem(int position) {
        return this.objects.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View row = convertView;
        ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();
            row = LayoutInflater.from(context).inflate(R.layout.gallery_view_row, parent, false);
            holder.imageView = row.findViewById(R.id.filePath);
            row.setTag(holder);
        } else holder = (ViewHolder) row.getTag();

        String file_path = objects.get(position);
        GlideApp.with(context).load(MovieDB.IMAGE_URL + context.getResources().getString(R.string.galleryImgSize) + file_path)
                .into(holder.imageView);
        return row;
    }
}
