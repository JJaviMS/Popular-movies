package com.example.jjavims.popularmovies;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.jjavims.popularmovies.data.FilmsContract;

import butterknife.BindView;
import butterknife.ButterKnife;


public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks {
    @BindView(R.id.poster_detail)
    ImageView posterIv;
    @BindView(R.id.title_text_view)
    TextView titleTv;
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
    private final int REVIEWS_FROM_SERVER = 369;
    private final int TRAILERS_FROM_SERVER = 370;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);
        Intent intentWhichStartedActivity = getIntent();
        if (intentWhichStartedActivity != null) {
            Bundle extras = intentWhichStartedActivity.getExtras();
            if (extras != null) {
                if (extras.containsKey(MainActivity.FILM_URI)) {
                    mUri = intentWhichStartedActivity.getData();
                    if (mUri != null) {
                        id = Integer.valueOf(mUri.getLastPathSegment());
                    }
                }
            }

        }
        Glide.with(this).load("http://image.tmdb.org/t/p/w185/sM33SANp9z6rXW8Itn7NnG1GOEs.jpg").into(posterIv);
        if (mUri == null)
            throw new RuntimeException("Invalid URI");
        //TODO Here
    }

    @Override
    public Loader onCreateLoader(int i, Bundle bundle) {
        switch (i) {
            case FILM_LOADER_ID: {
                return new CursorLoader(this, mUri, FILM_PROJECTION, null, null, null);
            }
            case REVIEWS_LOADER_ID: {
                return new CursorLoader(this, mUri, REVIEW_PROJECTION, null, null, null);
            }
            case TRAILERS_LOADER_ID: {
                return new CursorLoader(this, mUri, VIDEOS_PROJECTION, null, null, null);
            }
        }
    }

    @Override
    public void onLoadFinished(Loader loader, Object o) {

    }

    @Override
    public void onLoaderReset(Loader loader) {

    }
}
