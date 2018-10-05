package com.gdevelopers.movies.adapters;

import android.content.Context;
import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.gdevelopers.movies.R;
import com.gdevelopers.movies.helpers.DateHelper;
import com.gdevelopers.movies.helpers.ItemTouchHelperAdapter;
import com.gdevelopers.movies.helpers.ItemTouchHelperViewHolder;
import com.gdevelopers.movies.objects.Movie;

import java.util.List;


public class MovieListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ItemTouchHelperAdapter {

    private final List<Movie> mItems;
    private final Context context;
    private OnItemClickListener onItemClickListener;
    private final int resource;
    private OnItemDeleted onItemDeleted;


    public MovieListAdapter(Context context, List<Movie> mItems) {
        super();
        this.context = context;
        this.mItems = mItems;
        this.resource = R.layout.user_movie_row_layout;
    }

    public void setOnItemDeleted(OnItemDeleted onItemDeleted) {
        this.onItemDeleted = onItemDeleted;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(context);
        return new MyViewHolder(inflater.inflate(resource, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, final int i) {
        MyViewHolder holder = (MyViewHolder) viewHolder;
        Movie movie = mItems.get(i);
        holder.nameTv.setText(movie.getTitle());
        Double vote = movie.getVoteAverage();

        holder.ratingTv.setText(vote == 0.0 ? context.getString(R.string.empty_text) :
                context.getString(R.string.vote_average_over_ten, vote));
        holder.dateTv.setText(DateHelper.formatDate(movie.getReleaseDate()));

        Glide.with(context).load(movie.getBackdropPath())
                .into(holder.backdropIv);

        Glide.with(context).load(movie.getPosterPath())
                .into(holder.posterIv);

    }

    public interface OnItemDeleted {
        void onItemDeleted(Movie movie, int position);
    }

    @Override
    public void onItemDismiss(int position) {
        Movie movie = mItems.get(position);
        mItems.remove(position);
        notifyItemRemoved(position);
        onItemDeleted.onItemDeleted(movie, position);
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

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, ItemTouchHelperViewHolder {

        private final TextView nameTv;
        private final TextView ratingTv;
        private final TextView dateTv;
        private final ImageView posterIv;
        private final ImageView backdropIv;

        MyViewHolder(View itemView) {
            super(itemView);
            nameTv = itemView.findViewById(R.id.movie_title);
            posterIv = itemView.findViewById(R.id.movie_poster);
            backdropIv = itemView.findViewById(R.id.movie_backdrop);
            ratingTv = itemView.findViewById(R.id.movie_rating);
            dateTv = itemView.findViewById(R.id.movie_date);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onItemClickListener.onItemClick(getAdapterPosition(), posterIv);
        }

        @Override
        public void onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemClear() {
            itemView.setBackgroundColor(0);
        }
    }

    public void restoreItem(Movie item, int position) {
        mItems.add(position, item);
        // notify item added by position
        notifyItemInserted(position);
    }
}
