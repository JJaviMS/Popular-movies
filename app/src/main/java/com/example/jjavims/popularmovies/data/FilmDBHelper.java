package com.example.jjavims.popularmovies.data;

import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.jjavims.popularmovies.R;

import butterknife.BindString;
import butterknife.ButterKnife;

/**
 * Created by JjaviMS on 21/02/2018.
 *
 * @author JJaviMS
 */

public class FilmDBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "films.db";

    private static final int DATABASE_VERSION = 1;

    @BindString(R.string.pref_sort_popularity)String popularity;
    @BindString(R.string.pref_sort_top_rated) String topRated;


    public FilmDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        ButterKnife.bind((Activity) context);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        /*String to create an auxiliary table*/

        final String SQL_CREATE_FILMS_TABLE =
                "CREATE TABLE " + FilmsContract.FilmEntry.TABLE_NAME + " ( " +
                        FilmsContract.FilmEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        FilmsContract.FilmEntry.TITLE + " TEXT NOT NULL, " +
                        FilmsContract.FilmEntry.RELEASE_DATA + " TEXT NOT NULL, "+
                        FilmsContract.FilmEntry.SYNOPSIS + " TEXT NOT NULL, "+
                        FilmsContract.FilmEntry.MOVIE_POSTER_PATH + " TEXT NOT NULL, "+
                        /*The film by default is not favorite, so the value of favorite should
                        be favorite, which means is 0
                         */
                        FilmsContract.FilmEntry.IS_FAVORITE + " TEXT DEFAULT 0,"+
                        FilmsContract.FilmEntry.TYPE_OF_SORT + " TEXT NOT NULL);";



        sqLiteDatabase.execSQL(SQL_CREATE_FILMS_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FilmsContract.FilmEntry.TABLE_NAME);

        onCreate(sqLiteDatabase);
    }
}
