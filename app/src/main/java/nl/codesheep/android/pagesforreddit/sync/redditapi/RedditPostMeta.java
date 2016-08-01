package nl.codesheep.android.pagesforreddit.sync.redditapi;

import com.google.gson.annotations.SerializedName;

import nl.codesheep.android.pagesforreddit.data.models.RedditPost;

public class RedditPostMeta {

    @SerializedName("data")
    public RedditPost redditPost;

}
