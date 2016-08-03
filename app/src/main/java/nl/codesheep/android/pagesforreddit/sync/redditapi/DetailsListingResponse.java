package nl.codesheep.android.pagesforreddit.sync.redditapi;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Rien on 02/08/2016.
 */
public class DetailsListingResponse {

    @SerializedName("data")
    public DetailsListing listing;

    public class DetailsListing {

        @SerializedName("children")
        public List<RedditCommentListing> commentListings;

    }

    public class RedditCommentListing {
        @SerializedName("data")
        public RedditComment comment;
    }

    public class RedditComment {

        @Expose
        public int score;
        @Expose
        public int ups;
        @Expose
        public int down;
        @Expose
        public String body;
        @Expose
        public String author;

        @SerializedName("replies")
        public DetailsListingResponse repliesListing;

    }
}
