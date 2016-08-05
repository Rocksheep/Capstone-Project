package nl.codesheep.android.pagesforreddit;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
    private RedditPost mRedditPost;

    public ContentFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mInflater = inflater;
        View rootView = mInflater.inflate(R.layout.content_fragment, container, false);
        mContainer = (LinearLayout) rootView.findViewById(R.id.post_container);

        Bundle bundle = getArguments();
        mRedditPost = bundle.getParcelable("post");


        Log.d(TAG, "Loading image: " + mRedditPost.imageUrl);

        if (mRedditPost.thumbnailUrl != null) {
            Log.d(TAG, "Loading thumbnail " + mRedditPost.thumbnailUrl);
            ImageView imageView = (ImageView) mContainer.findViewById(R.id.post_image);
            imageView.setVisibility(View.VISIBLE);
            if (mRedditPost.videoUrl != null) {
                Log.d(TAG, "Video found");
                mContainer.findViewById(R.id.post_video).setVisibility(View.VISIBLE);
            }
            Picasso.with(getContext()).load(mRedditPost.thumbnailUrl.replace("&amp;", "&")).into(imageView);
        }
        else if (mRedditPost.selftext != null && !mRedditPost.selftext.equals("")) {
            mContainer.findViewById(R.id.post_image).setVisibility(View.GONE);
            TextView selfTextView = (TextView) mContainer.findViewById(R.id.post_selftext);
            selfTextView.setText(mRedditPost.selftext);
            selfTextView.setVisibility(View.VISIBLE);
        }

        Log.d(TAG, "Full link: " + mRedditPost.permalink);
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(DetailsListingResponse.RedditComment.class, new RedditCommentDeserializer());
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RedditService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gsonBuilder.create()))
                .build();
        RedditService.ListingCalls service = retrofit.create(RedditService.ListingCalls.class);
        service.details(mRedditPost.permalink).enqueue(this);


        TextView titleView = (TextView) mContainer.findViewById(R.id.post_title);
        titleView.setText(mRedditPost.title);
        titleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRedditPost.url != null && !mRedditPost.url.equals("")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mRedditPost.url));
                    startActivity(intent);
                }
            }
        });
        TextView authorView = (TextView) mContainer.findViewById(R.id.post_author);
        String details = String.format(getContext().getString(R.string.post_details), mRedditPost.author, "2 hours ago", mRedditPost.subreddit);
        authorView.setText(details);
        TextView numCommentsView = (TextView) mContainer.findViewById(R.id.post_comments);
        String numComments = String.format(getContext().getString(R.string.num_comments), mRedditPost.numComments);
        numCommentsView.setText(numComments);
        TextView points = (TextView) mContainer.findViewById(R.id.post_points);
        points.setText(Integer.toString(mRedditPost.ups - mRedditPost.downs));

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
