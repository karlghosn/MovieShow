package com.gdevelopers.movies.objects;

import com.google.gson.annotations.SerializedName;

public class Comment {
    @SerializedName("id")
    private int id;
    @SerializedName("comment")
    private String comment;
    @SerializedName("spoiler")
    private boolean spoiler;
    @SerializedName("likes")
    private int likes;
    @SerializedName("user_rating")
    private int userRating;
    @SerializedName("updated_at")
    private String updatedAt;
    @SerializedName("user")
    private CommentUser commentUser;
    private String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public CommentUser getCommentUser() {
        return commentUser;
    }

    public void setSpoiler(boolean spoiler) {
        this.spoiler = spoiler;
    }

    public int getId() {
        return id;
    }

    public String getComment() {
        return comment;
    }

    public boolean isSpoiler() {
        return spoiler;
    }

    public int getLikes() {
        return likes;
    }

    public int getUserRating() {
        return userRating;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public class CommentUser {
        @SerializedName("username")
        private String username;

        public String getUsername() {
            return username;
        }
    }
}
