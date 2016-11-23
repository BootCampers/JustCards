package org.bootcamp.fiftytwo.models;

import android.util.Log;

import org.bootcamp.fiftytwo.utils.Constants;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

import static org.bootcamp.fiftytwo.utils.Constants.DISPLAY_NAME;
import static org.bootcamp.fiftytwo.utils.Constants.TAG;
import static org.bootcamp.fiftytwo.utils.Constants.USER_AVATAR_URI;
import static org.bootcamp.fiftytwo.utils.Constants.USER_ID;

@Parcel(analyze = User.class)
public class User {

    private String userId; //match this with userId of Parse to keep them unique
    private String displayName;
    private String avatarUri;
    private List<Card> cards = new ArrayList<>();

    public User() {
    }

    public User(String avatarUri, String displayName) {
        this.avatarUri = avatarUri;
        this.displayName = displayName;
    }

    public User(String avatarUri, String displayName, String userId) {
        this.avatarUri = avatarUri;
        this.displayName = displayName;
        this.userId = userId;
    }

    public User(JSONObject data) throws JSONException {
        displayName = data.getString(Constants.DISPLAY_NAME);
        avatarUri = data.getString(Constants.USER_AVATAR_URI);
        userId = data.getString(Constants.USER_ID);
    }

    public static User fromJson(JSONObject jsonObject) {
        try {
            String displayName = jsonObject.getString(DISPLAY_NAME);
            String avatarUri = jsonObject.getString(USER_AVATAR_URI);
            String userId = jsonObject.getString(USER_ID);
            Log.d(TAG, "fromJson--" + displayName + "--" + avatarUri + "--" + userId);
            return new User(avatarUri, displayName, userId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getAvatarUri() {
        return avatarUri;
    }

    public void setAvatarUri(String avatarUri) {
        this.avatarUri = avatarUri;
    }

    public List<Card> getCards() {
        return cards;
    }

    @Override
    public String toString() {
        return "User{" + "avatarUri='" + avatarUri + '\'' + ", displayName='" + displayName + '\'' + ", userId='" + userId + '\'' + '}';
    }

}