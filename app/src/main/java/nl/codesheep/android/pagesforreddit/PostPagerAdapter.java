package nl.codesheep.android.pagesforreddit;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

import nl.codesheep.android.pagesforreddit.data.models.RedditPost;

/**
 * Created by Rien on 03/08/2016.
 */
public class PostPagerAdapter extends SmartFragmentStatePagerAdapter {

    List<RedditPost> posts;

    public PostPagerAdapter(FragmentManager fm) {
        super(fm);
        posts = new ArrayList<RedditPost>();
    }

    @Override
    public Fragment getItem(int position) {
        ContentFragment contentFragment = new ContentFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("post", posts.get(position));
        contentFragment.setArguments(bundle);

        return contentFragment;
    }

    @Override
    public int getCount() {
        return posts.size();
    }

    public void addPost(RedditPost post) {
        this.posts.add(post);
        notifyDataSetChanged();
    }

    public void setPosts(List<RedditPost> posts) {
        this.posts = posts;
        notifyDataSetChanged();
    }
}
