package nl.codesheep.android.pagesforreddit;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import nl.codesheep.android.pagesforreddit.data.RedditPostsTable;
import nl.codesheep.android.pagesforreddit.data.RedditProvider;
import nl.codesheep.android.pagesforreddit.data.SubRedditsTable;
import nl.codesheep.android.pagesforreddit.data.models.RedditPost;
import nl.codesheep.android.pagesforreddit.data.models.SubReddit;
import nl.codesheep.android.pagesforreddit.dialogs.AddSubRedditDialog;
import nl.codesheep.android.pagesforreddit.sync.PostFetcher;
import nl.codesheep.android.pagesforreddit.sync.SyncAdapter;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, LoaderManager.LoaderCallbacks<Cursor> {

    private static final int URL_LOADER = 0;
    public static final int MENU_LOADER = 1;
    private PostPagerAdapter mPostPagerAdapter;
    private ViewPager mViewPager;
    private PostFetcher mPostFetcher;
    private SubMenu mSubMenu;

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



//        SyncAdapter.initializeSyncAdapter(this);
        mPostFetcher = new PostFetcher(this);
        mPostFetcher.execute();

        mPostPagerAdapter = new PostPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mViewPager.setAdapter(mPostPagerAdapter);
        getSupportLoaderManager().initLoader(URL_LOADER, null, this);
        getSupportLoaderManager().initLoader(MENU_LOADER, null, this);
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
            if (currentItem % 18 == 0) {
                mPostFetcher.execute();
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
            AddSubRedditDialog dialog = new AddSubRedditDialog();
            dialog.show(getSupportFragmentManager(), "test");
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
                mPostPagerAdapter.setPosts(posts);
                break;
            case MENU_LOADER:
                mSubMenu.clear();
                List<SubReddit> subReddits = new ArrayList<>();
                while (data.moveToNext()) {
                    SubReddit subReddit = SubReddit.createFromCursor(data);
                    mSubMenu.add(subReddit.getName());
                    subReddits.add(subReddit);
                }
                mPostFetcher.setSubReddits(subReddits);
                mPostFetcher.execute();
                mViewPager.setCurrentItem(0);
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
