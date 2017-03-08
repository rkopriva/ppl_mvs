package com.udacity.koprivarudolf.popularmovies.store;

import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.Table;

/**
 * Created by rudolfkopriva on 07.03.17.
 */

@Database(version = MoviesDatabase.VERSION)
public final class MoviesDatabase {

    public static final int VERSION = 1;

    @Table(FavoriteColumns.class) public static final String FAVORITES = "favorites";
}
