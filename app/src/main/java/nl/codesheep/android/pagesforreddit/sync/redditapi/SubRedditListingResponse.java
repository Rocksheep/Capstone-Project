package nl.codesheep.android.pagesforreddit.sync.redditapi;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import nl.codesheep.android.pagesforreddit.data.models.SubReddit;


public class SubRedditListingResponse {

    @SerializedName("data")
    public SubRedditListing listing;

    public class SubRedditListing {
        public String after;
        public List<SubRedditListingItem> children;
    }

    public class SubRedditListingItem {
        @SerializedName("data")
        public SubReddit subReddit;
    }
}
