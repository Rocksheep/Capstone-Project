package nl.codesheep.android.pagesforreddit;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import nl.codesheep.android.pagesforreddit.data.RedditProvider;
import nl.codesheep.android.pagesforreddit.data.SubRedditsTable;
import nl.codesheep.android.pagesforreddit.data.models.SubReddit;
import nl.codesheep.android.pagesforreddit.sync.redditapi.ListingResponse;
import nl.codesheep.android.pagesforreddit.sync.redditapi.RedditService;
import nl.codesheep.android.pagesforreddit.views.DividerItemDecoration;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SubRedditList extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int SUBREDDIT_LOADER = 0;
    private static final String TAG = SubRedditList.class.getSimpleName();
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private SubRedditListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_reddit_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRecyclerView = (RecyclerView) findViewById(R.id.subreddit_list);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));

        mAdapter = new SubRedditListAdapter();
        mRecyclerView.setAdapter(mAdapter);

        getSupportLoaderManager().initLoader(SUBREDDIT_LOADER, null, this);

        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                SubReddit subReddit = mAdapter.getSubReddit(viewHolder.getAdapterPosition());
                getContentResolver().delete(
                        RedditProvider.SubReddits.SUBREDDITS,
                        SubRedditsTable.ID + " = ?",
                        new String[] {Long.toString(subReddit.getId())}
                );
            }

        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddSubRedditDialog();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSupportLoaderManager().restartLoader(SUBREDDIT_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == SUBREDDIT_LOADER) {
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
        if (loader.getId() == SUBREDDIT_LOADER) {
            List<SubReddit> subReddits = new ArrayList<>();
            while (data.moveToNext()) {
                subReddits.add(SubReddit.createFromCursor(data));
            }
            mAdapter.setList(subReddits);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    public class SubRedditListAdapter extends RecyclerView.Adapter<ViewHolder> {

        private List<SubReddit> mSubReddits = new ArrayList<>();

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.subreddit_list_item, parent, false);
            ViewHolder viewHolder = new ViewHolder((TextView) view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.mTextview.setText(mSubReddits.get(position).getName());
        }

        @Override
        public int getItemCount() {
            return mSubReddits.size();
        }

        public void setList(List<SubReddit> subReddits) {
            mSubReddits = subReddits;
            notifyDataSetChanged();
        }

        public SubReddit getSubReddit(int position) {
            return mSubReddits.get(position);
        }

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTextview;

        public ViewHolder(TextView textView) {
            super(textView);
            this.mTextview = textView;
        }
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
                                    Toast.makeText(SubRedditList.this, getString(R.string.subreddit_not_found), Toast.LENGTH_SHORT)
                                            .show();
                                }
                                dialog.dismiss();
                                Log.d(TAG, "Success, code: " + response.code());
                            }

                            @Override
                            public void onFailure(Call<ListingResponse> call, Throwable t) {
                                Toast.makeText(SubRedditList.this,getString(R.string.couldnt_reach_server), Toast.LENGTH_SHORT)
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
}
