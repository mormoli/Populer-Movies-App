package com.example.udacity.populermoviesapp.model;

import android.os.Parcel;
import android.os.Parcelable;

public class TheReview implements Parcelable {
    public static final Parcelable.Creator<TheReview> CREATOR = new Parcelable.Creator<TheReview>(){
        public TheReview createFromParcel(Parcel in){
            return new TheReview(in);
        }

        public TheReview[] newArray(int size){
            return new TheReview[size];
        }
    };

    private String id;
    private String author;
    private String content;
    private String url;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    private TheReview(Parcel in){
        this.id = in.readString();
        this.author = in.readString();
        this.content = in.readString();
        this.url = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.id);
        parcel.writeString(this.author);
        parcel.writeString(this.content);
        parcel.writeString(this.url);
    }

    @Override
    public String toString() {
        return "TheReview{" +
                "id='" + id + '\'' +
                ", author='" + author + '\'' +
                ", content='" + content + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
