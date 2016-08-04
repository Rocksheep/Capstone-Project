package nl.codesheep.android.pagesforreddit;

import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import nl.codesheep.android.pagesforreddit.data.RedditProvider;
import nl.codesheep.android.pagesforreddit.data.SubRedditsTable;
import nl.codesheep.android.pagesforreddit.data.models.SubReddit;
import nl.codesheep.android.pagesforreddit.views.DividerItemDecoration;

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
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
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
}
