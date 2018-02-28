package com.example.jjavims.popularmovies.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by JjaviMS on 20/02/2018.
 *
 * @author JJaviMS
 */

public class FilmsContract {

    public static final String CONTENT_AUTHORITY = "com.example.jjavims.popularmovies";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://"+CONTENT_AUTHORITY);

    public static final String PATH_FILM = "film";

    public static final String PATH_SORT = "sort";

    public static final String SORT_PARAM_POPULAR = "popular";
    public static final String SORT_PARAM_HIGH_RATE = "high_rate";

    public static final String PATH_FAVORITE = "favorite";

    public static final String PATH_FILM_TRAILERS = "trailers";

    public static final String PATH_FILM_REVIEWS = "reviews";

    public static final class FilmEntry implements BaseColumns{

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_FILM).build();

        public static Uri buildUriWithId(int id) {
            return CONTENT_URI.buildUpon().appendPath(String.valueOf(id)).build();
        }

        public static final String TABLE_NAME = "films";

        public static final String TITLE = "title";

        public static final String RELEASE_DATE = "release_date";

        public static final String MOVIE_POSTER_PATH = "poster";

        public static final String SYNOPSIS = "synopsis";

        public static final String IS_FAVORITE = "favorite";

        public static final String TYPE_OF_SORT = "type";

        public static final String VOTE_AVERAGE = "vote_average";

        public static final String POPULARITY = "popularity";
    }

    public static final class ReviewEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_FILM_REVIEWS).build();

        public static Uri buildUriWithFilmId(int id) {
            return CONTENT_URI.buildUpon().appendPath(PATH_FILM).appendPath(String.valueOf(id)).build();
        }

        public static final String TABLE_NAME = "reviews";
        public static final String REVIEW = "review";
        public static final String AUTHOR = "author";
        public static final String FILM_ID = "film_id";
    }

    public static final class VideosEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_FILM_TRAILERS).build();

        public static Uri buildUriWithFilmId(int id) {
            return CONTENT_URI.buildUpon().appendPath(PATH_FILM).appendPath(String.valueOf(id)).build();
        }

        public static final String TABLE_NAME = "trailers";
        public static final String KEY = "key";
        public static final String NAME = "name";
        public static final String FILM_ID = "film_id";
    }




}
