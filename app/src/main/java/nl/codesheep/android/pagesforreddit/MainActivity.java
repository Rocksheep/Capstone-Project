package nl.codesheep.android.pagesforreddit;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;
import java.util.List;

import nl.codesheep.android.pagesforreddit.data.RedditPostsTable;
import nl.codesheep.android.pagesforreddit.data.RedditProvider;
import nl.codesheep.android.pagesforreddit.data.SubRedditsTable;
import nl.codesheep.android.pagesforreddit.data.models.RedditPost;
import nl.codesheep.android.pagesforreddit.data.models.SubReddit;
import nl.codesheep.android.pagesforreddit.helpers.Utility;
import nl.codesheep.android.pagesforreddit.sync.RedditFetchService;
import nl.codesheep.android.pagesforreddit.sync.redditapi.ListingResponse;
import nl.codesheep.android.pagesforreddit.sync.redditapi.RedditService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, LoaderManager.LoaderCallbacks<Cursor> {

    private static final int URL_LOADER = 0;
    public static final int MENU_LOADER = 1;
    private static final String TAG = MainActivity.class.getSimpleName();
    private PostPagerAdapter mPostPagerAdapter;
    private ViewPager mViewPager;
    private TextView mNoSubsView;
    private SubMenu mSubMenu;
    private Menu mMenu;
    private AdView mAdView;
    private List<SubReddit> mSubReddits = new ArrayList<>();
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        Menu menu = navigationView.getMenu();
        mSubMenu = menu.addSubMenu("Subreddits");
        mNoSubsView = (TextView) findViewById(R.id.no_subreddits_message);
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        mFirebaseAnalytics.logEvent("open_app", new Bundle());
        Utility.setNextPageString(this, "");

//        SyncAdapter.initializeSyncAdapter(this);
        fetchPosts();


        mPostPagerAdapter = new PostPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mViewPager.setAdapter(mPostPagerAdapter);
        getSupportLoaderManager().initLoader(URL_LOADER, null, this);
        getSupportLoaderManager().initLoader(MENU_LOADER, null, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSupportLoaderManager().restartLoader(URL_LOADER, null, this);
        getSupportLoaderManager().restartLoader(MENU_LOADER, null, this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        mMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_dismiss) {
            int currentItem = mViewPager.getCurrentItem();
            mViewPager.setCurrentItem(currentItem + 1);
            if (currentItem > 0 && currentItem % 20 == 0) {
                fetchPosts();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.sign_in) {

        } else if (id == R.id.add_subreddit) {
            showAddSubRedditDialog();
        }
        else if (id == R.id.manage_subreddits) {
            Intent intent = new Intent(this, SubRedditList.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case URL_LOADER:
                return new CursorLoader(
                        this,
                        RedditProvider.Posts.POSTS,
                        RedditPostsTable.PROJECTION,
                        null,
                        null,
                        null
                );
            case MENU_LOADER:
                return new CursorLoader(
                        this,
                        RedditProvider.SubReddits.SUBREDDITS,
                        SubRedditsTable.PROJECTION,
                        null,
                        null,
                        "Name DESC"
                );
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case URL_LOADER:
                List<RedditPost> posts = new ArrayList<>();
                while (data.moveToNext()) {
                    RedditPost post = RedditPost.createFromCursor(data);
                    posts.add(post);
                }
                if (mMenu != null) {
                    if (posts.isEmpty()) {
                        mMenu.getItem(0).setVisible(false);
                    } else {
                        mMenu.getItem(0).setVisible(true);
                    }
                }

                mPostPagerAdapter.setPosts(posts);
                mViewPager.setCurrentItem(0);
                break;
            case MENU_LOADER:
                mSubMenu.clear();
                mSubReddits.clear();
                while (data.moveToNext()) {
                    SubReddit subReddit = SubReddit.createFromCursor(data);
                    mSubMenu.add(subReddit.getName());
                    mSubReddits.add(subReddit);
                }
                if (!mSubReddits.isEmpty()) {
                    mNoSubsView.setVisibility(View.GONE);
                    mViewPager.setVisibility(View.VISIBLE);
                }
                else {
                    mNoSubsView.setVisibility(View.VISIBLE);
                    mViewPager.setVisibility(View.GONE);
                }

                fetchPosts();
                mViewPager.setCurrentItem(0);
                mPostPagerAdapter.setPosts(new ArrayList<RedditPost>());
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private void showAddSubRedditDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setView(R.layout.add_subreddit_dialog)
                .setTitle(R.string.add_subreddit)
                .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, int which) {
                        EditText subredditField = (EditText) ((AlertDialog) dialog).findViewById(R.id.dialog_subreddit);
                        final String addedSubreddit = subredditField.getText().toString();
                        Retrofit retrofit = new Retrofit.Builder()
                                .baseUrl(RedditService.BASE_URL)
                                .addConverterFactory(GsonConverterFactory.create())
                                .build();
                        RedditService.ListingCalls service = retrofit.create(RedditService.ListingCalls.class);
                        service.hotPosts(addedSubreddit, "").enqueue(new Callback<ListingResponse>() {
                            @Override
                            public void onResponse(Call<ListingResponse> call, Response<ListingResponse> response) {
                                if (response.code() == 200) {
                                    ContentValues contentValues = new ContentValues();
                                    contentValues.put("name", addedSubreddit);
                                    getContentResolver().insert(RedditProvider.SubReddits.SUBREDDITS, contentValues);
                                    Log.d(TAG, "Storing sub: " + addedSubreddit);
                                } else {
                                    Toast.makeText(MainActivity.this, getString(R.string.subreddit_not_found), Toast.LENGTH_SHORT)
                                            .show();
                                }
                                dialog.dismiss();
                                Log.d(TAG, "Success, code: " + response.code());
                            }

                            @Override
                            public void onFailure(Call<ListingResponse> call, Throwable t) {
                                Toast.makeText(MainActivity.this,getString(R.string.couldnt_reach_server), Toast.LENGTH_SHORT)
                                        .show();
                                dialog.dismiss();
                            }
                        });
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        builder.create().show();
    }

    private void fetchPosts() {
        String subs = "";
        if (mSubReddits.size() > 0) {
            for (SubReddit subReddit : mSubReddits) {
                subs += "+" + subReddit.getName();
            }
            Intent intent = new Intent(this, RedditFetchService.class);;
            intent.putExtra("subreddits", subs);
            Log.d(TAG, "Loading data for subs: " + subs);
            startService(intent);
        }
    }
}
