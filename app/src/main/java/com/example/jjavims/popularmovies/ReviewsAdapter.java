package com.example.jjavims.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by JjaviMS on 03/03/2018.
 *
 * @author JJaviMS
 */

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewViewHolder> {

    Context mContext;
    ReviewsCallback mReviewsCallback;
    private Cursor mReviews;

    public ReviewsAdapter(Context context, ReviewsCallback reviewsCallback) {
        mContext = context;
        mReviewsCallback = reviewsCallback;
    }

    public interface ReviewsCallback {
        void reviewOnClick(String author, String review);
    }

    @Override
    public ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutId = R.layout.review_item;

        View view = LayoutInflater.from(mContext).inflate(layoutId, parent, false);
        view.setFocusable(true);
        return new ReviewViewHolder(view);


    }

    @Override
    public void onBindViewHolder(ReviewViewHolder holder, int position) {
        mReviews.moveToPosition(position);
        holder.mAuthorTv.setText(mReviews.getString(DetailActivity.INDEX_REVIEW_AUTHOR));
    }

    public void setAdapter(Cursor cursor) {
        mReviews = cursor;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (mReviews == null) return 0;
        else return mReviews.getCount();
    }

    class ReviewViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.author_text_view)
        TextView mAuthorTv;

        public ReviewViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mReviews.moveToPosition(getAdapterPosition());
            mReviewsCallback.reviewOnClick(mReviews.getString(DetailActivity.INDEX_REVIEW_AUTHOR),
                    mReviews.getString(DetailActivity.INDEX_REVIEW_REVIEW));
        }
    }
}
