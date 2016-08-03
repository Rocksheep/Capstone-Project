package nl.codesheep.android.pagesforreddit;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Type;
import java.util.List;

import nl.codesheep.android.pagesforreddit.data.RedditPostsTable;
import nl.codesheep.android.pagesforreddit.data.RedditProvider;
import nl.codesheep.android.pagesforreddit.data.models.RedditPost;
import nl.codesheep.android.pagesforreddit.sync.redditapi.DetailsListingResponse;
import nl.codesheep.android.pagesforreddit.sync.redditapi.RedditService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ContentFragment extends Fragment implements Callback<List<DetailsListingResponse>> {

    public static final String TAG = ContentFragment.class.getSimpleName();
    private static final int URL_LOADER = 0;
    private LayoutInflater mInflater;
    private LinearLayout mContainer;

    public ContentFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mInflater = inflater;
//        getLoaderManager().initLoader(URL_LOADER, null, this);

//        if (cursor == null) {
//            View rootView = inflater.inflate(R.layout.no_posts, container, false);
//            return rootView;
//        }
//        else {
            View rootView = mInflater.inflate(R.layout.content_fragment, container, false);
            mContainer = (LinearLayout) rootView.findViewById(R.id.post_container);

            Bundle bundle = getArguments();
            RedditPost redditPost = bundle.getParcelable("post");


        Log.d(TAG, "Loading image: " + redditPost.imageUrl);

        if (redditPost.thumbnailUrl != null) {
            Log.d(TAG, "Loading thumbnail " + redditPost.thumbnailUrl);
            ImageView imageView = (ImageView) mContainer.findViewById(R.id.post_image);
            Picasso.with(getContext()).load(redditPost.thumbnailUrl.replace("&amp;", "&")).into(imageView);
        }

        Log.d(TAG, "Full link: " + redditPost.permalink);
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(DetailsListingResponse.RedditComment.class, new RedditCommentDeserializer());
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RedditService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gsonBuilder.create()))
                .build();
        RedditService.ListingCalls service = retrofit.create(RedditService.ListingCalls.class);
        service.details(redditPost.permalink).enqueue(this);


        TextView titleView = (TextView) mContainer.findViewById(R.id.post_title);
        titleView.setText(redditPost.title);
        TextView authorView = (TextView) mContainer.findViewById(R.id.post_author);
        String details = String.format(getContext().getString(R.string.post_details), redditPost.author, "2 hours ago", redditPost.subreddit);
        authorView.setText(details);
        TextView numCommentsView = (TextView) mContainer.findViewById(R.id.post_comments);
        String numComments = String.format(getContext().getString(R.string.num_comments), redditPost.numComments);
        numCommentsView.setText(numComments);
        TextView points = (TextView) mContainer.findViewById(R.id.post_points);
        points.setText(Integer.toString(redditPost.ups - redditPost.downs));

        return rootView;
//        }

    }

    @Override
    public void onResponse(Call<List<DetailsListingResponse>> call, Response<List<DetailsListingResponse>> response) {

            Log.d(TAG, call.request().url().toString());

        List<DetailsListingResponse> details = response.body();
        if (details != null) {
            List<DetailsListingResponse.RedditCommentListing> commentListings = details.get(1).listing.commentListings;
            int commentLevel = 0;
            appendCommentsToView(commentListings, commentLevel);
        }
        else {
            Log.d(TAG, "No comments");
        }
    }

    @Override
    public void onFailure(Call<List<DetailsListingResponse>> call, Throwable t) {
        Log.e(TAG, "Couldn't get data");
        t.printStackTrace();
    }

    private void appendCommentsToView(List<DetailsListingResponse.RedditCommentListing> commentListings, int commentLevel) {
        for (DetailsListingResponse.RedditCommentListing listing : commentListings) {
            DetailsListingResponse.RedditComment comment = listing.comment;
            Log.d(TAG, "Comment: " + comment.body);
            CardView commentView = (CardView) mInflater.inflate(R.layout.comment, mContainer, false);
            if (commentLevel > 0) {
                ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) commentView.getLayoutParams();
                layoutParams.leftMargin = 16 * commentLevel;
            }
            TextView author = (TextView) commentView.findViewById(R.id.comment_author);
            author.setText(comment.author);
            TextView body = (TextView) commentView.findViewById(R.id.comment_body);
            body.setText(comment.body);
            mContainer.addView(commentView);
            if (comment.repliesListing != null) {
                Log.d(TAG, "More comments found");
                appendCommentsToView(comment.repliesListing.listing.commentListings, commentLevel + 1);
            }
        }
    }

