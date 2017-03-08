package com.udacity.koprivarudolf.popularmovies.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by rudolfkopriva on 04.03.17.
 */

public class ReviewListResultModel implements Parcelable {

    private String author;
    private String content;
    private String url;

    public ReviewListResultModel() {
    }

    private ReviewListResultModel(Parcel in){
        author = in.readString();
        content = in.readString();
        url = in.readString();
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(author);
        parcel.writeString(content);
        parcel.writeString(url);
    }

    public static final Parcelable.Creator<ReviewListResultModel> CREATOR = new Parcelable.Creator<ReviewListResultModel>() {
        @Override
        public ReviewListResultModel createFromParcel(Parcel parcel) {
            return new ReviewListResultModel(parcel);
        }

        @Override
        public ReviewListResultModel[] newArray(int i) {
            return new ReviewListResultModel[i];
        }

    };
}
