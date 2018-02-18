package com.example.jjavims.popularmovies;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.jjavims.popularmovies.utils.JSONUtils;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by JjaviMS on 18/02/2018.
 *
 * @author JJaviMS
 */

public class FilmAdapter extends RecyclerView.Adapter<FilmAdapter.FilmViewHolder> {

    private JSONObject [] mFilms;

    private final Context mContext;

    public interface FilmAdapterListener {
        void onClick (JSONObject jsonObject);
    }
    private final FilmAdapterListener mFilmAdapterListener;

    FilmAdapter(Context context, FilmAdapterListener filmAdapterListener){
        mContext = context;
        mFilmAdapterListener = filmAdapterListener;
    }

    @Override
    public FilmViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutId = R.layout.poster_layout;

        View view = LayoutInflater.from(mContext).inflate(layoutId,parent,false);
        GridLayoutManager.LayoutParams lp = (GridLayoutManager.LayoutParams) view.getLayoutParams();
        lp.height = parent.getMeasuredHeight()/2;
        view.setLayoutParams(lp);
        view.setFocusable(true);
        return new FilmViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FilmViewHolder holder, int position) {
        JSONObject object = mFilms[position];
        try {
            Glide.with(mContext).load(JSONUtils.getImageURL(object)).into(holder.posterImageView);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        if (mFilms==null) return 0;
        else return mFilms.length;
    }

    void setFilms(JSONObject[] films){
        mFilms = films;
        notifyDataSetChanged();
    }

    class FilmViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.image_poster)ImageView posterImageView;
        FilmViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            mFilmAdapterListener.onClick(mFilms[position]);
        }
    }
}
