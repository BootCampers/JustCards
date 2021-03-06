package org.justcards.android.utils;

import org.justcards.android.models.User;

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
            "Audrey",
            "Tiffany",
            "Ferris",
            "Nathan",
            "Don",
            "Joker",
            "Michelle",
            "Romano",
            "Steve",
            "Ellie",
            "Trump",
            "Tom",
            "Bob"};

    public static String[] playerAvatars = {
            "http://i.imgur.com/IA4R3xt.jpg", // Now you see me
            "http://i.imgur.com/Ff4sFSI.jpg", // Audrey Hepburn
            "http://i.imgur.com/EZgJK05.jpg", // Golden Gate Bridge
            "http://i.imgur.com/vwqqqAW.png", // Ferris Bueller
            "http://i.imgur.com/zI4lCCJ.jpg", // DiCaprio
            "http://i.imgur.com/LngWF3K.jpg", // Godfather
            "http://i.imgur.com/VCY27Er.jpg", // Joker
            "http://i.imgur.com/8iASC56.jpg", // Afghan Girl
            "http://i.imgur.com/G0cx2jC.jpg", // Superman
            "http://i.imgur.com/UMUY9Yn.jpg", // Steve Jobs
            "http://i.imgur.com/9OHzici.jpg", // Ellie Kemper
            "http://i.imgur.com/PK8mnCC.jpg", // Trump
            "http://i.imgur.com/GkyKh.jpg",   // Avatar
            "http://i.imgur.com/jrmh8XL.jpg", // Cartoon Puppy
            "https://i.imgur.com/bmfKrEA.jpg" // Iron Hulk
    };

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