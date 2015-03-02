package com.codepath.apps.mysimpletweets.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.astuetz.PagerSlidingTabStrip;
import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.TwitterApplication;
import com.codepath.apps.mysimpletweets.TwitterClient;
import com.codepath.apps.mysimpletweets.adapters.SmartFragmentStatePagerAdapter;
import com.codepath.apps.mysimpletweets.fragments.BaseTimelineFragment;
import com.codepath.apps.mysimpletweets.fragments.HomeTimelineFragment;
import com.codepath.apps.mysimpletweets.fragments.MentionsTimelineFragment;
import com.codepath.apps.mysimpletweets.helpers.Utils;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.codepath.apps.mysimpletweets.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

public class TimelineActivity extends ActionBarActivity implements BaseTimelineFragment.OnItemSelectedListener {
    private static final int REQUEST_CODE_COMPOSE = 21;
    private TwitterClient client;
    private TweetsPagerAdapter tweetsPagerAdapter;
    private ViewPager pager;
    private User user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tweetsPagerAdapter = new TweetsPagerAdapter(getSupportFragmentManager());
        pager = (ViewPager) findViewById(R.id.viewpager);
        pager.setAdapter(tweetsPagerAdapter);
        PagerSlidingTabStrip tabStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabStrip.setViewPager(pager);
        client = TwitterApplication.getRestClient();
        getProfile();
    }

    private void getProfile() {
        if (!Utils.isNetworkAvailable(this)) {
            Utils.showNetworkErrorToast(TimelineActivity.this);
            return;
        }
        client.getProfile(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                user = User.createOrUpdate(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                if (errorResponse != null)
                    Log.d("DEBUG", errorResponse.toString());
                Utils.showNetworkErrorToast(TimelineActivity.this);
            }

            public void onFailure(int statusCode, Header[] headers, String errorString, Throwable throwable) {
                if (errorString != null)
                    Log.d("DEBUG", errorString);
                Utils.showNetworkErrorToast(TimelineActivity.this);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                if (errorResponse != null)
                    Log.d("DEBUG", errorResponse.toString());
                Utils.showNetworkErrorToast(TimelineActivity.this);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_timeline, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_compose) {
            compose(null);
        } else if (id == R.id.action_profile) {
            profile();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_COMPOSE) {
            // Extract name value from result extras
            Tweet tweet = (Tweet) data.getSerializableExtra("tweet");
            BaseTimelineFragment baseTimelineFragment =
                    (BaseTimelineFragment) tweetsPagerAdapter.getRegisteredFragment(0);
            baseTimelineFragment.insert(tweet, 0);
        }
    }

    public void profile() {
        Intent i = new Intent(this, ProfileActivity.class);
        i.putExtra("user", user);
        startActivity(i);
    }

    public void compose(Tweet tweetToReplyTo) {
        if (user == null || !Utils.isNetworkAvailable(this)) {
            Utils.showNetworkErrorToast(this);
            return;
        }
        Intent i = new Intent(this, ComposeActivity.class);
        i.putExtra("user", user);
        if (tweetToReplyTo != null) {
            i.putExtra("tweet", tweetToReplyTo);
        }
        startActivityForResult(i, REQUEST_CODE_COMPOSE);
    }

    public void replyTweet(Tweet tweet) {
        compose(tweet);
    }

    public class TweetsPagerAdapter extends SmartFragmentStatePagerAdapter {
        final int PAGE_COUNT = 2;
        private String[] tabTitles = {"Home", "Mentions"};
        public TweetsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return new HomeTimelineFragment();
            } else if (position == 1) {
                return new MentionsTimelineFragment();
            } else {
                return null;
            }
        }
        @Override
        public int getCount() {
            return tabTitles.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }
    }
}
