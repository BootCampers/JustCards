package org.bootcamp.fiftytwo.network;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.parse.ParseCloud;
import com.parse.ParsePush;
import com.parse.ParseQuery;

import org.bootcamp.fiftytwo.activities.GameViewManagerActivity;
import org.bootcamp.fiftytwo.models.Card;
import org.bootcamp.fiftytwo.models.Game;
import org.bootcamp.fiftytwo.models.GameTable;
import org.bootcamp.fiftytwo.models.User;
import org.bootcamp.fiftytwo.utils.Constants;
import org.bootcamp.fiftytwo.utils.PlayerUtils;

import java.util.HashMap;
import java.util.HashSet;

import static io.fabric.sdk.android.Fabric.TAG;
import static org.bootcamp.fiftytwo.models.User.getJson;
import static org.bootcamp.fiftytwo.utils.AppUtils.getList;
import static org.bootcamp.fiftytwo.utils.AppUtils.isEmpty;
import static org.bootcamp.fiftytwo.utils.Constants.COMMON_IDENTIFIER;
import static org.bootcamp.fiftytwo.utils.Constants.FROM_POSITION;
import static org.bootcamp.fiftytwo.utils.Constants.PARAM_CARDS;
import static org.bootcamp.fiftytwo.utils.Constants.PARAM_GAME_NAME;
import static org.bootcamp.fiftytwo.utils.Constants.PARAM_PLAYER;
import static org.bootcamp.fiftytwo.utils.Constants.PARAM_POSITION;
import static org.bootcamp.fiftytwo.utils.Constants.PARSE_DEAL_CARDS;
import static org.bootcamp.fiftytwo.utils.Constants.PARSE_DEAL_CARDS_TO_TABLE;
import static org.bootcamp.fiftytwo.utils.Constants.PARSE_EXCHANGE_CARD_WITH_TABLE;
import static org.bootcamp.fiftytwo.utils.Constants.PARSE_NEW_PLAYER_ADDED;
import static org.bootcamp.fiftytwo.utils.Constants.PARSE_PLAYER_LEFT;
import static org.bootcamp.fiftytwo.utils.Constants.PARSE_SWAP_CARD_WITHIN_TABLE;
import static org.bootcamp.fiftytwo.utils.Constants.PARSE_TOGGLE_CARDS_VISIBILITY;
import static org.bootcamp.fiftytwo.utils.Constants.SERVER_FUNCTION_NAME;
import static org.bootcamp.fiftytwo.utils.Constants.TABLE_PICKED;
import static org.bootcamp.fiftytwo.utils.Constants.TO_POSITION;
import static org.bootcamp.fiftytwo.utils.NetworkUtils.isNetworkAvailable;

/**
 * The code that processes this function is listed at:
 *
 * @link {https://github.com/rogerhu/parse-server-push-marker-example/blob/master/cloud/main.js}
 */
public class ParseUtils {

    private Context context;
    private String gameName; //used for channel name
    private User currentLoggedInUser;

    public ParseUtils(final Context context, final String gameName) {
        this.gameName = gameName;
        this.context = context;
        currentLoggedInUser = User.getCurrentUser(context);
    }

    public interface OnGameExistsListener {
        void onGameExistsResult(final boolean result);
    }

    public User getCurrentUser() {
        return currentLoggedInUser;
    }

    public void saveCurrentUser(boolean isDealer) {
        currentLoggedInUser.setDealer(isDealer);
        currentLoggedInUser.save(context);
    }

    public static boolean isSelf(final User user) {
        return user.getUserId().equalsIgnoreCase(User.getCurrentUser().getObjectId());
    }

