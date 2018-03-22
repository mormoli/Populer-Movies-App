package com.example.udacity.populermoviesapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.udacity.populermoviesapp.model.TheMovie;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailsActivity extends AppCompatActivity {
    @BindView(R.id.original_title_label)
    TextView originalTitle;
    @BindView(R.id.poster_image_tv)
    ImageView posterImage;
    @BindView(R.id.vote_avarage_tv)
    TextView voteAvarageText;
    @BindView(R.id.release_date_tv)
    TextView releaseDateText;
    @BindView(R.id.overview_tv)
    TextView overviewText;
    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ButterKnife.bind(this);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getting intent values from MainActivity
        Intent intent = getIntent();
        TheMovie movieToShow = intent.getParcelableExtra("movie");
        //Setting texts on the details view for a selected movie
        overviewText.setText(movieToShow.getOverview());
        releaseDateText.setText(movieToShow.getReleaseDate());
        //loading images in to image view with picasso
        Glide.with(this)
                .load(movieToShow.getPosterPath())
                .into(posterImage);
        originalTitle.setText(movieToShow.getOriginalTitle());
        String vote = Float.toString(movieToShow.getVoteAvarage());
        voteAvarageText.setText(vote);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home){
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }
}
