package com.gdevelopers.movies.objects;

import android.os.Parcel;
import android.os.Parcelable;


public class Show implements Parcelable{
    private String character;
    private String title;
    private String posterPath;
    private String releaseDate;
    private String mediaType;
    private String department;
    private String job;
    private int id;

    public Show() {
    }

    private Show(Parcel in) {
        character = in.readString();
        title = in.readString();
        posterPath = in.readString();
        releaseDate = in.readString();
        mediaType = in.readString();
        department = in.readString();
        job = in.readString();
        id = in.readInt();
    }

    public static final Creator<Show> CREATOR = new Creator<Show>() {
        @Override
        public Show createFromParcel(Parcel in) {
            return new Show(in);
        }

        @Override
        public Show[] newArray(int size) {
            return new Show[size];
        }
    };

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCharacter() {
        return character;
    }

    public void setCharacter(String character) {
        this.character = character;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(character);
        parcel.writeString(title);
        parcel.writeString(posterPath);
        parcel.writeString(releaseDate);
        parcel.writeString(mediaType);
        parcel.writeString(department);
        parcel.writeString(job);
        parcel.writeInt(id);
    }
}
