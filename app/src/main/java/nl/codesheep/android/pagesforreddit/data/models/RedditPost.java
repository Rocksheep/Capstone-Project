package nl.codesheep.android.pagesforreddit.data.models;

import android.content.ContentValues;
import android.database.Cursor;

import nl.codesheep.android.pagesforreddit.data.RedditPostsTable;

public class RedditPost {

    public long id = 0;

    public String redditId;
    public String subreddit;
    public String title;
    public String author;
    public String permalink;

    public ContentValues toContentValues() {
        ContentValues contentValues = new ContentValues();
        if (id != 0) {
            contentValues.put(RedditPostsTable.ID, id);
        }
        contentValues.put(RedditPostsTable.AUTHOR, author);
        contentValues.put(RedditPostsTable.REDDIT_ID, redditId);
        contentValues.put(RedditPostsTable.PERMALINK, permalink);
        contentValues.put(RedditPostsTable.TITLE, title);
        contentValues.put(RedditPostsTable.SUBREDDIT, subreddit);

        return contentValues;
    }

    public static RedditPost createFromCursor(Cursor cursor) {
        RedditPost redditPost = new RedditPost();
        redditPost.id = cursor.getLong(cursor.getColumnIndex(RedditPostsTable.ID));
        redditPost.subreddit = cursor.getString(cursor.getColumnIndex(RedditPostsTable.SUBREDDIT));
        redditPost.title = cursor.getString(cursor.getColumnIndex(RedditPostsTable.TITLE));
        redditPost.redditId = cursor.getString(cursor.getColumnIndex(RedditPostsTable.REDDIT_ID));
        redditPost.author = cursor.getString(cursor.getColumnIndex(RedditPostsTable.AUTHOR));
        redditPost.permalink = cursor.getString(cursor.getColumnIndex(RedditPostsTable.PERMALINK));
        return redditPost;
    }

}
