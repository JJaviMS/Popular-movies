package com.example.jjavims.popularmovies;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
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


public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks,
        VideosAdapter.VideosAdapterListener, ReviewsAdapter.ReviewsCallback {
    //Bind views with ButterKnife
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
    @BindView(R.id.linear_reviews)
    LinearLayout mReviewsLinearLayout;
    @BindView(R.id.linear_trailers)
    LinearLayout mTrailersLinearLayout;
    @BindView(R.id.favorite_image_view)
    ImageView mFavoriteIv;

    //The film ID
    private int id;
    //The film URI
    private Uri mUri;

    //The film projection to query into the database
    public static final String[] FILM_PROJECTION = {
            FilmsContract.FilmEntry.TITLE,
            FilmsContract.FilmEntry.VOTE_AVERAGE,
            FilmsContract.FilmEntry.MOVIE_POSTER_PATH,
            FilmsContract.FilmEntry.SYNOPSIS,
            FilmsContract.FilmEntry.IS_FAVORITE,
            FilmsContract.FilmEntry.RELEASE_DATE
    };

    //The index of the projection
    public static final int INDEX_FILM_TITLE = 0;
    public static final int INDEX_FILM_VOTE_AVERAGE = 1;
    public static final int INDEX_FILM_POSTER_PATH = 2;
    public static final int INDEX_FILM_SYNOPSIS = 3;
    public static final int INDEX_FILM_IS_FAVORITE = 4;
    public static final int INDEX_FILM_RELEASE_DATE = 5;

    //The projection to query the Reviews information
    public static final String[] REVIEW_PROJECTION = {
            FilmsContract.ReviewEntry.AUTHOR,
            FilmsContract.ReviewEntry.REVIEW
    };

    //Index of the Reviews projection
    public static final int INDEX_REVIEW_AUTHOR = 0;
    public static final int INDEX_REVIEW_REVIEW = 1;

    //Projection to query the videos in the database
    public static final String[] VIDEOS_PROJECTION = {
            FilmsContract.VideosEntry.NAME,
            FilmsContract.VideosEntry.KEY
    };

    //Index from the videos projection
    public static final int INDEX_VIDEO_NAME = 0;
    public static final int INDEX_VIDEO_KEY = 1;

    //Loaders ID
    private final int FILM_LOADER_ID = 366;
    private final int TRAILERS_LOADER_ID = 367;
    private final int REVIEWS_LOADER_ID = 368;
    private final int REVIEWS_FROM_SERVER_LOADER_ID = 369;
    private final int TRAILERS_FROM_SERVER_LOADER_ID = 370;

    //Adapters of the RecyclerViews
    private VideosAdapter mVideosAdapter;

    //Check if the loaders have already try to get information from the server
    private boolean fetchedReviews = false;
    private boolean fetchedTrailers = false;

    //Objects to register the IntentFilter if there is no Internet connection
    private IntentFilter mInternetStatusChangedFilter;
    private InternetBroadcasterReceiver mInternetBroadcasterReceiver;
    private boolean receiverRegister = false;

    private boolean isFavorite;

    private ReviewsAdapter mReviewsAdapter;

    public static String FILM_NAME_INTENT_KEY = "film";
    public static String FILM_REVIEW_INTENT_KEY = "review";


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
        mTrailersRecyclerView.setAdapter(mVideosAdapter);
        LinearLayoutManager layoutManagerReviews = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mReviewsRecyclerView.setLayoutManager(layoutManagerReviews);
        mReviewsAdapter = new ReviewsAdapter(this, this);
        mReviewsRecyclerView.setAdapter(mReviewsAdapter);
        LinearLayoutManager layoutManagerVideos = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mTrailersRecyclerView.setLayoutManager(layoutManagerVideos);
        mTrailersRecyclerView.addItemDecoration(new DividerItemDecoration(this, layoutManagerVideos.getOrientation()));

        mInternetStatusChangedFilter = new IntentFilter();
        mInternetStatusChangedFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        mInternetBroadcasterReceiver = new InternetBroadcasterReceiver();

        getSupportLoaderManager().initLoader(FILM_LOADER_ID, null, this);
        getSupportLoaderManager().initLoader(TRAILERS_LOADER_ID, null, this);
        getSupportLoaderManager().initLoader(REVIEWS_LOADER_ID, null, this);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);

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
                    if (NetworkUtils.checkInternetStatus(this) && fetchedReviews) {
                        fetchedReviews = true;
                        getSupportLoaderManager().restartLoader(REVIEWS_FROM_SERVER_LOADER_ID, null, this);
                    } else {
                        hydeReviews();
                        // If there is not connectivity register the Receiver
                        if (!receiverRegister) {
                            registerReceiver(mInternetBroadcasterReceiver, mInternetStatusChangedFilter);
                            receiverRegister = true;
                        }

                    }
                } else {
                    mReviewsAdapter.setAdapter(cursor);
                    showReviews();
                }
                break;
            }
            case TRAILERS_LOADER_ID: {
                Cursor cursor = (Cursor) o;
                if ((cursor == null || cursor.getCount() == 0)) {
                    if (NetworkUtils.checkInternetStatus(this) && fetchedTrailers) {
                        getSupportLoaderManager().restartLoader(TRAILERS_FROM_SERVER_LOADER_ID, null, this);
                        fetchedTrailers = true;
                    } else {
                        hydeTrailers();
                        // If there is not connectivity register the Receiver
                        if (!receiverRegister) {
                            registerReceiver(mInternetBroadcasterReceiver, mInternetStatusChangedFilter);
                            receiverRegister = true;
                        }
                    }
                } else {
                    mVideosAdapter.setAdapter(cursor);
                    showTrailers();
                }
                break;
            }
            case TRAILERS_FROM_SERVER_LOADER_ID: {
                ContentValues[] cv = (ContentValues[]) o;
                Uri uri = FilmsContract.VideosEntry.CONTENT_URI;
                if (cv != null) {
                    getContentResolver().bulkInsert(uri, cv);
                    if (receiverRegister) {
                        unregisterReceiver(mInternetBroadcasterReceiver);
                        receiverRegister = false;
                    }
                }
                getSupportLoaderManager().restartLoader(TRAILERS_LOADER_ID, null, this);
                break;
            }
            case REVIEWS_FROM_SERVER_LOADER_ID: {
                ContentValues[] cv = (ContentValues[]) o;
                Uri uri = FilmsContract.ReviewEntry.CONTENT_URI;
                if (cv != null) {
                    getContentResolver().bulkInsert(uri, cv);
                    if (receiverRegister) {
                        unregisterReceiver(mInternetBroadcasterReceiver);
                        receiverRegister = false;
                    }
                }
                getSupportLoaderManager().restartLoader(REVIEWS_LOADER_ID, null, this);
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

    @Override
    public void reviewOnClick(String filmName, String review) {
        Intent intent = new Intent(this, ReviewsActivity.class);
        intent.putExtra(FILM_NAME_INTENT_KEY, filmName);
        intent.putExtra(FILM_REVIEW_INTENT_KEY, review);
        startActivity(intent);
    }


    private static class VideosAsyncTask extends AsyncTaskLoader<ContentValues[]> {
        private final int mId;

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
        private final int mId;

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

        @Override
        protected void onStartLoading() {
            super.onStartLoading();
            forceLoad();
        }
    }

    /**
     * Auxiliary method to set the data into the views
     *
     * @param cursor The cursor which contains the information to display into the views
     */
    private void setData(Cursor cursor) {
        cursor.moveToFirst();
        mDateTv.setText(cursor.getString(INDEX_FILM_RELEASE_DATE));
        mOverviewTv.setText(cursor.getString(INDEX_FILM_SYNOPSIS));
        mTitleTv.setText(cursor.getString(INDEX_FILM_TITLE));
        mVoteAverageTv.setText(getString(R.string.value_over_10, cursor.getString(INDEX_FILM_VOTE_AVERAGE)));
        Glide.with(this).load(NetworkUtils.getImageURL(cursor.getString(INDEX_FILM_POSTER_PATH))).into(mPosterIv);
        isFavorite = cursor.getInt(INDEX_FILM_IS_FAVORITE) == 1;
        if (!isFavorite) {
            Glide.with(this).load(R.drawable.ic_favorite_border_black_24dp).into(mFavoriteIv);
        } else {
            Glide.with(this).load(R.drawable.ic_favorite_black_24dp).into(mFavoriteIv);
        }
        mFavoriteIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isFavorite) {
                    Glide.with(DetailActivity.this).load(R.drawable.ic_favorite_border_black_24dp).into(mFavoriteIv);
                    ContentValues cv = new ContentValues();
                    cv.put(FilmsContract.FilmEntry.IS_FAVORITE, 0);
                    getContentResolver().update(mUri, cv, null, null);
                } else {
                    Glide.with(DetailActivity.this).load(R.drawable.ic_favorite_black_24dp).into(mFavoriteIv);
                    ContentValues cv = new ContentValues();
                    cv.put(FilmsContract.FilmEntry.IS_FAVORITE, 1);
                    getContentResolver().update(mUri, cv, null, null);
                }
                MainActivity.needsUpdate = true;
            }
        });

    }

    /**
     * Auxiliary method to hyde the reviews if there is no information to display
     */
    private void hydeReviews() {
        mReviewsLinearLayout.setVisibility(View.GONE);
    }

    /**
     * Auxiliary method to hyde the information of the trailers if there is no information to display
     */
    private void hydeTrailers() {
        mTrailersLinearLayout.setVisibility(View.GONE);
    }

    /**
     * Auxiliary method to show the information
     */
    private void showReviews() {
        if (mReviewsLinearLayout.getVisibility() == View.GONE)
            mReviewsLinearLayout.setVisibility(View.VISIBLE);
    }

    /**
     * Auxiliary method to show the the videos
     */
    private void showTrailers() {
        if (mTrailersLinearLayout.getVisibility() == View.GONE)
            mTrailersLinearLayout.setVisibility(View.VISIBLE);
    }

    /*
     *Auxiliary class to restart the loaders when the Internet connection is restores
     */
    private class InternetBroadcasterReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            getSupportLoaderManager().restartLoader(REVIEWS_FROM_SERVER_LOADER_ID, null, DetailActivity.this);
            getSupportLoaderManager().restartLoader(TRAILERS_FROM_SERVER_LOADER_ID, null, DetailActivity.this);

        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (receiverRegister)
            unregisterReceiver(mInternetBroadcasterReceiver);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
