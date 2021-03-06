package com.example.jjavims.popularmovies.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.jjavims.popularmovies.BuildConfig;
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
    private static final String MOVIE_PAGE_QUERY = "page";

    private static final String IMAGE_URL = "http://image.tmdb.org/t/p";
    private static final String SIZE = "w185";

    private static final String MY_API_KEY = BuildConfig.MOVIES_API_KEY;

    private static final String VIDEOS_PATH = "videos";
    private static final String REVIEWS_PATH = "reviews";

    private static final String YOUTUBE_URL = "https://www.youtube.com/";
    private static final String YOUTUBE_PATH_WATCH = "watch";
    private static final String YOUTUBE_QUERY_VIEDO = "v";

    /**
     * Get the JSON data from the server
     *
     * @param url The URL to fetch the data
     * @return The String containing the JSON information
     * @throws IOException Related to Network reading
     */
    private static String getHttpResponse(URL url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try {
            InputStream inputStream = connection.getInputStream();

            Scanner scanner = new Scanner(inputStream);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();

            String data = null;
            if (hasInput) {
                data = scanner.next();
            }
            scanner.close();
            return data;
        } finally {
            connection.disconnect();
        }
    }

    /**
     * Creates the URL to fetch movie data sorted by popularity
     *
     * @param page The page to query
     * @return URL to fetch the data
     */
    @Nullable
    private static URL buildUrlWithPopular(int page) {
        if (page < 1) throw new IllegalArgumentException();
        Uri movieUri = Uri.parse(MOVIES_URL).buildUpon().appendPath(MOVIE_PATH).appendPath(POPULAR)
                .appendQueryParameter(API_KEY, MY_API_KEY)
                .appendQueryParameter(MOVIE_PAGE_QUERY, String.valueOf(page))
                .build();

        try {
            URL url = new URL(movieUri.toString());
            Log.v("URL", url.toString());
            return url;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Creates the URL to fetch movie data sorted by top rated
     *
     * @param page The page to query
     * @return URL to fetch the data
     */
    @Nullable
    private static URL buildUrlWithTopRated(int page) {
        if (page < 1) throw new IllegalArgumentException();
        Uri movieUri = Uri.parse(MOVIES_URL).buildUpon().appendPath(MOVIE_PATH).appendPath(TOP_RATED)
                .appendQueryParameter(API_KEY, MY_API_KEY)
                .appendQueryParameter(MOVIE_PAGE_QUERY, String.valueOf(page))
                .build();

        try {
            URL url = new URL(movieUri.toString());
            Log.v("URL", url.toString());
            return url;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get the server JSON data
     *
     * @param context   The Activity context
     * @param sortOrder String from the SharedPreferences which indicates the way to sort the data
     * @return The JSON information from the server
     * @throws IOException Related to the network connection
     */
    @Nullable
    public static String getServerResponse(Context context, String sortOrder, int page) throws IOException {
        Log.v("Internet connection", "Starting connection");
        if (!checkInternetStatus(context))
            return null; // If the device is not connected to the Internet just return null
        if (context.getString(R.string.pref_sort_popularity).equals(sortOrder)) {
            return getHttpResponse(buildUrlWithPopular(page));
        } else if (context.getString(R.string.pref_sort_top_rated).equals(sortOrder)) {
            return getHttpResponse(buildUrlWithTopRated(page));
        } else {
            return null;
        }
    }

    /**
     * Creates the poster URL to retrieve the image
     *
     * @param path The image path
     * @return The Image URL
     */
    public static String getImageURL(String path) {
        StringBuilder builder = new StringBuilder(path);
        builder.deleteCharAt(0);
        String postPath = builder.toString();
        Uri uri = Uri.parse(IMAGE_URL).buildUpon().appendPath(SIZE).appendPath(postPath).build();

        Log.v("URL", uri.toString());
        return uri.toString();
    }

    /**
     * Check is the device is connected to the Internet
     *
     * @param context Context where the app is called
     * @return Returns true if the device is connected to the Internet, returns null otherwise
     */
    public static boolean checkInternetStatus(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo status = null;
        if (connectivityManager != null) {
            status = connectivityManager.getActiveNetworkInfo();
        }
        return status != null && status.isConnectedOrConnecting();
    }

    /**
     * Create the URL to get reviews from the server
     *
     * @param filmId The id that the film has on the movie database
     * @return The URL to get the reviews
     */
    @Nullable
    private static URL getReviewURL(int filmId) {
        Uri reviewsUri = Uri.parse(MOVIES_URL).buildUpon().appendPath(MOVIE_PATH).appendPath(String.valueOf(filmId))
                .appendPath(REVIEWS_PATH).appendQueryParameter(API_KEY, MY_API_KEY).build();

        try {
            URL url = new URL(reviewsUri.toString());
            Log.v("URL", url.toString());
            return url;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Returns the reviews from the server
     *
     * @param context The context where the method is called
     * @param filmId  The ID of the film on the movie database which you want to fetch the data
     * @return The JSON with the reviews information
     */
    @Nullable
    public static String getReviewsServerResponse(Context context, int filmId) {
        if (!checkInternetStatus(context)) return null;

        URL url = getReviewURL(filmId);
        if (url == null) return null;
        try {
            return getHttpResponse(url);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Returns the URL to fetch the trailers from a movie
     *
     * @param filmId The id that the film has on the movie database
     * @return The URL to fetch the data
     */
    @Nullable
    private static URL getTrailersURL(int filmId) {
        Uri reviewsUri = Uri.parse(MOVIES_URL).buildUpon().appendPath(MOVIE_PATH).appendPath(String.valueOf(filmId))
                .appendPath(VIDEOS_PATH).appendQueryParameter(API_KEY, MY_API_KEY).build();

        try {
            URL url = new URL(reviewsUri.toString());
            Log.v("URL", url.toString());
            return url;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Nullable
    public static String getVideosServerResponse(Context context, int filmId) {
        if (!checkInternetStatus(context)) return null;

        URL url = getTrailersURL(filmId);
        if (url == null) return null;
        try {
            return getHttpResponse(url);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Nullable
    public static URL getVideoUrl(String key) {
        Uri uri = Uri.parse(YOUTUBE_URL).buildUpon().appendPath(YOUTUBE_PATH_WATCH).appendQueryParameter(YOUTUBE_QUERY_VIEDO, key).build();
        try {
            URL url = new URL(uri.toString());
            Log.v("Youtube URL", url.toString());
            return url;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }


}
