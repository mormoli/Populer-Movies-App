package com.example.udacity.populermoviesapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class TheMovieList implements Parcelable{
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator(){
        public TheMovieList createFromParcel(Parcel in){
            return new TheMovieList(in);
        }

        public TheMovieList[] newArray(int size){
            return new TheMovieList[size];
        }
    };

    private int page;
    private ArrayList<TheMovie> results;
    private int totalResults;
    private int totalPages;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public ArrayList<TheMovie> getResults() {
        return results;
    }

    public void setResults(ArrayList<TheMovie> results) {
        this.results = results;
    }

    public int getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(int totalResults) {
        this.totalResults = totalResults;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }
    @SuppressWarnings("unchecked")
    private TheMovieList(Parcel in){
        this.page = in.readInt();
        results = new ArrayList<>();
        in.readTypedList(results, TheMovie.CREATOR);
        this.totalResults = in.readInt();
        this.totalPages = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.page);
        parcel.writeTypedList(results);
        parcel.writeInt(totalResults);
        parcel.writeInt(totalPages);
    }
}
