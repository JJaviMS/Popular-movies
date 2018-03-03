package com.example.jjavims.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.jjavims.popularmovies.data.FilmsContract;
import com.example.jjavims.popularmovies.utils.JSONUtils;
import com.example.jjavims.popularmovies.utils.NetworkUtils;

import org.json.JSONException;

import java.net.URL;

import butterknife.BindView;
import butterknife.ButterKnife;


public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks, VideosAdapter.VideosAdapterListener {
    @BindView(R.id.poster_detail)
    ImageView mPosterIv;
    @BindView(R.id.title_text_view)
    TextView mTitleTv;
    @BindView(R.id.trailers_recycler_view)
    RecyclerView mTrailersRecyclerView;
    @BindView(R.id.reviews_recycler_view)
    RecyclerView mReviewsRecyclerView;
    @BindView(R.id.date_text_view)
    TextView mDateTv;
    @BindView(R.id.vote_average_text_view)
    TextView mVoteAverageTv;
    @BindView(R.id.overview_text_view)
    TextView mOverviewTv;

    private int id;
    Uri mUri;

    public static final String[] FILM_PROJECTION = {
            FilmsContract.FilmEntry.TITLE,
            FilmsContract.FilmEntry.VOTE_AVERAGE,
            FilmsContract.FilmEntry.MOVIE_POSTER_PATH,
            FilmsContract.FilmEntry.SYNOPSIS,
            FilmsContract.FilmEntry.IS_FAVORITE,
            FilmsContract.FilmEntry.RELEASE_DATE
    };

    public static final int INDEX_FILM_TITLE = 0;
    public static final int INDEX_FILM_VOTE_AVERAGE = 1;
    public static final int INDEX_FILM_POSTER_PATH = 2;
    public static final int INDEX_FILM_SYNOPSIS = 3;
    public static final int INDEX_FILM_IS_FAVORITE = 4;
    public static final int INDEX_FILM_RELEASE_DATE = 5;

    public static final String[] REVIEW_PROJECTION = {
            FilmsContract.ReviewEntry.AUTHOR,
            FilmsContract.ReviewEntry.REVIEW
    };
    public static final int INDEX_REVIEW_AUTHOR = 0;
    public static final int INDEX_REVIEW_REVIEW = 1;

    public static final String[] VIDEOS_PROJECTION = {
            FilmsContract.VideosEntry.NAME,
            FilmsContract.VideosEntry.KEY
    };
    public static final int INDEX_VIDEO_NAME = 0;
    public static final int INDEX_VIDEO_KEY = 1;

    private final int FILM_LOADER_ID = 366;
    private final int TRAILERS_LOADER_ID = 367;
    private final int REVIEWS_LOADER_ID = 368;
    private final int REVIEWS_FROM_SERVER_LOADER_ID = 369;
    private final int TRAILERS_FROM_SERVER_LOADER_ID = 370;

    private VideosAdapter mVideosAdapter;


    private boolean fetchedReviews = false;
    private boolean fetchedTrailers = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);
        Intent intentWhichStartedActivity = getIntent();
        if (intentWhichStartedActivity != null) {
            mUri = intentWhichStartedActivity.getData();
            if (mUri != null) {
                id = Integer.valueOf(mUri.getLastPathSegment());
            }
        }
        if (mUri == null)
            throw new RuntimeException("Invalid URI");

        mVideosAdapter = new VideosAdapter(this, this);
        mReviewsRecyclerView.setAdapter(mVideosAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mReviewsRecyclerView.setLayoutManager(layoutManager);

        getSupportLoaderManager().initLoader(FILM_LOADER_ID, null, this);
        getSupportLoaderManager().initLoader(TRAILERS_LOADER_ID, null, this);
        getSupportLoaderManager().initLoader(REVIEWS_LOADER_ID, null, this);

    }

    @Override
    public Loader onCreateLoader(int i, Bundle bundle) {
        switch (i) {
            case FILM_LOADER_ID: {
                return new CursorLoader(this, mUri, FILM_PROJECTION, null, null, null);
            }
            case REVIEWS_LOADER_ID: {
                return new CursorLoader(this, FilmsContract.ReviewEntry.buildUriWithFilmId(id), REVIEW_PROJECTION, null, null, null);
            }
            case TRAILERS_LOADER_ID: {
                return new CursorLoader(this, FilmsContract.VideosEntry.buildUriWithFilmId(id), VIDEOS_PROJECTION, null, null, null);
            }
            case TRAILERS_FROM_SERVER_LOADER_ID: {
                return new VideosAsyncTask(this, id);
            }
            case REVIEWS_FROM_SERVER_LOADER_ID: {
                return new ReviewsAsyncTask(this, id);
            }
            default:
                throw new RuntimeException("Loader not implemented");
        }
    }

    @Override
    public void onLoadFinished(Loader loader, Object o) {
        switch (loader.getId()) {
            case FILM_LOADER_ID:
                setData((Cursor) o);
                break;
            case REVIEWS_LOADER_ID: {
                Cursor cursor = (Cursor) o;
                if ((cursor == null || cursor.getCount() == 0)) {
                    if (NetworkUtils.checkInternetStatus(this) && !fetchedReviews) {
                        fetchedReviews = true;
                        getSupportLoaderManager().restartLoader(REVIEWS_FROM_SERVER_LOADER_ID, null, this);
                    } else {
                        hydeReviews();
                    }
                } else {
                    //TODO Here change
                }
                break;
            }
            case TRAILERS_LOADER_ID: {
                Cursor cursor = (Cursor) o;
                if ((cursor == null || cursor.getCount() == 0)) {
                    if (NetworkUtils.checkInternetStatus(this) && !fetchedTrailers) {
                        fetchedTrailers = true;
                        getSupportLoaderManager().restartLoader(TRAILERS_FROM_SERVER_LOADER_ID, null, this);
                    } else {
                        hydeFilms();
                    }
                } else {
                    mVideosAdapter.setAdapter(cursor);
                }
                break;
            }
            case TRAILERS_FROM_SERVER_LOADER_ID: {
                ContentValues[] cv = (ContentValues[]) o;
                Uri uri = FilmsContract.FilmEntry.CONTENT_URI;
                getContentResolver().bulkInsert(uri, cv);
                getSupportLoaderManager().restartLoader(TRAILERS_LOADER_ID, null, this);
                break;
            }
            case REVIEWS_FROM_SERVER_LOADER_ID: {
                ContentValues[] cv = (ContentValues[]) o;
                Uri uri = FilmsContract.ReviewEntry.CONTENT_URI;
                getContentResolver().bulkInsert(uri, cv);
                getSupportLoaderManager().restartLoader(TRAILERS_LOADER_ID, null, this);
                break;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }

    @Override
    public void onClick(String key) {
        URL url = NetworkUtils.getVideoUrl(key);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        if (url != null) {
            intent.setData(Uri.parse(url.toString()));
            startActivity(intent);
        } else {
            Toast.makeText(this, R.string.error_url, Toast.LENGTH_SHORT).show();
        }
    }


    private static class VideosAsyncTask extends AsyncTaskLoader<ContentValues[]> {
        private int mId;

        VideosAsyncTask(Context context, int id) {
            super(context);
            mId = id;

        }

        @Override
        public ContentValues[] loadInBackground() {
            try {
                return JSONUtils.parseVideosJSON(NetworkUtils.getVideosServerResponse(getContext(), mId));
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onStartLoading() {
            super.onStartLoading();
            forceLoad();
        }
    }

    private static class ReviewsAsyncTask extends AsyncTaskLoader<ContentValues[]> {
        private int mId;

        ReviewsAsyncTask(Context context, int id) {
            super(context);
            mId = id;
        }

        @Override
        public ContentValues[] loadInBackground() {
            try {
                return JSONUtils.parseReviewsJSON(NetworkUtils.getReviewsServerResponse(getContext(), mId));
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    private void setData(Cursor cursor) {
        cursor.moveToFirst();
        mDateTv.setText(cursor.getString(INDEX_FILM_RELEASE_DATE));
        mOverviewTv.setText(cursor.getString(INDEX_FILM_SYNOPSIS));
        mTitleTv.setText(cursor.getString(INDEX_FILM_TITLE));
        mVoteAverageTv.setText(getString(R.string.value_over_10, cursor.getString(INDEX_FILM_VOTE_AVERAGE)));
        Glide.with(this).load(NetworkUtils.getImageURL(cursor.getString(INDEX_FILM_POSTER_PATH))).into(mPosterIv);

    }

    private void hydeReviews() {
        mReviewsRecyclerView.setVisibility(View.GONE);
    }

    private void hydeFilms() {
        mTrailersRecyclerView.setVisibility(View.GONE);
    }
}
