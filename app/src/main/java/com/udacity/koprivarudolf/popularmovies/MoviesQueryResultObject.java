package com.udacity.koprivarudolf.popularmovies;

import java.util.List;

/**
 * Created by rudolfkopriva on 21.01.17.
 *
 * Query result object
 */

public class MoviesQueryResultObject {
    private List<MovieListResultObject> results;

    public MoviesQueryResultObject(List<MovieListResultObject> results) {
        this.results = results;
    }

    public List<MovieListResultObject> getResults() {
        return results;
    }

    public void setResults(List<MovieListResultObject> results) {
        this.results = results;
    }
}
