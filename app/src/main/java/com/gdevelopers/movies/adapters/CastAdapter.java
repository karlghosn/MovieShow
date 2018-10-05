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
import com.gdevelopers.movies.objects.Cast;

import java.util.List;

public class CastAdapter extends RecyclerView.Adapter<CastAdapter.ViewHolder> {

    private final List<Cast> mItems;
    private final Context context;
    private final int resource;
    private OnItemClickListener onItemClickListener;


    public CastAdapter(Context context, List<Cast> mItems) {
        super();
        this.context = context;
        this.mItems = mItems;
        this.resource = R.layout.cast_row_layout;
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
        viewHolder.castTv.setText(cast.getName().split(" ")[0]);
        viewHolder.characterTv.setText(cast.getCharacter());
        GlideApp.with(context).load(cast.getProfile_path())
                .placeholder(R.drawable.placeholder)
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
        final int maxCount = 8;
        return mItems.size() > maxCount ? maxCount : mItems.size();
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

}
