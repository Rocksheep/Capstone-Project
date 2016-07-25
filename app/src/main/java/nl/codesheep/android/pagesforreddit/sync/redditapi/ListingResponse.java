package nl.codesheep.android.pagesforreddit.sync.redditapi;

import com.google.gson.annotations.SerializedName;

public class ListingResponse {

    @SerializedName("data")
    public Listing listing;

}
