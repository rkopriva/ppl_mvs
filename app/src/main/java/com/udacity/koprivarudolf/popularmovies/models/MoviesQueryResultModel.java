package com.udacity.koprivarudolf.popularmovies.models;

import java.util.List;

/**
 * Created by rudolfkopriva on 21.01.17.
 *
 * Query result object
 */

public class MoviesQueryResultModel {
    private List<MovieListResultModel> results;

    public MoviesQueryResultModel(List<MovieListResultModel> results) {
        this.results = results;
    }

    public List<MovieListResultModel> getResults() {
        return results;
    }

    public void setResults(List<MovieListResultModel> results) {
        this.results = results;
    }
}
