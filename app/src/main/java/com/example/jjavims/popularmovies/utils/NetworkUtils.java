package com.example.jjavims.popularmovies.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

public final class NetworkUtils {

    private static final String MOVIES_URL = "https://api.themoviedb.org/3";
    private static final String API_KEY = "api_key";
    private static final String POPULAR = "popular";
    private static final String TOP_RATED = "top_rated";
    private static final String MOVIE_PATH = "movie";

    private static final String IMAGE_URL = "http://image.tmdb.org/t/p";
    private static final String SIZE = "w185";

    /**
     * Get the JSON data from the server
     * @param url The URL to fetch the data
     * @return The String containing the JSON information
     * @throws IOException Related to Network reading
     */
    private static String getHttpResponse(URL url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try{
            InputStream inputStream = connection.getInputStream();

            Scanner scanner = new Scanner(inputStream);
            scanner.useDelimiter("\\A");

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

    /**
     * Get the server JSON data
     * @param context The Activity context
     * @param sortOrder String from the SharedPreferences which indicates the way to sort the data
     * @return The JSON information from the server
     * @throws IOException Related to the network connection
     */
    public static String getServerResponse (Context context,String sortOrder) throws IOException {
        if (!checkInternetStatus(context)) return null; // If the device is not connected to the Internet just return null
        if (context.getString(R.string.pref_sort_popularity).equals(sortOrder)){
            return getHttpResponse(buildUrlWithPopular(context));
        } else if (context.getString(R.string.pref_sort_top_rated).equals(sortOrder)){
            return getHttpResponse(buildUrlWithTopRated(context));
        }else{
            return null;
        }
    }

    /**
     * Creates the poster URL to retrieve the image
     * @param path The image path
     * @return The Image URL
     */
    static String getImageURL(String path) {
        Uri uri = Uri.parse(IMAGE_URL).buildUpon().appendPath(SIZE).appendPath(path).build();

        Log.v("URL",uri.toString());
        return uri.toString();
    }

    /**
     * Check is the device is connected to the Internet
     * @param context Context where the app is called
     * @return Returns true if the device is connected to the Internet, returns null otherwise
     */
    private static boolean checkInternetStatus (Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo status = null;
        if (connectivityManager != null) {
            status = connectivityManager.getActiveNetworkInfo();
        }
        return status!=null && status.isConnectedOrConnecting();
    }

}
