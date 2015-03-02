package com.codepath.apps.mysimpletweets.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.query.Select;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class User extends Model implements Serializable {
    @Column(name = "name")
    String name;
    @Column(name = "uid", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    long uid;
    @Column(name = "screen_name")
    String screenName;
    @Column(name = "profile_img_url")
    String profileImgUrl;
    @Column(name = "profile_banner_img_url")
    String profileBannerImgUrl;
    @Column(name = "followers_count")
    int followersCount;
    @Column(name = "friends_count")
    int friendsCount;
    @Column(name = "statuses_count")
    int statusesCount;
    @Column(name = "description")
    String description;

    public String getProfileBannerImgUrl() {
        return profileBannerImgUrl;
    }

    public int getFollowersCount() {
        return followersCount;
    }

    public int getFriendsCount() {
        return friendsCount;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public long getUid() {
        return uid;
    }

    public String getScreenName() {
        return screenName;
    }

    public String getProfileImgUrl() {
        return profileImgUrl;
    }

    public static User byUid(Long uid) {
        return new Select().from(User.class).where("uid = ?", uid).executeSingle();
    }

    public int getStatusesCount() {
        return statusesCount;
    }

    public static User createOrUpdate(JSONObject jsonObject) {
        User user = null;
        try {
            long uid = jsonObject.getLong("id");
            user = User.byUid(uid);
            if (user == null) {
                user = new User();
            }
            user.name = jsonObject.getString("name");
            user.uid = jsonObject.getLong("id");
            user.screenName = jsonObject.getString("screen_name");
            user.profileImgUrl = jsonObject.getString("profile_image_url");
            user.profileBannerImgUrl = jsonObject.optString("profile_banner_url", null);
            user.friendsCount = jsonObject.getInt("friends_count");
            user.followersCount = jsonObject.getInt("followers_count");
            user.statusesCount = jsonObject.getInt("statuses_count");
            user.description = jsonObject.getString("description");
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        user.save();
        return user;
    }
}
