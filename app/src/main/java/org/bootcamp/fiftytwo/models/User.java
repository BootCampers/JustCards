package org.bootcamp.fiftytwo.models;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.parse.ParseUser;

import org.bootcamp.fiftytwo.utils.Constants;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static org.bootcamp.fiftytwo.utils.Constants.DISPLAY_NAME;
import static org.bootcamp.fiftytwo.utils.Constants.IS_DEALER;
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
    private boolean isDealer;
    private List<Card> cards = new ArrayList<>();

    public User() {
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

    public static User getCurrentUser(final Context context) {
        SharedPreferences userPrefs = context.getSharedPreferences(USER_PREFS, MODE_PRIVATE);
        String displayName = userPrefs.getString(Constants.DISPLAY_NAME, "unknown");
        String avatarUri = userPrefs.getString(Constants.USER_AVATAR_URI, getDefaultAvatar());
        String userId = userPrefs.getString(Constants.USER_ID, "usedIdUnknown");
        return new User(avatarUri, displayName, userId);
    }

    public static ParseUser getCurrentUser() {
        return ParseUser.getCurrentUser();
    }

    public static User get(final Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(USER_PREFS, MODE_PRIVATE);
        String displayName = sharedPreferences.getString(DISPLAY_NAME, "");
        String avatarUri = sharedPreferences.getString(USER_AVATAR_URI, "");
        String userId = sharedPreferences.getString(USER_ID, "");

        if (!displayName.isEmpty() && !userId.isEmpty()) {
            return new User(avatarUri, displayName, userId);
        } else {
            return null;
        }
    }

    public void save(final Context context) {
        context.getSharedPreferences(USER_PREFS, MODE_PRIVATE)
                .edit()
                .putString(DISPLAY_NAME, displayName)
                .putString(USER_AVATAR_URI, avatarUri)
                .putString(USER_ID, userId)
                .putBoolean(IS_DEALER, isDealer)
                .apply();
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

    public void setDealer(boolean dealer) {
        isDealer = dealer;
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