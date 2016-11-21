package org.bootcamp.fiftytwo.models;

import android.util.Log;

import com.parse.ParseUser;

import org.bootcamp.fiftytwo.utils.Constants;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Parcel(analyze = User.class)
public class User {

    private String avatarUri;
    private String displayName;
    private String userId; //match this with userid of Parse to keep them unique

    public User() {}

    public User(String avatarUri, String displayName) {
        this.avatarUri = avatarUri;
        this.displayName = displayName;
    }

    public String getAvatarUri() {
        return avatarUri;
    }

    public void setAvatarUri(String avatarUri) {
        this.avatarUri = avatarUri;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getUserId() {

        if(ParseUser.getCurrentUser() != null){
            return  ParseUser.getCurrentUser().getObjectId();
        } else {
            Log.e(Constants.TAG , "No parse login. Current user null");
            return "unknown";
        }
    }

    public User(JSONObject data) throws JSONException {
        displayName = data.getString(Constants.DISPLAY_NAME);
        avatarUri = data.getString(Constants.USER_AVATAR_URI);
        userId = data.getString(Constants.USER_ID);
    }

    @Override
    public String toString() {
        return "User{" +
                "avatarUri='" + avatarUri + '\'' +
                ", displayName='" + displayName + '\'' +
                ", userId='" + userId + '\'' +
                '}';
    }

    // TODO: This method needs to be removed once the Player is passed via intents / arguments
    public static User getDummyPlayer() {
        return new User("", "Ankit");
    }

    // TODO: This method needs to be removed once the Player List is passed via intents / arguments
    public static List<User> getDummyPlayers(int count) {
        Random rand = new Random();
        List<User> players = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            int index = rand.nextInt(dummyPlayers.length);
            players.add(new User(dummyPlayerAvatars[index], dummyPlayers[index]));
        }
        return players;
    }

    // TODO: This variable needs to be removed once real players are added
    private static String[] dummyPlayers = {
            "Sid",
            "Gretchen",
            "Tiffany",
            "Mitchel",
            "Kiley",
            "Mackenzie",
            "Jeffie",
            "Romano",
            "Leonardo",
            "Jared"};

    // TODO: This variable needs to be removed once real players are added
    private static String[] dummyPlayerAvatars = {
            "http://i.imgur.com/GkyKh.jpg",
            "http://i.imgur.com/4M8vzoD.png",
            "http://i.imgur.com/Fankh2h.jpg",
            "http://i.imgur.com/i7zmanJ.jpg",
            "http://i.imgur.com/jrmh8XL.jpg",
            "http://i.imgur.com/VCY27Er.jpg",
            "",
            "",
            "",
            ""};
}