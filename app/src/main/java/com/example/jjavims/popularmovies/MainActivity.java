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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.jjavims.popularmovies.data.MoviesPrefSync;
import com.example.jjavims.popularmovies.utils.JSONUtils;
import com.example.jjavims.popularmovies.utils.NetworkUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<JSONObject[]> {


    private final int LOADER_ID = 564; //ID for the Loader

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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

        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader loader, JSONObject[] data) {

    }

    @Override
    public void onLoaderReset(Loader loader) {

    }

    private static class MyAsyncTask extends AsyncTaskLoader<JSONObject[]> {
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

            } catch (IOException | JSONException e){
                e.printStackTrace();
                return null;
            }
        }
    }
}
