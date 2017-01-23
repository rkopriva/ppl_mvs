package com.udacity.koprivarudolf.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import java.util.Locale;

/**
 * Activity with details of the selected movie
 */
public class DetailActivity extends AppCompatActivity {

    //Constant key used to store movie details
    public static final String PARCEL_DATA_MOVIE_DETAILS = "PARCEL_DATA_MOVIE_DETAILS";

    /**
     * Views from the layout
     */
    private TextView titleView;
    private TextView originalTitleView;
    private ImageView moviePosterImageView;
    private TextView movieYearView;
    private TextView movieRatingView;
    private TextView movieDescriptionView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //enable back button to return to parent activity
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //Load details about the movie, we passed this details as parcelable extra when this activity was launched
        Intent intent = getIntent();
        MovieListResultObject movieDetails = intent.getParcelableExtra(DetailActivity.PARCEL_DATA_MOVIE_DETAILS);

        //Title view (localized)
        titleView = (TextView) findViewById(R.id.title);
        titleView.setText(movieDetails.getTitle());

        //Original view. Is displayed when it differs from the title
        originalTitleView = (TextView) findViewById(R.id.original_title);
        if (!movieDetails.isTitleSameAsOriginalTitle()) {
            originalTitleView.setVisibility(View.VISIBLE);
            originalTitleView.setText(movieDetails.getOriginal_title());
        }
        else {
            originalTitleView.setVisibility(View.GONE);
        }

        //Poster image
        moviePosterImageView = (ImageView) findViewById(R.id.movie_poster_image);
        Picasso.with(this)
                .load(movieDetails.getPosterImageURL())
                .into(moviePosterImageView);

        //Movie Year
        movieYearView = (TextView) findViewById(R.id.movie_year);
        movieYearView.setText(movieDetails.getRelease_date());

        //Movie Rating
        movieRatingView = (TextView) findViewById(R.id.movie_rating);
        movieRatingView.setText(String.format(Locale.getDefault(),"%.1f/10", movieDetails.getVote_average()));

        //Movie plot
        movieDescriptionView = (TextView) findViewById(R.id.movie_description);
        movieDescriptionView.setText(movieDetails.getOverview());
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
}
