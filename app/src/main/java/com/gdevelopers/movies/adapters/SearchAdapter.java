package com.gdevelopers.movies.adapters;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gdevelopers.movies.R;
import com.gdevelopers.movies.helpers.DateHelper;
import com.gdevelopers.movies.helpers.GlideApp;
import com.gdevelopers.movies.objects.Search;

import java.util.List;


public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {

    private final List<Search> mItems;
    private final Context context;
    private final int resource;
    private OnItemClickListener onItemClickListener;


    public SearchAdapter(Context context, List<Search> mItems, int resource) {
        super();
        this.context = context;
        this.mItems = mItems;
        this.resource = resource;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(resource, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {
        Search search = mItems.get(i);
        viewHolder.nameTv.setText(search.getTitle());
        String type = search.getMediaType();

        if (type != null)
            viewHolder.typeTv.setText(getType(search.getMediaType()));
        else viewHolder.typeTv.setVisibility(View.GONE);

        if (search.getReleaseDate().equals(""))
            viewHolder.dateTv.setVisibility(View.GONE);
        else viewHolder.dateTv.setText(DateHelper.formatDate(search.getReleaseDate()));

        Float voteAverage = search.getVoteAverage();
        if (voteAverage != null)
            viewHolder.ratingTv.setText(voteAverage == 0 ? context.getString(R.string.empty_text) :
                    context.getString(R.string.vote_average_over_ten, search.getVoteAverage()));
        else viewHolder.voteLayout.setVisibility(View.GONE);


        GlideApp.with(context).load(search.getPosterPath())
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .into(viewHolder.searchIv);


    }

    private String getType(String type) {
        switch (type) {
            case "movie":
                return "Movie";
            case "tv":
                return "TV Show";
            case "person":
                return "Actor";
        }
        return "";
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


    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView nameTv, dateTv, typeTv, ratingTv;
        private final ImageView searchIv;
        private final LinearLayout voteLayout;

        ViewHolder(View itemView) {
            super(itemView);
            nameTv = itemView.findViewById(R.id.search_name);
            searchIv = itemView.findViewById(R.id.search_image);
            typeTv = itemView.findViewById(R.id.search_type);
            dateTv = itemView.findViewById(R.id.search_date);
            ratingTv = itemView.findViewById(R.id.vote_average);
            voteLayout = itemView.findViewById(R.id.vote_layout);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onItemClickListener.onItemClick(getAdapterPosition(), searchIv);
        }
    }

}
