package com.gdevelopers.movies.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.gdevelopers.movies.R;
import com.gdevelopers.movies.helpers.GlideApp;
import com.gdevelopers.movies.objects.Show;

import java.util.ArrayList;
import java.util.List;


public class ShowAdapter extends RecyclerView.Adapter<ShowAdapter.ViewHolder> implements Filterable {

    private List<Show> mItems;
    private final Context context;
    private final List<Show> filterList;
    private ValueFilter valueFilter;
    private OnItemClickListener onItemClickListener;
    private final boolean showAll;
    private final boolean isCrew;

    public ShowAdapter(Context context, List<Show> mItems, boolean isCrew) {
        super();
        this.context = context;
        this.mItems = mItems;
        this.showAll = true;
        this.isCrew = isCrew;
        this.filterList = mItems;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(context);
        return new ViewHolder(inflater.inflate(R.layout.show_row_layout, viewGroup, false));

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Show movie = mItems.get(i);
        viewHolder.titleTv.setText(movie.getTitle());
        viewHolder.characterTv.setText(isCrew ? movie.getJob() : movie.getCharacter());
        GlideApp.with(context).load(movie.getPosterPath())
                .error(R.drawable.placeholder)
                .into(viewHolder.showIv);

    }

    @Override
    public int getItemCount() {
        if (showAll)
            return mItems.size();
        else
            return mItems.size() > 20 ? 20 : mItems.size();
    }


    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(Show show, View v);
    }


    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView titleTv;
        private final TextView characterTv;
        private final ImageView showIv;

        ViewHolder(View itemView) {
            super(itemView);
            titleTv = itemView.findViewById(R.id.show_title);
            characterTv = itemView.findViewById(R.id.show_character);
            showIv = itemView.findViewById(R.id.show_image);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onItemClickListener.onItemClick(mItems.get(getAdapterPosition()), v);
        }
    }

    @Override
    public Filter getFilter() {
        if (valueFilter == null) {
            valueFilter = new ValueFilter();
        }
        return valueFilter;
    }

    private class ValueFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            if (constraint != null && constraint.length() > 0) {
                List<Show> list = new ArrayList<>();
                for (int i = 0; i < filterList.size(); i++) {
                    if ((filterList.get(i).getTitle().toUpperCase())
                            .contains(constraint.toString().toUpperCase())) {
                        Show show = filterList.get(i);
                        list.add(show);
                    }
                }
                results.count = list.size();
                results.values = list;
            } else {
                results.count = filterList.size();
                results.values = filterList;
            }
            return results;

        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {
            mItems = (List<Show>) results.values;
            notifyDataSetChanged();
        }
    }
}
