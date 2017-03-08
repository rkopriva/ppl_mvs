package com.udacity.koprivarudolf.popularmovies.store;

import android.net.Uri;

import net.simonvt.schematic.annotation.ContentProvider;
import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.InexactContentUri;
import net.simonvt.schematic.annotation.TableEndpoint;

/**
 * Created by rudolfkopriva on 07.03.17.
 */

@ContentProvider(authority = MoviesProvider.AUTHORITY, database = MoviesDatabase.class)
public final class MoviesProvider {

    public static final String AUTHORITY = "com.udacity.koprivarudolf.popularmovies.store.MoviesProvider";

    static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    interface Path{
        String FAVORITES = "favorites";
    }

    private static Uri buildUri(String ... paths){
        Uri.Builder builder = BASE_CONTENT_URI.buildUpon();
        for (String path : paths){
            builder.appendPath(path);
        }
        return builder.build();
    }

    @TableEndpoint(table = MoviesDatabase.FAVORITES)
    public static class Favorites {

        @ContentUri(
                path = Path.FAVORITES,
                type = "vnd.android.cursor.dir/favorites")
        public static final Uri CONTENT_URI = buildUri(Path.FAVORITES);

        @InexactContentUri(
                name = "MOVIE_ID",
                path = Path.FAVORITES + "/#",
                type = "vnd.android.cursor.item/favorites",
                whereColumn = FavoriteColumns.MOVIE_ID,
                pathSegment = 1)
        public static Uri withMovieId(int movie_id){
            return buildUri(Path.FAVORITES, String.valueOf(movie_id));
        }
    }
}
