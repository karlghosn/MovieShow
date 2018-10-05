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
import com.gdevelopers.movies.objects.Cast;

import java.util.ArrayList;
import java.util.List;

public class AllCastAdapter extends RecyclerView.Adapter<AllCastAdapter.ViewHolder> implements Filterable {

    private List<Cast> mItems;
    private final List<Cast> filterList;
    private ValueFilter valueFilter;
    private final Context context;
    private final int resource;
    private OnItemClickListener onItemClickListener;


    public AllCastAdapter(Context context, List<Cast> mItems) {
        super();
        this.context = context;
        this.mItems = mItems;
        this.resource = R.layout.all_cast_row_layout;
        this.filterList = mItems;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(resource, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {
        Cast cast = mItems.get(i);
        viewHolder.castTv.setText(cast.getName());
        viewHolder.characterTv.setText(cast.getCharacter());
        GlideApp.with(context).load(cast.getProfile_path())
                .error(R.drawable.placeholder)
                .into(viewHolder.nameIv);
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

        private final TextView castTv;
        private final TextView characterTv;
        private final ImageView nameIv;

        ViewHolder(View itemView) {
            super(itemView);
            castTv = itemView.findViewById(R.id.cast_name);
            nameIv = itemView.findViewById(R.id.name_image);
            characterTv = itemView.findViewById(R.id.cast_character);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onItemClickListener.onItemClick(getAdapterPosition(), nameIv);
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
                List<Cast> list = new ArrayList<>();
                for (int i = 0; i < filterList.size(); i++) {
                    if ((filterList.get(i).getName().toUpperCase())
                            .contains(constraint.toString().toUpperCase()) || (filterList.get(i).getCharacter().toUpperCase())
                            .contains(constraint.toString().toUpperCase())) {
                        Cast cast = filterList.get(i);
                        list.add(cast);
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
            mItems = (List<Cast>) results.values;
            notifyDataSetChanged();
        }
    }
}
