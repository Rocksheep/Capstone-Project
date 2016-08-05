package nl.codesheep.android.pagesforreddit.data;

import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.Table;

@Database(version=PagesDatabase.VERSION)
public final class PagesDatabase {
    public static final int VERSION = 2;

    @Table(SubRedditsTable.class) public static final String SUBREDDITS = "subreddits";
    @Table(RedditPostsTable.class) public static final String REDDIT_POSTS = "reddit_posts";
}
