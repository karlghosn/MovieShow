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
import com.gdevelopers.movies.helpers.MovieDB;
import com.gdevelopers.movies.objects.Trailer;

import java.util.List;


public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.ViewHolder> {
    private final List<Trailer> mItems;
    private final Context context;
    private final int resource;
    private OnItemClickListener onItemClickListener;

    public TrailerAdapter(Context context, List<Trailer> mItems) {
        super();
        this.context = context;
        this.mItems = mItems;
        this.resource = R.layout.trailer_row_layout;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(resource, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {
        Trailer trailer = mItems.get(i);
        viewHolder.nameTv.setText(trailer.getName());
        GlideApp.with(context).load(MovieDB.TRAILER_IMAGE_URL + trailer.getSource() + "/" + "hqdefault.jpg")
                .centerCrop()
                .into(viewHolder.videoIv);
    }

    public interface OnItemClickListener {
        void onItemClick(Trailer trailer);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public int getItemCount() {
        final int maxCount = 8;
        return mItems.size() > maxCount ? maxCount : mItems.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView nameTv;
        private final ImageView videoIv;

        ViewHolder(View itemView) {
            super(itemView);
            nameTv = itemView.findViewById(R.id.video_name);
            videoIv = itemView.findViewById(R.id.filePath);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onItemClickListener.onItemClick(mItems.get(getAdapterPosition()));
        }
    }
}
