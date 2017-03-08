package com.udacity.koprivarudolf.popularmovies.models;

import java.util.List;

/**
 * Created by rudolfkopriva on 04.03.17.
 */

public class VideoQueryResultModel {

    private List<VideoListResultModel> results;

    public VideoQueryResultModel(List<VideoListResultModel> results) {
        this.results = results;
    }

    public List<VideoListResultModel> getResults() {
        return results;
    }

    public void setResults(List<VideoListResultModel> results) {
        this.results = results;
    }
}
