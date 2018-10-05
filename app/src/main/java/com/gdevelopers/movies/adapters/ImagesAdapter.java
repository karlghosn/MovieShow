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
import com.gdevelopers.movies.helpers.MovieDB;

import java.util.List;

public class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.ViewHolder> {
    private final List<String> mItems;
    private final Context context;
    private final int resource;
    private OnItemClickListener onItemClickListener;

    public ImagesAdapter(Context context, List<String> mItems, boolean isActor) {
        super();
        this.context = context;
        this.mItems = mItems;
        this.resource = isActor ? R.layout.actor_images_row_layout : R.layout.images_row_layout;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(resource, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {
        String image = mItems.get(i);
        String url = MovieDB.IMAGE_URL + context.getString(R.string.galleryImgSize) + image;
        GlideApp.with(context).load(url)
                .centerCrop()
                .into(viewHolder.imageView);
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public int getItemCount() {
        final int maxCount = 10;
        return mItems.size() > maxCount ? maxCount : mItems.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final ImageView imageView;

        ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.filePath);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onItemClickListener.onItemClick(getAdapterPosition());
        }
    }
}

