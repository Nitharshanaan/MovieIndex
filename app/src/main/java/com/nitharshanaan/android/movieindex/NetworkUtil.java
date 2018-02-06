package com.nitharshanaan.android.movieindex;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by nitha on 11/28/2017.
 */

public class NetworkUtil {

    public static final String QUERY_KEY = "api_key";
    public final static String BASE_URL = "https://api.themoviedb.org/3/movie/";

    public NetworkUtil() {
    }

    public static URL buildUrl(String cond, String api) {

        Uri uri = Uri.parse(BASE_URL).buildUpon()
                .appendPath(cond)
                .appendQueryParameter(QUERY_KEY, api)
                .build();

        URL url = null;

        try {
            url = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    public static URL buildFavUrl(int id, String api) {

        Uri uri = Uri.parse(BASE_URL).buildUpon()
                .appendPath(String.valueOf(id))
                .appendQueryParameter(QUERY_KEY, api)
                .build();

        URL url = null;

        try {
            url = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    public static String getJson(URL url) throws IOException {

        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();

        try {
            InputStream stream = connection.getInputStream();
            Scanner scanner = new Scanner(stream);
            scanner.useDelimiter("\\A");
            boolean hasData = scanner.hasNext();

            if (hasData) {
                return scanner.next();
            } else {
                return null;
            }
        } catch (Exception e) {
            Log.d("Error", e.toString());
            return null;
        } finally {
            connection.disconnect();
        }

    }

    public static ArrayList<Movie> parseJSON(String json) {
        final String ID = "id";
        final String TITLE = "title";
        final String LANGUAGE = "original_language";
        final String RATING = "vote_average";
        final String THUMBNAIL = "poster_path";
        final String RESULTS = "results";
        final String OVERVIEW = "overview";
        final String RELEASE_DATE = "release_date";
        final String BACKDROP = "backdrop_path";
        final String BASE_IMAGE_URL = "https://image.tmdb.org/t/p/w500";
        ArrayList<Movie> movies = new ArrayList<>();

        try {
            JSONObject jsonMovieMasterObject = new JSONObject(json);
            JSONArray jsonMasterArray = jsonMovieMasterObject.getJSONArray(RESULTS);
            int numberOfMoviesFetched = jsonMasterArray.length();

            for (int i = 0; i < numberOfMoviesFetched; i++) {
                JSONObject movieJSON = jsonMasterArray.getJSONObject(i);
                Movie movie = new Movie(
                        movieJSON.getInt(ID),
                        movieJSON.getString(TITLE),
                        movieJSON.getString(LANGUAGE),
                        BASE_IMAGE_URL + movieJSON.getString(THUMBNAIL),
                        String.valueOf(movieJSON.getInt(RATING)),
                        movieJSON.getString(OVERVIEW),
                        movieJSON.getString(RELEASE_DATE),
                        BASE_IMAGE_URL + movieJSON.getString(BACKDROP)
                );
                movies.add(movie);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return movies;
    }

    public static Movie parseFavJSON(String json) {
        final String ID = "id";
        final String TITLE = "title";
        final String LANGUAGE = "original_language";
        final String RATING = "vote_average";
        final String THUMBNAIL = "poster_path";
        final String OVERVIEW = "overview";
        final String RELEASE_DATE = "release_date";
        final String BACKDROP = "backdrop_path";
        final String BASE_IMAGE_URL = "https://image.tmdb.org/t/p/w500";
        ArrayList<Movie> moviesFav = new ArrayList<>();

        try {
            JSONObject movieJSON = new JSONObject(json);

            Movie movieFavs = new Movie(
                    movieJSON.getInt(ID),
                    movieJSON.getString(TITLE),
                    movieJSON.getString(LANGUAGE),
                    BASE_IMAGE_URL + movieJSON.getString(THUMBNAIL),
                    String.valueOf(movieJSON.getInt(RATING)),
                    movieJSON.getString(OVERVIEW),
                    movieJSON.getString(RELEASE_DATE),
                    BASE_IMAGE_URL + movieJSON.getString(BACKDROP)
            );

            return movieFavs;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

    }
}
