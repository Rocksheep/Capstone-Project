package nl.codesheep.android.pagesforreddit.widget;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import nl.codesheep.android.pagesforreddit.R;
import nl.codesheep.android.pagesforreddit.data.RedditPostsTable;
import nl.codesheep.android.pagesforreddit.data.RedditProvider;
import nl.codesheep.android.pagesforreddit.data.models.RedditPost;

/**
 * Created by Rien on 06/08/2016.
 */
public class WidgetIntentService extends IntentService {

    private static final String TAG = WidgetIntentService.class.getSimpleName();

    public WidgetIntentService() {
        super("WidgetIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this,
                RedditWidget.class));

        Cursor cursor = getContentResolver().query(
                RedditProvider.Posts.POSTS,
                RedditPostsTable.PROJECTION,
                null,
                null,
                null);
        if (cursor == null) {
            return;
        }
        if (!cursor.moveToFirst()) {
            cursor.close();
            return;
        }
        Log.d(TAG, "Loading data for widget");
        RedditPost post = RedditPost.createFromCursor(cursor);
        Log.d(TAG, "Found " + Integer.toString(appWidgetIds.length) + " widgets");
        cursor.close();
        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(getPackageName(), R.layout.reddit_widget);
            views.setTextViewText(R.id.widget_title, post.title);
            if (post.imageUrl != null) {
                views.setViewVisibility(R.id.widget_image, View.VISIBLE);
                views.setImageViewUri(R.id.widget_image, Uri.parse(post.imageUrl));
            }
            else {
                views.setViewVisibility(R.id.widget_image, View.GONE);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                views.setContentDescription(R.id.widget_title, post.title);
            }
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }
}
