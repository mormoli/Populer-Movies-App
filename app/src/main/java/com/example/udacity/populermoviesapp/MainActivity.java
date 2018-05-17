package com.example.udacity.populermoviesapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.v4.graphics.BitmapCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.example.udacity.populermoviesapp.data.FavoritesContract;
import com.example.udacity.populermoviesapp.model.TheMovie;
import com.example.udacity.populermoviesapp.model.TheMovieList;
import com.example.udacity.populermoviesapp.utils.CustomArrayAdapter;
import com.example.udacity.populermoviesapp.utils.FavoritesAdapter;
import com.example.udacity.populermoviesapp.utils.TheMovieInterface;
import com.facebook.stetho.Stetho;
import com.facebook.stetho.okhttp3.StethoInterceptor;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements
        SharedPreferences.OnSharedPreferenceChangeListener{
    private static final String TAG = MainActivity.class.getSimpleName();

    private static final String BASE_URL = "https://api.themoviedb.org/3/";
    //Array List to save results from the movie database
    private ArrayList<TheMovie> theMovies = new ArrayList<>();
    //Array list to retrieve results from user favorite movie list.
    ArrayList<Favorites> theFavorites = new ArrayList<>();
    //Array List to save poster path URL's
    private ArrayList<String> imageLinks = new ArrayList<>();
    //shared preferences user selection string
    private String userSelection;
    //grid view initialization
    @BindView(R.id.grid_view_tv)
    GridView gridView;

    //private static boolean PREFERENCES_HAVE_BEEN_UPDATED = false;
    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Stetho is a tool created by facebook to view your database in chrome inspect.
        // The code below integrates Stetho into your app. More information here:
        // http://facebook.github.io/stetho/
        Stetho.initializeWithDefaults(this);
        new OkHttpClient.Builder()
                .addNetworkInterceptor(new StethoInterceptor())
                .build();
        //gridView = findViewById(R.id.grid_view_tv);
        ButterKnife.bind(this);
        //default value in shared preferences
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        final String defaultValue = sharedPreferences.getString("userKey", getString(R.string.default_movie_sort_order));
        if(userSelection == null)
            userSelection = defaultValue;
        if(savedInstanceState != null){
            //Toast.makeText(this, userSelection, Toast.LENGTH_LONG).show();
            if(userSelection.equalsIgnoreCase("favorites")){
                if(!theFavorites.isEmpty()) theFavorites.clear();
                retrieveDBFavorites();
            } else {
                populateUI();
            }
        }
        //no internet connection has been found.
        if(!isNetworkConnected()){
            showMessageOnError();//showing message to the user
        } else {//programs saved instance state always null couldn't locate the solution...
            if(savedInstanceState == null) { //if program first time running.
                //initialization of Array lists results from movie database and image URL's
                //Log.d(TAG, defaultValue);
                //initConnection(getString(R.string.default_movie_sort_order));
                if(defaultValue.equalsIgnoreCase(getString(R.string.pref_user_favorites))){
                    if(!theFavorites.isEmpty()) theFavorites.clear();
                    retrieveDBFavorites();
                } else
                    initConnection(defaultValue);
            }
        }
        //Open details activity by clicking image
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                //int imageId = view.getId();
                if(userSelection.equalsIgnoreCase(getString(R.string.pref_user_favorites)) && !theFavorites.isEmpty()){
                    Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
                    //int bitmapByteCount = BitmapCompat.getAllocationByteCount(theFavorites.get(position).getImage());
                    //int movieId = Integer.valueOf(theFavorites.get(position).getId());
                    //getContentResolver().delete(FavoritesContract.FavoritesEntry.buildFavoritesUri(movieId),null,null);
                    //Log.d(TAG, "Array size: "+theFavorites.size());
                    //Log.d(TAG, "item position: "+position);
                    //Log.d(TAG, "image size: "+ bitmapByteCount);
                    intent.putExtra("selection" , userSelection);
                    //instead of passing data with bitmap image etc sending only primary key attribute to other activities to make query again
                    //Passing bitmap image data with intent causing Security Exception because of its size and heap size allocation.
                    int movieId = Integer.parseInt(theFavorites.get(position).getId());
                    Log.d(TAG, "primary key: " + movieId);
                    intent.putExtra("favorite", movieId);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
                    intent.putExtra("selection" , userSelection);
                    intent.putExtra("movie", theMovies.get(position));
                    //Log.d(TAG, String.valueOf(position));
                    startActivity(intent);
                }
            }
        });
        /*
         * Register MainActivity as an OnPreferenceChangedListener to receive a callback when a
         * SharedPreference has changed. Please note that we must unregister MainActivity as an
         * OnSharedPreferenceChanged listener in onDestroy to avoid any memory leaks.
         */
        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);
    }
    /*
    * Method that initialize connection to the server the movies database and
    * retrieve data using retrofit2 and retrofit2 gson converter using user api key from strings.xml*/
    private void initConnection(String sortMovieCategory){
        //clear arrays before query.
        if(!theMovies.isEmpty()) theMovies.clear();
        if(!imageLinks.isEmpty()) imageLinks.clear();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        //uses methods from created the movie interface @GET path
        TheMovieInterface theMovieInterface = retrofit.create(TheMovieInterface.class);
        Call<TheMovieList> call;
        //checks sorted method from settings and query
        if(sortMovieCategory.equals(getString(R.string.default_movie_sort_order))) {
            call = theMovieInterface.getPopularMovies(getString(R.string.api_key));
        } else {
            call = theMovieInterface.getTopMovies(getString(R.string.api_key));
        }
        call.enqueue(new Callback<TheMovieList>() {
            //on successful initialization of call on reponse method runs
            @Override
            public void onResponse(Call<TheMovieList> call, Response<TheMovieList> response) {
                theMovies = response.body().getResults();
                for(TheMovie t : theMovies){
                    if(t.getPosterPath().isEmpty() || t.getPosterPath() == null) {
                        imageLinks.add(t.getBackdropPath());
                        //Log.d(TAG, t.getBackdropPath());
                    } else {
                        imageLinks.add(t.getPosterPath());
                        //Log.d(TAG, t.getPosterPath());
                    }
                }
                populateUI();
            }

            @Override
            public void onFailure(Call<TheMovieList> call, Throwable t) {
                Log.d(TAG, t.toString());
            }
        });
    }
    /*
    * method that populates user interface and set adapter*/
    private void populateUI(){
        if(imageLinks.isEmpty()) initConnection(userSelection);
        CustomArrayAdapter customArrayAdapter = new CustomArrayAdapter(this, imageLinks);
        gridView.setAdapter(customArrayAdapter);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList("imageLinks", imageLinks);
        outState.putParcelableArrayList("theMovies", theMovies);
        //outState.putParcelableArrayList("theFavorites", theFavorites);
        outState.putString("userSelection", userSelection);
    }
    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        imageLinks = savedInstanceState.getStringArrayList("imageLinks");
        theMovies = savedInstanceState.getParcelableArrayList("theMovies");
        //theFavorites = savedInstanceState.getParcelableArrayList("theFavorites");
        userSelection = savedInstanceState.getString("userSelection");
        if(!theFavorites.isEmpty()) theFavorites.clear();
        retrieveDBFavorites();
    }

    //onDestroy and unregister MainActivity as a SharedPreferenceChangedListener
    @Override
    protected void onDestroy() {
        super.onDestroy();

        /* Unregister MainActivity as an OnPreferenceChangedListener to avoid any memory leaks. */
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }

   /* @Override
    protected void onPause() {
        super.onPause();
        PreferenceManager.getDefaultSharedPreferences(this)
                .edit()
                .clear()
                .apply();
        PreferenceManager.setDefaultValues(this, R.xml.pref_general, true);
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /* Use AppCompatActivity's method getMenuInflater to get a handle on the menu inflater */
        MenuInflater inflater = getMenuInflater();
        /* Use the inflater's inflate method to inflate our menu layout to this menu */
        inflater.inflate(R.menu.settings, menu);
        /* Return true so that the menu is displayed in the Toolbar */
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent startSettingsActivity = new Intent(this, SettingsActivity.class);
            startActivity(startSettingsActivity);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    /*
    *   Toast
    *   show related message to the user.
    * */
    private void showMessageOnError() {
        //finish();
        Toast.makeText(this, R.string.no_connection_error, Toast.LENGTH_SHORT).show();
    }
    /*
    * Method that checks all internet providers
    *
    * Android getAllNetworkInfo() is Deprecated.
    * @see "https://stackoverflow.com/questions/32242384/android-getallnetworkinfo-is-deprecated-what-is-the-alternative"
    * @return internet connection status.
    * */
    @SuppressWarnings("ConstantConditions") //may produce null exception on method !
    private boolean isNetworkConnected(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            Network[] networks = connectivityManager.getAllNetworks();
            NetworkInfo networkInfo;
            for(Network network : networks){
                networkInfo = connectivityManager.getNetworkInfo(network);
                if(networkInfo.getState().equals(NetworkInfo.State.CONNECTED)){
                    return true;
                }
            }
        } else {
            if(connectivityManager != null){
                //noinspection deprecation
                NetworkInfo[] networkInfos = connectivityManager.getAllNetworkInfo();
                if(networkInfos != null){
                    for(NetworkInfo networkInfo : networkInfos){
                        if(networkInfo.getState() == NetworkInfo.State.CONNECTED){
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }
    //method that retrieves favorite list from database and sets view adapter for the list
    public void retrieveDBFavorites(){
        Cursor c = getContentResolver().query(FavoritesContract.FavoritesEntry.CONTENT_URI, null, null, null, null);

        if( c != null ){
            if(c.moveToFirst()){
                do{
                    String id = c.getString(c.getColumnIndex(FavoritesContract.FavoritesEntry._ID));
                    String title = c.getString(c.getColumnIndex(FavoritesContract.FavoritesEntry.COLUMN_MOVIE_TITLE));
                    String rating = c.getString(c.getColumnIndex(FavoritesContract.FavoritesEntry.COLUMN_RATING));
                    String releaseDate = c.getString(c.getColumnIndex(FavoritesContract.FavoritesEntry.COLUMN_RELEASE_DATE));
                    String synopsis = c.getString(c.getColumnIndex(FavoritesContract.FavoritesEntry.COLUMN_SYNOPSIS));
                    byte[] b = c.getBlob(2);
                    Bitmap bImage = BitmapFactory.decodeByteArray(b, 0, b.length);
                    theFavorites.add(new Favorites(id, title, bImage, releaseDate, rating, synopsis));
                    //posterImage.setImageBitmap(bm);
                } while (c.moveToNext());
                FavoritesAdapter favoritesAdapter = new FavoritesAdapter(this, theFavorites);
                gridView.setAdapter(favoritesAdapter);
                c.close();
            }
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        //PREFERENCES_HAVE_BEEN_UPDATED = true;
        //although in preference screen only list preference exists, this is for future use.
        if(s.equals(getString(R.string.pref_user_key))){
            userSelection = sharedPreferences.getString("userKey", getString(R.string.default_movie_sort_order));
            if(userSelection.equalsIgnoreCase(getString(R.string.pref_user_favorites))) {
                //theMovies.clear();
                //imageLinks.clear();
                if(!theFavorites.isEmpty()) theFavorites.clear();//clear before query
                retrieveDBFavorites();
                if(theFavorites.isEmpty()) {//user choose Favorite in the settigns menu but favorite list is empty
                    //show message to the user !
                    Toast.makeText(this, getString(R.string.empty_favorite_list_error), Toast.LENGTH_LONG).show();
                    //set settings to default value defined in pref_general.xml file.
                    PreferenceManager.getDefaultSharedPreferences(this)
                            .edit()
                            .clear()
                            .apply();
                    PreferenceManager.setDefaultValues(this, R.xml.pref_general, true);

                }
                return;
            }
            //Log.d(TAG, userSelection);
            //check internet connection status before query
            if(!isNetworkConnected()){
                showMessageOnError();
                return;
            } else {
                //theFavorites.clear();
                //initialization new query from selected value in settings
                initConnection(userSelection);
            }
        }
    }
}
