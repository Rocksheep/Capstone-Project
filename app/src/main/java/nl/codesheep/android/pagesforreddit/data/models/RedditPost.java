package nl.codesheep.android.pagesforreddit.data.models;

import android.content.ContentValues;
import android.database.Cursor;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import nl.codesheep.android.pagesforreddit.data.RedditPostsTable;

public class RedditPost {

    public long mId = 0;

    @SerializedName("id")
    public String redditId;
    public String subreddit;
    public String title;
    public String author;
    public String permalink;
    public int ups;
    public int downs;
    @SerializedName("num_comments")
    public int numComments;
    public String selftext;
    public String url;
    public String imageUrl;
    public String thumbnailUrl;

    public RedditPreview preview = null;

    public ContentValues toContentValues() {
        ContentValues contentValues = new ContentValues();
        if (mId != 0) {
            contentValues.put(RedditPostsTable.ID, mId);
        }
        contentValues.put(RedditPostsTable.AUTHOR, author);
        contentValues.put(RedditPostsTable.REDDIT_ID, redditId);
        contentValues.put(RedditPostsTable.PERMALINK, permalink);
        contentValues.put(RedditPostsTable.TITLE, title);
        contentValues.put(RedditPostsTable.SUBREDDIT, subreddit);
        contentValues.put(RedditPostsTable.UPS, ups);
        contentValues.put(RedditPostsTable.DOWNS, downs);
        contentValues.put(RedditPostsTable.NUM_COMMENTS, numComments);
        contentValues.put(RedditPostsTable.SELFTEXT, selftext);

        if (preview == null) {
            contentValues.put(RedditPostsTable.IMAGE_URL, url);
            contentValues.put(RedditPostsTable.THUMBNAIL_URL, thumbnailUrl);
        }
        else {
            contentValues.put(RedditPostsTable.IMAGE_URL, preview.images.get(0).source.url);
            List<RedditImageSource> images = preview.images.get(0).resolutions;
            String sourceUrl = null;
            for (RedditImageSource image : images) {
                if (image.width == 640) {
                    sourceUrl = image.url;
                    break;
                }
            }
            if (sourceUrl == null) {

                sourceUrl = images.get(images.size() - 1).url;
            }
            contentValues.put(RedditPostsTable.THUMBNAIL_URL, sourceUrl);
        }
        return contentValues;
    }

    public static RedditPost createFromCursor(Cursor cursor) {
        RedditPost redditPost = new RedditPost();
        redditPost.mId = cursor.getLong(cursor.getColumnIndex(RedditPostsTable.ID));
        redditPost.subreddit = cursor.getString(cursor.getColumnIndex(RedditPostsTable.SUBREDDIT));
        redditPost.title = cursor.getString(cursor.getColumnIndex(RedditPostsTable.TITLE));
        redditPost.redditId = cursor.getString(cursor.getColumnIndex(RedditPostsTable.REDDIT_ID));
        redditPost.author = cursor.getString(cursor.getColumnIndex(RedditPostsTable.AUTHOR));
        redditPost.permalink = cursor.getString(cursor.getColumnIndex(RedditPostsTable.PERMALINK));
        redditPost.ups = cursor.getInt(cursor.getColumnIndex(RedditPostsTable.UPS));
        redditPost.downs = cursor.getInt(cursor.getColumnIndex(RedditPostsTable.DOWNS));
        redditPost.numComments = cursor.getInt(cursor.getColumnIndex(RedditPostsTable.NUM_COMMENTS));
        redditPost.selftext = cursor.getString(cursor.getColumnIndex(RedditPostsTable.SELFTEXT));
        redditPost.imageUrl = cursor.getString(cursor.getColumnIndex(RedditPostsTable.IMAGE_URL));
        redditPost.thumbnailUrl = cursor.getString(cursor.getColumnIndex(RedditPostsTable.THUMBNAIL_URL));
        return redditPost;
    }

    public class RedditPreview {
        List<RedditImage> images;
    }

    public class RedditImage {
        RedditImageSource source;
        List<RedditImageSource> resolutions;
    }

    public class RedditImageSource {
        String url;
        int width;
        int height;
    }
}
