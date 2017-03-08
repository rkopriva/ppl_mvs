package com.udacity.koprivarudolf.popularmovies.models;

import java.util.List;

/**
 * Created by rudolfkopriva on 04.03.17.
 */

public class ReviewQueryResultModel {
    private List<ReviewListResultModel> results;

    public ReviewQueryResultModel(List<ReviewListResultModel> results) {
        this.results = results;
    }

    public List<ReviewListResultModel> getResults() {
        return results;
    }

    public void setResults(List<ReviewListResultModel> results) {
        this.results = results;
    }
}
