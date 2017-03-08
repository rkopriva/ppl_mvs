package com.udacity.koprivarudolf.popularmovies;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.udacity.koprivarudolf.popularmovies.adapters.PopularMovieAdapter;
import com.udacity.koprivarudolf.popularmovies.api.IMovieDBService;
import com.udacity.koprivarudolf.popularmovies.api.MovieDBServiceFactory;
import com.udacity.koprivarudolf.popularmovies.models.MovieListResultModel;
import com.udacity.koprivarudolf.popularmovies.models.MoviesQueryResultModel;
import com.udacity.koprivarudolf.popularmovies.store.MoviesProvider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Main activity displays scrollable list of popular movies posters. Data is loaded from the service.
 */
public class MainActivity extends AppCompatActivity {

    /**
     * Enum with list types (popular/top rated)
     */
    private enum MovieListType {
        POPULAR,
        TOPRATED,
        FAVORITES
    }

    /**
     * We want to watch the Favorites provider.
     */
    class FavoritesObserver extends ContentObserver {
        public FavoritesObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            this.onChange(selfChange, null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            //If the current list type if FAVORITES, then we refresh the list
            if (currentListType == MovieListType.FAVORITES) {
                loadMoviesForCurrentListType();
            }
        }
    }

    private FavoritesObserver _observer = new FavoritesObserver(new Handler());

    private static final String TAG = MainActivity.class.getSimpleName();
    //Constant used to save/restore list of movies
    private static final String PARCEL_DATA_MOVIES = "PARCEL_DATA_MOVIES";
    //Constant used to save/restore type of list
    private static final String PARCEL_DATA_LIST_TYPE = "PARCEL_DATA_LIST_TYPE";

    private IMovieDBService _movieDBService;

    //The current list type (default is POPULAR)
    private MovieListType currentListType = MovieListType.POPULAR;

    //The adapter
    private PopularMovieAdapter popularMovieAdapter;

    //The grid view
    private GridView gridView;

    //The current loaded list of movies
    private ArrayList<MovieListResultModel> movieListResultModels;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        _movieDBService = MovieDBServiceFactory.getMovieDBService();

        //Check if we have saved list of movies
        if(savedInstanceState != null && savedInstanceState.containsKey(PARCEL_DATA_MOVIES)) {
            movieListResultModels = savedInstanceState.getParcelableArrayList(PARCEL_DATA_MOVIES);
        }
        else {
            movieListResultModels = new ArrayList<>();
        }
        //Check last used list type from bundle data
        if(savedInstanceState != null && savedInstanceState.containsKey(PARCEL_DATA_LIST_TYPE)) {
            currentListType = (MovieListType)savedInstanceState.get(PARCEL_DATA_LIST_TYPE);
        }

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        popularMovieAdapter = new PopularMovieAdapter(this, movieListResultModels);

        //We set title based on current list type
        setTitleForCurrentListType();

        //Get a reference to the ListView, and attach this adapter to it.
        gridView = (GridView) findViewById(R.id.popular_movies_grid);
        gridView.setAdapter(popularMovieAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long arg3) {
                MovieListResultModel movieListResultModel = (MovieListResultModel)adapter.getItemAtPosition(position);
                showMovieDetails(movieListResultModel);
            }
        });
        //if the movie list is empty (first start), we load it from the server
        if (movieListResultModels.size() == 0) {
            loadMoviesForCurrentListType();
        }

        //register provider observer, so we can refresh the list in case the movie was set/unset as favorite
        getContentResolver().registerContentObserver(MoviesProvider.Favorites.CONTENT_URI, true, _observer);
    }

    @Override
    public void onDestroy() {
        //unregister the provider observer
        getContentResolver().unregisterContentObserver(_observer);
        super.onDestroy();
    }

    /**
     * Launch DetailActivity with details of selected movie
     * @param movieListResultModel selected movie
     */
    private void showMovieDetails(MovieListResultModel movieListResultModel) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(DetailActivity.PARCEL_DATA_MOVIE_DETAILS, movieListResultModel);
        startActivity(intent);
    }

    /**
     * Load list of movies from the service
     */
    private void loadMoviesForCurrentListType() {
        Call<MoviesQueryResultModel> request = null;
        switch (currentListType) {
            case TOPRATED:
                request = _movieDBService.getTopRatedMovies(1);
                break;
            case POPULAR:
                request = _movieDBService.getPopularMovies(1);
                break;
            case FAVORITES:
                //For favorites we load the list from content provider
                Cursor favoriteMoviesCursor = getContentResolver().query(MoviesProvider.Favorites.CONTENT_URI, null, null, null, null);
                movieListResultModels.clear();
                while (favoriteMoviesCursor.moveToNext()) {
                    movieListResultModels.add(MovieListResultModel.fromCursor(favoriteMoviesCursor));
                }
                popularMovieAdapter.notifyDataSetChanged();
                if (movieListResultModels.size() == 0) {
                    Toast.makeText(this, R.string.no_favorite_movies, Toast.LENGTH_LONG).show();
                }
                return;
        }

        try {
            request.enqueue(new Callback<MoviesQueryResultModel>() {
                @Override
                public void onResponse(Call<MoviesQueryResultModel> call, Response<MoviesQueryResultModel> response) {
                    if (response.code() == 200) {
                        movieListResultModels.clear();
                        movieListResultModels.addAll(response.body().getResults());
                        popularMovieAdapter.notifyDataSetChanged();
                        Log.d(TAG, "Movies loaded");
                    }
                    else {
                        Log.e(TAG, String.format(Locale.getDefault(), "Failed to load movies with response code %d", response.code()));
                        showLoadingError(R.string.error_reason_unknown);
                    }
                }

                @Override
                public void onFailure(Call<MoviesQueryResultModel> call, Throwable t) {
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
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
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
            case FAVORITES:
                setTitle(R.string.title_favorites);
                break;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //Save movie data, so it can restore when needed (orientation change)
        outState.putParcelableArrayList(PARCEL_DATA_MOVIES, movieListResultModels);
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
        else if (id == R.id.action_favorites) {
            //load or reload favorites movies
            currentListType = MovieListType.FAVORITES;
            setTitleForCurrentListType();
            loadMoviesForCurrentListType();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
