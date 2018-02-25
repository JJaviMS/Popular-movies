package com.example.jjavims.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
        FilmAdapter.FilmAdapterListener {


    private final int LOADER_ID_FROM_DATABASE = 564; //ID for the Loader
    private final int LOADER_ID_GET_NEW_DATA = 364;
    public static final String INTENT_RAW_DATA_NAME = "Data";


    public static final String[] FILM_MAIN_PROJECTION = {
            FilmsContract.FilmEntry.TITLE,
            FilmsContract.FilmEntry.MOVIE_POSTER_PATH,
            FilmsContract.FilmEntry.RELEASE_DATE,
            FilmsContract.FilmEntry._ID
    };

    public static final int INDEX_FILM_TITLE = 0;
    public static final int INDEX_FILM_MOVIE_PATH = 1;
    public static final int INDEX_FILM_MOVIE_RELEASE_DATE = 2;
    public static final int INDEX_FILM_MOVIE_ID = 3;


    @BindView(R.id.recycle_view_poster)
    RecyclerView mRecyclerView;
    @BindView(R.id.no_data_layout)
    LinearLayout mLinearLayout;
    @BindView(R.id.loading_indicator)
    ProgressBar mProgressBar;
    private FilmAdapter mFilmAdapter;

    private String sortOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);

        mRecyclerView.setLayoutManager(gridLayoutManager);
        mFilmAdapter = new FilmAdapter(this, this);

        mRecyclerView.setAdapter(mFilmAdapter);

        sortOrder = MoviesPrefSync.getSort(this);

        getSupportLoaderManager().initLoader(LOADER_ID_FROM_DATABASE, null, this);
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
                editor.putInt(getString(R.string.page_shared_pref), 1);
                editor.apply();
                getSupportLoaderManager().initLoader(LOADER_ID_GET_NEW_DATA, null, this);
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
                if (NetworkUtils.checkInternetStatus(this) || sortOrder.equals(getString(R.string.pref_sort_favorite))) {
                    //Start to fetch the data
                    changeLoadToNetwork();
                    //getSupportLoaderManager().initLoader(LOADER_ID_GET_NEW_DATA, null, this);
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
                changeLoadToDatabase();
                //getSupportLoaderManager().restartLoader(LOADER_ID_FROM_DATABASE,null,this);
                //getSupportLoaderManager().initLoader(LOADER_ID_FROM_DATABASE, null, this);
                //When the data is inserted in the database we try to load the data again
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
        intent.putExtra(INTENT_RAW_DATA_NAME, id);
        startActivity(intent);
    }

    private void showLoading() {
        mLinearLayout.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
    }

    private static class MyAsyncTask extends AsyncTaskLoader<ContentValues[]> {

        MyAsyncTask(@NonNull Context context) {
            super(context);
        }

        @Nullable
        @Override
        public ContentValues[] loadInBackground() {
            try {
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                int page = sharedPreferences.getInt(getContext().getString(R.string.page_shared_pref), 1);
                String response =
                        NetworkUtils.getServerResponse(getContext(), MoviesPrefSync.getSort(getContext()), page);//TODO Actualizar esto
                SharedPreferences.Editor editor = sharedPreferences.edit();
                page++;
                editor.putInt(getContext().getString(R.string.page_shared_pref), page);
                editor.apply();

                String sortType = MoviesPrefSync.getSort(getContext());
                String sort = null;
                if (sortType.equals(getContext().getString(R.string.pref_sort_popularity))) {
                    sort = FilmsContract.SORT_PARAM_POPULAR;
                } else if (sortType.equals(getContext().getString(R.string.pref_sort_top_rated))) {
                    sort = FilmsContract.SORT_PARAM_HIGH_RATE;
                }
                return JSONUtils.getFilmJSON(response, sort);

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

    private void changeLoadToNetwork() {
        getSupportLoaderManager().destroyLoader(LOADER_ID_FROM_DATABASE);
        getSupportLoaderManager().initLoader(LOADER_ID_GET_NEW_DATA, null, this);
    }

    private void changeLoadToDatabase() {
        getSupportLoaderManager().destroyLoader(LOADER_ID_GET_NEW_DATA);
        getSupportLoaderManager().initLoader(LOADER_ID_FROM_DATABASE, null, this);
    }


}
