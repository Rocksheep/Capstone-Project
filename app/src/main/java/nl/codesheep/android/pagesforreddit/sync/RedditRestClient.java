package nl.codesheep.android.pagesforreddit.sync;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import java.io.IOException;
import java.util.List;

import nl.codesheep.android.pagesforreddit.data.RedditPostsTable;
import nl.codesheep.android.pagesforreddit.data.RedditProvider;
import nl.codesheep.android.pagesforreddit.data.SubRedditsTable;
import nl.codesheep.android.pagesforreddit.data.models.SubReddit;
import nl.codesheep.android.pagesforreddit.helpers.Utility;
import nl.codesheep.android.pagesforreddit.sync.redditapi.AccountResponse;
import nl.codesheep.android.pagesforreddit.sync.redditapi.RedditService;
import nl.codesheep.android.pagesforreddit.sync.redditapi.SubRedditListingResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Rien on 07/08/2016.
 */
public class RedditRestClient {

    private static final String TAG = RedditRestClient.class.getSimpleName();
    Context mContext;
    private static String CLIENT_ID = "";
    private static String CLIENT_SECRET = "";
    private static final String BASE_URL = "https://www.reddit.com/api/v1/";
    private static String REDIRECT_URL = "http://codesheep.nl";
    private String mAfter = "";

    public RedditRestClient(Context context) {
        mContext = context;
    }

    public void fetchSubreddits() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RedditService.BASE_OAUTH_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        String authCode = Utility.getAuthCode(mContext);
        RedditService.OauthCalls service = retrofit.create(RedditService.OauthCalls.class);
        service.subreddits("Bearer " + Utility.getToken(mContext), mAfter).enqueue(new Callback<SubRedditListingResponse>() {
            @Override
            public void onResponse(Call<SubRedditListingResponse> call, Response<SubRedditListingResponse> response) {
                Log.d(TAG, "Code: " + response.code());
                if (response.code() == 200) {
                    mAfter = response.body().listing.after;
                    List<SubRedditListingResponse.SubRedditListingItem> subs = response.body().listing.children;
                    for (SubRedditListingResponse.SubRedditListingItem item : subs) {
                        Log.d(TAG, "Inserting sub: " + item.subReddit.getName());
                        SubReddit subReddit = item.subReddit;
                        Cursor cursor = mContext.getContentResolver().query(
                                RedditProvider.SubReddits.SUBREDDITS,
                                SubRedditsTable.PROJECTION,
                                "name = ?",
                                new String[] {subReddit.getName()},
                                null
                        );
                        if (cursor != null && !cursor.moveToFirst()) {
                            mContext.getContentResolver().insert(
                                    RedditProvider.SubReddits.SUBREDDITS,
                                    subReddit.toContentValues()
                            );
                        }
                    }
                    if (mAfter != null && !mAfter.isEmpty()) {
                        fetchSubreddits();
                    }
                }
                else {
                    try {
                        Log.d(TAG, response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<SubRedditListingResponse> call, Throwable t) {
                Log.e(TAG, call.request().url().toString());

                t.printStackTrace();
            }
        });
    }

}
