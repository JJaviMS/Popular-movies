package com.example.jjavims.popularmovies.utils;

import android.content.ContentValues;

import com.example.jjavims.popularmovies.data.FilmsContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by JjaviMS on 17/02/2018.
 *
 * @author JJaviMS
 */

public class JSONUtils {

    public static ContentValues[] getFilmJSON(String rawjSON, String sort) throws JSONException {
        String OVERVIEW = "overview";
        String VOTES = "vote_average";
        String REALESE_DATE = "release_date";
        String TITLE = "title";
        String POSTER_PATH = "poster_path";
        String RESULT = "results";
        String ID = "id";
        String popularity = "popularity";
        if (rawjSON!=null) {
            JSONObject base = new JSONObject(rawjSON);


            JSONArray films = base.getJSONArray(RESULT);
            ContentValues[] values = new ContentValues[films.length()];
            for (int i = 0; i < films.length(); i++) {
                JSONObject object = films.getJSONObject(i);
                ContentValues cv = new ContentValues();
                cv.put(FilmsContract.FilmEntry.TITLE, object.getString(TITLE));
                cv.put(FilmsContract.FilmEntry.RELEASE_DATE, object.getString(REALESE_DATE));
                cv.put(FilmsContract.FilmEntry.VOTE_AVERAGE, object.getDouble(VOTES));
                cv.put(FilmsContract.FilmEntry.SYNOPSIS, object.getString(OVERVIEW));
                cv.put(FilmsContract.FilmEntry.MOVIE_POSTER_PATH, object.getString(POSTER_PATH));
                cv.put(FilmsContract.FilmEntry.TYPE_OF_SORT, sort);
                cv.put(FilmsContract.FilmEntry._ID, object.getInt(ID));
                cv.put(FilmsContract.FilmEntry.POPULARITY, object.getDouble(popularity));
                values[i] = cv;
            }
            return values;
        }else return null;
    }

    public static String getImageURL(String path) throws JSONException {
        path = new StringBuilder(path).deleteCharAt(0).toString();
        return NetworkUtils.getImageURL(path);

    }


}
