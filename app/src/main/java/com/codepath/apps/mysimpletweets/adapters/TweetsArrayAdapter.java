package com.codepath.apps.mysimpletweets.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class TweetsArrayAdapter extends ArrayAdapter<Tweet> {

    public TweetsArrayAdapter(Context context, List<Tweet> tweets) {
        super(context, android.R.layout.simple_list_item_1, tweets);
    }

    private static class ViewHolder {
        ImageView profileImage;
        ImageView mediaImage;
        ImageView replyImage;
        TextView userName;
        TextView screenName;
        TextView timeStamp;
        TextView retweetCount;
        TextView favoriteCount;
        TextView body;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Tweet tweet = getItem(position);
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_tweet, parent, false);
            viewHolder.profileImage = (ImageView) convertView.findViewById(R.id.ivProfileImage);
            viewHolder.mediaImage = (ImageView) convertView.findViewById(R.id.ivMediaImage);
            viewHolder.replyImage = (ImageView) convertView.findViewById(R.id.ivReplyImage);
            viewHolder.userName = (TextView) convertView.findViewById(R.id.tvUsername);
            viewHolder.screenName = (TextView) convertView.findViewById(R.id.tvScreenName);
            viewHolder.timeStamp = (TextView) convertView.findViewById(R.id.tvTimeStamp);
            viewHolder.favoriteCount = (TextView) convertView.findViewById(R.id.tvFavoriteCount);
            viewHolder.retweetCount = (TextView) convertView.findViewById(R.id.tvRetweetCount);
            viewHolder.body = (TextView) convertView.findViewById(R.id.tvBody);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.replyImage.setTag(tweet);
        viewHolder.profileImage.setImageResource(0);
        viewHolder.mediaImage.setImageResource(0);
        Picasso.with(getContext()).load(tweet.getUser().getProfileImgUrl()).into(viewHolder.profileImage);

        if (tweet.getMediaUrl() != null) {
            Picasso.with(getContext()).load(tweet.getMediaUrl()).placeholder(R.drawable.placeholder_320).into(viewHolder.mediaImage);
            viewHolder.mediaImage.setVisibility(View.VISIBLE);
        } else {
            viewHolder.mediaImage.setVisibility(View.GONE);
        }
        viewHolder.userName.setText(tweet.getUser().getName());
        viewHolder.screenName.setText("@" + tweet.getUser().getScreenName());
        viewHolder.timeStamp.setText(getRelativeTimeStamp(tweet.getCreatedAt()));
        if (tweet.getFavoriteCount() > 0) {
            viewHolder.favoriteCount.setText(String.valueOf(tweet.getFavoriteCount()));
        } else {
            viewHolder.favoriteCount.setText("");
        }
        if (tweet.getRetweetCount() > 0) {
            viewHolder.retweetCount.setText(String.valueOf(tweet.getRetweetCount()));
        } else {
            viewHolder.retweetCount.setText("");
        }
        viewHolder.body.setText(tweet.getBody());
        return convertView;
    }

    public String getRelativeTimeStamp(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateSeconds = sf.parse(rawJsonDate).getTime() / 1000;
            return getRelativeTimeStamp(dateSeconds);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return relativeDate;
    }

    private String getRelativeTimeStamp(long timeStampSeconds) {
        long curTime = System.currentTimeMillis() / 1000;

        long elapsedTime = Math.max(curTime - timeStampSeconds, 0);
        int day = (int) TimeUnit.SECONDS.toDays(elapsedTime);
        long hours = TimeUnit.SECONDS.toHours(elapsedTime) - (day * 24);
        long minute = TimeUnit.SECONDS.toMinutes(elapsedTime) - (TimeUnit.SECONDS.toHours(elapsedTime)* 60);
        long second = TimeUnit.SECONDS.toSeconds(elapsedTime) - (TimeUnit.SECONDS.toMinutes(elapsedTime) * 60);

        if (day > 0) {
            return day + "d";
        } else if (hours > 0) {
            return hours + "h";
        } else if (minute > 0) {
            return minute + "m";
        } else {
            return second + "s";
        }
    }
}
