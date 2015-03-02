package com.codepath.apps.mysimpletweets.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.fragments.BaseTimelineFragment;
import com.codepath.apps.mysimpletweets.fragments.UserDescriptionFragment;
import com.codepath.apps.mysimpletweets.fragments.UserInfoFragment;
import com.codepath.apps.mysimpletweets.fragments.UserTimelineFragment;
import com.codepath.apps.mysimpletweets.helpers.Utils;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.codepath.apps.mysimpletweets.models.User;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends ActionBarActivity implements BaseTimelineFragment.OnItemSelectedListener {
    private UserTimelineFragment userTimelineFragment;
    private User user;
    private static final int REQUEST_CODE_COMPOSE = 21;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        user = (User) getIntent().getSerializableExtra("user");
        Toolbar toolbar = (Toolbar) findViewById(R.id.tbProfile);
        toolbar.setTitle("Profile");
        setSupportActionBar(toolbar);
        final ImageView ivProfileBackground = (ImageView) findViewById(R.id.ivProfileBackgroud);
        if (user.getProfileBannerImgUrl() == null) {
            Picasso.with(this).load(R.drawable.default_profile_background).into(ivProfileBackground);
        } else {
            Picasso.with(this).load(user.getProfileBannerImgUrl()).into(ivProfileBackground);
        }

        TextView tvTweetCount = (TextView) findViewById(R.id.tvTweetCount);
        TextView tvFollowing = (TextView) findViewById(R.id.tvFollowing);
        TextView tvFollowers = (TextView) findViewById(R.id.tvFollowers);

        tvFollowers.setText(Utils.withSuffix(user.getFollowersCount()));
        tvFollowing.setText(Utils.withSuffix(user.getFriendsCount()));
        tvTweetCount.setText(Utils.withSuffix(user.getStatusesCount()));


        if (savedInstanceState == null) {
            userTimelineFragment = UserTimelineFragment.newInstance(user);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.flContainer, userTimelineFragment);
            ft.commit();
        }

        ViewPager pager = (ViewPager) findViewById(R.id.viewpager);
        pager.setAdapter(new ProfilePagerAdapter(user, getSupportFragmentManager()));
        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                if (position == 1) {
                    ivProfileBackground.setColorFilter(Color.argb(120, 0, 0, 0));
                } else {
                    ivProfileBackground.clearColorFilter();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    public class ProfilePagerAdapter extends FragmentPagerAdapter {
        User user;
        public ProfilePagerAdapter(User user, FragmentManager fm) {
            super(fm);
            this.user = user;
        }
        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return UserInfoFragment.newInstance(user);
            } else if (position == 1) {
                return UserDescriptionFragment.newInstance(user);
            } else {
                return null;
            }
        }
        @Override
        public int getCount() {
            return 2;
        }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_COMPOSE) {
        }
    }
}
