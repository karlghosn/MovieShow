package com.gdevelopers.movies.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gdevelopers.movies.R;
import com.gdevelopers.movies.helpers.LetterTileProvider;
import com.gdevelopers.movies.objects.UserList;

import java.util.List;


public class UserListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<UserList> mItems;
    private final Context context;
    private OnItemClickListener onItemClickListener;


    public UserListAdapter(Context context, List<UserList> mItems) {
        super();
        this.context = context;
        this.mItems = mItems;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(context);
        return new MyViewHolder(inflater.inflate(R.layout.user_list_row_layout, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, final int i) {
        MyViewHolder holder = (MyViewHolder) viewHolder;
        UserList userList = mItems.get(i);
        holder.nameTv.setText(userList.getName());
        String count = userList.getItemCount();
        holder.itemsTv.setText(!count.equals("1") ? count + " items" : count + " item");
        String description = userList.getDescription();
        holder.descriptionTv.setText(description.equals("") ? "No description" : description);

        LetterTileProvider letterTileProvider = new LetterTileProvider(context);
        holder.imageView.setImageBitmap(letterTileProvider.getCircularLetterTile(userList.getName()));

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


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView nameTv;
        private final TextView descriptionTv;
        private final TextView itemsTv;
        private final ImageView imageView;
        public final RelativeLayout viewForeground;

        MyViewHolder(View itemView) {
            super(itemView);
            nameTv = itemView.findViewById(R.id.list_name);
            itemsTv = itemView.findViewById(R.id.list_items);
            imageView = itemView.findViewById(R.id.icon_profile);
            descriptionTv = itemView.findViewById(R.id.list_description);
            viewForeground = itemView.findViewById(R.id.view_foreground);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onItemClickListener.onItemClick(getAdapterPosition());
        }
    }

    public void removeItem(int position) {
        mItems.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreItem(UserList item, int position) {
        mItems.add(position, item);
        // notify item added by position
        notifyItemInserted(position);
    }

}
