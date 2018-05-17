package com.example.udacity.populermoviesapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class TheReviewList implements Parcelable {
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator(){
        public TheReviewList createFromParcel(Parcel in){
            return new TheReviewList(in);
        }

        public TheReviewList[] newArray(int size){
            return new TheReviewList[size];
        }
    };

    private int id;
    private int page;
    private ArrayList<TheReview> results;
    private int totalPages;
    private  int totalResults;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public ArrayList<TheReview> getResults() {
        return results;
    }

    public void setResults(ArrayList<TheReview> results) {
        this.results = results;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public int getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(int totalResults) {
        this.totalResults = totalResults;
    }

    private TheReviewList(Parcel in){
        this.id = in.readInt();
        this.page = in.readInt();
        results = new ArrayList<>();
        in.readTypedList(results, TheReview.CREATOR);
        this.totalPages = in.readInt();
        this.totalResults = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.id);
        parcel.writeInt(this.page);
        parcel.writeTypedList(results);
        parcel.writeInt(this.totalPages);
        parcel.writeInt(this.totalResults);
    }
}
