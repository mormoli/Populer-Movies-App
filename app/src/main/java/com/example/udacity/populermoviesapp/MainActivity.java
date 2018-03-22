package com.example.udacity.populermoviesapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
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

import com.example.udacity.populermoviesapp.model.TheMovie;
import com.example.udacity.populermoviesapp.model.TheMovieList;
import com.example.udacity.populermoviesapp.utils.CustomArrayAdapter;
import com.example.udacity.populermoviesapp.utils.TheMovieInterface;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
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
    private ArrayList<TheMovie> theMovies;
    //Array List to save poster path URL's
    private ArrayList<String> imageLinks = new ArrayList<>();
    //grid view initialization
    @BindView(R.id.grid_view_tv)
    GridView gridView;

    //private static boolean PREFERENCES_HAVE_BEEN_UPDATED = false;
    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //gridView = findViewById(R.id.grid_view_tv);
        ButterKnife.bind(this);
        //no internet connection has been found.
        if(!isNetworkConnected()){
            showMessageOnError();//showing message to the user
        } else {
            if(savedInstanceState == null) { //if program first time running.
                //initialization of Array lists results from movie database and image URL's
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
                String defaultValue = sharedPreferences.getString("userKey", "1");
                //Log.d(TAG, defaultValue);
                initConnection(defaultValue);
            }
        }
        //Open details activity by clicking image
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                //int imageId = view.getId();
                Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
                intent.putExtra("movie", theMovies.get(position));
                //Log.d(TAG, String.valueOf(position));
                startActivity(intent);
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
        //int imgDimension = 200;
        //dynamically settings image width and column size
        //DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        //float width = displayMetrics.widthPixels / displayMetrics.density;
        //int columns = (int) width/imgDimension;
        //imgDimension = (int) width/2;
        //Log.d(TAG," columns: " + columns);
        //gridView.setNumColumns(columns);
        CustomArrayAdapter customArrayAdapter = new CustomArrayAdapter(this, imageLinks, 200);
        gridView.setAdapter(customArrayAdapter);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList("imageLinks", imageLinks);
        outState.putParcelableArrayList("theMovies", theMovies);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        imageLinks = savedInstanceState.getStringArrayList("imageLinks");
        theMovies = savedInstanceState.getParcelableArrayList("theMovies");
        populateUI();
        //customArrayAdapter = new CustomArrayAdapter(this, imageLinks, 200);
        //gridView.setAdapter(customArrayAdapter);
    }

    //onDestroy and unregister MainActivity as a SharedPreferenceChangedListener
    @Override
    protected void onDestroy() {
        super.onDestroy();

        /* Unregister MainActivity as an OnPreferenceChangedListener to avoid any memory leaks. */
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }

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

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        //PREFERENCES_HAVE_BEEN_UPDATED = true;
        //although in preference screen only list preference exists, this is for future use.
        if(s.equals(getString(R.string.pref_user_key))){
            String userSelection = sharedPreferences.getString("userKey", "1");
            //Log.d(TAG, userSelection);
            //check internet connection status before query
            if(!isNetworkConnected()){
                showMessageOnError();
                return;
            } else {
                //clear arrays before query.
                theMovies.clear();
                imageLinks.clear();
                //initialization new query from selected value in settings
                initConnection(userSelection);
            }
        }
    }
}
