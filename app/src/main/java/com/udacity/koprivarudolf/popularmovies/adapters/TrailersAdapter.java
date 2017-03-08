package com.udacity.koprivarudolf.popularmovies.adapters;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.udacity.koprivarudolf.popularmovies.R;
import com.udacity.koprivarudolf.popularmovies.models.VideoListResultModel;

import java.util.List;

/**
 * Created by rudolfkopriva on 05.03.17.
 *
 * The adapter for Trailers of a movie
 */

public class TrailersAdapter  extends ArrayAdapter<VideoListResultModel> {
    private static final String LOG_TAG = TrailersAdapter.class.getSimpleName();

    static class ViewHolder {
        TextView nameText;
        ImageButton playButton;

        public ViewHolder(View view) {
            nameText = (TextView) view.findViewById(R.id.trailer_text);
            playButton = (ImageButton) view.findViewById(R.id.play_image_button);
        }
    }

    public interface OnPlayListener {
        void onPlayTrailer(VideoListResultModel trailer);
    }

    private OnPlayListener onPlayListener;

    /**
     * The context is used to inflate the layout file, and the List is the data we want
     * to populate into the lists
     *
     * @param context        The current context. Used to inflate the layout file.
     * @param videoListResultModels A List of VideoListResultModel objects to display in a list
     * @param onPlayListener  Listener for play
     */
    public TrailersAdapter(Activity context, List<VideoListResultModel> videoListResultModels, OnPlayListener onPlayListener) {
        super(context, 0, videoListResultModels);
        this.onPlayListener = onPlayListener;
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
        // Gets the MovieListResultModel object from the ArrayAdapter at the appropriate position
        final VideoListResultModel videoListResultModel = getItem(position);
        TrailersAdapter.ViewHolder viewHolder;

        //Use view holder for performance reasons
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.trailer_item, parent, false);
            viewHolder = new TrailersAdapter.ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (TrailersAdapter.ViewHolder)convertView.getTag();
        }

        viewHolder.nameText.setText(videoListResultModel.getName());
        viewHolder.playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onPlayListener != null) {
                    onPlayListener.onPlayTrailer(videoListResultModel);
                }
            }
        });

        return convertView;
    }
}
