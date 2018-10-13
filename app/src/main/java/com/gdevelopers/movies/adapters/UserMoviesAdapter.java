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
import com.gdevelopers.movies.objects.Movie;

import java.util.List;


public class UserMoviesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<Movie> mItems;
    private final Context context;
    private final UserMoviesAdapter adapter = this;
    private OnItemClickListener onItemClickListener;
    private OnRemoveListener onRemoveListener;
    private final int TYPE_MOVIE = 0;
    private OnLoadMoreListener loadMoreListener;
    private boolean isLoading = false;


    public UserMoviesAdapter(Context context, List<Movie> mItems) {
        super();
        this.context = context;
        this.mItems = mItems;
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
            return new MyViewHolder(inflater.inflate(R.layout.favourite_row_layout, viewGroup, false));
        } else {
            return new LoadHolder(inflater.inflate(R.layout.vertical_row_layout, viewGroup, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, int i) {

        if (i >= getItemCount() - 1 && !isLoading && loadMoreListener != null) {
            isLoading = true;
            Handler handler = new Handler();
            final Runnable r = () -> loadMoreListener.onLoadMore(adapter);

            handler.post(r);
        }

        if (getItemViewType(i) == TYPE_MOVIE) {
            final MyViewHolder holder = (MyViewHolder) viewHolder;
            final Movie movie = mItems.get(i);
            holder.nameTv.setText(movie.getTitle());

            GlideApp.with(context).load(movie.getPosterPath())
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .into(holder.movieIv);

            holder.removeIv.setOnClickListener(view -> onRemoveListener.onRemove(movie, holder.getAdapterPosition()));
        }

    }

    public void removeAt(int position) {
        mItems.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mItems.size());
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


    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView nameTv;
        private final ImageView movieIv;
        private final ImageView removeIv;

        MyViewHolder(View itemView) {
            super(itemView);
            nameTv = itemView.findViewById(R.id.name);
            movieIv = itemView.findViewById(R.id.favorite_image);
            removeIv = itemView.findViewById(R.id.remove_favourite);
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
        void onLoadMore(UserMoviesAdapter adapter);
    }

    public interface OnRemoveListener {
        void onRemove(Movie movie, int position);
    }

    public void setOnRemoveListener(OnRemoveListener onRemoveListener) {
        this.onRemoveListener = onRemoveListener;
    }

    public void setLoadMoreListener(OnLoadMoreListener loadMoreListener) {
        this.loadMoreListener = loadMoreListener;
    }

}
