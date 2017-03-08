package com.udacity.koprivarudolf.popularmovies.store;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;

import static net.simonvt.schematic.annotation.DataType.Type.INTEGER;
import static net.simonvt.schematic.annotation.DataType.Type.REAL;
import static net.simonvt.schematic.annotation.DataType.Type.TEXT;

/**
 * Created by rudolfkopriva on 07.03.17.
 */

public interface FavoriteColumns {

    @DataType(INTEGER) @PrimaryKey
    @AutoIncrement String _ID = "_id";

    @DataType(INTEGER) @NotNull
    String MOVIE_ID = "movie_id";

    @DataType(TEXT) @NotNull
    String MOVIE_TITLE = "movie_title";

    @DataType(TEXT) @NotNull
    String ORIGINAL_TITLE = "movie_original_title";

    @DataType(TEXT) @NotNull
    String POSTER_PATH = "movie_poster_path";

    @DataType(REAL) @NotNull
    String VOTE_AVERAGE = "movie_vote_average";

    @DataType(TEXT) @NotNull
    String RELEASE_DATE = "movie_release_date";

    @DataType(TEXT) @NotNull
    String OVERVIEW = "movie_overview";
}
