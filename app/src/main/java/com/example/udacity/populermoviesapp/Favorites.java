package com.example.udacity.populermoviesapp;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

/*
* String _ID = "_id";
* String COLUMN_MOVIE_TITLE = "movie_title";
* String COLUMN_IMAGE = "image";
* String COLUMN_RELEASE_DATE = "release_date";
* String COLUMN_RATING = "rating";
* String COLUMN_SYNOPSIS = "synopsis";
*/
public class Favorites implements Parcelable{
    public static final Parcelable.Creator<Favorites> CREATOR = new Parcelable.Creator<Favorites>(){
        public Favorites createFromParcel(Parcel in){ return new Favorites(in); }
        public Favorites[] newArray(int size){ return new Favorites[size]; }
    };
    private String id;
    private String title;
    private Bitmap image;
    private String releaseDate;
    private String rating;
    private String synopsis;

    public Favorites(String id, String title, Bitmap image, String releaseDate, String rating, String synopsis) {
        this.id = id;
        this.title = title;
        this.image = image;
        this.releaseDate = releaseDate;
        this.rating = rating;
        this.synopsis = synopsis;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Bitmap getImage() {
        return image;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getRating() {
        return rating;
    }

    public String getSynopsis() {
        return synopsis;
    }

    private Favorites(Parcel in){
        this.id = in.readString();
        this.title = in.readString();
        this.image = in.readParcelable(Bitmap.class.getClassLoader());
        this.releaseDate = in.readString();
        this.rating = in.readString();
        this.synopsis = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.id);
        parcel.writeString(this.title);
        parcel.writeParcelable(this.image, 0);
        parcel.writeString(this.releaseDate);
        parcel.writeString(this.rating);
        parcel.writeString(this.synopsis);
    }
}
