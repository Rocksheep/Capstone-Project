package nl.codesheep.android.pagesforreddit.data.models;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import nl.codesheep.android.pagesforreddit.data.RedditPostsTable;

public class RedditPost implements Parcelable {

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
    public String videoUrl;

    public Media media;

    public RedditPreview preview = null;

    public RedditPost() {

    }

    protected RedditPost(Parcel in) {
        mId = in.readLong();
        redditId = in.readString();
        subreddit = in.readString();
        title = in.readString();
        author = in.readString();
        permalink = in.readString();
        ups = in.readInt();
        downs = in.readInt();
        numComments = in.readInt();
        selftext = in.readString();
        url = in.readString();
        imageUrl = in.readString();
        thumbnailUrl = in.readString();
        videoUrl = in.readString();
    }

    public static final Creator<RedditPost> CREATOR = new Creator<RedditPost>() {
        @Override
        public RedditPost createFromParcel(Parcel in) {
            return new RedditPost(in);
        }

        @Override
        public RedditPost[] newArray(int size) {
            return new RedditPost[size];
        }
    };

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
        contentValues.put(RedditPostsTable.URL, url);

        if (media != null) {
            videoUrl = url;
            contentValues.put(RedditPostsTable.VIDEO_URL, videoUrl);
        }
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
        redditPost.url = cursor.getString(cursor.getColumnIndex(RedditPostsTable.URL));
        redditPost.videoUrl = cursor.getString(cursor.getColumnIndex(RedditPostsTable.VIDEO_URL));
        return redditPost;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(redditId);
        dest.writeString(subreddit);
        dest.writeString(title);
        dest.writeString(author);
        dest.writeString(permalink);
        dest.writeInt(ups);
        dest.writeInt(downs);
        dest.writeInt(numComments);
        dest.writeString(selftext);
        dest.writeString(url);
        dest.writeString(imageUrl);
        dest.writeString(thumbnailUrl);
        dest.writeString(videoUrl);
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

    public class Media {

        String type;

    }
}
