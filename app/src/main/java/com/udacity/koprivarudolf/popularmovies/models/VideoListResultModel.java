package com.udacity.koprivarudolf.popularmovies.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.udacity.koprivarudolf.popularmovies.Constants;

/**
 * Created by rudolfkopriva on 04.03.17.
 */

public class VideoListResultModel implements Parcelable {

    private String id;
    private String key;
    private String name;
    private String site;
    private int size;
    private String type;

    public VideoListResultModel() {
    }

    private VideoListResultModel(Parcel in){
        id = in.readString();
        key = in.readString();
        name = in.readString();
        site = in.readString();
        size = in.readInt();
        type = in.readString();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(key);
        parcel.writeString(name);
        parcel.writeString(site);
        parcel.writeInt(size);
        parcel.writeString(type);
    }

    public static final Parcelable.Creator<VideoListResultModel> CREATOR = new Parcelable.Creator<VideoListResultModel>() {
        @Override
        public VideoListResultModel createFromParcel(Parcel parcel) {
            return new VideoListResultModel(parcel);
        }

        @Override
        public VideoListResultModel[] newArray(int i) {
            return new VideoListResultModel[i];
        }

    };
}
