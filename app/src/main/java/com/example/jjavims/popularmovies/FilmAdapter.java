package com.example.jjavims.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.jjavims.popularmovies.data.MoviesPrefSync;
import com.example.jjavims.popularmovies.utils.NetworkUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by JjaviMS on 18/02/2018.
 *
 * @author JJaviMS
 */

public class FilmAdapter extends RecyclerView.Adapter<FilmAdapter.FilmViewHolder> {

    private Cursor mFilms;

    private final Context mContext;


    public interface FilmAdapterListener {
        void onClick(int id);

        void bottomReached(int position);
    }

    private final FilmAdapterListener mFilmAdapterListener;

    FilmAdapter(Context context, FilmAdapterListener filmAdapterListener) {
        mContext = context;
        mFilmAdapterListener = filmAdapterListener;
    }

    @Override
    public FilmViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutId = R.layout.poster_layout;

        View view = LayoutInflater.from(mContext).inflate(layoutId, parent, false);
        GridLayoutManager.LayoutParams lp = (GridLayoutManager.LayoutParams) view.getLayoutParams();
        lp.height = parent.getMeasuredHeight() / 2;
        view.setLayoutParams(lp);
        view.setFocusable(true);
        return new FilmViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FilmViewHolder holder, int position) {
        mFilms.moveToPosition(position);
        String object = mFilms.getString(MainActivity.INDEX_FILM_MOVIE_PATH);
        String url = NetworkUtils.getImageURL(object);
        Glide.with(mContext).load(url).into(holder.posterImageView);
        if ((!MoviesPrefSync.getSort(mContext).equals(mContext.getString(R.string.pref_sort_favorite)))) {
            if (position == mFilms.getCount() - 1 && !holder.hasCharged) {
                mFilmAdapterListener.bottomReached(position);
                holder.hasCharged = true;
            }
        }
    }

    @Override
    public int getItemCount() {
        if (mFilms == null) return 0;
        else return mFilms.getCount();
    }

    void setFilms(Cursor films) {
        mFilms = films;
        notifyDataSetChanged();
        Log.v("Cursor has chenged", String.valueOf(getItemCount()));
    }

    class FilmViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private boolean hasCharged; //This boolean is to detect if this View has already fetched new data
        @BindView(R.id.image_poster)
        ImageView posterImageView;

        FilmViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
            hasCharged = false;

        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            mFilms.moveToPosition(position);
            mFilmAdapterListener.onClick(mFilms.getInt(MainActivity.INDEX_FILM_MOVIE_ID));
        }

    }
}
