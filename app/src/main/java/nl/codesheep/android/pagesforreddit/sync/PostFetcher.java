package nl.codesheep.android.pagesforreddit.sync;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import nl.codesheep.android.pagesforreddit.data.RedditProvider;
import nl.codesheep.android.pagesforreddit.data.models.SubReddit;
import nl.codesheep.android.pagesforreddit.sync.redditapi.ListingResponse;
import nl.codesheep.android.pagesforreddit.sync.redditapi.RedditPostMeta;
import nl.codesheep.android.pagesforreddit.sync.redditapi.RedditService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Rien on 03/08/2016.
 */
public class PostFetcher {

    private static final String TAG = PostFetcher.class.getSimpleName();
    private ContentResolver mContentResolver;
    private String mAfter = "";
    private List<SubReddit> mSubReddits = new ArrayList<>();

    public PostFetcher (Context context) {
        mContentResolver = context.getContentResolver();
    }

    public void setSubReddits(List<SubReddit> subReddits) {
        mSubReddits = subReddits;
        mAfter = "";
    }

    public void execute() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RedditService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RedditService.ListingCalls service = retrofit.create(RedditService.ListingCalls.class);

        if (mAfter.equals("") && !mSubReddits.isEmpty()) {
            deleteOldData();
        }

        String subs = "";
        if (mSubReddits.size() > 0) {
            for (SubReddit subReddit : mSubReddits) {
                subs += "+" + subReddit.getName();
            }
            syncListings(service.hotPosts(subs, mAfter));
        }
    }

    private void deleteOldData() {
        mContentResolver.delete(RedditProvider.Posts.POSTS, null, null);
    }

    private void syncListings(Call<ListingResponse> call) {
        call.enqueue(new Callback<ListingResponse>() {

            @Override
            public void onResponse(Call<ListingResponse> call, Response<ListingResponse> response) {
                Log.d(TAG, "Response received");
                Log.d(TAG, call.request().url().toString());
                mAfter = response.body().listing.after;
                for (RedditPostMeta postMeta : response.body().listing.redditPostMetas) {
                    ContentValues values = postMeta.redditPost.toContentValues();
                    mContentResolver.insert(
                            RedditProvider.Posts.POSTS,
                            values
                    );
                }
            }

            @Override
            public void onFailure(Call<ListingResponse> call, Throwable t) {
                Log.e(TAG, "Couldn't get results");
                t.printStackTrace();
            }
        });
    }

}
