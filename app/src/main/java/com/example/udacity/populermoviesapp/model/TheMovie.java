package com.example.udacity.populermoviesapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/*
* sample data {https://api.themoviedb.org/3/movie/top_rated?api_key=}
* results [{
*	poster_path       : string or null
*	adult             : boolean
*	overview          : string
*	release_date      : string
*	genre_ids         : array[integer]
*	id	          : integer
*	original_title    : string
*	original_language : string
*	title		  : string
*	backdrop_path	  : string or null
*	popularity	  : number float
*	vote_count	  : integer
*	video		  : boolean
*	vote_average	  : number float
* },
*/
/*
*    original title
*    movie poster image thumbnail
*    A plot synopsis (called overview in the api)
*    user rating (called vote_average in the api)
*    release date
******/
public class TheMovie implements Parcelable {
    //while android studio recommends below method can be package-private and program works with android default emulator
    //on nox 6.0.6.1 emulator gives BadParcelableException if you remove public declaration from method!
    public static final Parcelable.Creator<TheMovie> CREATOR = new Parcelable.Creator<TheMovie>(){
        public TheMovie createFromParcel(Parcel in){
            return new TheMovie(in);
        }

        public TheMovie[] newArray(int size){
            return new TheMovie[size];
        }
    };
    private String imagePath = "https://image.tmdb.org/t/p/w185";
    @SerializedName("poster_path")
    private String posterPath;
    //private boolean adult;
    @SerializedName("overview")
    private String overview;
    @SerializedName("release_date")
    private String releaseDate;
    //private List<Integer> genreIds;
    //private int id;
    @SerializedName("original_title")
    private String originalTitle;
    //private String originalLanguage;
    //private String title;
    @SerializedName("backdrop_path")
    private String backdropPath;
    //private float popularity;
    //private int voteCount;
    //private boolean video;
    @SerializedName("vote_average")
    private float voteAvarage;

    public TheMovie(){}
    //Constructor
    /*public TheMovie(String posterPath, boolean adult, String overview, String releaseDate, List<Integer> genreIds,
                    int id, String originalTitle, String originalLanguage, String title, String backdropPath,
                    float popularity, int voteCount, boolean video, float voteAvarage){
        this.posterPath = posterPath;
        this.adult = adult;
        this.overview = overview;
        this.releaseDate = releaseDate;
        this.genreIds = genreIds;
        this.id = id;
        this.originalTitle = originalTitle;
        this.originalLanguage = originalLanguage;
        this.title = title;
        this.backdropPath = backdropPath;
        this.popularity = popularity;
        this.voteCount = voteCount;
        this.video = video;
        this.voteAvarage = voteAvarage;
    }*/

    public String getPosterPath() {
        return imagePath + posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    /*public boolean isAdult() {
        return adult;
    }*/

    /*public void setAdult(boolean adult) {
        this.adult = adult;
    }*/

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    /*public List<Integer> getGenreIds() {
        return genreIds;
    }*/

    /*public void setGenreIds(List<Integer> genreIds) {
        this.genreIds = genreIds;
    }*/

    /*public int getId() {
        return id;
    }*/

    /*public void setId(int id) {
        this.id = id;
    }*/

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    /*public String getOriginalLanguage() {
        return originalLanguage;
    }*/

    /*public void setOriginalLanguage(String originalLanguage) {
        this.originalLanguage = originalLanguage;
    }*/

    /*public String getTitle() {
        return title;
    }*/

    /*public void setTitle(String title) {
        this.title = title;
    }*/

    public String getBackdropPath() {
        return imagePath + backdropPath;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }

    /*public float getPopularity() {
        return popularity;
    }*/

    /*public void setPopularity(float popularity) {
        this.popularity = popularity;
    }*/

    /*public int getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(int voteCount) {
        this.voteCount = voteCount;
    }

    public boolean isVideo() {
        return video;
    }

    public void setVideo(boolean video) {
        this.video = video;
    }*/

    public float getVoteAvarage() {
        return voteAvarage;
    }

    public void setVoteAvarage(float voteAvarage) {
        this.voteAvarage = voteAvarage;
    }

    //Parcelling data
    @SuppressWarnings("unchecked")
    private TheMovie(Parcel in){
        //this.adult = in.readByte() != 0;
        this.overview = in.readString();
        this.releaseDate = in.readString();
        this.posterPath = in.readString();
        //unchecked array list to serializable object
        //this.genreIds = new ArrayList<>();
        //in.readList(genreIds, null);
        //this.id = in.readInt();
        this.originalTitle = in.readString();
        //this.originalLanguage = in.readString();
        //this.title = in.readString();
        this.backdropPath = in.readString();
        //this.popularity = in.readFloat();
        //this.voteCount = in.readInt();
        //this.video = in.readByte() != 0;
        this.voteAvarage = in.readFloat();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        //parcel.writeByte((byte) (this.adult ? 1 : 0));
        parcel.writeString(this.overview);
        parcel.writeString(this.releaseDate);
        parcel.writeString(this.posterPath);
        //parcel.writeList(this.genreIds);
        //parcel.writeInt(this.id);
        parcel.writeString(this.originalTitle);
        //parcel.writeString(this.originalLanguage);
        //parcel.writeString(this.title);
        parcel.writeString(this.backdropPath);
        //parcel.writeFloat(this.popularity);
        //parcel.writeInt(this.voteCount);
        //parcel.writeByte((byte) (this.video ? 1 : 0));
        parcel.writeFloat(this.voteAvarage);
    }

    @Override
    public String toString() {
        return "TheMovie{" + '\n' +
                "posterPath='" + posterPath + '\n' +
                ", overview='" + overview + '\n' +
                ", releaseDate='" + releaseDate + '\n' +
                ", originalTitle='" + originalTitle + '\n' +
                ", backdropPath='" + backdropPath + '\n' +
                ", voteAvarage=" + voteAvarage + '\n' +
                '}';
    }
}
