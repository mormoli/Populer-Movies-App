package com.example.udacity.populermoviesapp.utils;


import com.example.udacity.populermoviesapp.R;
import com.example.udacity.populermoviesapp.model.TheMovieList;
import com.example.udacity.populermoviesapp.model.TheReviewList;
import com.example.udacity.populermoviesapp.model.TheVideoList;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface TheMovieInterface {
    @GET ("movie/popular")
    Call<TheMovieList> getPopularMovies(@Query("api_key") String apiKey);

    @GET ("movie/top_rated")
    Call<TheMovieList> getTopMovies(@Query("api_key") String apiKey);

    @GET ("movie/{id}/videos")
    Call<TheVideoList> getVideos(@Path("id") int movieId, @Query("api_key") String apiKey);

    @GET ("movie/{id}/reviews")
    Call<TheReviewList> getReviews(@Path("id") int movieId, @Query("api_key") String apiKey);
}
