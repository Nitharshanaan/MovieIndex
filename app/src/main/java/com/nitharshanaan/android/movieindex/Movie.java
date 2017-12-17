package com.nitharshanaan.android.movieindex;

import android.databinding.BindingAdapter;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

/**
 * Created by nitha on 11/28/2017.
 */

public class Movie implements Parcelable {

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
    public String TITLE;
    public String LANGUAGE;
    public String THUMBNAIL_URL;
    public String RATING;
    public String OVERVIEW;
    public String RELEASE_DATE;
    public String BACKDROP;

    public Movie(String TITLE, String LANGUAGE, String THUMBNAIL_URL, String RATING, String OVERVIEW, String RELEASE_DATE, String BACKDROP) {
        this.TITLE = TITLE;
        this.LANGUAGE = LANGUAGE;
        this.THUMBNAIL_URL = THUMBNAIL_URL;
        this.RATING = RATING;
        this.OVERVIEW = OVERVIEW;
        this.RELEASE_DATE = RELEASE_DATE;
        this.BACKDROP = BACKDROP;
    }

    protected Movie(Parcel in) {
        TITLE = in.readString();
        LANGUAGE = in.readString();
        THUMBNAIL_URL = in.readString();
        RATING = in.readString();
        OVERVIEW = in.readString();
        RELEASE_DATE = in.readString();
        BACKDROP = in.readString();
    }

    @BindingAdapter({"android:imageUrl"})
    public static void loadImage(ImageView view, String imageUrl) {
        if (!imageUrl.isEmpty()) {
            Glide.with(view.getContext())
                    .load(imageUrl)
                    .placeholder(R.drawable.image_off)
                    .into(view);
        } else {
            view.setBackgroundResource(R.drawable.image_off);
        }

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(TITLE);
        parcel.writeString(LANGUAGE);
        parcel.writeString(THUMBNAIL_URL);
        parcel.writeString(RATING);
        parcel.writeString(OVERVIEW);
        parcel.writeString(RELEASE_DATE);
        parcel.writeString(BACKDROP);
    }
}
