package com.udacity.koprivarudolf.popularmovies;

/**
 * Created by rudolfkopriva on 21.01.17.
 *
 * The adapter for Main activity with movie posters
 */
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class PopularMovieAdapter extends ArrayAdapter<MovieListResultObject> {
    private static final String LOG_TAG = PopularMovieAdapter.class.getSimpleName();

    static class ViewHolder{
        ImageView iconView;

        public ViewHolder(View view) {
            iconView = (ImageView) view.findViewById(R.id.popular_movie_image);
        }
    }

    /**
     * The context is used to inflate the layout file, and the List is the data we want
     * to populate into the lists
     *
     * @param context        The current context. Used to inflate the layout file.
     * @param movieListResultObjects A List of MovieListResultObject objects to display in a list
     */
    public PopularMovieAdapter(Activity context, List<MovieListResultObject> movieListResultObjects) {
        super(context, 0, movieListResultObjects);
    }

    /**
     * Provides a view for an AdapterView (ListView, GridView, etc.)
     *
     * @param position    The AdapterView position that is requesting a view
     * @param convertView The recycled view to populate.
     * @param parent The parent ViewGroup that is used for inflation.
     * @return The View for the position in the AdapterView.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Gets the MovieListResultObject object from the ArrayAdapter at the appropriate position
        MovieListResultObject movieListResultObject = getItem(position);

        ViewHolder viewHolder;

        //Use view holder for performance reasons
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.popular_movie_item, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder)convertView.getTag();
        }

        //Used Picasso to load images
        Picasso.with(getContext())
                .load(movieListResultObject.getPosterImageURL())
                .into(viewHolder.iconView);

        return convertView;
    }
}