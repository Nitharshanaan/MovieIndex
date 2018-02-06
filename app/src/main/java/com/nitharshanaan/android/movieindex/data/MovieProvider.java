package com.nitharshanaan.android.movieindex.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import static com.nitharshanaan.android.movieindex.data.MovieContract.CONTENT_AUTHORITY;
import static com.nitharshanaan.android.movieindex.data.MovieContract.MovieEntry.TABLE_NAME;
import static com.nitharshanaan.android.movieindex.data.MovieContract.PATH_MOVIES;

/**
 * Created by nitha on 1/9/2018.
 */

public class MovieProvider extends ContentProvider {

    private static final String TAG = MovieDbHelper.class.getSimpleName();
    private static final int MOVIES = 1;                // For whole table
    private static final int MOVIES_ID = 2;            // For a specific row in a table identified by MOVIE_ID
    private static final int MOVIES_NAME = 3;// For a specific row in a table identified by MOVIE NAME
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMatcher.addURI(CONTENT_AUTHORITY, PATH_MOVIES, MOVIES);
        uriMatcher.addURI(CONTENT_AUTHORITY, PATH_MOVIES + "/#", MOVIES_ID);
        uriMatcher.addURI(CONTENT_AUTHORITY, PATH_MOVIES + "/*", MOVIES_NAME);
    }

    private MovieDbHelper dbHelper;

    @Override
    public boolean onCreate() {
        dbHelper = new MovieDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor;

        switch (uriMatcher.match(uri)) {

            case MOVIES:
                cursor = database.query(TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;

            case MOVIES_ID:
                selection = MovieContract.MovieEntry._ID + " = ?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;

            default:
                throw new IllegalArgumentException(TAG + "Unknown URI: " + uri);
        }

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        switch (uriMatcher.match(uri)) {

            case MOVIES:
                return insertRecord(uri, values, TABLE_NAME);
            default:
                throw new IllegalArgumentException(TAG + "Unknown URI: " + uri);
        }
    }

    private Uri insertRecord(Uri uri, ContentValues values, String tableName) {

        SQLiteDatabase database = dbHelper.getWritableDatabase();
        long rowId = database.insert(tableName, null, values);

        if (rowId == -1) {
            Log.e(TAG, "Insert error for URI " + uri);
            return null;
        }

        return ContentUris.withAppendedId(uri, rowId);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {

        return deleteRecord(selection, selectionArgs, MovieContract.MovieEntry.TABLE_NAME);

    }

    private int deleteRecord(String selection, String[] selectionArgs, String tableName) {

        SQLiteDatabase database = dbHelper.getWritableDatabase();
        int rowsDeleted = database.delete(tableName, selection, selectionArgs);
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
