package org.bootcamp.fiftytwo.models;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.bootcamp.fiftytwo.utils.Constants;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static org.bootcamp.fiftytwo.utils.Constants.DISPLAY_NAME;
import static org.bootcamp.fiftytwo.utils.Constants.TAG;
import static org.bootcamp.fiftytwo.utils.Constants.USER_AVATAR_URI;
import static org.bootcamp.fiftytwo.utils.Constants.USER_ID;
import static org.bootcamp.fiftytwo.utils.Constants.USER_PREFS;
import static org.bootcamp.fiftytwo.utils.PlayerUtils.getDefaultAvatar;

@Parcel(analyze = User.class)
public class User {

    private String userId; //match this with userId of Parse to keep them unique
    private String displayName;
    private String avatarUri;
    private List<Card> cards = new ArrayList<>();
    private boolean isDealer;

    public User() {}

    public User(String avatarUri, String displayName) {
        this.avatarUri = avatarUri;
        this.displayName = displayName;
    }

    public User(String avatarUri, String displayName, String userId) {
        this.avatarUri = avatarUri;
        this.displayName = displayName;
        this.userId = userId;
    }

    public static User fromJson(JSONObject json) {
        try {
            String displayName = json.getString(DISPLAY_NAME);
            String avatarUri = json.getString(USER_AVATAR_URI);
            String userId = json.getString(USER_ID);
            Log.d(TAG, "fromJson--" + displayName + "--" + avatarUri + "--" + userId);
            return new User(avatarUri, displayName, userId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static JSONObject getJson(User user) throws JSONException {
        JSONObject json = new JSONObject();
        json.put(Constants.DISPLAY_NAME, user.getDisplayName());
        json.put(Constants.USER_AVATAR_URI, user.getAvatarUri());
        json.put(Constants.USER_ID, user.getUserId());
        return json;
    }

    public static User getCurrentUser(final Context context){
        SharedPreferences userPrefs = context.getSharedPreferences(USER_PREFS, MODE_PRIVATE);
        String displayName = userPrefs.getString(Constants.DISPLAY_NAME, "unknown");
        String profilePic = userPrefs.getString(Constants.USER_AVATAR_URI, getDefaultAvatar());
        String userId = userPrefs.getString(Constants.USER_ID, "usedIdUnknown");
        return new User(profilePic, displayName, userId);
    }

    public String getUserId() {
        return userId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getAvatarUri() {
        return avatarUri;
    }

    public List<Card> getCards() {
        return cards;
    }

    public boolean isDealer() {
        return isDealer;
    }

    @Override
    public String toString() {
        return "User{" + "avatarUri='" + avatarUri + '\'' + ", displayName='" + displayName + '\'' + ", userId='" + userId + '\'' + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;
        return userId != null ? userId.equals(user.userId) : user.userId == null;
    }

    @Override
    public int hashCode() {
        return userId != null ? userId.hashCode() : 0;
    }
}