package nl.codesheep.android.pagesforreddit.sync.redditapi;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public final class RedditService {
    public static final String BASE_URL = "http://reddit.com";

    public interface ListingCalls {
        @GET("/r/{subreddits}/hot.json")
        Call<ListingResponse> hotPosts(@Path("subreddits") String subreddits, @Query("after") String after);

        @GET("{permalink}.json")
        Call<List<DetailsListingResponse>> details(@Path(value="permalink", encoded = true) String permalink);
    }
}
