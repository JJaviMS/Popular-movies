package com.example.jjavims.popularmovies.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;

import com.example.jjavims.popularmovies.R;

/**
 * Created by JjaviMS on 17/02/2018.
 *
 * @author JJaviMS
 */

public final class MoviesPrefSync {

    /**
     * Return the sort parameter from the SharedPreference
     * @param context The activity context
     * @return The sort parameter
     */
    public static String getSort (Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        String key = context.getResources().getString(R.string.pref_sort_key);

        return sharedPreferences.getString(key, context.getString(R.string.pref_sort_popularity));
    }
}
