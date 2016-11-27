package org.bootcamp.fiftytwo.models;

import android.util.Log;

import com.google.gson.Gson;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import org.bootcamp.fiftytwo.utils.Constants;

import java.util.List;

import static org.bootcamp.fiftytwo.utils.Constants.PARAMS_PLAYER_GAME;

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
        game.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.d(Constants.TAG, "Passed");
                } else {
                    Log.d(Constants.TAG, "Null " + e.getMessage());
                }
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

}