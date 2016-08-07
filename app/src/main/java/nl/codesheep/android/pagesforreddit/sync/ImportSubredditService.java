package nl.codesheep.android.pagesforreddit.sync;

import android.app.IntentService;
import android.content.Intent;

public class ImportSubredditService extends IntentService {

    public ImportSubredditService() {
        super("ImportSubredditService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        new RedditRestClient(getApplicationContext()).fetchSubreddits();
    }
}
