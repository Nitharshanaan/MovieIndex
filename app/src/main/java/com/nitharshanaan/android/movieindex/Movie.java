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

    public int id;
    public String title;
    public String language;
    public String thumbnailUrl;
    public String rating;
    public String overview;
    public String releaseDate;
    public String backdrop;

    public Movie(int id, String title, String language, String thumbnailUrl, String rating, String overview, String releaseDate, String backdrop) {
        this.id = id;
        this.title = title;
        this.language = language;
        this.thumbnailUrl = thumbnailUrl;
        this.rating = rating;
        this.overview = overview;
        this.releaseDate = releaseDate;
        this.backdrop = backdrop;
    }

    protected Movie(Parcel in) {
        id = in.readInt();
        title = in.readString();
        language = in.readString();
        thumbnailUrl = in.readString();
        rating = in.readString();
        overview = in.readString();
        releaseDate = in.readString();
        backdrop = in.readString();
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
        parcel.writeInt(id);
        parcel.writeString(title);
        parcel.writeString(language);
        parcel.writeString(thumbnailUrl);
        parcel.writeString(rating);
        parcel.writeString(overview);
        parcel.writeString(releaseDate);
        parcel.writeString(backdrop);
    }
}
