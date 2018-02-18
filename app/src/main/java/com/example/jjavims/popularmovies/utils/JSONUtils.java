package com.example.jjavims.popularmovies.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;

/**
 * Created by JjaviMS on 17/02/2018.
 *
 * @author JJaviMS
 */

public class JSONUtils {

    private static String RESULT = "results";
    private static String POSTER_PATH = "poster_path";
    private static String OVERVIEW = "overview";
    private static String TITLE = "title";
    private static String REALESE_DATE = "release_date";
    private static String VOTES = "vote_average";

    public static JSONObject [] getFilmJSON (String rawjSON) throws JSONException {

        JSONObject base = new JSONObject(rawjSON);

        JSONArray films = base.getJSONArray(RESULT);
        JSONObject [] results;
        results = new JSONObject[films.length()];
        for (int i=0;i<films.length();i++){
            results[i] = films.getJSONObject(i);
        }
        return results;
    }

    public static String getImageURL (JSONObject film) throws JSONException, MalformedURLException {
        String path = film.getString(POSTER_PATH);
        path = new StringBuilder(path).deleteCharAt(0).toString();
        return NetworkUtils.getImageURL(path);

    }

    public static String getFilmInformation (JSONObject film) throws JSONException {
        return film.getString(TITLE) + "\n" +
                film.getString(OVERVIEW) + "\n" +
                film.getString(REALESE_DATE) + "\n" +
                film.getString(VOTES) + "\n";
    }
}
