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
import com.gdevelopers.movies.helpers.DateHelper;
import com.gdevelopers.movies.helpers.GlideApp;
import com.gdevelopers.movies.helpers.MovieDB;
import com.gdevelopers.movies.objects.Episode;

import java.util.List;


public class EpisodesAdapter extends RecyclerView.Adapter<EpisodesAdapter.ViewHolder> {

    private final List<Episode> mItems;
    private final Context context;
    private final int resource;
    private OnItemClickListener onItemClickListener;


    public EpisodesAdapter(Context context, List<Episode> mItems) {
        super();
        this.context = context;
        this.mItems = mItems;
        this.resource = R.layout.episode_row_layout;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(resource, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {
        Episode episode = mItems.get(i);
        viewHolder.numberTv.setText(context.getString(R.string.episode_number, episode.getNumber()));
        viewHolder.nameTv.setText(episode.getName());

        String airDate = episode.getAirDate();
        String date = DateHelper.getDays(airDate);
        viewHolder.dateTv.setText(date.contains("-") ? DateHelper.formatDate(airDate) + ", in " + date.replaceAll("-", "") + " days" :
                DateHelper.formatDate(airDate));

        GlideApp.with(context).load(MovieDB.IMAGE_URL + context.getString(R.string.galleryImgSize) + episode.getStillPath())
                .centerCrop()
                .error(R.drawable.placeholder)
                .into(viewHolder.episodeIv);
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
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

        private final TextView numberTv;
        private final TextView nameTv;
        private final TextView dateTv;
        private final ImageView episodeIv;

        ViewHolder(View itemView) {
            super(itemView);
            numberTv = itemView.findViewById(R.id.episode_number);
            episodeIv = itemView.findViewById(R.id.episode_image);
            nameTv = itemView.findViewById(R.id.episode_name);
            dateTv = itemView.findViewById(R.id.episode_date);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onItemClickListener.onItemClick(getAdapterPosition());
        }
    }

}