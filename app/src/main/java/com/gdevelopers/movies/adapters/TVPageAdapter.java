package com.gdevelopers.movies.adapters;


import android.content.Context;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gdevelopers.movies.R;
import com.gdevelopers.movies.helpers.GlideApp;
import com.gdevelopers.movies.helpers.MovieDB;
import com.gdevelopers.movies.objects.TVShow;

import java.util.List;

public class TVPageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<TVShow> mItems;
    private final Context context;
    private OnItemClickListener onItemClickListener;
    private final TVPageAdapter adapter = this;
    private final int TYPE_MOVIE = 0;
    private OnLoadMoreListener loadMoreListener;
    private boolean isLoading = false;
    private final String type;
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

    public TVPageAdapter(Context context, List<TVShow> mItems, String type) {
        super();
        this.context = context;
        this.mItems = mItems;
        this.type = type;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(context);
        if (i == TYPE_MOVIE) {
            return new ViewHolder(inflater.inflate(R.layout.home_list_item_layout, viewGroup, false));
        } else {
            return new LoadHolder(inflater.inflate(R.layout.horizontal_row_load, viewGroup, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, int i) {

        if (i >= getItemCount() - 1 && !isLoading && loadMoreListener != null && currentPage < totalPages) {
            isLoading = true;
            Handler handler = new Handler();
            final Runnable r = new Runnable() {
                public void run() {
                    loadMoreListener.onLoadMore(adapter);
                }
            };
            handler.post(r);
        }

        if (getItemViewType(i) == TYPE_MOVIE) {
            ViewHolder holder = (ViewHolder) viewHolder;
            TVShow tvShow = mItems.get(i);
            holder.titleTv.setText(tvShow.getName());
            GlideApp.with(context).load(MovieDB.IMAGE_URL + context.getResources().getString(R.string.imageSize) + tvShow.getPosterPath())
                    .error(R.drawable.placeholder)
                    .into(holder.movieIv);
        }

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

    @Override
    public int getItemCount() {

        return mItems.size();
    }


    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


    public interface OnItemClickListener {
        void onItemClick(TVShow tvShow, View v);
    }


    private class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView titleTv;
        private final ImageView movieIv;

        ViewHolder(View itemView) {
            super(itemView);
            titleTv = itemView.findViewById(R.id.movie_title);
            movieIv = itemView.findViewById(R.id.movie_image);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onItemClickListener.onItemClick(mItems.get(getAdapterPosition()), v);
        }
    }

    private class LoadHolder extends RecyclerView.ViewHolder {
        LoadHolder(View itemView) {
            super(itemView);
        }
    }

    public List<TVShow> getmItems() {
        return mItems;
    }

    public void notifyDataChanged() {
        notifyDataSetChanged();
        isLoading = false;
    }


    public interface OnLoadMoreListener {
        void onLoadMore(TVPageAdapter adapter);
    }

    public void setLoadMoreListener(OnLoadMoreListener loadMoreListener) {
        this.loadMoreListener = loadMoreListener;
    }
}

