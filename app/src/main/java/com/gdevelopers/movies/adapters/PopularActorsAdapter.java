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

import com.bumptech.glide.Glide;
import com.gdevelopers.movies.R;
import com.gdevelopers.movies.helpers.MovieDB;
import com.gdevelopers.movies.objects.Actor;

import java.util.List;


public class PopularActorsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<Actor> mItems;
    private final Context context;
    private final PopularActorsAdapter adapter = this;
    private OnItemClickListener onItemClickListener;
    private static final int TYPE_MOVIE = 0;
    private OnLoadMoreListener loadMoreListener;
    private boolean isLoading = false;
    private final boolean isHome;


    public PopularActorsAdapter(Context context, List<Actor> mItems, boolean isHome) {
        super();
        this.context = context;
        this.mItems = mItems;
        this.isHome = isHome;
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
            return new MyViewHolder(inflater.inflate(isHome ? R.layout.home_actor_row_layout : R.layout.popular_people_row_layout, viewGroup, false));
        } else {
            return new LoadHolder(inflater.inflate(R.layout.vertical_row_layout, viewGroup, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, final int i) {

        if (i >= getItemCount() - 1 && !isLoading && loadMoreListener != null) {
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
            MyViewHolder holder = (MyViewHolder) viewHolder;
            Actor actor = mItems.get(i);
            String name = isHome ? actor.getName().replace(" ", "\n") : actor.getName();
            holder.nameTv.setText(name);
            Glide.with(context)
                    .load(MovieDB.IMAGE_URL + context.getResources().getString(R.string.imageSize) + actor.getProfilePath())
                    .into(holder.imageIv);
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


    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView nameTv;
        private final ImageView imageIv;

        MyViewHolder(View itemView) {
            super(itemView);
            nameTv = itemView.findViewById(R.id.cast_name);
            imageIv = itemView.findViewById(R.id.name_image);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onItemClickListener.onItemClick(getAdapterPosition(), imageIv);
        }
    }

    private class LoadHolder extends RecyclerView.ViewHolder {
        LoadHolder(View itemView) {
            super(itemView);
        }
    }

    public List<Actor> getmItems() {
        return mItems;
    }

    public void notifyDataChanged() {
        notifyDataSetChanged();
        isLoading = false;
    }


    public interface OnLoadMoreListener {
        void onLoadMore(PopularActorsAdapter adapter);
    }

    public void setLoadMoreListener(OnLoadMoreListener loadMoreListener) {
        this.loadMoreListener = loadMoreListener;
    }

}
