package com.example.udacity.populermoviesapp.utils;


import com.example.udacity.populermoviesapp.R;
import com.example.udacity.populermoviesapp.model.TheMovieList;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface TheMovieInterface {
    @GET ("movie/popular")
    Call<TheMovieList> getPopularMovies(@Query("api_key") String apiKey);

    @GET ("movie/top_rated")
    Call<TheMovieList> getTopMovies(@Query("api_key") String apiKey);
}
