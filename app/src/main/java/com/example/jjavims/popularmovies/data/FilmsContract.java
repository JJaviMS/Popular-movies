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

    public static final String PATH_TYPE_OF_SORT = "sort_order";

    public static final class FilmEntry implements BaseColumns{

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_FILM).build();

        public static final String TABLE_NAME = "films";

        public static final String TITLE = "title";

        public static final String RELEASE_DATA = "release date";

        public static final String MOVIE_POSTER_PATH = "poster";

        public static final String SYNOPSIS = "synopsis";

        public static final String IS_FAVORITE = "favorite";

        public static final String TYPE_OF_SORT = "type";
    }


    public static final class SortOrderEntry implements BaseColumns{
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_TYPE_OF_SORT).build();

        public static final String TABLE_NAME = "sort order";

        public static final String TYPE_OF_SORT = "type of sort";
    }
}
