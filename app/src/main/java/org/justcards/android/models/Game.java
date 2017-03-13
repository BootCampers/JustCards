package org.justcards.android.models;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.parse.ParseClassName;
import com.parse.ParseObject;

import org.justcards.android.utils.Constants;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static org.justcards.android.utils.Constants.GAME_NAME;
import static org.justcards.android.utils.Constants.GAME_PREFS;
import static org.justcards.android.utils.Constants.PARAMS_PLAYER_GAME;

/**
 * Created by baphna on 11/26/2016.
 */
@ParseClassName("Game")
public class Game extends ParseObject {

    public Game() {
        super();
    }

    private void setGameName(String gameName) {
        put(Constants.PARAM_GAME_NAME, gameName);
    }

    private void addPlayer(User user) {
        Gson gson = new Gson();
        put(Constants.PARAMS_PLAYER_GAME, gson.toJson(user));
    }

    public static void save(String gameName, User player) {
        Game game = new Game();
        game.setGameName(gameName);
        game.addPlayer(player);
        game.saveInBackground(e -> {
            if (e == null) {
                Log.d(Constants.TAG, "Passed");
            } else {
                Log.d(Constants.TAG, "Null " + e.getMessage());
            }
        });
    }

    public User getPlayer() {
        return new Gson().fromJson(getString(PARAMS_PLAYER_GAME), User.class);
    }

    public String getGameName() {
        return getString(Constants.PARAM_GAME_NAME);
    }

    public void removePlayer(User player) {
        List<User> players = getList(Constants.PARAMS_PLAYER_GAME);
        for (User user : players) {
            if (user.getUserId().equals(player.getUserId())) {
                players.remove(user);
            }
        }
    }

    public static void saveName(final String gameName, final Context context) {
        context.getSharedPreferences(GAME_PREFS, MODE_PRIVATE)
                .edit()
                .putString(GAME_NAME, gameName)
                .apply();
    }

    public static String getName(final Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(GAME_PREFS, MODE_PRIVATE);
        String gameName = sharedPreferences.getString(GAME_NAME, "");
        return !gameName.isEmpty() ? gameName : null;
    }

}