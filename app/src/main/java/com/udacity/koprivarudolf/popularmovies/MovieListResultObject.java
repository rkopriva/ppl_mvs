package com.udacity.koprivarudolf.popularmovies;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.net.URI;
import java.net.URL;

/**
 * Created by rudolfkopriva on 21.01.17.
 */
public class MovieListResultObject implements Parcelable {
    private String title;
    private String original_title;
    private String poster_path;
    private double vote_average;
    private String release_date;
    private String overview;

    public MovieListResultObject() {
    }

    private MovieListResultObject(Parcel in){
        title = in.readString();
        original_title = in.readString();
        poster_path = in.readString();
        vote_average = in.readDouble();
        release_date = in.readString();
        overview = in.readString();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isTitleSameAsOriginalTitle() {
        if (title == null) {
            return original_title == null;
        }
        else {
            return title.equals(original_title);
        }
    }

    public String getOriginal_title() {
        return original_title;
    }

    public void setOriginal_title(String original_title) {
        this.original_title = original_title;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public void setPoster_path(String poster_path) {
        this.poster_path = poster_path;
    }

    public double getVote_average() {
        return vote_average;
    }

    public void setVote_average(double vote_average) {
        this.vote_average = vote_average;
    }

    public String getRelease_date() {
        return release_date;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getPosterImageURL() {
        return Constants.HTTP_IMAGE_TMDB_ORG_T_P_W185 + poster_path;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeString(original_title);
        parcel.writeString(poster_path);
        parcel.writeDouble(vote_average);
        parcel.writeString(release_date);
        parcel.writeString(overview);
    }

    public static final Parcelable.Creator<MovieListResultObject> CREATOR = new Parcelable.Creator<MovieListResultObject>() {
        @Override
        public MovieListResultObject createFromParcel(Parcel parcel) {
            return new MovieListResultObject(parcel);
        }

        @Override
        public MovieListResultObject[] newArray(int i) {
            return new MovieListResultObject[i];
        }

    };
}
