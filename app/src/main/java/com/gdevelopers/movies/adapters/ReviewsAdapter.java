package com.gdevelopers.movies.adapters;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gdevelopers.movies.R;
import com.gdevelopers.movies.objects.Review;

import java.util.List;


public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ViewHolder> {

    private final List<Review> mItems;
    private final int resource;
    private final int count;
    private OnItemClickListener onItemClickListener;


    public ReviewsAdapter(List<Review> mItems, boolean showAll) {
        super();
        this.mItems = mItems;
        this.resource = R.layout.reviews_row_layout;
        if ((!showAll && mItems.size() < 3) || showAll)
            count = mItems.size();
        else count = 3;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(resource, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {
        Review review = mItems.get(i);
        viewHolder.authorTv.setText(review.getAuthor());
        viewHolder.contentTv.setText(review.getContent());
        if (i == count - 1)
            viewHolder.separator.setVisibility(View.GONE);
    }


    @Override
    public int getItemCount() {
        return count;
    }


    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView authorTv;
        private final TextView contentTv;
        private final View separator;

        ViewHolder(View itemView) {
            super(itemView);
            authorTv = itemView.findViewById(R.id.review_author);
            contentTv = itemView.findViewById(R.id.review_content);
            separator = itemView.findViewById(R.id.separator);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onItemClickListener.onItemClick(mItems.get(getAdapterPosition()));
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


    public interface OnItemClickListener {
        void onItemClick(Review review);
    }

}
