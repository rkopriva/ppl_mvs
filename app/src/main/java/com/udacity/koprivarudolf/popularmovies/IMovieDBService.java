package com.udacity.koprivarudolf.popularmovies;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by rudolfkopriva on 21.01.17.
 */

public interface IMovieDBService {
    @GET("3/movie/popular")
    Call<MoviesQueryResultObject> getPopularMovies(@Query("page") int page);

    @GET("3/movie/top_rated")
    Call<MoviesQueryResultObject> getTopRatedMovies(@Query("page") int page);
}