package nl.codesheep.android.pagesforreddit.sync.redditapi;

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
        public List<RedditComment> comments;

    }

    public class RedditComment {

        public int score;
        public int ups;
        public int down;
        public String body;

    }
}
