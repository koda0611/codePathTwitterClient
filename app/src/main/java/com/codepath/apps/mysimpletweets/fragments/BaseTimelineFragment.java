package com.codepath.apps.mysimpletweets.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.TwitterApplication;
import com.codepath.apps.mysimpletweets.TwitterClient;
import com.codepath.apps.mysimpletweets.adapters.TweetsArrayAdapter;
import com.codepath.apps.mysimpletweets.helpers.EndlessScrollListener;
import com.codepath.apps.mysimpletweets.helpers.Utils;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alvin on 2/24/15.
 */
public abstract class BaseTimelineFragment extends Fragment {
    protected TweetsArrayAdapter aTweets;
    protected SwipeRefreshLayout swipeContainer;
    protected ArrayList<Tweet> tweets;
    protected ListView lvTweets;
    protected TwitterClient client;
    protected OnItemSelectedListener listener;
    private boolean forceOfflineMode = false;

    public enum FetchMode {
        NEW_TWEETS, OLD_TWEETS
    }

    public interface OnItemSelectedListener {
        void replyTweet(Tweet tweet);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof OnItemSelectedListener) {
            listener = (OnItemSelectedListener) activity;
        } else {
            throw new ClassCastException(activity.toString()
                    + " must implement OnItemSelectedListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tweets_list, container, false);

        lvTweets = (ListView) v.findViewById(R.id.lvTweets);
        lvTweets.setAdapter(aTweets);

        swipeContainer = (SwipeRefreshLayout) v.findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                populateTimeline(FetchMode.NEW_TWEETS);
            }
        });

        lvTweets.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                populateTimeline(FetchMode.OLD_TWEETS);
                Log.d("Debug", "onLoadMore, totalItemsCount:" + totalItemsCount);
            }
        });

        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        populateTimeline(FetchMode.NEW_TWEETS);
        return v;
    }

    protected void populateTimeline(FetchMode mode){
        if (!Utils.isNetworkAvailable(getActivity())){
            Utils.showNetworkErrorToast(getActivity());
            return;
        }
        if (mode == FetchMode.NEW_TWEETS) {
            Tweet.deleteAll();
            clearTweets();
        }
        fetchTimeline(mode);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = TwitterApplication.getRestClient();
        tweets = new ArrayList<>();
        aTweets = new TweetsArrayAdapter(getActivity(), tweets, listener);
    }

    public void insert(Tweet tweet, int index) {
        aTweets.insert(tweet, index);
    }

    public void addAll(List<Tweet> tweets) {
        aTweets.addAll(tweets);
    }

    public void clearTweets() {
        aTweets.clear();
    }

    public void setRefreshing(boolean refreshing) {
        swipeContainer.setRefreshing(refreshing);
    }

    protected long getMaxId(FetchMode mode) {
        if (mode == FetchMode.NEW_TWEETS)
            return 0;
        else {
            return tweets.get(tweets.size() - 1).getUid() - 1;
        }
    }

    public JsonHttpResponseHandler createJsonHandler() {

        JsonHttpResponseHandler handler = new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.d("DEBUG", response.toString());
                addAll(Tweet.fromJson(response));
                setRefreshing(false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                if (errorResponse != null)
                    Log.d("DEBUG", errorResponse.toString());
                setRefreshing(false);
                Utils.showNetworkErrorToast(getActivity());
            }
        };
        return handler;
    }

    protected abstract void fetchTimeline(FetchMode mode);
}
