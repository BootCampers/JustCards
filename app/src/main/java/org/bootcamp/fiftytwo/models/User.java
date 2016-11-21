package org.bootcamp.fiftytwo.models;

import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Parcel(analyze = User.class)
public class User {

    private String avatarUri;
    private String name;

    public User() {}

    public User(String avatarUri, String name) {
        this.avatarUri = avatarUri;
        this.name = name;
    }

    public String getAvatarUri() {
        return avatarUri;
    }

    public void setAvatarUri(String avatarUri) {
        this.avatarUri = avatarUri;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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