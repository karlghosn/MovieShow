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
import com.gdevelopers.movies.objects.Season;

import java.util.List;


public class SeasonAdapter extends RecyclerView.Adapter<SeasonAdapter.ViewHolder> {

    private final List<Season> mItems;
    private final Context context;
    private final int resource;
    private OnItemClickListener onItemClickListener;

    public SeasonAdapter(Context context, List<Season> mItems) {
        super();
        this.context = context;
        this.mItems = mItems;
        this.resource = R.layout.season_row_layout;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(resource, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {
        Season season = mItems.get(i);
        viewHolder.episodesTv.setText(context.getString(R.string.episodes_number, season.getEpisodeCount()));

        String airDate = season.getAirDate();
        viewHolder.nameTv.setText(context.getString(R.string.season_date, season.getNumber(), DateHelper.formatYearDate(airDate)));

        GlideApp.with(context).load(season.getPosterPath())
                .error(R.drawable.placeholder)
                .into(viewHolder.searchIv);

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

        private final TextView nameTv;
        private final TextView episodesTv;
        private final ImageView searchIv;

        ViewHolder(View itemView) {
            super(itemView);
            nameTv = itemView.findViewById(R.id.season_name);
            searchIv = itemView.findViewById(R.id.season_image);
            episodesTv = itemView.findViewById(R.id.season_episodes);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onItemClickListener.onItemClick(getAdapterPosition());
        }
    }

}