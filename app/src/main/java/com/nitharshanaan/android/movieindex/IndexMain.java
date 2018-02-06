package com.nitharshanaan.android.movieindex;


import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nitharshanaan.android.movieindex.data.MovieContract;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class IndexMain extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String> {

    public static final String API_KEY = BuildConfig.MOVIE_DB_API_KEY;
    private static final String TAG = IndexMain.class.getSimpleName();
    private static final String MOVIES_URL_EXTRA = "query";
    private static final int MOVIE_ASYNC_LOADER = 30;
    private static final int CURSOR_LOADER_ID = 31;
    static String BUNDLE_RECYCLER_LAYOUT = "recycler_layout";
    static String BUNDLE_RECYCLER_MOVIEDATA = "recycler_moviedata";
    ArrayList<Movie> movieDBMasterList = new ArrayList<Movie>();
    Exception ex = null;
    Exception ex1 = null;
    private RecyclerView mRecyclerView;
    private TextView mErrorMessageDisplay;
    private ImageView mErrorImageDisplay;
    private String loadParam;
    private String currentCondition;
    private SwipeRefreshLayout resultSwipeRefreshLayout;

    //CursorLoader for loading data from database
    private LoaderManager.LoaderCallbacks<Cursor> dataResultLoaderListener
            = new LoaderManager.LoaderCallbacks<Cursor>() {
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {

            return new AsyncTaskLoader<Cursor>(getApplicationContext()) {

                @Override
                public Cursor loadInBackground() {
                    Cursor cursor;

                    movieDBMasterList = new ArrayList<Movie>();

                    try {
                        String[] projection = {
                                //MovieContract.MovieEntry._ID,
                                MovieContract.MovieEntry.COLUMN_MOVIEID,
                                //MovieContract.MovieEntry.COLUMN_TITLE
                        };

                        String selection = null;
                        String[] selectionArgs = null;
                        String sortOrder = null;
                        Uri uri = MovieContract.MovieEntry.CONTENT_URI;
                        cursor = getContentResolver().query(uri, projection, selection, selectionArgs, sortOrder);

                        int movieDbIdIndex = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIEID);


                        if (cursor != null) {

                            int i = 0;
                            while (cursor.moveToNext()) {    // Cursor iterates through all rows
                                i++;
                            }
                            Log.i(TAG, "countval" + String.valueOf(i));
                            cursor.moveToFirst();
                            while (cursor.moveToNext()) {    // Cursor iterates through all rows
                                int id = cursor.getInt(movieDbIdIndex);
                                URL builtURL = NetworkUtil.buildFavUrl(id, API_KEY);
                                String result = null;
                                result = NetworkUtil.getJson(builtURL);
                                Movie movies = NetworkUtil.parseFavJSON(result);
                                movieDBMasterList.add(movies);

                            }

                        }
                        return cursor;

                    } catch (Exception e) {
                        ex1 = e;
                        e.printStackTrace();
                        return null;
                    }


                }

                @Override
                protected void onStartLoading() {

                    mErrorMessageDisplay.setVisibility(View.INVISIBLE);
                    mRecyclerView.setVisibility(View.INVISIBLE);

                }
            };
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            resultSwipeRefreshLayout.setRefreshing(false);

            if (data != null && data.getCount() > 0) {
                showJsonDataView();
                MoviesAdapter adapter = new MoviesAdapter(IndexMain.this, movieDBMasterList);
                mRecyclerView.setAdapter(adapter);
            } else if (ex1 != null) {
                Toast.makeText(IndexMain.this, getResources().getString(R.string.sql_failed) + ex1.getMessage(), Toast.LENGTH_LONG).show();
                showErrorMessage();
            } else {
                Toast.makeText(IndexMain.this, "You have not chosen any Favourite movies yet! ", Toast.LENGTH_LONG).show();
            }
        }


        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_index_main);

        resultSwipeRefreshLayout = findViewById(R.id.swiperefresh);

        mRecyclerView = findViewById(R.id.recycler_view);

        if (getApplication().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
            mRecyclerView.setLayoutManager(mLayoutManager);
        } else {
            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 4);
            mRecyclerView.setLayoutManager(mLayoutManager);
        }

        mRecyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mErrorMessageDisplay = findViewById(R.id.tv_error_message_display);
        mErrorImageDisplay = findViewById(R.id.ivOffline);

        /*
        * Sets up a SwipeRefreshLayout.OnRefreshListener that is invoked when the user
        * performs a swipe-to-refresh gesture.
        */

        resultSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {


            }
        });


        if (savedInstanceState != null) {
            Parcelable savedRecyclerViewState = savedInstanceState.getParcelable(BUNDLE_RECYCLER_LAYOUT);
            mRecyclerView.getLayoutManager().onRestoreInstanceState(savedRecyclerViewState);
            movieDBMasterList = savedInstanceState.getParcelableArrayList(BUNDLE_RECYCLER_MOVIEDATA);
            MoviesAdapter adapter = new MoviesAdapter(IndexMain.this, movieDBMasterList);
            mRecyclerView.setAdapter(adapter);
        } else {

            Bundle queryBundle = new Bundle();
            queryBundle.putString(MOVIES_URL_EXTRA, getResources().getString(R.string.popularity));
            getSupportLoaderManager().restartLoader(MOVIE_ASYNC_LOADER, queryBundle, this);

        }

    }

    private void loadMovie(String condGet) {

        Bundle queryBundle = new Bundle();
        queryBundle.putString(MOVIES_URL_EXTRA, condGet);


        android.support.v4.app.LoaderManager loaderManager = getSupportLoaderManager();

        android.support.v4.content.Loader<String> movieIndexLoader = loaderManager.getLoader(MOVIE_ASYNC_LOADER);


        if (movieIndexLoader == null) {
            Log.d(TAG, "Life initialize loader ");
            loaderManager.initLoader(MOVIE_ASYNC_LOADER, queryBundle, this);
        } else {
            Log.d(TAG, "Life restart loader ");
            loaderManager.restartLoader(MOVIE_ASYNC_LOADER, queryBundle, this);
        }
    }

    // Converting dp to pixel
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    // Inflate Menu item in Main screen.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sort_menu, menu);
        return true;
    }

    // Menu item selected.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_highest_rated:
                if (item.isChecked()) item.setChecked(false);
                else item.setChecked(true);
                loadMovie(getString(R.string.average_rated));
                return true;
            case R.id.menu_most_popular:
                if (item.isChecked()) item.setChecked(false);
                else item.setChecked(true);
                loadMovie(getString(R.string.popularity));
                return true;
            case R.id.menu_favorites:
                if (item.isChecked()) item.setChecked(false);
                else item.setChecked(true);
                android.support.v4.app.LoaderManager loaderManager = getSupportLoaderManager();
                loaderManager.restartLoader(CURSOR_LOADER_ID, null, dataResultLoaderListener).forceLoad();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showErrorMessage() {
        // hide the currently visible data
        mRecyclerView.setVisibility(View.INVISIBLE);
        // Then, show the error
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
        mErrorImageDisplay.setVisibility(View.VISIBLE);
    }

    private void showJsonDataView() {
        // First, make sure the error is invisible
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        mErrorImageDisplay.setVisibility(View.INVISIBLE);
        // Then, make sure the JSON data is visible
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    // AsyncTaskLoader for network operations such (Most popular and Top rated)
    @Override
    public Loader<String> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<String>(this) {

            @Override
            protected void onStartLoading() {
                if (args == null) {
                    return;
                }

                mErrorMessageDisplay.setVisibility(View.INVISIBLE);
                mRecyclerView.setVisibility(View.INVISIBLE);

                forceLoad();

            }

            @Override
            public String loadInBackground() {
                String moviesQueryUrlString = args.getString(MOVIES_URL_EXTRA);

                try {
                    URL builtURL = NetworkUtil.buildUrl(moviesQueryUrlString, API_KEY);
                    String result = null;
                    return result = NetworkUtil.getJson(builtURL);
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        };
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<String> loader, String data) {
        resultSwipeRefreshLayout.setRefreshing(false);

        if (null == data) {
            showErrorMessage();
        } else {
            showJsonDataView();
            movieDBMasterList = NetworkUtil.parseJSON(data);

            MoviesAdapter adapter = new MoviesAdapter(IndexMain.this, movieDBMasterList);
            mRecyclerView.setAdapter(adapter);
        }
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<String> loader) {
    }
    // End of Network AsyncTaskLoader

    // RecyclerView item decoration - give equal margin around grid item

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(BUNDLE_RECYCLER_LAYOUT, mRecyclerView.getLayoutManager().onSaveInstanceState());
        outState.putParcelableArrayList(BUNDLE_RECYCLER_MOVIEDATA, movieDBMasterList);
    }

    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

}