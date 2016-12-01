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
import static org.bootcamp.fiftytwo.utils.Constants.IS_SHOWING_CARDS;
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
    private boolean isShowingCards;
    private boolean isActive;
    private List<Card> cards = new ArrayList<>();

    public User() {}

    public User(String avatarUri, String displayName, String userId) {
        this.avatarUri = avatarUri;
        this.displayName = displayName;
        this.userId = userId;
    }

    public User(String avatarUri, String displayName, String userId, boolean isDealer, boolean isShowingCards) {
        this(avatarUri, displayName, userId);
        this.isDealer = isDealer;
        this.isShowingCards = isShowingCards;
    }

    public static User fromJson(JSONObject json) {
        try {
            String displayName = json.getString(DISPLAY_NAME);
            String avatarUri = json.getString(USER_AVATAR_URI);
            String userId = json.getString(USER_ID);
            boolean isDealer = json.getBoolean(IS_DEALER);
            boolean isShowingCards = json.getBoolean(IS_SHOWING_CARDS);
            Log.d(TAG, "fromJson--" + displayName + "--" + avatarUri + "--" + userId);
            return new User(avatarUri, displayName, userId, isDealer, isShowingCards);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static JSONObject getJson(User user) throws JSONException {
        JSONObject json = new JSONObject();
        json.put(DISPLAY_NAME, user.getDisplayName());
        json.put(USER_AVATAR_URI, user.getAvatarUri());
        json.put(USER_ID, user.getUserId());
        json.put(IS_DEALER, user.isDealer());
        json.put(Constants.IS_SHOWING_CARDS, user.isShowingCards());
        return json;
    }

    public static User getCurrentUser(final Context context) {
        SharedPreferences userPrefs = context.getSharedPreferences(USER_PREFS, MODE_PRIVATE);
        String displayName = userPrefs.getString(DISPLAY_NAME, "unknown");
        String avatarUri = userPrefs.getString(USER_AVATAR_URI, getDefaultAvatar());
        String userId = userPrefs.getString(USER_ID, "usedIdUnknown");
        boolean isDealer = userPrefs.getBoolean(IS_DEALER, false);
        boolean isShowingCards = userPrefs.getBoolean(IS_SHOWING_CARDS, false);
        return new User(avatarUri, displayName, userId, isDealer, isShowingCards);
    }

    public static ParseUser getCurrentUser() {
        return ParseUser.getCurrentUser();
    }

    public static User get(final Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(USER_PREFS, MODE_PRIVATE);
        String displayName = sharedPreferences.getString(DISPLAY_NAME, "");
        String avatarUri = sharedPreferences.getString(USER_AVATAR_URI, "");
        String userId = sharedPreferences.getString(USER_ID, "");
        boolean isDealer = sharedPreferences.getBoolean(IS_DEALER, false);
        boolean isShowingCards = sharedPreferences.getBoolean(IS_SHOWING_CARDS, false);

        if (!displayName.isEmpty() && !userId.isEmpty()) {
            return new User(avatarUri, displayName, userId, isDealer, isShowingCards);
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

    public boolean isDealer() {
        return isDealer;
    }

    public void setDealer(boolean dealer) {
        isDealer = dealer;
    }

    public List<Card> getCards() {
        return cards;
    }

    public boolean isShowingCards() {
        return isShowingCards;
    }

    public void setShowingCards(boolean showingCards) {
        isShowingCards = showingCards;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId='" + userId + '\'' +
                ", displayName='" + displayName + '\'' +
                ", avatarUri='" + avatarUri + '\'' +
                ", isDealer=" + isDealer +
                ", isShowingCards=" + isShowingCards +
                ", cards=" + cards +
                '}';
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