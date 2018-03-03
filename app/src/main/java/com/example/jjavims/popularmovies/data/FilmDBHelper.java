package com.example.jjavims.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by JjaviMS on 21/02/2018.
 *
 * @author JJaviMS
 */

public class FilmDBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "films.db";

    private static final int DATABASE_VERSION = 9;


    public FilmDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        /*String to create an auxiliary table*/

        final String SQL_CREATE_FILMS_TABLE =
                "CREATE TABLE " + FilmsContract.FilmEntry.TABLE_NAME + " ( " +
                        FilmsContract.FilmEntry._ID + " INTEGER PRIMARY KEY, " +
                        FilmsContract.FilmEntry.TITLE + " TEXT NOT NULL UNIQUE, " +
                        FilmsContract.FilmEntry.RELEASE_DATE + " TEXT NOT NULL, " +
                        FilmsContract.FilmEntry.SYNOPSIS + " TEXT NOT NULL, " +
                        FilmsContract.FilmEntry.MOVIE_POSTER_PATH + " TEXT NOT NULL, " +
                        /*The film by default is not favorite, so the value of favorite should
                        be favorite, which means is 0
                         */
                        FilmsContract.FilmEntry.IS_FAVORITE + " TEXT DEFAULT 0," +
                        FilmsContract.FilmEntry.VOTE_AVERAGE + " DOUBLE NOT NULL, " +
                        FilmsContract.FilmEntry.POPULARITY + " DOUBLE NOT NULL, " +
                        FilmsContract.FilmEntry.TYPE_OF_SORT + " TEXT NOT NULL);";


        sqLiteDatabase.execSQL(SQL_CREATE_FILMS_TABLE);

        final String SQL_CREATE_TRAILERS_TABLE =
                "CREATE TABLE " + FilmsContract.VideosEntry.TABLE_NAME + " ( " +
                        FilmsContract.VideosEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        FilmsContract.VideosEntry.NAME + " TEXT NOT NULL, " +
                        FilmsContract.VideosEntry.KEY + " TEXT NOT NULL UNIQUE, " +
                        FilmsContract.VideosEntry.FILM_ID + " INTEGER NOT NULL, " +
                        " FOREIGN KEY (" + FilmsContract.VideosEntry.FILM_ID + ") REFERENCES " +
                        FilmsContract.FilmEntry.TABLE_NAME + " ( " + FilmsContract.FilmEntry._ID + ") " +
                        "ON DELETE CASCADE ON UPDATE CASCADE);";
        sqLiteDatabase.execSQL(SQL_CREATE_TRAILERS_TABLE);

        final String SQL_CREATE_REVIEWS_TABLE =
                "CREATE TABLE " + FilmsContract.ReviewEntry.TABLE_NAME + " ( " +
                        FilmsContract.ReviewEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        FilmsContract.ReviewEntry.AUTHOR + " TEXT NOT NULL, " +
                        FilmsContract.ReviewEntry.REVIEW + " TEXT NOT NULL, " +
                        FilmsContract.ReviewEntry.FILM_ID + " INTEGER NOT NULL, " +
                        " FOREIGN KEY (" + FilmsContract.VideosEntry.FILM_ID + ") REFERENCES " +
                        FilmsContract.FilmEntry.TABLE_NAME + " ( " + FilmsContract.FilmEntry._ID + ") " +
                        "ON DELETE CASCADE);";

        sqLiteDatabase.execSQL(SQL_CREATE_REVIEWS_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FilmsContract.FilmEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FilmsContract.VideosEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FilmsContract.ReviewEntry.TABLE_NAME);

        onCreate(sqLiteDatabase);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly())
            //Enable database cascade delete
            db.setForeignKeyConstraintsEnabled(true);
    }
}
