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

    public static final String BASE_URL = "https://api.themoviedb.org/3/discover/movie";
    public static final String QUERY_KEY = "api_key";
    public static final String QUERY_SORT = "sort_by";

    public NetworkUtil() {
    }

    public static URL buildUrl(String condition, String api) {

        Uri uri = Uri.parse(BASE_URL).buildUpon()
                .appendQueryParameter(QUERY_SORT, condition)
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
}
