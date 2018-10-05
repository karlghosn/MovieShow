package com.gdevelopers.movies.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
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
import com.gdevelopers.movies.activities.ActorDetailsActivity;
import com.gdevelopers.movies.helpers.GlideApp;
import com.gdevelopers.movies.objects.Crew;

import java.util.ArrayList;
import java.util.List;

public class AllCrewAdapter extends RecyclerView.Adapter<AllCrewAdapter.ViewHolder> implements Filterable {

    private List<Crew> mItems;
    private final List<Crew> filterList;
    private ValueFilter valueFilter;
    private final Context context;
    private final int resource;

    public AllCrewAdapter(Context context, List<Crew> mItems) {
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
        Crew crew = mItems.get(i);
        viewHolder.castTv.setText(crew.getName());
        viewHolder.characterTv.setText(crew.getJob());
        GlideApp.with(context).load(crew.getProfilePath())
                .error(R.drawable.placeholder)
                .into(viewHolder.nameIv);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    private Object getItem(int position) {
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
            Crew crew = (Crew) getItem(getAdapterPosition());

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                nameIv.setTransitionName(context.getString(R.string.cast_image));
            }
            Intent intent = new Intent(context, ActorDetailsActivity.class);
            intent.putExtra("id", crew.getId());
            intent.putExtra("title", crew.getName());
            intent.putExtra("image", crew.getProfilePath());
            context.startActivity(intent);
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
                List<Crew> list = new ArrayList<>();
                for (int i = 0; i < filterList.size(); i++) {
                    if ((filterList.get(i).getName().toUpperCase())
                            .contains(constraint.toString().toUpperCase()) || (filterList.get(i).getJob().toUpperCase())
                            .contains(constraint.toString().toUpperCase())) {
                        Crew crew = filterList.get(i);
                        list.add(crew);
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
            mItems = (List<Crew>) results.values;
            notifyDataSetChanged();
        }
    }

}
