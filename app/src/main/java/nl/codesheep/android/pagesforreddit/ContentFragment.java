package nl.codesheep.android.pagesforreddit;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;

import nl.codesheep.android.pagesforreddit.data.RedditPostsTable;
import nl.codesheep.android.pagesforreddit.data.RedditProvider;
import nl.codesheep.android.pagesforreddit.data.models.RedditPost;
import nl.codesheep.android.pagesforreddit.sync.redditapi.DetailsListingResponse;
import nl.codesheep.android.pagesforreddit.sync.redditapi.ListingResponse;
import nl.codesheep.android.pagesforreddit.sync.redditapi.RedditService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ContentFragment extends Fragment implements Callback<List<DetailsListingResponse>> {

    public static final String TAG = ContentFragment.class.getSimpleName();

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
            if (cursor.moveToNext()) {
                RedditPost redditPost = RedditPost.createFromCursor(cursor);
                Log.d(TAG, "Loading image: " + redditPost.imageUrl);

                if (redditPost.thumbnailUrl != null) {
                    Log.d(TAG, "Loading thumbnail " + redditPost.thumbnailUrl);
                    ImageView imageView = (ImageView) rootView.findViewById(R.id.post_image);
                    Picasso.with(getContext()).load(redditPost.thumbnailUrl.replace("&amp;", "&")).into(imageView);
                }

                Log.d(TAG, "Full link: " + redditPost.permalink);
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(RedditService.BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                RedditService.ListingCalls service = retrofit.create(RedditService.ListingCalls.class);
                service.details(redditPost.permalink).enqueue(this);

                TextView titleView = (TextView) rootView.findViewById(R.id.post_title);
                titleView.setText(redditPost.title);
                TextView authorView = (TextView) rootView.findViewById(R.id.post_author);
                String details = String.format(getContext().getString(R.string.post_details), redditPost.author, "2 hours ago", redditPost.subreddit);
                authorView.setText(details);
                TextView numCommentsView = (TextView) rootView.findViewById(R.id.post_comments);
                String numComments = String.format(getContext().getString(R.string.num_comments), redditPost.numComments);
                numCommentsView.setText(numComments);
                TextView points = (TextView) rootView.findViewById(R.id.post_points);
                points.setText(Integer.toString(redditPost.ups - redditPost.downs));
            }
            cursor.close();
            return rootView;
        }

    }

    @Override
    public void onResponse(Call<List<DetailsListingResponse>> call, Response<List<DetailsListingResponse>> response) {

            Log.d(TAG, call.request().url().toString());

        List<DetailsListingResponse> details = response.body();
        if (details != null) {
            List<DetailsListingResponse.RedditComment> comments = details.get(0).listing.comments;
            for (DetailsListingResponse.RedditComment comment : comments) {
                Log.d(TAG, "Comment: " + comment.body);
            }
        }
        else {
            Log.d(TAG, "No comments");
        }
    }

    @Override
    public void onFailure(Call<List<DetailsListingResponse>> call, Throwable t) {

    }
}
