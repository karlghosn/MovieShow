package com.gdevelopers.movies.adapters;


import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.gdevelopers.movies.R;
import com.gdevelopers.movies.helpers.GlideApp;
import com.gdevelopers.movies.helpers.LobsterTextView;
import com.gdevelopers.movies.helpers.MovieDB;
import com.gdevelopers.movies.helpers.OnClickHelper;
import com.gdevelopers.movies.objects.Movie;
import com.gdevelopers.movies.objects.TVShow;

import java.util.List;

public class HomeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<Movie> movieList;
    private final List<TVShow> tvShowList;
    private final Context context;
    private final boolean isMovie;

    public HomeAdapter(Context context, List<Movie> movieList, List<TVShow> tvShowList, boolean isMovie) {
        super();
        this.context = context;
        this.movieList = movieList;
        this.tvShowList = tvShowList;
        this.isMovie = isMovie;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(context);
        return new MyViewHolder(inflater.inflate(R.layout.home_list_item_layout, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, int i) {
        MyViewHolder holder = (MyViewHolder) viewHolder;
        if (isMovie) {
            Movie movie = movieList.get(i);
            holder.titleTv.setText((movie.getTitle()));
            GlideApp.with(context).load(MovieDB.IMAGE_URL + context.getResources().getString(R.string.imageSize) + movie.getPosterPath())
                    .error(R.drawable.placeholder)
                    .into(holder.movieIv);
        } else {
            TVShow tvShow = tvShowList.get(i);
            holder.titleTv.setText(tvShow.getName());
            GlideApp.with(context).load(MovieDB.IMAGE_URL + context.getResources().getString(R.string.imageSize) + tvShow.getPosterPath())
                    .error(R.drawable.placeholder)
                    .into(holder.movieIv);
        }
    }


    @Override
    public int getItemCount() {
        return isMovie ? movieList.size() : tvShowList.size();
    }

    private class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final LobsterTextView titleTv;
        private final ImageView movieIv;

        MyViewHolder(View itemView) {
            super(itemView);
            titleTv = itemView.findViewById(R.id.movie_title);
            movieIv = itemView.findViewById(R.id.movie_image);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (isMovie) {
                Movie movie = movieList.get(getAdapterPosition());
                OnClickHelper.movieClicked(v.getContext(), movie.getTitle(), movie.getPosterPath(),
                        String.valueOf(movie.getId()), movieIv);
            } else {
                TVShow tvShow = tvShowList.get(getAdapterPosition());
                OnClickHelper.tvClicked(v.getContext(), tvShow.getName(), tvShow.getPosterPath(),
                        String.valueOf(tvShow.getId()), movieIv);
            }
        }
    }
}
