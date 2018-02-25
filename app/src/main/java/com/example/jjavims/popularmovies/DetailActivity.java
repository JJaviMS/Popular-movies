package com.example.jjavims.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;


public class DetailActivity extends AppCompatActivity {
    @BindView(R.id.poster_detail)
    ImageView posterIv;
    @BindView(R.id.detail_text_view)
    TextView infoTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);
        Intent intentWhichStartedActivity = getIntent();
        String rawJSON=null;
        if (intentWhichStartedActivity != null) {
            Bundle extras = intentWhichStartedActivity.getExtras();
            if (extras != null) {
                if (extras.containsKey(MainActivity.INTENT_RAW_DATA_NAME))
                    rawJSON = intentWhichStartedActivity.getStringExtra(MainActivity.INTENT_RAW_DATA_NAME);
            }

        }
        if (rawJSON==null)
            throw new NullPointerException("The rawJSON can not be null");
        try {
            JSONObject jsonObject = new JSONObject(rawJSON);
            //infoTv.setText(JSONUtils.getFilmInformation(jsonObject));
            //Glide.with(this).load(JSONUtils.getImageURL(jsonObject)).into(posterIv);
            //TODO Aqui
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
