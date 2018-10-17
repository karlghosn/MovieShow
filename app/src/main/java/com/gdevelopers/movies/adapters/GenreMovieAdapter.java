package com.gdevelopers.movies.adapters;

import android.content.Context;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.gdevelopers.movies.R;
import com.gdevelopers.movies.helpers.DateHelper;
import com.gdevelopers.movies.helpers.GlideApp;
import com.gdevelopers.movies.helpers.MovieDB;
import com.gdevelopers.movies.objects.Movie;

import java.util.ArrayList;
import java.util.List;


public class GenreMovieAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {

    private List<Movie> mItems;
    private final Context context;
    private final GenreMovieAdapter adapter = this;
    private OnItemClickListener onItemClickListener;
    private final int TYPE_MOVIE = 0;
    private OnLoadMoreListener loadMoreListener;
    private final int resource;
    private boolean isLoading = false;
    private ValueFilter valueFilter;
    private final List<Movie> filterList;


    public GenreMovieAdapter(Context context, List<Movie> mItems) {
        super();
        this.context = context;
        this.mItems = mItems;
        this.resource = R.layout.genre_movie_row_layout;
        this.filterList = mItems;
    }

    @Override
    public int getItemViewType(int position) {
        if (mItems.get(position).getType() != null) {
            if (mItems.get(position).getType().equals("movie")) {
                return TYPE_MOVIE;
            } else {
                return 1;
            }
        }
        return TYPE_MOVIE;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(context);
        if (i == TYPE_MOVIE) {
            return new GenreMovieAdapter.ViewHolder(inflater.inflate(resource, viewGroup, false));
        } else {
            return new GenreMovieAdapter.LoadHolder(inflater.inflate(R.layout.vertical_row_layout, viewGroup, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, final int i) {

        if (i >= getItemCount() - 1 && !isLoading && loadMoreListener != null) {
            isLoading = true;
            Handler handler = new Handler();
            final Runnable r = () -> loadMoreListener.onLoadMore(adapter);

            handler.post(r);
        }

        if (getItemViewType(i) == TYPE_MOVIE) {
            ViewHolder holder = (ViewHolder) viewHolder;
            Movie movie = mItems.get(i);
            holder.nameTv.setText(movie.getTitle());
            holder.ratingTv.setText(context.getString(R.string.vote_average_over_ten, movie.getVoteAverage()));
            holder.dateTv.setText(DateHelper.formatDate(movie.getReleaseDate()));

            GlideApp.with(context).load(MovieDB.IMAGE_URL + context.getString(R.string.imageSize) + movie.getPosterPath())
                    .error(R.drawable.placeholder)
                    .into(holder.movieIv);
        }
    }


    public interface OnItemClickListener {
        void onItemClick(int position, ImageView imageView);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public Object getItem(int position) {
        return this.mItems.get(position);
    }


    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView nameTv;
        private final TextView ratingTv;
        private final TextView dateTv;
        private final ImageView movieIv;

        ViewHolder(View itemView) {
            super(itemView);
            nameTv = itemView.findViewById(R.id.movie_name);
            movieIv = itemView.findViewById(R.id.movie_image);
            ratingTv = itemView.findViewById(R.id.movie_rating);
            dateTv = itemView.findViewById(R.id.movie_date);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onItemClickListener.onItemClick(getAdapterPosition(), movieIv);
        }
    }

    private class LoadHolder extends RecyclerView.ViewHolder {
        LoadHolder(View itemView) {
            super(itemView);
        }
    }

    public List<Movie> getmItems() {
        return mItems;
    }

    public void notifyDataChanged() {
        notifyDataSetChanged();
        isLoading = false;
    }


    public interface OnLoadMoreListener {
        void onLoadMore(GenreMovieAdapter adapter);
    }

    public void setLoadMoreListener(OnLoadMoreListener loadMoreListener) {
        this.loadMoreListener = loadMoreListener;
    }

    @Override
    public Filter getFilter() {
        if (valueFilter == null) {
            valueFilter = new ValueFilter();
        }
        return valueFilter;
    }

    private class ValueFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            if (constraint != null && constraint.length() > 0) {
                List<Movie> list = new ArrayList<>();
                for (int i = 0; i < filterList.size(); i++) {
                    if ((filterList.get(i).getTitle().toUpperCase())
                            .contains(constraint.toString().toUpperCase())) {
                        Movie movie = filterList.get(i);
                        list.add(movie);
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
            mItems = (List<Movie>) results.values;
            notifyDataSetChanged();
        }
    }
}
