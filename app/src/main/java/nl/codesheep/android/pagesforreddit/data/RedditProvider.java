package nl.codesheep.android.pagesforreddit.data;

import android.net.Uri;

import net.simonvt.schematic.annotation.ContentProvider;
import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.TableEndpoint;

@ContentProvider(authority= RedditProvider.AUTHORITY, database = PagesDatabase.class)
public class RedditProvider {

    public static final String AUTHORITY =
            "nl.codesheep.android.pagesforreddit";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    interface Path {
        String SUBREDDITS = "subreddits";
        String POSTS = "posts";
    }

    private RedditProvider() {

    }

    private static Uri buildUri(String... paths) {
        Uri.Builder uriBuilder = BASE_CONTENT_URI.buildUpon();
        for (String path : paths) {
            uriBuilder.appendPath(path);
        }
        return uriBuilder.build();
    }

    @TableEndpoint(table = PagesDatabase.SUBREDDITS) public static class SubReddits {
        @ContentUri(
                path = Path.SUBREDDITS,
                type = "vnd.android.cursor.dir/subreddit"
        )
        public static final Uri SUBREDDITS = buildUri(Path.SUBREDDITS);
    }

    @TableEndpoint(table = PagesDatabase.REDDIT_POSTS)
    public static class Posts {
        @ContentUri(
                path = Path.POSTS,
                type = "vnd.android.cursor.dir/posts"
        )
        public static final Uri POSTS = buildUri(Path.POSTS);
    }
}
