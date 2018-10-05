package com.gdevelopers.movies.helpers;

import android.widget.RemoteViews;

public class MyRemoteView extends RemoteViews {
    private int id;

    public MyRemoteView(String packageName) {
        super(packageName, com.gdevelopers.movies.R.layout.widget_list_item);
    }

    @SuppressWarnings("unused")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
