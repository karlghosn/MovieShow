package com.gdevelopers.movies.adapters;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gdevelopers.movies.R;
import com.gdevelopers.movies.helpers.DateHelper;
import com.gdevelopers.movies.helpers.DialogHelper;
import com.gdevelopers.movies.objects.Comment;
import com.google.android.material.button.MaterialButton;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CommentsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<Comment> mItems;
    private final int resource;
    private final Context context;
    private final int typeMovie = 0;
    private OnLoadMoreListener loadMoreListener;
    private int currentPage;
    private int totalPages;
    private boolean isLoading = false;
    private final CommentsAdapter adapter = this;

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public CommentsAdapter(Context context, List<Comment> mItems) {
        super();
        this.context = context;
        this.mItems = mItems;
        this.resource = R.layout.comment_row_layout;
    }

    @Override
    public int getItemViewType(int position) {
        if (mItems.get(position).getType() != null) {
            if (mItems.get(position).getType().equals("comment")) {
                return typeMovie;
            } else {
                return 1;
            }
        }
        return typeMovie;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(context);
        if (i == typeMovie) {
            return new MyViewHolder(inflater.inflate(resource, viewGroup, false));
        } else {
            return new LoadHolder(inflater.inflate(R.layout.vertical_row_layout, viewGroup, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, final int i) {

        if (i >= getItemCount() - 1 && !isLoading && loadMoreListener != null && currentPage < totalPages) {
            isLoading = true;
            Handler handler = new Handler();
            final Runnable r = () -> loadMoreListener.onLoadMore(adapter);
            handler.post(r);
        }

        if (getItemViewType(i) == typeMovie) {
            Comment comment = mItems.get(i);
            MyViewHolder holder = (MyViewHolder) viewHolder;
            holder.usernameTv.setText(comment.getCommentUser().getUsername());
            String updateStr = DateHelper.formatCommentDate(comment.getUpdatedAt());
            holder.dateTv.setText(updateStr);
            holder.ratingTv.setText(context.getString(R.string.comment_rating, comment.getUserRating()));
            holder.commentTv.setVisibility(comment.isSpoiler() ? View.GONE : View.VISIBLE);
            holder.spoilerContainer.setVisibility(comment.isSpoiler() ? View.VISIBLE : View.GONE);
            holder.commentTv.setText(comment.getComment());
            holder.likesTv.setText(String.valueOf(comment.getLikes()));

            holder.showBt.setOnClickListener(v -> {
                comment.setSpoiler(false);
                holder.commentTv.setVisibility(View.VISIBLE);
                holder.spoilerContainer.setVisibility(View.GONE);
            });
        }

    }


    @Override
    public int getItemCount() {
        return mItems.size();
    }

    private class LoadHolder extends RecyclerView.ViewHolder {
        LoadHolder(View itemView) {
            super(itemView);
        }
    }

    public void notifyDataChanged() {
        notifyDataSetChanged();
        isLoading = false;
    }


    public interface OnLoadMoreListener {
        void onLoadMore(CommentsAdapter adapter);
    }

    public List<Comment> getmItems() {
        return mItems;
    }

    public void setLoadMoreListener(OnLoadMoreListener loadMoreListener) {
        this.loadMoreListener = loadMoreListener;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        private final TextView usernameTv;
        private final TextView dateTv;
        private final TextView ratingTv;
        private final TextView commentTv;
        private final MaterialButton likesTv;
        private final LinearLayout spoilerContainer;
        private final MaterialButton showBt;
        private final MaterialButton commentBt;

        MyViewHolder(View itemView) {
            super(itemView);
            usernameTv = itemView.findViewById(R.id.username);
            dateTv = itemView.findViewById(R.id.updatedDate);
            ratingTv = itemView.findViewById(R.id.rating);
            commentTv = itemView.findViewById(R.id.comment);
            likesTv = itemView.findViewById(R.id.likes);
            spoilerContainer = itemView.findViewById(R.id.spoiler_layout);
            showBt = itemView.findViewById(R.id.show_comment);
            commentBt = itemView.findViewById(R.id.leave_comment);

            likesTv.setOnClickListener(v -> DialogHelper.proVersionDialog(context));
            commentBt.setOnClickListener(v -> DialogHelper.proVersionDialog(context));
        }
    }

}
