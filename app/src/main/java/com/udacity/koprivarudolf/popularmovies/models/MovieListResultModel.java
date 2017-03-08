package com.udacity.koprivarudolf.popularmovies.models;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.udacity.koprivarudolf.popularmovies.Constants;
import com.udacity.koprivarudolf.popularmovies.store.FavoriteColumns;

/**
 * Created by rudolfkopriva on 21.01.17.
 */
public class MovieListResultModel implements Parcelable {
    private int id;
    private String title;
    private String original_title;
    private String poster_path;
    private double vote_average;
    private String release_date;
    private String overview;

    public MovieListResultModel() {
    }

    public static MovieListResultModel fromCursor(Cursor cursor) {
        MovieListResultModel model = new MovieListResultModel();
        model.id = cursor.getInt(cursor.getColumnIndex(FavoriteColumns.MOVIE_ID));
        model.title = cursor.getString(cursor.getColumnIndex(FavoriteColumns.MOVIE_TITLE));
        model.original_title = cursor.getString(cursor.getColumnIndex(FavoriteColumns.ORIGINAL_TITLE));
        model.poster_path = cursor.getString(cursor.getColumnIndex(FavoriteColumns.POSTER_PATH));
        model.vote_average = cursor.getDouble(cursor.getColumnIndex(FavoriteColumns.VOTE_AVERAGE));
        model.release_date = cursor.getString(cursor.getColumnIndex(FavoriteColumns.RELEASE_DATE));
        model.overview = cursor.getString(cursor.getColumnIndex(FavoriteColumns.OVERVIEW));
        return model;
    }

    public ContentValues createContentValues() {
        ContentValues cv = new ContentValues();
        cv.put(FavoriteColumns.MOVIE_ID, id);
        cv.put(FavoriteColumns.MOVIE_TITLE, title);
        cv.put(FavoriteColumns.ORIGINAL_TITLE, original_title);
        cv.put(FavoriteColumns.OVERVIEW, overview);
        cv.put(FavoriteColumns.POSTER_PATH, poster_path);
        cv.put(FavoriteColumns.RELEASE_DATE, release_date);
        cv.put(FavoriteColumns.VOTE_AVERAGE, vote_average);
        return cv;
    }

    private MovieListResultModel(Parcel in){
        id = in.readInt();
        title = in.readString();
        original_title = in.readString();
        poster_path = in.readString();
        vote_average = in.readDouble();
        release_date = in.readString();
        overview = in.readString();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
        parcel.writeInt(id);
        parcel.writeString(title);
        parcel.writeString(original_title);
        parcel.writeString(poster_path);
        parcel.writeDouble(vote_average);
        parcel.writeString(release_date);
        parcel.writeString(overview);
    }

    public static final Parcelable.Creator<MovieListResultModel> CREATOR = new Parcelable.Creator<MovieListResultModel>() {
        @Override
        public MovieListResultModel createFromParcel(Parcel parcel) {
            return new MovieListResultModel(parcel);
        }

        @Override
        public MovieListResultModel[] newArray(int i) {
            return new MovieListResultModel[i];
        }
    };
}
