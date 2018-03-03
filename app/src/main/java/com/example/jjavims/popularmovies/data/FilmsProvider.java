package com.example.jjavims.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by JjaviMS on 24/02/2018.
 *
 * @author JJaviMS
 */

public class FilmsProvider extends ContentProvider {

    public static final int CODE_FILMS = 100;
    public static final int CODE_POPULAR_FILMS = 101;
    public static final int CODE_HIGH_RATE_FILMS = 102;
    public static final int CODE_FILM_ID = 103;
    public static final int CODE_FILMS_FAVORITES = 104;

    public static final int CODE_TRAILERS = 200;
    public static final int CODE_TRAILERS_ID = 201;
    public static final int CODE_TRAILERS_MOVIE_ID = 203;

    public static final int CODE_REVIEWS = 300;
    public static final int CODE_REVIEWS_ID = 301;
    public static final int CODE_REVIEWS_MOVIE_ID = 302;


    private static UriMatcher sUriMatcher = buildURiMatcher();
    private FilmDBHelper mFilmDBHelper;

    public static UriMatcher buildURiMatcher() {
        final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = FilmsContract.CONTENT_AUTHORITY;

        uriMatcher.addURI(authority, FilmsContract.PATH_FILM, CODE_FILMS);
        uriMatcher.addURI(authority, FilmsContract.PATH_FILM + "/" + FilmsContract.PATH_SORT +
                "/" + FilmsContract.SORT_PARAM_HIGH_RATE, CODE_HIGH_RATE_FILMS);
        uriMatcher.addURI(authority, FilmsContract.PATH_FILM + "/" + FilmsContract.PATH_SORT +
                "/" + FilmsContract.SORT_PARAM_POPULAR, CODE_POPULAR_FILMS);

        uriMatcher.addURI(authority, FilmsContract.PATH_FILM + "/#", CODE_FILM_ID);
        uriMatcher.addURI(authority, FilmsContract.PATH_FILM + "/" + FilmsContract.PATH_FAVORITE, CODE_FILMS_FAVORITES);

        uriMatcher.addURI(authority, FilmsContract.PATH_FILM_TRAILERS, CODE_TRAILERS);
        uriMatcher.addURI(authority, FilmsContract.PATH_FILM_TRAILERS + "/#", CODE_TRAILERS_ID);
        uriMatcher.addURI(authority, FilmsContract.PATH_FILM_TRAILERS + "/" + FilmsContract.PATH_FILM + "/#", CODE_TRAILERS_MOVIE_ID);

        uriMatcher.addURI(authority, FilmsContract.PATH_FILM_REVIEWS, CODE_REVIEWS);
        uriMatcher.addURI(authority, FilmsContract.PATH_FILM_REVIEWS + "/#", CODE_REVIEWS_ID);
        uriMatcher.addURI(authority, FilmsContract.PATH_FILM_REVIEWS + "/" + FilmsContract.PATH_FILM + "/#", CODE_REVIEWS_MOVIE_ID);

        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        mFilmDBHelper = new FilmDBHelper(getContext());
        return true;
    }


    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        Cursor cursor;

