package org.bootcamp.fiftytwo.network;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import org.bootcamp.fiftytwo.interfaces.Callback;
import org.bootcamp.fiftytwo.models.Card;
import org.bootcamp.fiftytwo.models.Game;
import org.bootcamp.fiftytwo.models.GameTable;
import org.bootcamp.fiftytwo.utils.Constants;

import java.util.List;

import static io.fabric.sdk.android.Fabric.TAG;
import static org.bootcamp.fiftytwo.utils.AppUtils.isEmpty;
import static org.bootcamp.fiftytwo.utils.Constants.PARAM_GAME_NAME;

/**
 * Author: agoenka
 * Created At: 12/2/2016
 * Version: ${VERSION}
 */
public class ParseStorage {

    public interface OnGameExistsListener {
        void onGameExistsResult(final boolean result);
    }

    public static void checkGameExists(final String gameName, final ParseStorage.OnGameExistsListener listener) {
        ParseQuery<Game> query = ParseQuery.getQuery(Game.class);
        query.whereEqualTo(PARAM_GAME_NAME, gameName);
        query.findInBackground((itemList, e) -> {
            boolean result = false;
            if (e == null) {
                Log.d(Constants.TAG, " checkGameExists Found list : " + itemList.size());
                result = itemList.size() != 0;
            } else {
                Log.e(Constants.TAG, "checkGameExists Error: " + e.getMessage());
            }
            listener.onGameExistsResult(result);
        });
    }

    private static void findGameTables(@NonNull final String gameName, @NonNull final FindCallback<GameTable> callback) {
        ParseQuery<GameTable> query = ParseQuery.getQuery(GameTable.class);
        query.whereEqualTo(PARAM_GAME_NAME, gameName);
        query.findInBackground(callback);
    }

    private static List<GameTable> findGameTables(@NonNull final String gameName) {
        try {
            ParseQuery<GameTable> query = ParseQuery.getQuery(GameTable.class);
            query.whereEqualTo(PARAM_GAME_NAME, gameName);
            return query.find();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void deleteGameTables(final String gameName, final DeleteCallback callback) {
        GameTable.deleteAllInBackground(findGameTables(gameName), callback);
    }

    static void deleteGameTables(final String gameName, final Runnable runnable) {
        findGameTables(gameName, (itemList, e) -> {
            if (e == null) {
                if (isEmpty(itemList)) {
                    runnable.run();
                } else {
                    deleteGameTables(gameName, e1 -> {
                        if (e1 == null) {
                            runnable.run();
                        }
                    });
                }
            }
        });
    }

    public static void getGameTableCards(final String gameName, final int expectedCardCount, final Callback<List<Card>> callback) {
        findGameTables(gameName, (itemList, e) -> {
            if (e == null) {
                Log.d(Constants.TAG, "GameTable Found list : " + itemList.size());
                if (itemList.size() == 0) {
                    Log.e(TAG, "fetchAllTableCards: No game tables found in database.");
                } else if (itemList.size() > 1) {
                    Log.e(TAG, "fetchAllTableCards: More than one game tables found for this game: " + itemList.size());
                } else {
                    String cardsString = itemList.get(0).getCards();
                    List<Card> cards = new Gson().fromJson(cardsString, new TypeToken<List<Card>>() {}.getType());
                    if (cards == null) {
                        Log.e(TAG, "getGameTableCards: received cards from game table are null");
                    } else if (expectedCardCount != cards.size()) {
                        Log.e(TAG, "fetchAllTableCards: Number of table cards from db does not equal the no of cards dealt, dealt: "
                                + expectedCardCount
                                + ", received: "
                                + itemList.size());
                    } else {
                        callback.call(cards);
                    }
                }
            } else {
                Log.e(Constants.TAG, "Error: " + e.getMessage());
            }
        });
    }
}