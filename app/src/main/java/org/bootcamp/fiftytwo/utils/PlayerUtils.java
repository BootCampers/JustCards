package org.bootcamp.fiftytwo.utils;

import org.bootcamp.fiftytwo.models.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Author: agoenka
 * Created At: 11/22/2016
 * Version: ${VERSION}
 */
public class PlayerUtils {

    private static String[] playerNames = {
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

    public static String[] playerAvatars = {
            "http://i.imgur.com/GkyKh.jpg",
            "http://i.imgur.com/4M8vzoD.png",
            "http://i.imgur.com/Fankh2h.jpg",
            "http://i.imgur.com/i7zmanJ.jpg",
            "http://i.imgur.com/jrmh8XL.jpg",
            "http://i.imgur.com/VCY27Er.jpg",
            "http://i.imgur.com/UMUY9Yn.jpg",
            "http://i.imgur.com/9OHzici.jpg?1",
            "http://i.imgur.com/RZ0jFNp.gif",
            "http://i.imgur.com/ITwmNm3.jpg"};

    public static List<User> getPlayers(int count) {
        List<User> players = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();
        Random rand = new Random();
        for (int i = 0; i < count; i++) {
            int index = rand.nextInt(playerNames.length);
            while (indices.contains(index)) {
                index = rand.nextInt(playerNames.length);
            }
            indices.add(index);
            players.add(new User(playerAvatars[index], playerNames[index], String.valueOf(index)));
        }
        return players;
    }

    public static String getDefaultAvatar() {
        return playerAvatars[new Random().nextInt(playerAvatars.length)];
    }
}