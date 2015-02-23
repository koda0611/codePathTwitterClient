package com.codepath.apps.mysimpletweets.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.TwitterApplication;
import com.codepath.apps.mysimpletweets.TwitterClient;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.codepath.apps.mysimpletweets.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;
import org.json.JSONObject;

public class ComposeActivity extends ActionBarActivity {
    private TwitterClient client;
    private TextView tvUsername;
    private TextView tvScreenName;
    private TextView tvCharsLeft;
    private EditText etTweet;
    private Tweet tweetToReplyTo;
    private static final int MAX_CHARS_IN_TWEET = 140;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);
        client = TwitterApplication.getRestClient();
        Toolbar toolbar = (Toolbar) findViewById(R.id.tbCompose);
        toolbar.setTitle("");
        toolbar.setNavigationIcon(R.drawable.ic_action_back);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        User user = (User) getIntent().getSerializableExtra("user");
        tweetToReplyTo = (Tweet) getIntent().getSerializableExtra("tweet");
        tvUsername = (TextView) findViewById(R.id.tvComposeUserName);
        tvScreenName = (TextView) findViewById(R.id.tvComposeScreenName);
        ImageView ivProfile = (ImageView) findViewById(R.id.ivComposeProfileImage);
        tvCharsLeft = new TextView(this);
        tvUsername.setText(user.getName());
        tvScreenName.setText("@" + user.getScreenName());
        setupTweetInput();
        Picasso.with(this).load(user.getProfileImgUrl()).into(ivProfile);
    }

    private void setupTweetInput() {
        etTweet = (EditText) findViewById(R.id.etTweet);
        etTweet.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                tvCharsLeft.setText(String.valueOf(MAX_CHARS_IN_TWEET - etTweet.getText().length()));
            }
        });
        if (tweetToReplyTo != null) {
            etTweet.setText("@" + tweetToReplyTo.getUser().getScreenName());
            etTweet.setSelection(etTweet.getText().length());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_compose, menu);

        TextView tvTweet = new TextView(this);
        tvTweet.setText(R.string.action_tweet);
        tvTweet.setTextColor(getResources().getColor(R.color.white));
        tvTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postTweet();
            }
        });
        tvTweet.setPadding(5, 5, 20, 5);
        tvTweet.setTypeface(null, Typeface.BOLD);
        tvTweet.setTextSize(14);

        tvCharsLeft.setText(String.valueOf(MAX_CHARS_IN_TWEET));
        tvCharsLeft.setPadding(5, 5, 20, 5);
        tvCharsLeft.setTextColor(getResources().getColor(R.color.light_grey));
        tvCharsLeft.setTextSize(14);

        menu.add(0, Menu.NONE, 2, R.string.action_tweet).setActionView(tvTweet).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menu.add(0, Menu.NONE, 1, R.string.action_tweet).setActionView(tvCharsLeft).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    private void postTweet() {
        long replyStatusId = 0;
        if (tweetToReplyTo != null) {
            replyStatusId = tweetToReplyTo.getUid();
        }
        client.postTweet(etTweet.getText().toString(), replyStatusId, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("DEBUG", response.toString());
                Tweet tweet = Tweet.fromJson(response);
                Intent i = new Intent();
                i.putExtra("tweet", tweet);
                setResult(RESULT_OK, i);
                finish();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", errorResponse.toString());
            }
        });
    }
}
