package org.bootcamp.fiftytwo.models;

import android.util.Log;

import com.google.gson.Gson;
import com.parse.ParseClassName;
import com.parse.ParseObject;

import org.bootcamp.fiftytwo.utils.Constants;

import java.util.List;

/**
 * Created by baphna on 12/2/2016.
 */
@ParseClassName("GameTable")
public class GameTable extends ParseObject{

    public GameTable() {
        super();
    }

    private void setGameName(String gameName) {
        put(Constants.PARAM_GAME_NAME, gameName);
    }

    private void addCards(List<Card> cards) {
        Gson gson = new Gson();
        String jsonString = gson.toJson(cards);
        put(Constants.PARAM_GAME_TABLE, jsonString);
        Log.d(Constants.TAG, "GameTable saving cards: " + jsonString);
    }

    public static void save(String gameName, List<Card> cards) {
        GameTable gameTable = new GameTable();
        gameTable.setGameName(gameName);
        gameTable.addCards(cards);
        gameTable.saveInBackground(e -> {
            if (e == null) {
                Log.d(Constants.TAG, "GameTable save Passed");
            } else {
                Log.d(Constants.TAG, "GameTable save Null " + e.getMessage());
            }
        });
    }

    public String getCards() {
        return getString(Constants.PARAM_GAME_TABLE);
    }

    @Override
    public String toString() {
        return getCards();
    }
}
