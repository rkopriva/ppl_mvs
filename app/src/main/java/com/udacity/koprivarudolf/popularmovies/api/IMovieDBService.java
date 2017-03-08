package com.udacity.koprivarudolf.popularmovies.api;

import com.udacity.koprivarudolf.popularmovies.models.MoviesQueryResultModel;
import com.udacity.koprivarudolf.popularmovies.models.ReviewQueryResultModel;
import com.udacity.koprivarudolf.popularmovies.models.VideoQueryResultModel;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by rudolfkopriva on 21.01.17.
 */

public interface IMovieDBService {
    @GET("3/movie/popular")
    Call<MoviesQueryResultModel> getPopularMovies(@Query("page") int page);

    @GET("3/movie/top_rated")
    Call<MoviesQueryResultModel> getTopRatedMovies(@Query("page") int page);

    @GET("3/movie/{movie_id}/videos")
    Call<VideoQueryResultModel> getMovieVideos(@Path("movie_id") int movie_id);

    @GET("3/movie/{movie_id}/reviews")
    Call<ReviewQueryResultModel> getReviews(@Path("movie_id") int movie_id);
}