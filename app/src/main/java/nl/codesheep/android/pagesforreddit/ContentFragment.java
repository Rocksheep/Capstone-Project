package nl.codesheep.android.pagesforreddit;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import nl.codesheep.android.pagesforreddit.data.RedditPostsTable;
import nl.codesheep.android.pagesforreddit.data.RedditProvider;
import nl.codesheep.android.pagesforreddit.data.models.RedditPost;

public class ContentFragment extends Fragment {

    public static final String LOG = ContentFragment.class.getSimpleName();

    public ContentFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Cursor cursor = getContext().getContentResolver().query(
                RedditProvider.Posts.POSTS,
                RedditPostsTable.PROJECTION,
                null,
                null,
                null
        );

        if (cursor == null) {
            View rootView = inflater.inflate(R.layout.no_posts, container, false);
            return rootView;
        }
        else {
            View rootView = inflater.inflate(R.layout.content_fragment, container, false);
            RedditPost redditPost = RedditPost.createFromCursor(cursor);

            TextView titleView = (TextView) rootView.findViewById(R.id.post_title);
            titleView.setText(redditPost.title);
            TextView authorView = (TextView) rootView.findViewById(R.id.post_author);
            authorView.setText(redditPost.author);
            return rootView;
        }

    }
}
