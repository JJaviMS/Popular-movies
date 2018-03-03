package com.example.jjavims.popularmovies.utils;

import android.content.ContentValues;
import android.support.annotation.Nullable;

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

    private static final String ID = "id";
    private static final String RESULT = "results";

    /**
     * Parse the JSON information into an Array of ContentValues with all the Objects into the JSON
     *
     * @param rawJSON The JSON which contains the information
     * @param sort    The sort param
     * @return An Array with ContentValues with the information parsed
     * @throws JSONException Problem creating the JSON
     */
    public static ContentValues[] parseFilmJSON(String rawJSON, String sort) throws JSONException {
        final String OVERVIEW = "overview";
        final String VOTES = "vote_average";
        final String REALISE_DATE = "release_date";
        final String TITLE = "title";
        final String POSTER_PATH = "poster_path";


        String popularity = "popularity";
        if (rawJSON != null) {
            JSONObject base = new JSONObject(rawJSON);


            JSONArray films = base.getJSONArray(RESULT);
            ContentValues[] values = new ContentValues[films.length()];
            for (int i = 0; i < films.length(); i++) {
                JSONObject object = films.getJSONObject(i);
                ContentValues cv = new ContentValues();
                cv.put(FilmsContract.FilmEntry.TITLE, object.getString(TITLE));
                cv.put(FilmsContract.FilmEntry.RELEASE_DATE, object.getString(REALISE_DATE));
                cv.put(FilmsContract.FilmEntry.VOTE_AVERAGE, object.getDouble(VOTES));
                cv.put(FilmsContract.FilmEntry.SYNOPSIS, object.getString(OVERVIEW));
                cv.put(FilmsContract.FilmEntry.MOVIE_POSTER_PATH, object.getString(POSTER_PATH));
                cv.put(FilmsContract.FilmEntry.TYPE_OF_SORT, sort);
                cv.put(FilmsContract.FilmEntry._ID, object.getInt(ID));
                cv.put(FilmsContract.FilmEntry.POPULARITY, object.getDouble(popularity));
                values[i] = cv;
            }
            return values;
        } else return null;
    }

    /**
     * Create an Array of ContentValues which contains the information of the raw JSON parsed
     *
     * @param rawJSON The information in JSON format
     * @return The rawJSON parsed into a Content Values Array
     * @throws JSONException Error in the rawJSON String
     */
    @Nullable
    public static ContentValues[] parseReviewsJSON(String rawJSON) throws JSONException {
        final String AUTHOR = "author";
        final String CONTENT = "content";

        if (rawJSON != null) {
            JSONObject base = new JSONObject(rawJSON);
            int id = base.getInt(ID);

            JSONArray reviews = base.getJSONArray(RESULT);
            if (reviews.length() == 0) return null;
            ContentValues[] values = new ContentValues[reviews.length()];

            for (int i = 0; i < reviews.length(); i++) {
                JSONObject review = reviews.getJSONObject(i);

                ContentValues cv = new ContentValues();
                cv.put(FilmsContract.ReviewEntry.FILM_ID, id);
                cv.put(FilmsContract.ReviewEntry.AUTHOR, review.getString(AUTHOR));
                cv.put(FilmsContract.ReviewEntry.REVIEW, review.getString(CONTENT));
                values[i] = cv;
            }
            return values;
        } else
            return null;
    }

    /**
     * Parse the information of the trailers
     *
     * @param rawJSON The information in JSON format
     * @return The information parsed
     * @throws JSONException Error parsing the information
     */
    @Nullable
    public static ContentValues[] parseVideosJSON(String rawJSON) throws JSONException {
        final String KEY = "key";
        final String NAME = "name";

        if (rawJSON != null) {
            JSONObject base = new JSONObject(rawJSON);
            int id = base.getInt(ID);

            JSONArray videos = base.getJSONArray(RESULT);
            if (videos.length() == 0) return null;
            ContentValues[] values = new ContentValues[videos.length()];

            for (int i = 0; i < videos.length(); i++) {
                JSONObject video = videos.getJSONObject(i);

                ContentValues cv = new ContentValues();
                cv.put(FilmsContract.VideosEntry.KEY, video.getString(KEY));
                cv.put(FilmsContract.VideosEntry.FILM_ID, id);
                cv.put(FilmsContract.VideosEntry.NAME, video.getString(NAME));

                values[i] = cv;
            }
            return values;
        } else return null;

    }


}
