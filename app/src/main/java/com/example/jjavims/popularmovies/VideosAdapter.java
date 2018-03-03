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
 * Created by JjaviMS on 01/03/2018.
 *
 * @author JJaviMS
 */

public class VideosAdapter extends RecyclerView.Adapter<VideosAdapter.TrailerViewHolder> {

    private Cursor mVideos;
    private Context mContext;
    private VideosAdapterListener mListener;

    public VideosAdapter(Context context, VideosAdapterListener listener) {
        mContext = context;
        mListener = listener;
    }

    public interface VideosAdapterListener {
        void onClick(String key);
    }

    @Override
    public TrailerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutId = R.layout.videos_item;

        View view = LayoutInflater.from(mContext).inflate(layoutId, parent, false);
        view.setFocusable(false);
        return new TrailerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TrailerViewHolder holder, int position) {
        mVideos.moveToPosition(position);
        holder.mVideoNameTv.setText(mVideos.getString(DetailActivity.INDEX_VIDEO_NAME));
    }

    @Override
    public int getItemCount() {
        if (mVideos == null) return 0;
        else return mVideos.getCount();
    }

    public void setAdapter(Cursor cursor) {
        mVideos = cursor;
        notifyDataSetChanged();
    }

    class TrailerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.video_name)
        TextView mVideoNameTv;

        public TrailerViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mVideos.moveToPosition(getAdapterPosition());
            mListener.onClick(mVideos.getString(DetailActivity.INDEX_VIDEO_KEY));
        }
    }
}
