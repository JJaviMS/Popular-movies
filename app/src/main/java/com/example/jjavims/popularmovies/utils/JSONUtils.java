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

    private static String RESULT = "results";
    private static String POSTER_PATH = "poster_path";
    private static String OVERVIEW = "overview";
    private static String TITLE = "title";
    private static String REALESE_DATE = "release_date";
    private static String VOTES = "vote_average";

    public static String parseJSON (String rawjSON) throws JSONException {

        JSONObject base = new JSONObject(rawjSON);

        JSONArray films = base.getJSONArray(RESULT);
        String [] results = null;
        results = new String[films.length()];
        for (int i=0;i<films.length();i++){
            JSONObject film = films.getJSONObject(i);
            StringBuilder buffer = new StringBuilder();

            buffer.
        }
    }
}
