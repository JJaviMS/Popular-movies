package com.example.jjavims.popularmovies.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by JjaviMS on 17/02/2018.
 *
 * @author JJaviMS
 */

public class JSONUtils {

    public static JSONObject [] getFilmJSON (String rawjSON) throws JSONException {

        JSONObject base = new JSONObject(rawjSON);

        String RESULT = "results";
        JSONArray films = base.getJSONArray(RESULT);
        JSONObject [] results;
        results = new JSONObject[films.length()];
        for (int i=0;i<films.length();i++){
            results[i] = films.getJSONObject(i);
        }
        return results;
    }

    public static String getImageURL (JSONObject film) throws JSONException {
        String POSTER_PATH = "poster_path";
        String path = film.getString(POSTER_PATH);
        path = new StringBuilder(path).deleteCharAt(0).toString();
        return NetworkUtils.getImageURL(path);

    }

    public static String getFilmInformation (JSONObject film) throws JSONException {
        String OVERVIEW = "overview";
        String VOTES = "vote_average";
        String REALESE_DATE = "release_date";
        String TITLE = "title";
        return film.getString(TITLE) + "\n" +
                film.getString(OVERVIEW) + "\n" +
                film.getString(REALESE_DATE) + "\n" +
                film.getString(VOTES) + "\n";
    }
}
