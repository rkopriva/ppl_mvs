package com.udacity.koprivarudolf.popularmovies;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.GridView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Main activity displays scrollable list of popular movies posters. Data is loaded from the service.
 */
public class MainActivity extends AppCompatActivity {

    /**
     * Enum with list types (popular/top rated)
     */
    private enum MovieListType {
        POPULAR,
        TOPRATED
    }

    private static final String TAG = MainActivity.class.getSimpleName();
    //Constant used to save/restore list of movies
    private static final String PARCEL_DATA_MOVIES = "PARCEL_DATA_MOVIES";
    //Constant used to save/restore type of list
    private static final String PARCEL_DATA_LIST_TYPE = "PARCEL_DATA_LIST_TYPE";

    //Using retrofit to call the service and parse result
    private Retrofit retrofit;
    private IMovieDBService movieDBService;

    //The current list type (default is POPULAR)
    private MovieListType currentListType = MovieListType.POPULAR;

    //The adapter
    private PopularMovieAdapter popularMovieAdapter;

    //The grid view
    private GridView gridView;

    //The current loaded list of movies
    private ArrayList<MovieListResultObject> movieListResultObjects;

    /**
     * Create or return Retrofit service instance
     * @return Returns instance of IMovieDBService
     */
    private IMovieDBService getMovieDBService() {

        if (movieDBService == null) {
            //Read API key and check if it was configured
            String apiKey = BuildConfig.THEMOVIEDB_APIKEY;
            //Here I'm checking if reviewer configured the API key in bundle.gradle
            if (apiKey.equals("[YOUR API KEY]")) {
                throw new RuntimeException("Please setup the THEMOVIEDB_APIKEY in gradle.properties");
            }

            //Add interceptor to append api_key and locale for each request
            final String apiKeyValue = apiKey;
            OkHttpClient.Builder httpClient =
                    new OkHttpClient.Builder();
            httpClient.addInterceptor(new Interceptor() {
                @Override
                public okhttp3.Response intercept(Chain chain) throws IOException {
                    Request original = chain.request();
                    HttpUrl originalHttpUrl = original.url();

                    HttpUrl url = originalHttpUrl.newBuilder()
                            .addQueryParameter("api_key", apiKeyValue)
                            .addQueryParameter("language",
                                    String.format(Locale.US, "%s-%s",
                                            Locale.getDefault().getLanguage(),
                                            Locale.getDefault().getCountry()))
                            .build();

                    //Request customization: add request headers
                    Request.Builder requestBuilder = original.newBuilder()
                            .url(url);

                    Request request = requestBuilder.build();
                    return chain.proceed(request);
                }
            });

            retrofit = new Retrofit.Builder()
                    .baseUrl(Constants.MOVIE_DB_API)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient.build())
                    .build();

            //Remember the service in local variable
            movieDBService = retrofit.create(IMovieDBService.class);
        }
        return movieDBService;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        //Check if we have saved list of movies
        if(savedInstanceState != null && savedInstanceState.containsKey(PARCEL_DATA_MOVIES)) {
            movieListResultObjects = savedInstanceState.getParcelableArrayList(PARCEL_DATA_MOVIES);
        }
        else {
            movieListResultObjects = new ArrayList<>();
        }
        //Check last used list type from bundle data
        if(savedInstanceState != null && savedInstanceState.containsKey(PARCEL_DATA_LIST_TYPE)) {
            currentListType = (MovieListType)savedInstanceState.get(PARCEL_DATA_LIST_TYPE);
        }

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        popularMovieAdapter = new PopularMovieAdapter(this, movieListResultObjects);

        //We set title based on current list type
        setTitleForCurrentListType();

        //Get a reference to the ListView, and attach this adapter to it.
        gridView = (GridView) findViewById(R.id.popular_movies_grid);
        gridView.setAdapter(popularMovieAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long arg3) {
                MovieListResultObject movieListResultObject = (MovieListResultObject)adapter.getItemAtPosition(position);
                showMovieDetails(movieListResultObject);
            }
        });
        //if the movie list is empty (first start), we load it from the server
        if (movieListResultObjects.size() == 0) {
            loadMoviesForCurrentListType();
        }
    }

    /**
     * Launch DetailActivity with details of selected movie
     * @param movieListResultObject selected movie
     */
    private void showMovieDetails(MovieListResultObject movieListResultObject) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(DetailActivity.PARCEL_DATA_MOVIE_DETAILS, movieListResultObject);
        startActivity(intent);
    }

    /**
     * Load list of movies from the service
     */
    private void loadMoviesForCurrentListType() {
        Call<MoviesQueryResultObject> request = null;
        switch (currentListType) {
            case TOPRATED:
                request = getMovieDBService().getTopRatedMovies(1);
                break;
            case POPULAR:
                request = getMovieDBService().getPopularMovies(1);
                break;
        }

        try {
            request.enqueue(new Callback<MoviesQueryResultObject>() {
                @Override
                public void onResponse(Call<MoviesQueryResultObject> call, Response<MoviesQueryResultObject> response) {
                    if (response.code() == 200) {
                        movieListResultObjects.clear();
                        movieListResultObjects.addAll(response.body().getResults());
                        popularMovieAdapter.notifyDataSetChanged();
                        Log.d(TAG, "Movies loaded");
                    }
                    else {
                        Log.e(TAG, String.format(Locale.getDefault(), "Failed to load movies with response code %d", response.code()));
                        showLoadingError(R.string.error_reason_unknown);
                    }
                }

                @Override
                public void onFailure(Call<MoviesQueryResultObject> call, Throwable t) {
                    Log.e(TAG, "Failed to load movies with exception", t);
                    if (t instanceof IOException) {
                        //IOException stands for connection errors
                        showLoadingError(R.string.error_reason_connection);
                    }
                    else {
                        showLoadingError(R.string.error_reason_unknown);
                    }
                }
            });

        } catch (Exception e) {
            Log.e(TAG, "Failed to load movies with exception", e);
            showLoadingError(R.string.error_reason_unknown);
        }
    }

    /**
     * Show error and provide option to repeat the request
     * @param errorReason string resource id
     */
    private void showLoadingError(int errorReason) {
        AlertDialog.Builder builder =
            new AlertDialog.Builder(this)
                    .setTitle(errorReason)
                    .setMessage(R.string.error_loading_movies)
                    .setPositiveButton(R.string.retry, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            loadMoviesForCurrentListType();
                        }
                    })
                    .setNegativeButton(R.string.exit, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    });
        builder.show();
    }

    /**
     * Set title based on selected list type
     */
    private void setTitleForCurrentListType() {
        switch (currentListType) {
            case TOPRATED:
                setTitle(R.string.title_top_rated);
                break;
            case POPULAR:
                setTitle(R.string.title_popular);
                break;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //Save movie data, so it can restore when needed (orientation change)
        outState.putParcelableArrayList(PARCEL_DATA_MOVIES, movieListResultObjects);
        outState.putSerializable(PARCEL_DATA_LIST_TYPE, currentListType);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_popular) {
            //load or reload popular movies
            currentListType = MovieListType.POPULAR;
            setTitleForCurrentListType();
            loadMoviesForCurrentListType();
            return true;
        }
        else if (id == R.id.action_top_rated) {
            //load or reload top rated movies
            currentListType = MovieListType.TOPRATED;
            setTitleForCurrentListType();
            loadMoviesForCurrentListType();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
