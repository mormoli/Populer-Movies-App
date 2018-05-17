package com.example.udacity.populermoviesapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/*
* sample data {https://api.themoviedb.org/3/movie/{movie_id}/videos?api_key=}
* {
*  "id": 550,
*  "results": [
*    {
*      "id": "533ec654c3a36854480003eb",
*      "iso_639_1": "en",
*      "iso_3166_1": "US",
*      "key": "SUXWAEX2jlg",
*      "name": "Trailer 1",
*      "site": "YouTube",
*      "size": 720,
*      "type": "Trailer"
*    }
*  ]
*}
* */
public class TheVideo implements Parcelable{
    public static final Parcelable.Creator<TheVideo> CREATOR = new Parcelable.Creator<TheVideo>(){
        public TheVideo createFromParcel(Parcel in){
            return new TheVideo(in);
        }

        public TheVideo[] newArray(int size){
            return new TheVideo[size];
        }
    };
    @SerializedName("id")
    private String id;
    @SerializedName("iso_639_1")
    private String iso6391;
    @SerializedName("iso_3166_1")
    private String iso31661;
    @SerializedName("key")
    private String key;
    @SerializedName("name")
    private String name;
    @SerializedName("site")
    private String site;
    @SerializedName("size")
    private int size; // Allowed Values: 360, 480, 720, 1080
    @SerializedName("type")
    private String type; // Allowed Values: Trailer, Teaser, Clip, Featurette

    TheVideo(){}//default constructor

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIso6391() {
        return iso6391;
    }

    public void setIso6391(String iso6391) {
        this.iso6391 = iso6391;
    }

    public String getIso31661() {
        return iso31661;
    }

    public void setIso31661(String iso31661) {
        this.iso31661 = iso31661;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    private TheVideo(Parcel in){
        this.id = in.readString();
        this.iso6391 = in.readString();
        this.iso31661 = in.readString();
        this.key = in.readString();
        this.name = in.readString();
        this.site = in.readString();
        this.size = in.readInt();
        this.type = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.id);
        parcel.writeString(this.iso6391);
        parcel.writeString(this.iso31661);
        parcel.writeString(this.key);
        parcel.writeString(this.name);
        parcel.writeString(this.site);
        parcel.writeInt(this.size);
        parcel.writeString(this.type);
    }

    @Override
    public String toString() {
        return "TheVideo{" +
                "id='" + id + '\'' +
                ", iso6391='" + iso6391 + '\'' +
                ", iso31661='" + iso31661 + '\'' +
                ", key='" + key + '\'' +
                ", name='" + name + '\'' +
                ", site='" + site + '\'' +
                ", size=" + size +
                ", type='" + type + '\'' +
                '}';
    }
}
