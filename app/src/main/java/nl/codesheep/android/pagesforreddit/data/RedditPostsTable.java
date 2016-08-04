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
    @DataType(DataType.Type.INTEGER) @NotNull String UPS = "ups";
    @DataType(DataType.Type.INTEGER) @NotNull String DOWNS = "downs";
    @DataType(DataType.Type.INTEGER) @NotNull String NUM_COMMENTS = "num_comments";
    @DataType(DataType.Type.TEXT) String SELFTEXT = "selftext";
    @DataType(DataType.Type.TEXT) String URL = "url";
    @DataType(DataType.Type.TEXT) String IMAGE_URL = "image_url";
    @DataType(DataType.Type.TEXT) String THUMBNAIL_URL = "thumb_url";



    String[] PROJECTION = new String[] {
            ID,
            REDDIT_ID,
            SUBREDDIT,
            TITLE,
            AUTHOR,
            PERMALINK,
            UPS,
            DOWNS,
            NUM_COMMENTS,
            SELFTEXT,
            URL,
            IMAGE_URL,
            THUMBNAIL_URL
    };
}