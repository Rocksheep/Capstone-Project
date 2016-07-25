package nl.codesheep.android.pagesforreddit.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import nl.codesheep.android.pagesforreddit.R;
import nl.codesheep.android.pagesforreddit.sync.redditapi.ListingResponse;
import nl.codesheep.android.pagesforreddit.sync.redditapi.RedditPostMeta;
import nl.codesheep.android.pagesforreddit.sync.redditapi.RedditService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SyncAdapter extends AbstractThreadedSyncAdapter {

    private static final String TAG = SyncAdapter.class.getSimpleName();
    private static final int SYNC_INTERVAL = 60 * 180;
    private static final int SYNC_FLEXTIME = SYNC_INTERVAL / 3;
    private ContentResolver mContentResolver;

    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mContentResolver = context.getContentResolver();
    }

    public SyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        mContentResolver = context.getContentResolver();
    }

    @Override
    public void onPerformSync(
            Account account,
            Bundle extras,
            String authority,
            ContentProviderClient provider,
            SyncResult syncResult) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RedditService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RedditService.ListingCalls service = retrofit.create(RedditService.ListingCalls.class);

        deleteOldData();
        syncListings(service.hotPosts("awww"));
    }

    private void deleteOldData() {
//        mContentResolver.delete(SubRedditProvider.Listings.LISTINGS, null, null);
    }

    private void syncListings(Call<ListingResponse> call) {
        call.enqueue(new Callback<ListingResponse>() {
            @Override
            public void onResponse(Call<ListingResponse> call, Response<ListingResponse> response) {
                Log.d(TAG, "Response received");
                for (RedditPostMeta postMeta : response.body().listing.redditPostMetas) {
                    Log.d(TAG, "Post: " + postMeta.redditPost.title + "@" + postMeta.redditPost.subreddit);
                }
            }

            @Override
            public void onFailure(Call<ListingResponse> call, Throwable t) {
                Log.e(TAG, "Couldn't get results");
                t.printStackTrace();
            }
        });
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }

    @Nullable
    public static Account getSyncAccount(Context context) {
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        Account account = new Account(
                context.getString(R.string.app_name), context.getString(R.string.account_type)
        );

        if (accountManager.getPassword(account) == null) {
            if (!accountManager.addAccountExplicitly(account, "", null)) {
                Log.e(TAG, "Couldn't create account");
                return null;
            }
            onAccountCreated(context, account);
        }
        else {
            Log.d(TAG, "Old account found");
        }
        return account;
    }

    private static void onAccountCreated(Context context, Account account) {
        Log.d(TAG, "Account created");
        SyncAdapter.configurePeriodicSync(context);
        ContentResolver.setSyncAutomatically(
                account,
                context.getString(R.string.content_authority),
                true
        );
        syncImmediately(context);
    }

    private static void configurePeriodicSync(Context context) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SyncRequest syncRequest = new SyncRequest.Builder()
                    .syncPeriodic(SYNC_INTERVAL, SYNC_FLEXTIME)
                    .setSyncAdapter(account, authority)
                    .setExtras(new Bundle())
                    .build();
            ContentResolver.requestSync(syncRequest);
        }
        else {
            ContentResolver.addPeriodicSync(account, authority, new Bundle(), SYNC_INTERVAL);
        }
    }

    public static void syncImmediately(Context context) {
        Log.d(TAG, "Syncing immediately");
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(
                getSyncAccount(context),
                context.getString(R.string.content_authority),
                bundle
        );
    }
}
