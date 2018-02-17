package com.example.jjavims.popularmovies.utils;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.example.jjavims.popularmovies.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by JjaviMS on 15/02/2018.
 *
 * @author JJaviMS
 */

public class NetworkUtils {

    private static final String MOVIES_URL = "https://api.themoviedb.org/3/movie/";
    private static final String API_KEY = "api_key";
    private static final String POPULAR = "popular";
    private static final String TOP_RATED = "top_rated";
    private static final String MOVIE_PATH = "movie";

    /**
     * Get the JSON data from the server
     * @param url The URL to fetch the data
     * @return The String containing the JSON information
     * @throws IOException Related to Network reading
     */
    public static String getHttpResponse (URL url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try{
            InputStream inputStream = connection.getInputStream();

            Scanner scanner = new Scanner(inputStream);

            boolean hasInput = scanner.hasNext();

            String data =null;
            if (hasInput){
                data=scanner.next();
            }
            scanner.close();
            return data;
        }
        finally {
            connection.disconnect();
        }
    }

    /**
     * Auxiliar method to get the API key
     * @param context The app context
     * @return The API key
     */
    private static String getApiKey (Context context){
        return context.getResources().getString(R.string.MOVIEES_API);
    }

    /**
     * Creates the URL to fetch movie data sorted by popularity
     * @param context Context of the app
     * @return URL to fetch the data
     */
    private static URL buildUrlWithPopular (Context context){
        Uri movieUri = Uri.parse(MOVIES_URL).buildUpon().appendPath(MOVIE_PATH).appendPath(POPULAR)
                .appendQueryParameter(API_KEY,getApiKey(context))
                .build();

        try{
            URL url = new URL(movieUri.toString());
            Log.v("URL",url.toString());
            return url;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Creates the URL to fetch movie data sorted by top rated
     * @param context Context of the app
     * @return URL to fetch the data
     */
    private static URL buildUrlWithTopRated (Context context){
        Uri movieUri = Uri.parse(MOVIES_URL).buildUpon().appendPath(MOVIE_PATH).appendPath(TOP_RATED)
                .appendQueryParameter(API_KEY,getApiKey(context))
                .build();

        try{
            URL url = new URL(movieUri.toString());
            Log.v("URL",url.toString());
            return url;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
