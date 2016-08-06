package nl.codesheep.android.pagesforreddit.sync;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import nl.codesheep.android.pagesforreddit.R;
import nl.codesheep.android.pagesforreddit.data.RedditProvider;
import nl.codesheep.android.pagesforreddit.data.models.RedditPost;
import nl.codesheep.android.pagesforreddit.helpers.Utility;
import nl.codesheep.android.pagesforreddit.sync.redditapi.ListingResponse;
import nl.codesheep.android.pagesforreddit.sync.redditapi.RedditPostMeta;
import nl.codesheep.android.pagesforreddit.sync.redditapi.RedditService;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RedditFetchService extends IntentService {

    public static final String ACTION_DATA_UPDATED =
            "nl.codesheep.android.pagesforreddit.ACTION_DATA_CHANGED";
    private static final String TAG = RedditFetchService.class.getSimpleName();

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public RedditFetchService() {
        super("Reddit Download Service");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent.hasExtra("subreddits")) {
            String after = Utility.getNextPageString(getApplicationContext());
            String subreddits = intent.getStringExtra("subreddits");
            Log.d(TAG, "Intent received these subs: " + subreddits);
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(RedditService.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            RedditService.ListingCalls service = retrofit.create(RedditService.ListingCalls.class);
            try {
                deleteOldData();
                Response<ListingResponse> response = service.hotPosts(subreddits, after).execute();
                for (RedditPostMeta postMeta : response.body().listing.redditPostMetas) {
                    Log.d(TAG, "Storing post: "+postMeta.redditPost.title);
                    getContentResolver().insert(RedditProvider.Posts.POSTS, postMeta.redditPost.toContentValues());
                }
                Utility.setNextPageString(getApplicationContext(), response.body().listing.after);
                Intent dataUpdatedIntent = new Intent(ACTION_DATA_UPDATED);
                getApplicationContext().sendBroadcast(dataUpdatedIntent);
            } catch (IOException e) {
                showToast(getString(R.string.no_connection));
                e.printStackTrace();
            }
        }
        else {
            Log.e(TAG, "No subreddits given");
        }
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), getString(R.string.subreddit_not_found), Toast.LENGTH_SHORT)
                .show();
    }

    private void deleteOldData() {
        getContentResolver().delete(RedditProvider.Posts.POSTS, null, null);
    }
}
