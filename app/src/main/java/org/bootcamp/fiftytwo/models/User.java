package org.bootcamp.fiftytwo.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@ParseClassName("User")
@Parcel(analyze = User.class)
public class User  extends ParseObject {

    public User() {
        super();
    }

    public User(String avatarUri, String name) {
        super();
        setAvatarUri(avatarUri);
        setName(name);
    }

    public String getAvatarUri() {
        return getString("avatarUri");
    }

    public void setAvatarUri(String avatarUri) {
        put("avatarUri", avatarUri);
    }

    public String getName() {
        return getString("name");
    }

    public void setName(String name) {
        put("name", name);
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