    public void checkGameExists(final String gameName, final OnGameExistsListener listener) {
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

    public void joinChannel() {
        if (isNetworkAvailable(context)) {
            ParsePush.subscribeInBackground(gameName, e -> {
                if (e == null) {
                    Log.d(TAG, "done: Join Channel Succeeded!");
                    changeGameParticipation(true);
                } else {
                    Log.e(TAG, "done: Join Channel Failed: " + e.getMessage());
                }
            });
        }
    }

    public void removeChannel() {
        if (isNetworkAvailable(context)) {
            ParsePush.unsubscribeInBackground(gameName, e -> {
                if (e == null) {
                    Log.d(TAG, "done: Leave Channel Succeeded!");
                    changeGameParticipation(false);
                } else {
                    Log.e(TAG, "done: Leave Channel Failed: " + e.getMessage());
                }
            });
        }
    }

    /**
     * Broadcast whether current user is joining the game or leaving
     *
     * @param joining true if joining the game , false if leaving the game
     */
    private void changeGameParticipation(boolean joining) {
        JsonObject payload = getJson(currentLoggedInUser);
        if (joining) {
            payload.addProperty(COMMON_IDENTIFIER, PARSE_NEW_PLAYER_ADDED);
        } else {
            payload.addProperty(COMMON_IDENTIFIER, PARSE_PLAYER_LEFT);
        }
        sendBroadcastWithPayload(payload);
    }

    private void sendBroadcastWithPayload(final JsonObject payload) {
        if (isNetworkAvailable(context)) {
            HashMap<String, String> data = new HashMap<>();
            data.put("customData", payload.toString());
            data.put("channel", gameName);
            ParseCloud.callFunctionInBackground(SERVER_FUNCTION_NAME, data, (object, e) -> {
                if (e == null) {
                    Log.d(TAG, "sendBroadcastWithPayload: Succeeded! " + payload.toString());
                } else {
                    Log.e(TAG, "sendBroadcastWithPayload: Failed: Message: " + e.getMessage() + ": Object: " + object);
                }
            });
        }
        //TODO: retry this operation if it's network failure..
    }

    public void fetchPreviouslyJoinedUsers(final String gameName, final GameViewManagerActivity gameViewManagerActivity) {
        // Define the class we would like to query
        ParseQuery<Game> query = ParseQuery.getQuery(Game.class);
        // Define our query conditions
        query.whereEqualTo(PARAM_GAME_NAME, gameName);
        // Execute the find asynchronously
        query.findInBackground((itemList, e) -> {
            if (e == null) {
                Log.d(Constants.TAG, "Found list : " + itemList.size());

                final HashSet<User> players = new HashSet<>();

                for (Game game : itemList) {
                    players.add(game.getPlayer());
                }

                Log.d(TAG, "fetchPreviouslyJoinedUsers: No of players fetched from db: " + players.size());

                // TODO: This custom data generation is temporary and for testing purposes only
                if (isEmpty(players) || players.size() == 1) {
                    players.addAll(PlayerUtils.getPlayers(4));
                }

                for (User player : players) {
                    if (!isSelf(player)) {
                        gameViewManagerActivity.addPlayersToView(getList(player));
                    }
                }
            } else {
                Log.e(Constants.TAG, "Error: " + e.getMessage());
            }
        });
    }

    public void deleteUserFromDb(final String gameName, final User user) {
        // Define the class we would like to query
        ParseQuery<Game> query = ParseQuery.getQuery(Game.class);
        // Define our query conditions
        query.whereEqualTo(PARAM_GAME_NAME, gameName);
        // Execute the find asynchronously
        query.findInBackground((itemList, e) -> {
            if (e == null) {
                Log.d(Constants.TAG, "Found list : " + itemList.size());
                for (Game game : itemList) {
                    User element = game.getPlayer();
                    if (element.equals(user)) {
                        game.deleteInBackground(e1 -> {
                            if (e1 == null) {
                                Log.e(Constants.TAG, "Success delete ");
                            } else {
                                Log.e(Constants.TAG, "Delete error " + e1.getMessage());
                            }
                        });
                    }
                }
            } else {
                Log.e(Constants.TAG, "Error: " + e.getMessage());
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
    public void deleteGameFromServer(final String gameName) {
        ParseQuery<Game> query = ParseQuery.getQuery(Game.class);
        query.whereEqualTo(PARAM_GAME_NAME, gameName);
        query.findInBackground((itemList, e) -> {
            if (e == null) {
                Log.d(Constants.TAG, "deleteGameFromServer Found list : " + itemList.size());
                for (Game game : itemList) {
                    game.deleteInBackground(e1 -> {
                        if (e1 == null) {
                            Log.d(Constants.TAG, gameName + " deleted");
                        } else {
                            Log.e(Constants.TAG, "Failed to delete game on server " + e1.getMessage());
                        }
                    });
                }
            } else {
                Log.e(Constants.TAG, "deleteGameFromServer Error: " + e.getMessage());
            }
        });
    }

    public void toggleCardsVisibility(boolean toShow) {
        JsonObject payload = getJson(currentLoggedInUser);
        payload.addProperty(COMMON_IDENTIFIER, PARSE_TOGGLE_CARDS_VISIBILITY);
        payload.addProperty(PARSE_TOGGLE_CARDS_VISIBILITY, toShow);
        sendBroadcastWithPayload(payload);
    }

    /**
     * Dealer dealing cards to a particular user
     *
     * @param toUser to whom this is sent
     * @param card   which card
     */
    public void dealCards(final User toUser, final Card card) {
        JsonObject payload = getJson(currentLoggedInUser);
        payload.add(PARAM_PLAYER, getJson(toUser));
        payload.addProperty(PARAM_CARDS, new Gson().toJson(card));
        payload.addProperty(COMMON_IDENTIFIER, PARSE_DEAL_CARDS);
        sendBroadcastWithPayload(payload);
    }

    /**
     * Dealer moving cards to table
     *
     * @param card which card
     */
    public synchronized void dealCardsToTable(final Card card, final int position) {
        JsonObject payload = getJson(currentLoggedInUser);
        payload.add(PARAM_CARDS, new Gson().toJsonTree(card));
        payload.addProperty(PARAM_POSITION, position);
        payload.addProperty(COMMON_IDENTIFIER, PARSE_DEAL_CARDS_TO_TABLE);
        sendBroadcastWithPayload(payload);
    }

    /**
     * Player picks up a card from the table or drops one on the table
     *
     * @param card            which card
     * @param fromPosition    position of the card where it was picked from
     * @param toPosition      position of the card where it is dropped in
     * @param pickedFromTable true: if picked from table, false: if dropped on table
     */
    public void exchangeCardWithTable(final Card card, final int fromPosition, final int toPosition, final boolean pickedFromTable) {
        JsonObject payload = getJson(currentLoggedInUser);
        payload.add(PARAM_CARDS, new Gson().toJsonTree(card));
        payload.addProperty(FROM_POSITION, fromPosition);
        payload.addProperty(TO_POSITION, toPosition);
        payload.addProperty(TABLE_PICKED, pickedFromTable);
        payload.addProperty(COMMON_IDENTIFIER, PARSE_EXCHANGE_CARD_WITH_TABLE);
        sendBroadcastWithPayload(payload);
    }

    /**
     * Player swaps a card on the table from one position to another
     *
     * @param card         which card
     * @param fromPosition position of the card where it was picked from
     * @param toPosition   position of the card where it is dropped in
     */
    public void swapCardWithinTable(final Card card, final int fromPosition, final int toPosition) {
        JsonObject payload = getJson(currentLoggedInUser);
        payload.add(PARAM_CARDS, new Gson().toJsonTree(card));
        payload.addProperty(FROM_POSITION, fromPosition);
        payload.addProperty(TO_POSITION, toPosition);
        payload.addProperty(COMMON_IDENTIFIER, PARSE_SWAP_CARD_WITHIN_TABLE);
        sendBroadcastWithPayload(payload);
    }

    public void fetchAllTableCards(final String gameName) {
        // Define the class we would like to query
        ParseQuery<GameTable> query = ParseQuery.getQuery(GameTable.class);
        // Define our query conditions
        query.whereEqualTo(PARAM_GAME_NAME, gameName);
        // Execute the find asynchronously
        query.findInBackground((itemList, e) -> {
            if (e == null) {
                Log.d(Constants.TAG, "GameTable Found list : " + itemList.size());
                //TODO: Parse cards list from incoming json
            } else {
                Log.e(Constants.TAG, "Error: " + e.getMessage());
            }
        });
    }

}