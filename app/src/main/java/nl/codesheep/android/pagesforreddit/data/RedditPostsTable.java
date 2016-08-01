package nl.codesheep.android.pagesforreddit.data;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;

public interface RedditPostsTable  {

    @DataType(DataType.Type.INTEGER) @PrimaryKey @AutoIncrement String ID = "_id";

    @DataType(DataType.Type.TEXT) @NotNull String REDDIT_ID = "reddit_id";
    @DataType(DataType.Type.TEXT) @NotNull String SUBREDDIT = "subreddit";
    @DataType(DataType.Type.TEXT) @NotNull String TITLE = "title";
    @DataType(DataType.Type.TEXT) @NotNull String AUTHOR = "author";
    @DataType(DataType.Type.TEXT) @NotNull String PERMALINK = "permalink";


    String[] PROJECTION = new String[] {
            ID,
            REDDIT_ID,
            SUBREDDIT,
            TITLE,
            AUTHOR,
            PERMALINK
    };
}