package com.gdevelopers.movies.objects;

import android.os.Parcel;
import android.os.Parcelable;


public class Season implements Parcelable {
    private String airDate;
    private String episodeCount;
    private String posterPath;
    private String number;
    private long id;

    public Season() {
    }

    private Season(Parcel in) {
        airDate = in.readString();
        episodeCount = in.readString();
        posterPath = in.readString();
        number = in.readString();
        id = in.readLong();
    }

    public static final Creator<Season> CREATOR = new Creator<Season>() {
        @Override
        public Season createFromParcel(Parcel in) {
            return new Season(in);
        }

        @Override
        public Season[] newArray(int size) {
            return new Season[size];
        }
    };

    public void setId(long id) {
        this.id = id;
    }

    public String getAirDate() {
        return airDate;
    }

    public void setAirDate(String airDate) {
        this.airDate = airDate;
    }

    public String getEpisodeCount() {
        return episodeCount;
    }

    public void setEpisodeCount(String episodeCount) {
        this.episodeCount = episodeCount;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(airDate);
        parcel.writeString(episodeCount);
        parcel.writeString(posterPath);
        parcel.writeString(number);
        parcel.writeLong(id);
    }
}
