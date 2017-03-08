package com.udacity.koprivarudolf.popularmovies.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.udacity.koprivarudolf.popularmovies.R;
import com.udacity.koprivarudolf.popularmovies.models.ReviewListResultModel;
import com.udacity.koprivarudolf.popularmovies.models.VideoListResultModel;

import java.util.List;

/**
 * Created by rudolfkopriva on 05.03.17.
 *
 * The adapter for Reviews of a movie
 */

public class ReviewsAdapter extends ArrayAdapter<ReviewListResultModel> {
    private static final String LOG_TAG = ReviewsAdapter.class.getSimpleName();

    static class ViewHolder{
        TextView authorText;
        TextView contentText;

        public ViewHolder(View view) {
            authorText = (TextView) view.findViewById(R.id.author_text);
            contentText = (TextView) view.findViewById(R.id.content_text);
        }
    }

    /**
     * The context is used to inflate the layout file, and the List is the data we want
     * to populate into the lists
     *
     * @param context        The current context. Used to inflate the layout file.
     * @param reviewListResultModels A List of ReviewListResultModel objects to display in a list
     */
    public ReviewsAdapter(Activity context, List<ReviewListResultModel> reviewListResultModels) {
        super(context, 0, reviewListResultModels);
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
        // Gets the ReviewListResultModel object from the ArrayAdapter at the appropriate position
        ReviewListResultModel reviewListResultModel = getItem(position);

        ReviewsAdapter.ViewHolder viewHolder;

        //Use view holder for performance reasons
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.review_item, parent, false);
            viewHolder = new ReviewsAdapter.ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ReviewsAdapter.ViewHolder)convertView.getTag();
        }

        viewHolder.authorText.setText(reviewListResultModel.getAuthor());
        viewHolder.contentText.setText(reviewListResultModel.getContent());

        return convertView;
    }
}
