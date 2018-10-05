package com.gdevelopers.movies.objects;

import android.os.Parcel;
import android.os.Parcelable;

public class Cast implements Parcelable {
    private String character;
    private String credit_id;
    private String name;
    private String order;
    private String profile_path;
    private String id;

    public Cast() {
    }

    private Cast(Parcel in) {
        character = in.readString();
        credit_id = in.readString();
        name = in.readString();
        order = in.readString();
        profile_path = in.readString();
        id = in.readString();
    }

    public static final Creator<Cast> CREATOR = new Creator<Cast>() {
        @Override
        public Cast createFromParcel(Parcel in) {
            return new Cast(in);
        }

        @Override
        public Cast[] newArray(int size) {
            return new Cast[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCharacter() {
        return character;
    }

    public void setCharacter(String character) {
        this.character = character;
    }

    public void setCredit_id(String credit_id) {
        this.credit_id = credit_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public String getProfile_path() {
        return profile_path;
    }

    public void setProfile_path(String profile_path) {
        this.profile_path = profile_path;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(character);
        parcel.writeString(credit_id);
        parcel.writeString(name);
        parcel.writeString(order);
        parcel.writeString(profile_path);
        parcel.writeString(id);
    }
}
