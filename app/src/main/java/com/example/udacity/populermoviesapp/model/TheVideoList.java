package com.example.udacity.populermoviesapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class TheVideoList implements Parcelable {
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator(){
        public TheVideoList createFromParcel(Parcel in){
            return new TheVideoList(in);
        }

        public TheVideoList[] newArray(int size){
            return new TheVideoList[size];
        }
    };

    private int id;
    private ArrayList<TheVideo> results;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ArrayList<TheVideo> getResults() {
        return results;
    }

    public void setResults(ArrayList<TheVideo> results) {
        this.results = results;
    }

    private TheVideoList(Parcel in){
        this.id = in.readInt();
        results = new ArrayList<>();
        in.readTypedList(results, TheVideo.CREATOR);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.id);
        parcel.writeTypedList(results);
    }
}
