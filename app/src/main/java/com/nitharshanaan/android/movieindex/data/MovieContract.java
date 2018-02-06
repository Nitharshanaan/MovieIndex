package com.nitharshanaan.android.movieindex.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by nitha on 1/6/2018.
 */

public final class MovieContract {

    public static final String CONTENT_AUTHORITY = "com.nitharshanaan.android.movieindex.data.MovieProvider";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_MOVIES = "movies";

    public static final class MovieEntry implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_MOVIES);

        // Table Name
        public static final String TABLE_NAME = "movies";

        // Columns
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_MOVIEID = "movieid";
        public static final String COLUMN_TITLE = "movietitle";
    }
}
