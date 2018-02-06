package com.nitharshanaan.android.movieindex;

import android.content.ContentValues;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.nitharshanaan.android.movieindex.data.MovieContract;
import com.nitharshanaan.android.movieindex.databinding.ActivityMovieDetailBinding;


public class MovieDetail extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String> {

    private static final int SQL_LOADER_ID = 25;
    private static final String TAG = MovieDetail.class.getSimpleName();
    FloatingActionButton myFab;
    boolean currentState;
    Exception ex;
    //CursorLoader for loading data from database
    private LoaderManager.LoaderCallbacks<Boolean> sqlOperation
            = new LoaderManager.LoaderCallbacks<Boolean>() {
        @Override
        public Loader<Boolean> onCreateLoader(int id, final Bundle args) {

            return new AsyncTaskLoader<Boolean>(getApplicationContext()) {

                @Override
                public Boolean loadInBackground() {

                    try {
                        if (args.getString(getResources().getString(R.string.sql_operation)) == "query") {
                            String[] projection = {
                                    MovieContract.MovieEntry.COLUMN_MOVIEID,
                            };
                            int movieId = (args.getInt(getResources().getString(R.string.put_extra_movie_id)));
                            String selection = MovieContract.MovieEntry.COLUMN_MOVIEID + " = ? ";
                            String sortOrder = null;
                            String[] selectionArgs = new String[]{String.valueOf(movieId)};
                            Uri uri = MovieContract.MovieEntry.CONTENT_URI;
                            Cursor cursorReturnAfterQuery = getContentResolver().query(uri, projection, selection, selectionArgs, sortOrder);
                            return cursorReturnAfterQuery.getCount() > 0;
                        } else if (args.getString(getString(R.string.sql_operation)) == "delete") {
                            String movieId = (args.getString(getResources().getString(R.string.put_extra_movie_id)));
                            String selection = MovieContract.MovieEntry.COLUMN_MOVIEID + " = ? ";
                            String[] selectionArgs = new String[]{String.valueOf(movieId)};
                            Uri uri = MovieContract.MovieEntry.CONTENT_URI;
                            getContentResolver().delete(uri, selection, selectionArgs);
                        } else if (args.getString(getString(R.string.sql_operation)) == "insert") {
                            String movieId = (args.getString(getResources().getString(R.string.put_extra_movie_id)));
                            ContentValues contentValues = new ContentValues();
                            contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIEID, movieId);
                            Uri uri = MovieContract.MovieEntry.CONTENT_URI;
                            getContentResolver().insert(uri, contentValues);
                        }
                    } catch (Exception e) {
                        ex = e;
                    }

                    return null;


                }

                @Override
                protected void onStartLoading() {
                    forceLoad();

                }
            };
        }

        @Override
        public void onLoadFinished(Loader<Boolean> loader, Boolean result) {
            if (result == true) {
                fabStateChange(true);

            } else if (result == false) {
                fabStateChange(false);

            } else if (ex != null) {
                Toast.makeText(MovieDetail.this, getResources().getString(R.string.sql_oper) + ex.getMessage(), Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onLoaderReset(Loader<Boolean> loader) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.DetailTheme);
        setContentView(R.layout.activity_movie_detail);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initCollapsingToolbar();
        Movie movie = getIntent().getParcelableExtra("Movie");
        ActivityMovieDetailBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_movie_detail);
        binding.setMovie(movie);
        myFab = findViewById(R.id.fab);

        try {
            Glide.with(this).load(movie.backdrop).into((ImageView) findViewById(R.id.backdrop));
        } catch (Exception e) {
            e.printStackTrace();
        }

        queryOperation();

//        myFab.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//
//            }
//        });
    }

    //Cursor loader

    private void queryOperation() {
        Movie movieQuery = getIntent().getParcelableExtra("Movie");
        Uri uri = MovieContract.MovieEntry.CONTENT_URI;
        int movieExtraID = movieQuery.id;
        Bundle myBundle = new Bundle();
        myBundle.putString(getResources().getString(R.string.sql_operation), "query");
        myBundle.putString(getResources().getString(R.string.put_extra_url), uri.toString());
        myBundle.putInt(getResources().getString(R.string.put_extra_movie_id), movieExtraID);
        getSupportLoaderManager().restartLoader(SQL_LOADER_ID, myBundle, sqlOperation);
    }

    //end of cursor loader

    //Fab state change
    private void fabStateChange(boolean conditionChange) {

        // untag favorites intially
        if (conditionChange == false) {
            currentState = false;
            myFab.setImageDrawable(ContextCompat.getDrawable(MovieDetail.this, R.drawable.out_favorites));
        }
        //tag favorites initially
        else if (conditionChange == true) {
            currentState = true;
            myFab.setImageDrawable(ContextCompat.getDrawable(MovieDetail.this, R.drawable.in_favorites));

        }
    }



    private void initCollapsingToolbar() {
        final CollapsingToolbarLayout collapsingToolbar =
                findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(" ");
        AppBarLayout appBarLayout = findViewById(R.id.appbar);
        appBarLayout.setExpanded(true);

        // hiding & showing the title when toolbar expanded & collapsed
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbar.setTitle(getString(R.string.app_name));
                    isShow = true;
                } else if (isShow) {
                    collapsingToolbar.setTitle(" ");
                    isShow = false;
                }
            }
        });
    }


    @Override
    public Loader<String> onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String data) {

    }

    @Override
    public void onLoaderReset(Loader<String> loader) {

    }
}
