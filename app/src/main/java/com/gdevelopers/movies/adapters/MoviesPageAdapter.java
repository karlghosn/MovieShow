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
import com.gdevelopers.movies.helpers.GlideApp;
import com.gdevelopers.movies.helpers.MovieDB;
import com.gdevelopers.movies.helpers.OnClickHelper;
import com.gdevelopers.movies.objects.Movie;

import java.util.ArrayList;
import java.util.List;

public class MoviesPageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {

    private List<Movie> mItems;
    private final Context context;
    private final MoviesPageAdapter adapter = this;
    private final int typeMovie = 0;
    private OnLoadMoreListener loadMoreListener;
    private boolean isLoading = false;
    private final String type;
    private ValueFilter valueFilter;
    private final List<Movie> filterList;
    private final int resource;
    private final int loadResource;
    private int currentPage;
    private int totalPages;

    public String getType() {
        return type;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public MoviesPageAdapter(Context context, int resource, int loadResource, List<Movie> mItems, String type) {
        super();
        this.context = context;
        this.mItems = mItems;
        this.type = type;
        this.filterList = mItems;
        this.resource = resource;
        this.loadResource = loadResource;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(context);
        if (i == typeMovie) {
            return new MyViewHolder(inflater.inflate(resource, viewGroup, false));
        } else {
            return new LoadHolder(inflater.inflate(loadResource, viewGroup, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, int i) {
        if (i >= getItemCount() - 1 && !isLoading && loadMoreListener != null && currentPage < totalPages) {
            isLoading = true;
            Handler handler = new Handler();
            final Runnable r = () -> loadMoreListener.onLoadMore(adapter);
            handler.post(r);
        }

        if (getItemViewType(i) == typeMovie) {
            MyViewHolder holder = (MyViewHolder) viewHolder;
            Movie movie = mItems.get(i);
            holder.titleTv.setText(movie.getTitle());
            GlideApp.with(context).load(MovieDB.IMAGE_URL + context.getResources().getString(R.string.imageSize) + movie.getPosterPath())
                    .error(R.drawable.placeholder)
                    .into(holder.movieIv);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mItems.get(position).getType() != null) {
            if (mItems.get(position).getType().equals("movie")) {
                return typeMovie;
            } else {
                return 1;
            }
        }
        return typeMovie;
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    private class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView titleTv;
        private final ImageView movieIv;

        MyViewHolder(View itemView) {
            super(itemView);
            titleTv = itemView.findViewById(R.id.movie_title);
            movieIv = itemView.findViewById(R.id.movie_image);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Movie movie = mItems.get(getAdapterPosition());
            OnClickHelper.movieClicked(v.getContext(), movie.getTitle(), movie.getPosterPath(),
                    String.valueOf(movie.getId()), movieIv);
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
        void onLoadMore(MoviesPageAdapter adapter);
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
