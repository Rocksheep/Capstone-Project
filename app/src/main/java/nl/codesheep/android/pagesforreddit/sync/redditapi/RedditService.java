package nl.codesheep.android.pagesforreddit.sync.redditapi;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public final class RedditService {
    public static final String BASE_URL = "https://reddit.com";
    public static final String BASE_OAUTH_URL = "https://oauth.reddit.com";

    public interface ListingCalls {
        @GET("/r/{subreddits}/hot.json")
        Call<ListingResponse> hotPosts(@Path("subreddits") String subreddits, @Query("after") String after);

        @GET("{permalink}.json")
        Call<List<DetailsListingResponse>> details(@Path(value="permalink", encoded = true) String permalink);
    }

    public interface OauthCalls {
        @GET("/subreddits/mine/subscriber.json")
        Call<SubRedditListingResponse> subreddits(@Header("Authorization") String token, @Query("after") String after);
    }
}
