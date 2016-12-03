package org.bootcamp.fiftytwo.network;

import android.content.Context;
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
import org.bootcamp.fiftytwo.models.User;
import org.bootcamp.fiftytwo.utils.Constants;
import org.bootcamp.fiftytwo.utils.PlayerUtils;

import java.util.HashSet;
import java.util.List;

import static io.fabric.sdk.android.Fabric.TAG;
import static org.bootcamp.fiftytwo.network.ParseUtils.isSelf;
import static org.bootcamp.fiftytwo.utils.AppUtils.getList;
import static org.bootcamp.fiftytwo.utils.AppUtils.isEmpty;
import static org.bootcamp.fiftytwo.utils.Constants.PARAM_GAME_NAME;

/**
 * Author: agoenka
 * Created At: 12/2/2016
 * Version: ${VERSION}
 */
public class ParseDB {

    public interface OnGameExistsListener {
        void onGameExistsResult(final boolean result);
    }

    private static void findGame(final String gameName, FindCallback<Game> callback) {
        ParseQuery<Game> query = ParseQuery.getQuery(Game.class);
        query.whereEqualTo(PARAM_GAME_NAME, gameName);
        query.findInBackground(callback);
    }

    public static void checkGameExists(final String gameName, final ParseDB.OnGameExistsListener listener) {
        findGame(gameName, (itemList, e) -> {
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

    private static void deleteGame(final Game game) {
        game.deleteInBackground(e1 -> {
            if (e1 == null) {
                Log.e(Constants.TAG, "Success delete ");
            } else {
                Log.e(Constants.TAG, "Delete error " + e1.getMessage());
            }
        });
    }

    /**
     * TODO: Needs discussion
     * TODO: fix Delete error java.lang.ClassCastException: okhttp3.RequestBody$2 cannot be cast to com.parse.ParseOkHttpClient$ParseOkHttpRequestBody
     * Existing players can continue to play the game even after dealer leaves. No one else can join.
     *
     * @param gameName the game number that needs to be deleted
     */
    public static void deleteGame(final String gameName) {
        findGame(gameName, (itemList, e) -> {
            if (e == null) {
                Log.d(Constants.TAG, "deleteGame Found list : " + itemList.size());
                for (Game game : itemList) {
                    deleteGame(game);
                }
            } else {
                Log.e(Constants.TAG, "deleteGame Error: " + e.getMessage());
            }
        });
    }

    public static void deleteGamesForUser(final String gameName, final User user) {
        findGame(gameName, (itemList, e) -> {
            if (e == null) {
                Log.d(Constants.TAG, "Found list : " + itemList.size());
                for (Game game : itemList) {
                    User element = game.getPlayer();
                    if (element.equals(user)) {
                        deleteGame(game);
                    }
                }
            } else {
                Log.e(Constants.TAG, "Error: " + e.getMessage());
            }
        });
    }

    public static void findUsers(final Context context, final String gameName, final Callback<List<User>> callback) {
        findGame(gameName, (itemList, e) -> {
            if (e == null) {
                Log.d(Constants.TAG, "Found list : " + itemList.size());

                final HashSet<User> players = new HashSet<>();
                for (Game game : itemList) {
                    players.add(game.getPlayer());
                }
                Log.d(TAG, "findUsers: No of players fetched from db: " + players.size());

                // TODO: This custom data generation is temporary and for testing purposes only
                if ((isEmpty(players) || players.size() == 1) && User.getCurrentUser(context).isDealer()) {
                    List<User> dummyPlayers = PlayerUtils.getPlayers(4);
                    for (User dummyPlayer : dummyPlayers) {
                        Game.save(gameName, dummyPlayer);
                    }
                    players.addAll(dummyPlayers);
                }

                for (User player : players) {
                    if (!isSelf(player)) {
                        callback.call(getList(player));
                    }
                }
            } else {
                Log.e(Constants.TAG, "Error: " + e.getMessage());
            }
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
                    List<Card> cards = new Gson().fromJson(cardsString, new TypeToken<List<Card>>() {
                    }.getType());
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