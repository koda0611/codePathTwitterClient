package com.codepath.apps.mysimpletweets.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.TwitterApplication;
import com.codepath.apps.mysimpletweets.TwitterClient;
import com.codepath.apps.mysimpletweets.adapters.TweetsArrayAdapter;
import com.codepath.apps.mysimpletweets.helpers.EndlessScrollListener;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.codepath.apps.mysimpletweets.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class TimelineActivity extends ActionBarActivity {
    private static final int REQUEST_CODE_COMPOSE = 21;
    private TwitterClient client;
    private TweetsArrayAdapter aTweets;
    private SwipeRefreshLayout swipeContainer;
    private ArrayList<Tweet> tweets;
    private ListView lvTweets;
    private boolean forceOfflineMode = false;
    private User user;

    public void replyTweet(View view) {
        Tweet tweet = (Tweet) view.getTag();
        compose(tweet);
    }

    public enum FetchMode {
        NEW_TWEETS, OLD_TWEETS
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        lvTweets = (ListView) findViewById(R.id.lvTweets);
        tweets = new ArrayList<>();
        aTweets = new TweetsArrayAdapter(this, tweets);
        lvTweets.setAdapter(aTweets);
        client = TwitterApplication.getRestClient();

        lvTweets.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                populateTimeline(FetchMode.OLD_TWEETS);
                Log.d("Debug", "onLoadMore, totalItemsCount:" + totalItemsCount);
            }
        });
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                populateTimeline(FetchMode.NEW_TWEETS);
            }
        });
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        getProfile();
        aTweets.addAll(Tweet.getCachedTweets());
        populateTimeline(FetchMode.NEW_TWEETS);
    }

    private long getMaxId() {
        return tweets.get(tweets.size() - 1).getUid() - 1;
    }

    private void populateTimeline(final FetchMode mode) {
        if (forceOfflineMode) {
            return;
        }
        long maxId = 0;
        long sinceId = 0;

        if (mode == FetchMode.OLD_TWEETS) {
            maxId = getMaxId();
        }

        client.getHomeTimeline(maxId, sinceId, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.d("DEBUG", response.toString());
                if (mode == FetchMode.NEW_TWEETS) {
                    Tweet.deleteAll();
                    aTweets.clear();
                }
                aTweets.addAll(Tweet.fromJson(response));
                swipeContainer.setRefreshing(false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", errorResponse.toString());
                swipeContainer.setRefreshing(false);
                showNetworkErrorToast();
            }
        });
    }

    private void getProfile() {
        client.getProfile(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                user = User.createOrUpdate(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", errorResponse.toString());
                showNetworkErrorToast();
            }

            public void onFailure(int statusCode, Header[] headers, String errorString, Throwable throwable) {
                Log.d("DEBUG", errorString);
                showNetworkErrorToast();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Log.d("DEBUG", errorResponse.toString());
                showNetworkErrorToast();
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
            if (user != null) {
                compose(null);
                return true;
            } else {
                showNetworkErrorToast();
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_COMPOSE) {
            // Extract name value from result extras
            Tweet tweet = (Tweet) data.getSerializableExtra("tweet");
            aTweets.insert(tweet, 0);
        }
    }

    private void compose(Tweet tweetToReplyTo) {
        Intent i = new Intent(this, ComposeActivity.class);
        i.putExtra("user", user);
        if (tweetToReplyTo != null) {
            i.putExtra("tweet", tweetToReplyTo);
        }
        startActivityForResult(i, REQUEST_CODE_COMPOSE);
    }

    private void showNetworkErrorToast() {
        Toast.makeText(this, "Network unavailable please try again later", Toast.LENGTH_SHORT).show();
    }
}
