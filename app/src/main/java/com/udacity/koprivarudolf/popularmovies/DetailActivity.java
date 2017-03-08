package com.udacity.koprivarudolf.popularmovies;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.udacity.koprivarudolf.popularmovies.adapters.ReviewsAdapter;
import com.udacity.koprivarudolf.popularmovies.adapters.TrailersAdapter;
import com.udacity.koprivarudolf.popularmovies.api.IMovieDBService;
import com.udacity.koprivarudolf.popularmovies.api.MovieDBServiceFactory;
import com.udacity.koprivarudolf.popularmovies.models.MovieListResultModel;
import com.udacity.koprivarudolf.popularmovies.models.ReviewListResultModel;
import com.udacity.koprivarudolf.popularmovies.models.ReviewQueryResultModel;
import com.udacity.koprivarudolf.popularmovies.models.VideoListResultModel;
import com.udacity.koprivarudolf.popularmovies.models.VideoQueryResultModel;
import com.udacity.koprivarudolf.popularmovies.store.FavoriteColumns;
import com.udacity.koprivarudolf.popularmovies.store.MoviesProvider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Activity with details of the selected movie
 */
public class DetailActivity extends AppCompatActivity implements TrailersAdapter.OnPlayListener {

    //Constant key used to store movie details
    public static final String PARCEL_DATA_MOVIE_DETAILS = "PARCEL_DATA_MOVIE_DETAILS";

    //Constant used to save/restore list of trailers
    private static final String PARCEL_DATA_TRAILERS = "PARCEL_DATA_TRAILERS";

    //Constant used to save/restore list of reviews
    private static final String PARCEL_DATA_REVIEWS = "PARCEL_DATA_REVIEWS";

    private static final String TAG = MainActivity.class.getSimpleName();

    /**
     * Views from the layout
     */
    private TextView originalTitleView;
    private ImageView moviePosterImageView;
    private TextView movieYearView;
    private TextView movieRatingView;
    private TextView movieDescriptionView;

    private ProgressBar trailersProgressBar;
    private ImageView trailersIcon;
    private LinearLayout trailersListView;

    private Button favoriteButton;

    private ProgressBar reviewsProgressBar;
    private ImageView reviewsIcon;
    private LinearLayout reviewsListView;

    private MovieListResultModel _movieDetails;

    private ArrayList<VideoListResultModel> trailersList;
    private ArrayList<ReviewListResultModel> reviewsList;

    private IMovieDBService _movieDBService;

    private boolean isFavorite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _movieDBService = MovieDBServiceFactory.getMovieDBService();

        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //enable back button to return to parent activity
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //Load details about the movie, we passed this details as parcelable extra when this activity was launched
        Intent intent = getIntent();
        _movieDetails = intent.getParcelableExtra(DetailActivity.PARCEL_DATA_MOVIE_DETAILS);

        //Title (localized)
        setTitle(_movieDetails.getTitle());

        //Original view. Is displayed when it differs from the title
        originalTitleView = (TextView) findViewById(R.id.original_title);
        if (!_movieDetails.isTitleSameAsOriginalTitle()) {
            originalTitleView.setVisibility(View.VISIBLE);
            originalTitleView.setText(_movieDetails.getOriginal_title());
        }
        else {
            originalTitleView.setVisibility(View.GONE);
        }

        //Poster image
        moviePosterImageView = (ImageView) findViewById(R.id.movie_poster_image);
        Picasso.with(this)
                .load(_movieDetails.getPosterImageURL())
                .into(moviePosterImageView);

        //Movie Year
        movieYearView = (TextView) findViewById(R.id.movie_year);
        movieYearView.setText(_movieDetails.getRelease_date());

        //Movie Rating
        movieRatingView = (TextView) findViewById(R.id.movie_rating);
        movieRatingView.setText(String.format(Locale.getDefault(),"%.1f/10", _movieDetails.getVote_average()));

        //Movie plot
        movieDescriptionView = (TextView) findViewById(R.id.movie_description);
        movieDescriptionView.setText(_movieDetails.getOverview());

        //Favorite button
        favoriteButton = (Button) findViewById(R.id.favorite_button);

        //query the content provider to check if we set this movie as favorite
        Cursor result = getContentResolver().query(MoviesProvider.Favorites.withMovieId(_movieDetails.getId()), null, null, null, null);
        if (result.moveToNext()) {
            isFavorite = true;
        }
        result.close();

        updateFavoritesButtonText();

        favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isFavorite) {
                    getContentResolver().delete(
                            MoviesProvider.Favorites.withMovieId(_movieDetails.getId()), null, null);
                }
                else {
                    ContentValues cv = _movieDetails.createContentValues();
                    getContentResolver().insert(
                            MoviesProvider.Favorites.CONTENT_URI, cv);
                }
                isFavorite = !isFavorite;
                updateFavoritesButtonText();
            }
        });

        /** TRAILERS */

        //The list view here is a linear layout
        trailersListView = (LinearLayout) findViewById(R.id.trailers_list);
        trailersIcon = (ImageView) findViewById(R.id.trailers_icon);
        trailersProgressBar = (ProgressBar) findViewById(R.id.trailers_progress);

        trailersListView.setVisibility(View.GONE);
        trailersIcon.setVisibility(View.GONE);
        trailersProgressBar.setVisibility(View.VISIBLE);

        if(savedInstanceState != null && savedInstanceState.containsKey(PARCEL_DATA_TRAILERS)) {
            trailersList = savedInstanceState.getParcelableArrayList(PARCEL_DATA_TRAILERS);
            fillTrailersList();
        }
        else {
            trailersList = new ArrayList<>();
            loadVideos();
        }

        /** REVIEWS */

        //The list view here is a linear layout
        reviewsListView = (LinearLayout) findViewById(R.id.reviews_list);
        reviewsIcon = (ImageView) findViewById(R.id.reviews_icon);
        reviewsProgressBar = (ProgressBar) findViewById(R.id.reviews_progress);

        reviewsListView.setVisibility(View.GONE);
        reviewsIcon.setVisibility(View.GONE);
        reviewsProgressBar.setVisibility(View.VISIBLE);

        if(savedInstanceState != null && savedInstanceState.containsKey(PARCEL_DATA_REVIEWS)) {
            reviewsList = savedInstanceState.getParcelableArrayList(PARCEL_DATA_REVIEWS);
            fillReviewsList();
        }
        else {
            reviewsList = new ArrayList<>();
            loadReviews();
        }
    }

    //Change text of the favorite button based on current value of isFavorite variable
    private void updateFavoritesButtonText() {
        favoriteButton.setText(isFavorite ? R.string.remove_from_favorites : R.string.mark_as_favorite);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //Save trailers and reviews data, so it can restore when needed (orientation change)
        outState.putParcelableArrayList(PARCEL_DATA_TRAILERS, trailersList);
        outState.putParcelableArrayList(PARCEL_DATA_REVIEWS, reviewsList);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId()== android.R.id.home) {
            //Finish the details activity (this) to return to the main activity without destroying it's bundle data
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Load list of trailers from the service
     */
    private void loadVideos() {
        Call<VideoQueryResultModel> request = _movieDBService.getMovieVideos(_movieDetails.getId());

        try {
            request.enqueue(new Callback<VideoQueryResultModel>() {
                @Override
                public void onResponse(Call<VideoQueryResultModel> call, Response<VideoQueryResultModel> response) {
                    if (response.code() == 200) {
                        Log.d(TAG, "Movies loaded");
                        trailersList.addAll(response.body().getResults());
                        fillTrailersList();
                    }
                    else {
                        Log.e(TAG, String.format(Locale.getDefault(), "Failed to load videos with response code %d", response.code()));
                        showTrailersLoadingError(R.string.error_reason_unknown);
                    }
                }

                @Override
                public void onFailure(Call<VideoQueryResultModel> call, Throwable t) {
                    Log.e(TAG, "Failed to load movies with exception", t);
                    if (t instanceof IOException) {
                        //IOException stands for connection errors
                        showTrailersLoadingError(R.string.error_reason_connection);
                    }
                    else {
                        showTrailersLoadingError(R.string.error_reason_unknown);
                    }
                }
            });

        } catch (Exception e) {
            Log.e(TAG, "Failed to load videos with exception", e);
            showTrailersLoadingError(R.string.error_reason_unknown);
        }
    }

    /**
     * The trailers list is displayed in linear layout, this method will fill it from the trailersList array
     */
    private void fillTrailersList() {
        trailersListView.removeAllViews();
        if (trailersList.size() == 0) {
            showNoTrailersIcon();
            return;
        }
        TrailersAdapter trailersAdapter = new TrailersAdapter(DetailActivity.this, trailersList, this);
        for (int i = 0; i < trailersAdapter.getCount(); i++) {
            View view = trailersAdapter.getView(i, null, trailersListView);
            trailersListView.addView(view);
        }
        trailersListView.setVisibility(View.VISIBLE);
        trailersProgressBar.setVisibility(View.GONE);
        trailersIcon.setVisibility(View.GONE);
    }

    /**
     * Show error icon
     * @param errorReason string resource id
     */
    private void showTrailersLoadingError(int errorReason) {
        trailersProgressBar.setVisibility(View.GONE);
        trailersIcon.setVisibility(View.VISIBLE);
        trailersIcon.setImageResource(R.drawable.ic_warning_black_32dp);
        trailersIcon.setContentDescription(getString(R.string.error_loading_trailers));
        trailersListView.setVisibility(View.GONE);
    }

    /**
     * Called when movie has no trailers
     */
    private void showNoTrailersIcon() {
        trailersProgressBar.setVisibility(View.GONE);
        trailersIcon.setVisibility(View.VISIBLE);
        trailersIcon.setImageResource(R.drawable.ic_block_black_32dp);
        trailersIcon.setContentDescription(getString(R.string.no_trailers));
        trailersListView.setVisibility(View.GONE);
    }

    /**
     * Load list of reviews from the service
     */
    private void loadReviews() {
        Call<ReviewQueryResultModel> request = _movieDBService.getReviews(_movieDetails.getId());

        try {
            request.enqueue(new Callback<ReviewQueryResultModel>() {
                @Override
                public void onResponse(Call<ReviewQueryResultModel> call, Response<ReviewQueryResultModel> response) {
                    if (response.code() == 200) {
                        Log.d(TAG, "Reviews loaded");
                        reviewsList.addAll(response.body().getResults());
                        fillReviewsList();
                    }
                    else {
                        Log.e(TAG, String.format(Locale.getDefault(), "Failed to load reviews with response code %d", response.code()));
                        showReviewsLoadingError(R.string.error_reason_unknown);
                    }
                }

                @Override
                public void onFailure(Call<ReviewQueryResultModel> call, Throwable t) {
                    Log.e(TAG, "Failed to load reviews with exception", t);
                    if (t instanceof IOException) {
                        //IOException stands for connection errors
                        showReviewsLoadingError(R.string.error_reason_connection);
                    }
                    else {
                        showReviewsLoadingError(R.string.error_reason_unknown);
                    }
                }
            });

        } catch (Exception e) {
            Log.e(TAG, "Failed to load reviews with exception", e);
            showReviewsLoadingError(R.string.error_reason_unknown);
        }
    }

    /**
     * The reviews list is displayed in linear layout, this method will fill it from the reviewsList array
     */
    private void fillReviewsList() {
        reviewsListView.removeAllViews();
        if (reviewsList.size() == 0) {
            showNoReviewsIcon();
            return;
        }
        ReviewsAdapter reviewsAdapter = new ReviewsAdapter(DetailActivity.this, reviewsList);
        for (int i = 0; i < reviewsAdapter.getCount(); i++) {
            View view = reviewsAdapter.getView(i, null, reviewsListView);
            reviewsListView.addView(view);
        }
        reviewsListView.setVisibility(View.VISIBLE);
        reviewsProgressBar.setVisibility(View.GONE);
        reviewsIcon.setVisibility(View.GONE);
    }

    /**
     * Show error icon
     * @param errorReason string resource id
     */
    private void showReviewsLoadingError(int errorReason) {
        reviewsProgressBar.setVisibility(View.GONE);
        reviewsIcon.setVisibility(View.VISIBLE);
        reviewsIcon.setImageResource(R.drawable.ic_warning_black_32dp);
        trailersIcon.setContentDescription(getString(R.string.error_loading_reviews));
        reviewsListView.setVisibility(View.GONE);
    }

    /**
     * Called when movie has no reviews
     */
    private void showNoReviewsIcon() {
        reviewsProgressBar.setVisibility(View.GONE);
        reviewsIcon.setVisibility(View.VISIBLE);
        reviewsIcon.setImageResource(R.drawable.ic_block_black_32dp);
        trailersIcon.setContentDescription(getString(R.string.no_reviews));
        reviewsListView.setVisibility(View.GONE);
    }

    @Override
    public void onPlayTrailer(VideoListResultModel trailer) {
        if (trailer.getSite().equalsIgnoreCase("Youtube")) {
            Uri youtubeVideoUri =
                    Uri.parse("http://www.youtube.com/watch")
                            .buildUpon()
                            .appendQueryParameter("v", trailer.getKey())
                            .build();
            Intent intent = new Intent(Intent.ACTION_VIEW, youtubeVideoUri);
            startActivity(intent);
        }
        else {
            Toast.makeText(this, R.string.site_not_supported, Toast.LENGTH_SHORT).show();
        }
    }
}