//    @Override
//    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
//        switch (id) {
//            case URL_LOADER:
//                return new CursorLoader(
//                        getActivity(),
//                        RedditProvider.Posts.POSTS,
//                        RedditPostsTable.PROJECTION,
//                        null,
//                        null,
//                        null
//                );
//        }
//        return null;
//    }

//    @Override
//    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

//        if (cursor.moveToNext()) {
//            mContainer.findViewById(R.id.post).setVisibility(View.VISIBLE);
//            RedditPost redditPost = RedditPost.createFromCursor(cursor);
//            Log.d(TAG, "Loading image: " + redditPost.imageUrl);
//
//            if (redditPost.thumbnailUrl != null) {
//                Log.d(TAG, "Loading thumbnail " + redditPost.thumbnailUrl);
//                ImageView imageView = (ImageView) mContainer.findViewById(R.id.post_image);
//                Picasso.with(getContext()).load(redditPost.thumbnailUrl.replace("&amp;", "&")).into(imageView);
//            }
//
//            Log.d(TAG, "Full link: " + redditPost.permalink);
//            GsonBuilder gsonBuilder = new GsonBuilder();
//            gsonBuilder.registerTypeAdapter(DetailsListingResponse.RedditComment.class, new RedditCommentDeserializer());
//            Retrofit retrofit = new Retrofit.Builder()
//                    .baseUrl(RedditService.BASE_URL)
//                    .addConverterFactory(GsonConverterFactory.create(gsonBuilder.create()))
//                    .build();
//            RedditService.ListingCalls service = retrofit.create(RedditService.ListingCalls.class);
//            service.details(redditPost.permalink).enqueue(this);
//
//
//            TextView titleView = (TextView) mContainer.findViewById(R.id.post_title);
//            titleView.setText(redditPost.title);
//            TextView authorView = (TextView) mContainer.findViewById(R.id.post_author);
//            String details = String.format(getContext().getString(R.string.post_details), redditPost.author, "2 hours ago", redditPost.subreddit);
//            authorView.setText(details);
//            TextView numCommentsView = (TextView) mContainer.findViewById(R.id.post_comments);
//            String numComments = String.format(getContext().getString(R.string.num_comments), redditPost.numComments);
//            numCommentsView.setText(numComments);
//            TextView points = (TextView) mContainer.findViewById(R.id.post_points);
//            points.setText(Integer.toString(redditPost.ups - redditPost.downs));
//        }
//    }
//
//    @Override
//    public void onLoaderReset(Loader<Cursor> loader) {
//
//    }

    private class RedditCommentDeserializer implements JsonDeserializer<DetailsListingResponse.RedditComment>{

        @Override
        public DetailsListingResponse.RedditComment deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            Log.d(TAG, "Testing if object " + json.isJsonObject());
            Log.d(TAG, "Used json: " + json.toString());
            JsonObject object = json.getAsJsonObject();
            JsonElement replies = object.get("replies");

            Gson gson = new GsonBuilder()
                    .excludeFieldsWithoutExposeAnnotation().create();
            DetailsListingResponse.RedditComment comment = gson.fromJson(object, DetailsListingResponse.RedditComment.class);
            if (replies != null && replies.isJsonObject()) {
                DetailsListingResponse listing = context.deserialize(replies, DetailsListingResponse.class);
                comment.repliesListing = listing;
            }
            else {
                comment.repliesListing = null;
            }

            return comment;
        }
    }
}
