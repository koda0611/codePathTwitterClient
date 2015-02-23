package com.codepath.apps.mysimpletweets.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Table(name = "Tweets")
public class Tweet extends Model implements Serializable {
    @Column(name = "body")
    String body;
    @Column(name = "uid", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    long uid; //Unique id for tweet
    @Column(name = "retweet_count")
    int retweetCount;
    @Column(name = "favorite_count")
    int favoriteCount;
    @Column(name = "user")
    User user;
    @Column(name = "created_at")
    String createdAt;
    @Column(name = "media_url")
    String mediaUrl;


    public String getBody() {
        return body;
    }

    public long getUid() {
        return uid;
    }

    public int getRetweetCount() { return retweetCount; }

    public int getFavoriteCount() { return favoriteCount; }

    public String getCreatedAt() {
        return createdAt;
    }

    public User getUser() {
        return user;
    }

    public String getMediaUrl() { return mediaUrl; }

    // Make sure to always define this constructor with no arguments
    public Tweet() {
        super();
    }

    public static Tweet fromJson(JSONObject jsonObject) {
        Tweet tweet = new Tweet();
        // Deserialize json into object fields
        try {
            tweet.body = jsonObject.getString("text");
            tweet.uid = jsonObject.getLong("id");
            tweet.favoriteCount = jsonObject.getInt("favorite_count");
            tweet.retweetCount = jsonObject.getInt("retweet_count");
            tweet.createdAt = jsonObject.getString("created_at");
            tweet.user = User.createOrUpdate(jsonObject.getJSONObject("user"));
            JSONArray media = jsonObject.getJSONObject("entities").optJSONArray("media");
            if (media != null && media.length() > 0) {
                tweet.mediaUrl = media.getJSONObject(0).getString("media_url");
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        tweet.save();
        return tweet;
    }

    public static ArrayList<Tweet> fromJson(JSONArray jsonArray) {
        ArrayList<Tweet> results = new ArrayList<>();

        for (int i=0; i < jsonArray.length(); i++) {
            JSONObject resultJson;
            try {
                resultJson = jsonArray.getJSONObject(i);
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
            Tweet result = Tweet.fromJson(resultJson);
            if (result != null) {
                results.add(result);
            }
        }
        return results;
    }

    public static ArrayList<Tweet> getCachedTweets() {
        List<Tweet> ret = new Select().from(Tweet.class).orderBy("uid DESC").execute();

        return new ArrayList<>(ret);
    }

    public static void deleteAll() {
        new Delete().from(Tweet.class).execute();
    }
}
