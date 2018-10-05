package com.gdevelopers.movies.objects;

import com.gdevelopers.movies.model.KObject;


public class UserList extends KObject {
    private String itemCount;
    private String name;
    private String description;

    public UserList(long id) {
        super(id);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getItemCount() {
        return itemCount;
    }

    public void setItemCount(String itemCount) {
        this.itemCount = itemCount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
