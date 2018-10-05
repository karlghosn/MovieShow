package br.com.mauker.materialsearchview.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import br.com.mauker.materialsearchview.R;
import br.com.mauker.materialsearchview.db.HistoryContract;

/**
 * Created by mauker on 19/04/2016.
 * <p>
 * Default adapter used for the suggestion/history ListView.
 */
public class CursorSearchAdapter extends CursorAdapter {
    private List<String> stringList = new ArrayList<>();

    public CursorSearchAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ListViewHolder vh = new ListViewHolder(view);
        view.setTag(vh);

        String text = cursor.getString(cursor.getColumnIndexOrThrow(HistoryContract.HistoryEntry.COLUMN_QUERY));

        boolean isHistory = cursor.getInt(cursor.getColumnIndexOrThrow(
                HistoryContract.HistoryEntry.COLUMN_IS_HISTORY)) != 0;

        vh.tv_content.setText(text);

        if (isHistory) {
            vh.iv_icon.setImageResource(R.drawable.ic_history_gray);
        } else {
            vh.iv_icon.setImageResource(R.drawable.ic_action_search_white);
        }
    }

    @Override
    public Object getItem(int position) {
        String retString = "";

        // Move to position, get query
        Cursor cursor = getCursor();
        if (cursor.moveToPosition(position)) {
            retString = cursor.getString(cursor.getColumnIndexOrThrow(HistoryContract.HistoryEntry.COLUMN_QUERY));
            this.stringList.add(retString);
        }

        return retString;
    }

    public List<String> getStringList() {
        if (stringList == null)
            stringList = new ArrayList<>();
        return stringList;
    }

    private class ListViewHolder {
        final ImageView iv_icon;
        final TextView tv_content;

        ListViewHolder(View convertView) {
            iv_icon = convertView.findViewById(R.id.iv_icon);
            tv_content = convertView.findViewById(R.id.tv_str);
        }
    }
}
