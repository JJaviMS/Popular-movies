package com.example.jjavims.popularmovies;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.jjavims.popularmovies.data.MoviesPrefSync;
import com.example.jjavims.popularmovies.utils.NetworkUtils;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String []> {


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
    public Loader onCreateLoader(int id, Bundle args) {
        switch (id){
            case LOADER_ID:
                try {
                    return NetworkUtils.getServerResponse(MainActivity.this, MoviesPrefSync.getSort(MainActivity.this));
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }

    @Override
    public void onLoadFinished(Loader loader, String[] data) {

    }

    @Override
    public void onLoaderReset(Loader loader) {

    }
}