        switch (sUriMatcher.match(uri)) {
            case CODE_POPULAR_FILMS: {
                cursor = mFilmDBHelper.getReadableDatabase().query(FilmsContract.FilmEntry.TABLE_NAME, projection,
                        FilmsContract.FilmEntry.TYPE_OF_SORT + "=?", new String[]{FilmsContract.SORT_PARAM_POPULAR},
                        null, null, FilmsContract.FilmEntry.POPULARITY + " DESC");
                break;
            }
            case CODE_HIGH_RATE_FILMS: {
                cursor = mFilmDBHelper.getReadableDatabase().query(FilmsContract.FilmEntry.TABLE_NAME, projection,
                        FilmsContract.FilmEntry.TYPE_OF_SORT + "=?", new String[]{FilmsContract.SORT_PARAM_HIGH_RATE},
                        null, null, FilmsContract.FilmEntry.VOTE_AVERAGE + " DESC");
                break;
            }
            case CODE_FILMS_FAVORITES: {
                cursor = mFilmDBHelper.getReadableDatabase().query(FilmsContract.FilmEntry.TABLE_NAME, projection,
                        FilmsContract.FilmEntry.IS_FAVORITE + "=?", new String[]{String.valueOf(1)},
                        null, null, sortOrder);
                break;
            }
            case CODE_FILM_ID: {
                cursor = mFilmDBHelper.getReadableDatabase().query(FilmsContract.FilmEntry.TABLE_NAME, projection,
                        FilmsContract.FilmEntry._ID + "=?", new String[]{String.valueOf(uri.getLastPathSegment())},
                        null, null, sortOrder);
                break;
            }
            case CODE_REVIEWS_MOVIE_ID: {
                cursor = mFilmDBHelper.getWritableDatabase().query(FilmsContract.ReviewEntry.TABLE_NAME, projection,
                        FilmsContract.ReviewEntry.FILM_ID + "=?", new String[]{String.valueOf(uri.getLastPathSegment())},
                        null, null, FilmsContract.ReviewEntry._ID + " ASC");
                break;
            }
            case CODE_TRAILERS_MOVIE_ID: {
                cursor = mFilmDBHelper.getWritableDatabase().query(FilmsContract.VideosEntry.TABLE_NAME, projection,
                        FilmsContract.VideosEntry.FILM_ID + "=?", new String[]{String.valueOf(uri.getLastPathSegment())},
                        null, null, FilmsContract.VideosEntry._ID + " ASC");
                break;
            }
            default:
                throw new UnsupportedOperationException("Query not implemented");
        }
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        throw new UnsupportedOperationException("Not implemented, use bulk insert");
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        int rowsDeleted;

        switch (sUriMatcher.match(uri)) {
            case CODE_FILMS: {
                rowsDeleted = mFilmDBHelper.getWritableDatabase().delete(FilmsContract.FilmEntry.TABLE_NAME,
                        FilmsContract.FilmEntry.IS_FAVORITE + "=?", new String[]{String.valueOf(0)});
                //Delete only the not favorites films
                Log.v("Rows deleted", String.valueOf(rowsDeleted));
                break;
            }
            default:
                throw new UnsupportedOperationException();
        }
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        int rowsUpdated;

        switch (sUriMatcher.match(uri)) {
            case CODE_FILM_ID: {
                rowsUpdated = mFilmDBHelper.getWritableDatabase().update(FilmsContract.FilmEntry.TABLE_NAME, contentValues,
                        FilmsContract.FilmEntry._ID + "=?", new String[]{String.valueOf(uri.getLastPathSegment())});
                break;
            }
            default:
                throw new UnsupportedOperationException();
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        int rowsCreated;
        SQLiteDatabase db = mFilmDBHelper.getWritableDatabase();
        switch (sUriMatcher.match(uri)){
            case CODE_FILMS :{
                db.beginTransaction();
                rowsCreated = 0;
                try{
                    for (ContentValues cv : values){
                        long id = db.insert(FilmsContract.FilmEntry.TABLE_NAME,null,cv);
                        if (id!=-1){
                            rowsCreated++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                if (rowsCreated>0){
                    Context context = getContext();
                    if (context!=null){
                        ContentResolver contentResolver = context.getContentResolver();
                        if (contentResolver!=null){
                            contentResolver.notifyChange(uri,null);
                        }
                    }
                }
                break;
            }
            case CODE_REVIEWS: {
                db.beginTransaction();
                rowsCreated = 0;
                try {
                    for (ContentValues cv : values) {
                        long id = db.insert(FilmsContract.ReviewEntry.TABLE_NAME, null, cv);
                        if (id != -1) {
                            rowsCreated++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                if (rowsCreated > 0) {
                    Context context = getContext();
                    if (context != null) {
                        ContentResolver contentResolver = context.getContentResolver();
                        if (contentResolver != null) {
                            contentResolver.notifyChange(uri, null);
                        }
                    }
                }
                break;
            }
            case CODE_TRAILERS: {
                db.beginTransaction();
                rowsCreated = 0;
                try {
                    for (ContentValues cv : values) {
                        long id = db.insert(FilmsContract.VideosEntry.TABLE_NAME, null, cv);
                        if (id != -1) {
                            rowsCreated++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                if (rowsCreated > 0) {
                    Context context = getContext();
                    if (context != null) {
                        ContentResolver contentResolver = context.getContentResolver();
                        if (contentResolver != null) {
                            contentResolver.notifyChange(uri, null);
                        }
                    }
                }
                break;
            }
            default: throw new UnsupportedOperationException();
        }
        return rowsCreated;
    }
}
