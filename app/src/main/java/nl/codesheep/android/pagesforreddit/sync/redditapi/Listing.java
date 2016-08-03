package nl.codesheep.android.pagesforreddit.sync.redditapi;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Listing {

    @SerializedName("children")
    public List<RedditPostMeta> redditPostMetas = new ArrayList<>();
    public String after;

}
