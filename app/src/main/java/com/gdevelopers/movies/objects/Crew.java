package com.gdevelopers.movies.objects;

import android.os.Parcel;
import android.os.Parcelable;

public class Crew implements Parcelable {
    private String creditId;
    private String department;
    private String id;
    private String job;
    private String name;
    private String profilePath;

    public Crew() {
    }

    private Crew(Parcel in) {
        creditId = in.readString();
        department = in.readString();
        id = in.readString();
        job = in.readString();
        name = in.readString();
        profilePath = in.readString();
    }

    public static final Creator<Crew> CREATOR = new Creator<Crew>() {
        @Override
        public Crew createFromParcel(Parcel in) {
            return new Crew(in);
        }

        @Override
        public Crew[] newArray(int size) {
            return new Crew[size];
        }
    };

    public void setCreditId(String creditId) {
        this.creditId = creditId;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfilePath() {
        return profilePath;
    }

    public void setProfilePath(String profilePath) {
        this.profilePath = profilePath;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(creditId);
        parcel.writeString(department);
        parcel.writeString(id);
        parcel.writeString(job);
        parcel.writeString(name);
        parcel.writeString(profilePath);
    }
}
