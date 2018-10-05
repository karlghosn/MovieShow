package com.gdevelopers.movies.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gdevelopers.movies.R;
import com.gdevelopers.movies.helpers.GlideApp;
import com.gdevelopers.movies.objects.Genre;

import java.util.List;


public class DiscoverGenreAdapter extends RecyclerView.Adapter<DiscoverGenreAdapter.ViewHolder> {

    private final List<Genre> mItems;
    private final Context context;
    private final int resource;
    private CastAdapter.OnItemClickListener onItemClickListener;
    private final String[] images;


    public DiscoverGenreAdapter(Context context, List<Genre> mItems, String[] images) {
        super();
        this.context = context;
        this.mItems = mItems;
        this.resource = R.layout.genre_row_layout;
        this.images = images;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(resource, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {
        Genre genre = mItems.get(i);
        viewHolder.nameTv.setText(genre.getName());

        GlideApp.with(this.context).load(images[i])
                .centerCrop()
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .into(viewHolder.posterIv);
    }


    public void setOnItemClickListener(CastAdapter.OnItemClickListener onItemClickListener) {
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
        private final ImageView posterIv;

        ViewHolder(View itemView) {
            super(itemView);
            nameTv = itemView.findViewById(R.id.name);
            posterIv = itemView.findViewById(R.id.poster);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onItemClickListener.onItemClick(getAdapterPosition(), posterIv);
        }
    }

}
