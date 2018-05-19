package com.example.udacity.populermoviesapp;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.udacity.populermoviesapp.data.FavoritesContract;
import com.example.udacity.populermoviesapp.model.TheMovie;
import com.example.udacity.populermoviesapp.model.TheReview;
import com.example.udacity.populermoviesapp.model.TheReviewList;
import com.example.udacity.populermoviesapp.model.TheVideo;
import com.example.udacity.populermoviesapp.model.TheVideoList;
import com.example.udacity.populermoviesapp.utils.ReviewsAdapter;
import com.example.udacity.populermoviesapp.utils.TheMovieInterface;
import com.example.udacity.populermoviesapp.utils.VideosAdapter;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DetailsActivity extends AppCompatActivity implements VideosAdapter.OnItemClicked{
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
    @BindView(R.id.trailers_label)
    TextView trailersLabel;
    @BindView(R.id.reviews_label)
    TextView reviewsLabel;
    @BindView(R.id.dividerTwo)
    View dividerItem;
    @BindView(R.id.video_layout)
    RecyclerView recyclerView;
    @BindView(R.id.review_layout)
    RecyclerView rvRecyclerView;
    @BindView(R.id.button)
    Button button;
    @BindView(R.id.details_scroll_view)
    ScrollView detailsScrollView;
    private static final String BASE_URL = "https://api.themoviedb.org/3/";
    private static final String YOUTUBE_BASE_URL = "https://www.youtube.com/watch?v=";
    private static final String TAG = DetailsActivity.class.getSimpleName();
    private TheMovie movieToShow;
    private Favorites movieFromDatabase;
    //Array List to save results from the movie database
    private ArrayList<TheVideo> theVideos = new ArrayList<>();
    private ArrayList<TheReview> theReviews = new ArrayList<>();
    private VideosAdapter videosAdapter;
    private ReviewsAdapter reviewsAdapter;
    private long primaryKey;
    private String userSelection;
    private int scrollIndex = 0;
    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ButterKnife.bind(this);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //videosAdapter = new VideosAdapter(theVideos);
        //Get user selected string from shared preferences
        Intent intent = getIntent();
        userSelection = intent.getStringExtra("selection");
        if(userSelection.equalsIgnoreCase(getString(R.string.pref_user_favorites))){
            primaryKey = intent.getIntExtra("favorite", 0);
            movieFromDatabase = getMovieFromDatabase(primaryKey);
            populateUI();
            button.setText(getString(R.string.remove_favorite_button));
            button.setTag(1);
        } else {
            videosAdapter = new VideosAdapter(theVideos);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(videosAdapter);

            reviewsAdapter = new ReviewsAdapter(theReviews);
            RecyclerView.LayoutManager rvLayoutManager = new LinearLayoutManager(this);
            rvRecyclerView.setLayoutManager(rvLayoutManager);
            rvRecyclerView.setItemAnimator(new DefaultItemAnimator());
            rvRecyclerView.setAdapter(reviewsAdapter);
            //recyclerView.setAdapter(videosAdapter);
            //getting intent values from MainActivity
            movieToShow = intent.getParcelableExtra("movie");
            loadVideos(movieToShow.getId());
            //videosAdapter.setOnClick(this);
            loadReviews(movieToShow.getId());
            //Setting texts on the details view for a selected movie
            overviewText.setText(movieToShow.getOverview());
            releaseDateText.setText(movieToShow.getReleaseDate());
            //loading images in to image view with picasso
            Picasso.get()
                    .load(movieToShow.getPosterPath())
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.error)
                    .into(posterImage);
            originalTitle.setText(movieToShow.getOriginalTitle());
            String vote = Float.toString(movieToShow.getVoteAvarage());
            voteAvarageText.setText(vote);
            if(!checkMovieIfFavorite()) {
                button.setText(getString(R.string.favorite_button));
                button.setTag(2);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if(userSelection.equalsIgnoreCase(getString(R.string.pref_user_favorites))){
            outState.putParcelable("database", movieFromDatabase);
        } else {
            outState.putParcelable("internet", movieToShow);
            outState.putParcelableArrayList("videos", theVideos);
            outState.putParcelableArrayList("reviews", theReviews);
            outState.putIntArray("SCROLL_POSITION",
                    new int[]{ detailsScrollView.getScrollX(), detailsScrollView.getScrollY()});
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if(savedInstanceState != null){
            if(userSelection.equalsIgnoreCase(getString(R.string.pref_user_favorites))){
                movieFromDatabase = savedInstanceState.getParcelable("database");
            } else {
                movieToShow = savedInstanceState.getParcelable("internet");
                theVideos = savedInstanceState.getParcelableArrayList("videos");
                theReviews = savedInstanceState.getParcelableArrayList("reviews");
            }
            handleScreenRotationChange();
            //https://asishinwp.wordpress.com/2013/04/15/save-scrollview-position-resume-scrollview-from-that-position/
            final int[] position = savedInstanceState.getIntArray("SCROLL_POSITION");
            if(position != null)
                detailsScrollView.post(new Runnable() {
                    public void run() {
                        detailsScrollView.scrollTo(position[0], position[1]);
                    }
                });
        }
    }

    public void handleScreenRotationChange(){
        //Log.d(TAG, "onRestoreInstanceState called!");
        if(movieFromDatabase != null){
            populateUI();
        } else {
            overviewText.setText(movieToShow.getOverview());
            releaseDateText.setText(movieToShow.getReleaseDate());
            //loading images in to image view with picasso
            Picasso.get()
                    .load(movieToShow.getPosterPath())
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.error)
                    .into(posterImage);
            originalTitle.setText(movieToShow.getOriginalTitle());
            String vote = Float.toString(movieToShow.getVoteAvarage());
            voteAvarageText.setText(vote);

            if(theVideos.isEmpty()){//if no video link returned turn visibility off
                trailersLabel.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
                dividerItem.setVisibility(View.GONE);
            } else {
                videosAdapter = new VideosAdapter(theVideos);
                recyclerView.setAdapter(videosAdapter);
                //videosAdapter.notifyDataSetChanged();
            }

            if(theReviews.isEmpty()){//if no reviews retrieved set visibility off
                reviewsLabel.setVisibility(View.GONE);
                rvRecyclerView.setVisibility(View.GONE);
            } else {
                reviewsAdapter = new ReviewsAdapter(theReviews);
                rvRecyclerView.setAdapter(reviewsAdapter);
                //reviewsAdapter.notifyDataSetChanged();
            }
        }
    }

    /*@Override
    protected void onResume() {
        super.onResume();
        if(movieFromDatabase != null){
            populateUI();
        } else {
            overviewText.setText(movieToShow.getOverview());
            releaseDateText.setText(movieToShow.getReleaseDate());
            //loading images in to image view with picasso
            Picasso.get()
                    .load(movieToShow.getPosterPath())
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.error)
                    .into(posterImage);
            originalTitle.setText(movieToShow.getOriginalTitle());
            String vote = Float.toString(movieToShow.getVoteAvarage());
            voteAvarageText.setText(vote);

            if(theVideos.isEmpty()){//if no video link returned turn visibility off
                trailersLabel.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
                dividerItem.setVisibility(View.GONE);
            } else {
                videosAdapter = new VideosAdapter(theVideos);
                recyclerView.setAdapter(videosAdapter);
                //videosAdapter.notifyDataSetChanged();
            }

            if(theReviews.isEmpty()){//if no reviews retrieved set visibility off
                reviewsLabel.setVisibility(View.GONE);
                rvRecyclerView.setVisibility(View.GONE);
            } else {
                reviewsAdapter = new ReviewsAdapter(theReviews);
                rvRecyclerView.setAdapter(reviewsAdapter);
                //reviewsAdapter.notifyDataSetChanged();
            }
        }
    }*/

    //method that retrieves favorite movie from database by given movie id in other word its primary key in database.
    public Favorites getMovieFromDatabase(long movieId){
        Cursor c = getContentResolver().query(FavoritesContract.FavoritesEntry.buildFavoritesUri(movieId), null, null, null, null);
        if(c != null) {
            c.moveToFirst();
            String id = c.getString(c.getColumnIndex(FavoritesContract.FavoritesEntry._ID));
            String title = c.getString(c.getColumnIndex(FavoritesContract.FavoritesEntry.COLUMN_MOVIE_TITLE));
            String rating = c.getString(c.getColumnIndex(FavoritesContract.FavoritesEntry.COLUMN_RATING));
            String releaseDate = c.getString(c.getColumnIndex(FavoritesContract.FavoritesEntry.COLUMN_RELEASE_DATE));
            String synopsis = c.getString(c.getColumnIndex(FavoritesContract.FavoritesEntry.COLUMN_SYNOPSIS));
            byte[] b = c.getBlob(2);
            Bitmap bImage = BitmapFactory.decodeByteArray(b, 0, b.length);
            Favorites favorites = new Favorites(id, title, bImage, releaseDate, rating, synopsis);
            c.close();
            return favorites;
        }

        return null;
    }

    public void populateUI(){
        overviewText.setText(movieFromDatabase.getSynopsis());
        releaseDateText.setText(movieFromDatabase.getReleaseDate());
        originalTitle.setText(movieFromDatabase.getTitle());
        voteAvarageText.setText(movieFromDatabase.getRating());
        //Image poster = (Image) movieFromDatabase.getImage();
        posterImage.setImageBitmap(movieFromDatabase.getImage());
        Log.d(TAG, "image name : " + movieFromDatabase.getImage().toString());
        /*Picasso.get()
                .load(new File(movieFromDatabase.getImage()+ ".jpeg"))
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.error)
                .into(posterImage);*/
        //since database and favorite list is offline usage and no movies or reviews saved to the database
        //so below views no longer needed.
        trailersLabel.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
        dividerItem.setVisibility(View.GONE);
        reviewsLabel.setVisibility(View.GONE);
        rvRecyclerView.setVisibility(View.GONE);
    }
    @SuppressWarnings("ConstantConditions")
    //method that make query to database and if movie name exist in database change button text and tag.
    public boolean checkMovieIfFavorite(){
        Cursor c = getContentResolver().query(FavoritesContract.FavoritesEntry.CONTENT_URI, null, null, null, null);
        if(c.moveToFirst()) {
            do {
                String movieTitle = c.getString(c.getColumnIndex(FavoritesContract.FavoritesEntry.COLUMN_MOVIE_TITLE));
                if(movieTitle.equalsIgnoreCase(originalTitle.getText().toString())){//movie already exist in the database
                    button.setText(getString(R.string.remove_favorite_button));
                    button.setTag(1);
                    c.close();
                    return true;
                }
            } while (c.moveToNext());
        }
        c.close();
        return false;
    }
    //Make as Favorite button click method
    public void addToFavorites(View view){
        Object delete = 1;
        if(view.getTag() == delete){
            deleteFavoritesEntry(primaryKey);
        } else {
            byte[] imageToDB = getBytes();
            String userVote = String.valueOf(movieToShow.getVoteAvarage());
            ContentValues contentValues = new ContentValues();
            contentValues.put(FavoritesContract.FavoritesEntry.COLUMN_MOVIE_TITLE, movieToShow.getOriginalTitle());
            contentValues.put(FavoritesContract.FavoritesEntry.COLUMN_IMAGE, imageToDB);
            contentValues.put(FavoritesContract.FavoritesEntry.COLUMN_RELEASE_DATE, movieToShow.getReleaseDate());
            contentValues.put(FavoritesContract.FavoritesEntry.COLUMN_RATING, userVote);
            contentValues.put(FavoritesContract.FavoritesEntry.COLUMN_SYNOPSIS, movieToShow.getOverview());
            getContentResolver().insert(FavoritesContract.FavoritesEntry.CONTENT_URI, contentValues);
            String message = originalTitle.getText() + " " + getString(R.string.movie_added_message);
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            //after adding movie to the database change text of the button also usage of the button
            button.setText(getString(R.string.remove_favorite_button));
            button.setTag(1);
        }
    }
    //delete from database movie entry
    public void deleteFavoritesEntry(long movieId){
        int i = getContentResolver().delete(FavoritesContract.FavoritesEntry.buildFavoritesUri(movieId),null,null);
        String message = originalTitle.getText() + " " + getString(R.string.movie_removed_message);
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        if(i >= 1){//if row removed from database successfully
            button.setText(getString(R.string.favorite_button));
            button.setTag(2);
        }
    }
    //convert image to byte array to save database
    //to retrieve byte[] image = cursor.getBlob(column index);
    //https://stackoverflow.com/questions/4715044/android-how-to-convert-whole-imageview-to-bitmap
    public byte[] getBytes(){
        byte[] imageByteArray = new byte[0];
        try{
            //URL imageUrl = new URL(movieToShow.getPosterPath());
            posterImage.setDrawingCacheEnabled(true);
            posterImage.buildDrawingCache(true);
            Bitmap bitmap = Bitmap.createBitmap(posterImage.getDrawingCache());
            //Bitmap originalSize = getResizedBitmap(bitmap, R.dimen.original_image_width, R.dimen.original_image_height);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, stream);
            imageByteArray = stream.toByteArray();
            posterImage.setDrawingCacheEnabled(false);
            stream.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imageByteArray;
    }
    //trying to reduce bitmap image size with width and height values
    public Bitmap getResizedBitmap(Bitmap image, int bitmapWidth, int bitmapHeight) {
        return Bitmap.createScaledBitmap(image, bitmapWidth, bitmapHeight, true);
    }
    /*
    @SuppressWarnings("ConstantConditions")
    public void retrieveDBFavorites(){
        Cursor c = getContentResolver().query(FavoritesContract.FavoritesEntry.CONTENT_URI, null, null, null, null);

        if(c.moveToFirst()){
            do{
                Toast.makeText(this,
                        c.getString(c.getColumnIndex(FavoritesContract.FavoritesEntry._ID))+
                ", " + c.getString(c.getColumnIndex(FavoritesContract.FavoritesEntry.COLUMN_MOVIE_TITLE))+
                ", " + c.getString(c.getColumnIndex(FavoritesContract.FavoritesEntry.COLUMN_RATING))+
                ", " + c.getString(c.getColumnIndex(FavoritesContract.FavoritesEntry.COLUMN_RELEASE_DATE))+
                ", " + c.getString(c.getColumnIndex(FavoritesContract.FavoritesEntry.COLUMN_SYNOPSIS)), Toast.LENGTH_LONG).show();
                byte[] b = c.getBlob(2);
                Bitmap bm = BitmapFactory.decodeByteArray(b, 0, b.length);
                posterImage.setImageBitmap(bm);
            } while (c.moveToNext());
        }
        c.close();
    }*/
    //Method that retrieves video links for corresponding movie.
    public void loadVideos(int movieId){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        //uses methods from created the movie interface @GET path
        TheMovieInterface theMovieInterface = retrofit.create(TheMovieInterface.class);
        Call<TheVideoList> call;
        call = theMovieInterface.getVideos(movieId, getString(R.string.api_key));

        call.enqueue(new Callback<TheVideoList>() {
            @Override
            public void onResponse(Call<TheVideoList> call, Response<TheVideoList> response) {
                theVideos = response.body().getResults();
                //videosAdapter = new VideosAdapter(theVideos);
                //recyclerView.setAdapter(videosAdapter);
                if(theVideos.isEmpty()){//if no video link returned turn visibility off
                    trailersLabel.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.GONE);
                    dividerItem.setVisibility(View.GONE);
                } else {
                    videosAdapter = new VideosAdapter(theVideos);
                    recyclerView.setAdapter(videosAdapter);
                    //videosAdapter.notifyDataSetChanged();
                }
                //videosAdapter.notifyDataSetChanged();
                //recyclerView.setAdapter(videosAdapter);
                /*//find type Trailer and key for youtube videos
                for(TheVideo t: theVideos){
                    if(t.getType().equalsIgnoreCase("trailer") || t.getType().equalsIgnoreCase("teaser")){

                    }
                }*/
                //Log.d(TAG, theVideos.toString());
            }

            @Override
            public void onFailure(Call<TheVideoList> call, Throwable t) {
                Log.d(TAG, t.toString());
            }
        });
    }

    @Override
    public void onItemClick(String key) {
        // The onClick implementation of the RecyclerView item click
        // already handled in onBindView method.
    }

    //Method that retrieves reviews for corresponding movie.
    public void loadReviews(int movieId){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        //uses methods from created the movie interface @GET path
        TheMovieInterface theMovieInterface = retrofit.create(TheMovieInterface.class);
        Call<TheReviewList> call;
        call = theMovieInterface.getReviews(movieId, getString(R.string.api_key));

        call.enqueue(new Callback<TheReviewList>() {
            @Override
            public void onResponse(Call<TheReviewList> call, Response<TheReviewList> response) {
                theReviews = response.body().getResults();
                //reviewsAdapter.notifyDataSetChanged();
                //reviewsAdapter = new ReviewsAdapter(theReviews);
                //rvRecyclerView.setAdapter(reviewsAdapter);
                if(theReviews.isEmpty()){//if no reviews retrieved set visibility off
                    reviewsLabel.setVisibility(View.GONE);
                    rvRecyclerView.setVisibility(View.GONE);
                } else {
                    reviewsAdapter = new ReviewsAdapter(theReviews);
                    rvRecyclerView.setAdapter(reviewsAdapter);
                    //reviewsAdapter.notifyDataSetChanged();
                }
                //Log.d(TAG, theReviews.toString());
            }

            @Override
            public void onFailure(Call<TheReviewList> call, Throwable t) {
                Log.d(TAG, t.toString());
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home){
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
