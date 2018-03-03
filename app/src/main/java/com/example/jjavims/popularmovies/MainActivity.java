package com.example.jjavims.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.jjavims.popularmovies.data.FilmsContract;
import com.example.jjavims.popularmovies.data.MoviesPrefSync;
import com.example.jjavims.popularmovies.utils.JSONUtils;
import com.example.jjavims.popularmovies.utils.NetworkUtils;

import org.json.JSONException;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks,
        FilmAdapter.FilmAdapterListener, SharedPreferences.OnSharedPreferenceChangeListener {

    //ID loaders
    private final int LOADER_ID_FROM_DATABASE = 564; //ID for the Loader
    private final int LOADER_ID_GET_NEW_DATA = 364;
    public static final String FILM_URI = "Data";

    //Projection to query the data
    public static final String[] FILM_MAIN_PROJECTION = {
            FilmsContract.FilmEntry.TITLE,
            FilmsContract.FilmEntry.MOVIE_POSTER_PATH,
            FilmsContract.FilmEntry.RELEASE_DATE,
            FilmsContract.FilmEntry._ID
    };

    //Index of the projection
    public static final int INDEX_FILM_TITLE = 0;
    public static final int INDEX_FILM_MOVIE_PATH = 1;
    public static final int INDEX_FILM_MOVIE_RELEASE_DATE = 2;
    public static final int INDEX_FILM_MOVIE_ID = 3;

    //Bind views with ButterKnife
    @BindView(R.id.recycle_view_poster)
    RecyclerView mRecyclerView;
    @BindView(R.id.no_data_layout)
    LinearLayout mLinearLayout;
    @BindView(R.id.loading_indicator)
    ProgressBar mProgressBar;

    //The adapter of the recyclerView
    private FilmAdapter mFilmAdapter;

    private String sortOrder;

    public static boolean needsUpdate = false;

    private GridLayoutManager gridLayoutManager;
    private String GRID_LAYOUT_STATE = "grid";

    @Override
    protected void onResume() {
        super.onResume();
        if (needsUpdate) {
            getSupportLoaderManager().restartLoader(LOADER_ID_FROM_DATABASE, null, this);
            needsUpdate = true;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        int orientation = getResources().getConfiguration().orientation;
        getResources().getConfiguration();
        int span = orientation == Configuration.ORIENTATION_PORTRAIT ? 2 : 6;

        gridLayoutManager = new GridLayoutManager(this, span, GridLayoutManager.VERTICAL, false);

        mRecyclerView.setLayoutManager(gridLayoutManager);
        mFilmAdapter = new FilmAdapter(this, this);

        mRecyclerView.setAdapter(mFilmAdapter);

        sortOrder = MoviesPrefSync.getSort(this);

        getSupportLoaderManager().initLoader(LOADER_ID_FROM_DATABASE, null, this);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();

        menuInflater.inflate(R.menu.main_act_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.setting_act:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            case R.id.reload_data:
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt(getString(R.string.page_shared_pref_popular), 1);
                editor.putInt(getString(R.string.page_shared_pref_best_rated), 1);
                editor.apply();
                getContentResolver().delete(FilmsContract.FilmEntry.CONTENT_URI, null, null);
                getSupportLoaderManager().restartLoader(LOADER_ID_GET_NEW_DATA, null, this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        switch (id) {
            case LOADER_ID_FROM_DATABASE:
                showLoading();
                Uri.Builder uriBuilder = FilmsContract.FilmEntry.CONTENT_URI.buildUpon();
                if (sortOrder.equals(getString(R.string.pref_sort_popularity)))
                    uriBuilder.appendPath(FilmsContract.PATH_SORT).appendPath(FilmsContract.SORT_PARAM_POPULAR);
                else if (sortOrder.equals(getString(R.string.pref_sort_top_rated))) {
                    uriBuilder.appendPath(FilmsContract.PATH_SORT).appendPath(FilmsContract.SORT_PARAM_HIGH_RATE);
                } else if (sortOrder.equals(getString(R.string.pref_sort_favorite))) {
                    uriBuilder.appendPath(FilmsContract.PATH_FAVORITE);
                } else {
                    throw new UnsupportedOperationException();
                }
                Uri uri = uriBuilder.build();
                return new CursorLoader(this, uri, FILM_MAIN_PROJECTION, null, null, null);
            case LOADER_ID_GET_NEW_DATA:
                if (args == null)
                    showLoading();
                return new MyAsyncTask(this);
            default:
                return null;

        }
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {
        if (loader.getId() == LOADER_ID_FROM_DATABASE) {
            Cursor cursor = (Cursor) data;
            if ((data == null || cursor.getCount() == 0)) {
                if (NetworkUtils.checkInternetStatus(this) && !sortOrder.equals(getString(R.string.pref_sort_favorite))) {
                    //Start to fetch the data
                    getSupportLoaderManager().restartLoader(LOADER_ID_GET_NEW_DATA, null, this);
                } else {
                    showEmpty();
                }
            } else {
                showData();
                mFilmAdapter.setFilms(cursor);
            }
        } else if (loader.getId() == LOADER_ID_GET_NEW_DATA) {
            ContentValues[] cv = (ContentValues[]) data;
            if (cv == null) {
                showEmpty();
                Toast.makeText(this, R.string.error_retrieving_data, Toast.LENGTH_SHORT).show();
            } else {
                getContentResolver().bulkInsert(FilmsContract.FilmEntry.CONTENT_URI, cv);
                getSupportLoaderManager().restartLoader(LOADER_ID_FROM_DATABASE, null, this);

            }
        }

    }

    private void showData() {
        mLinearLayout.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.GONE);
    }

    private void showEmpty() {
        mLinearLayout.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void onLoaderReset(Loader loader) {
        mFilmAdapter.setFilms(null);
    }

    @Override
    public void onClick(int id) {
        Intent intent = new Intent(this, DetailActivity.class);
        Uri uri = FilmsContract.FilmEntry.buildUriWithId(id);
        intent.setData(uri);
        startActivity(intent);
    }

    private void showLoading() {
        mLinearLayout.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void bottomReached(int position) {
        if (NetworkUtils.checkInternetStatus(this)) {
            Bundle bundle = new Bundle();
            Log.v("Main", "Last pos reached");
            getSupportLoaderManager().restartLoader(LOADER_ID_GET_NEW_DATA, bundle, this);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        if (s.equals(getString(R.string.pref_sort_key))) {
            sortOrder = sharedPreferences.getString(s, sortOrder);
            getSupportLoaderManager().restartLoader(LOADER_ID_FROM_DATABASE, null, this);
        }
    }

    private static class MyAsyncTask extends AsyncTaskLoader<ContentValues[]> {

        MyAsyncTask(@NonNull Context context) {
            super(context);
        }

        @Nullable
        @Override
        public ContentValues[] loadInBackground() {
            try {
                Context context = getContext();
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                int page;
                if (MoviesPrefSync.getSort(context).equals(context.getString(R.string.page_shared_pref_popular))) {
                    page = sharedPreferences.getInt(getContext().getString(R.string.page_shared_pref_popular), 1);
                } else {
                    page = sharedPreferences.getInt(context.getString(R.string.page_shared_pref_best_rated), 1);
                }
                String response =
                        NetworkUtils.getServerResponse(context, MoviesPrefSync.getSort(getContext()), page);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                page++;
                Log.v("Querying", "Page number: " + (page - 1));
                if (response != null && context.getString(R.string.page_shared_pref_popular).equals(MoviesPrefSync.getSort(context)))
                    editor.putInt(getContext().getString(R.string.page_shared_pref_popular), page);
                else if (response != null)
                    editor.putInt(context.getString(R.string.page_shared_pref_best_rated), page);
                editor.apply();

                String sortType = MoviesPrefSync.getSort(context);
                String sort = null;
                if (sortType.equals(context.getString(R.string.pref_sort_popularity))) {
                    sort = FilmsContract.SORT_PARAM_POPULAR;
                } else if (sortType.equals(context.getString(R.string.pref_sort_top_rated))) {
                    sort = FilmsContract.SORT_PARAM_HIGH_RATE;
                }
                return JSONUtils.parseFilmJSON(response, sort);

            } catch (IOException | JSONException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onStartLoading() {
            forceLoad();
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(GRID_LAYOUT_STATE, gridLayoutManager.onSaveInstanceState());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        gridLayoutManager.onRestoreInstanceState(savedInstanceState.getParcelable(GRID_LAYOUT_STATE));
    }
}
