package com.example.jjavims.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.jjavims.popularmovies.data.MoviesPrefSync;
import com.example.jjavims.popularmovies.utils.JSONUtils;
import com.example.jjavims.popularmovies.utils.NetworkUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<JSONObject[]>,
        FilmAdapter.FilmAdapterListener {


    private final int LOADER_ID = 564; //ID for the Loader

    @BindView(R.id.recycle_view_poster)
    RecyclerView mRecyclerView;
    private FilmAdapter mFilmAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);

        mRecyclerView.setLayoutManager(gridLayoutManager);
        mFilmAdapter = new FilmAdapter(this, this);

        mRecyclerView.setAdapter(mFilmAdapter);

        getSupportLoaderManager().initLoader(LOADER_ID, null, this);
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
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<JSONObject[]> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case LOADER_ID:
                return new MyAsyncTask(this);
            default:
                return null;

        }
    }

    @Override
    public void onLoadFinished(Loader loader, JSONObject[] data) {
        mFilmAdapter.setFilms(data);
    }

    @Override
    public void onLoaderReset(Loader loader) {
        mFilmAdapter.setFilms(null);
    }

    @Override
    public void onClick(JSONObject jsonObject) {

    }

    private static class MyAsyncTask extends AsyncTaskLoader<JSONObject[]> {

        private JSONObject [] mData;
        MyAsyncTask(@NonNull Context context) {
            super(context);
        }

        @Nullable
        @Override
        public JSONObject[] loadInBackground() {
            try {
                String response =
                        NetworkUtils.getServerResponse(getContext(), MoviesPrefSync.getSort(getContext()));


                return JSONUtils.getFilmJSON(response);

            } catch (IOException | JSONException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onStartLoading() {
            forceLoad();
        }

        @Override
        public void deliverResult(@Nullable JSONObject[] data) {
            mData =data;
            super.deliverResult(data);
        }
    }
}
