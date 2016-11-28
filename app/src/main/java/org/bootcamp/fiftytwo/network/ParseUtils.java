package org.bootcamp.fiftytwo.network;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.bootcamp.fiftytwo.activities.GameViewManagerActivity;
import org.bootcamp.fiftytwo.models.Card;
import org.bootcamp.fiftytwo.models.Game;
import org.bootcamp.fiftytwo.models.User;
import org.bootcamp.fiftytwo.utils.Constants;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import static io.fabric.sdk.android.Fabric.TAG;
import static org.bootcamp.fiftytwo.models.User.getJson;
import static org.bootcamp.fiftytwo.utils.AppUtils.getList;
import static org.bootcamp.fiftytwo.utils.Constants.COMMON_IDENTIFIER;
import static org.bootcamp.fiftytwo.utils.Constants.PARAM_CARDS;
import static org.bootcamp.fiftytwo.utils.Constants.PARAM_GAME_NAME;
import static org.bootcamp.fiftytwo.utils.Constants.PARAM_PLAYER;
import static org.bootcamp.fiftytwo.utils.Constants.PARSE_DEAL_CARDS;
import static org.bootcamp.fiftytwo.utils.Constants.PARSE_DEAL_CARDS_TO_TABLE;
import static org.bootcamp.fiftytwo.utils.Constants.PARSE_NEW_PLAYER_ADDED;
import static org.bootcamp.fiftytwo.utils.Constants.PARSE_PLAYERS_EXCHANGE_CARDS;
import static org.bootcamp.fiftytwo.utils.Constants.PARSE_PLAYER_LEFT;
import static org.bootcamp.fiftytwo.utils.Constants.PARSE_TABLE_CARD_EXCHANGE;
import static org.bootcamp.fiftytwo.utils.Constants.SERVER_FUNCTION_NAME;
import static org.bootcamp.fiftytwo.utils.Constants.TABLE_PICKED;
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

    public ParseUtils(Context context, String gameName) {
        this.gameName = gameName;
        this.context = context;
        currentLoggedInUser = User.getCurrentUser(context);
    }

    public User getCurrentUser() {
        return currentLoggedInUser;
    }

    public static boolean isSelf(final User user) {
        return user.getUserId().equalsIgnoreCase(ParseUser.getCurrentUser().getObjectId());
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
        ParsePush.unsubscribeInBackground(gameName);
    }

    private void sendBroadcastWithPayload(JSONObject payload) {
        if (isNetworkAvailable(context)) {
            HashMap<String, String> data = new HashMap<>();
            data.put("customData", payload.toString());
            data.put("channel", gameName);
            ParseCloud.callFunctionInBackground(SERVER_FUNCTION_NAME, data, (object, e) -> {
                if (e == null) {
                    Log.d(TAG, "sendBroadcastWithPayload: Succeeded!");
                } else {
                    Log.e(TAG, "sendBroadcastWithPayload: Failed: Message: " + e.getMessage() + ": Object: " + object);
                }
            });
        }
        //TODO: retry this operation if it's network failure..
    }

    /**
     * Broadcast whether current user is joining the game or leaving
     *
     * @param joining true if joining the game , false if leaving the game
     */
    public void changeGameParticipation(boolean joining) {
        try {
            JSONObject payload = getJson(currentLoggedInUser);
            if (joining) {
                payload.put(COMMON_IDENTIFIER, PARSE_NEW_PLAYER_ADDED);
            } else {
                payload.put(COMMON_IDENTIFIER, PARSE_PLAYER_LEFT);
            }
            sendBroadcastWithPayload(payload);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void fetchPreviouslyJoinedUsers(final String gameName, final GameViewManagerActivity gameViewManagerActivity) {
        // Define the class we would like to query
        ParseQuery<Game> query = ParseQuery.getQuery(Game.class);
        // Define our query conditions
        query.whereEqualTo(PARAM_GAME_NAME, gameName);
        // Execute the find asynchronously
        query.findInBackground((itemList, e) -> {
            if (e == null) {
                Log.d("item", "Found list : " + itemList.size());

                final HashSet<User> playersList = new HashSet<>();

                for (Game game : itemList) {
                    playersList.add(game.getPlayer());
                }

                for (User player : playersList) {
                    if (!isSelf(player)) {
                        gameViewManagerActivity.addPlayersToView(getList(player));
                    }
                }
            } else {
                Log.e("item", "Error: " + e.getMessage());
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
                Log.d("item", "Found list : " + itemList.size());
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
                Log.e("item", "Error: " + e.getMessage());
            }
        });
    }

    /**
     * Dealer dealing cards to a particular user
     *
     * @param toUser to whom this is sent
     * @param cards  which cards
     */
    public void dealCards(User toUser, List<Card> cards) {
        try {
            JSONObject payload = getJson(currentLoggedInUser);

            JSONObject toUserJson = getJson(toUser);
            payload.put(PARAM_PLAYER, toUserJson);

            String cardJson = new Gson().toJson(cards, new TypeToken<List<Card>>() {}.getType());
            payload.put(PARAM_CARDS, cardJson);

            payload.put(COMMON_IDENTIFIER, PARSE_DEAL_CARDS);
            sendBroadcastWithPayload(payload);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Dealer moving cards to table
     *
     * @param cards which cards
     */
    public void dealCardsToTable(List<Card> cards) {
        try {
            JSONObject payload = getJson(currentLoggedInUser);

            String cardJson = new Gson().toJson(cards, new TypeToken<List<Card>>() {}.getType());
            payload.put(PARAM_CARDS, cardJson);

            payload.put(COMMON_IDENTIFIER, PARSE_DEAL_CARDS_TO_TABLE);
            sendBroadcastWithPayload(payload);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Current user passing card to particular user
     *
     * @param toUser to whom this is sent
     * @param card   which card
     */
    public void exchangeCard(User toUser, Card card) {
        try {
            JSONObject payload = getJson(currentLoggedInUser);

            JSONObject toUserJson = getJson(toUser);
            payload.put(PARAM_PLAYER, toUserJson);

            String cardJson = new Gson().toJson(card);
            payload.put(PARAM_CARDS, cardJson);

            payload.put(COMMON_IDENTIFIER, PARSE_PLAYERS_EXCHANGE_CARDS);
            sendBroadcastWithPayload(payload);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * TODO: This API needs discussion
     * Send card to or pick from table
     *
     * @param cards           which cards
     * @param pickedFromTable true if picked from table, false if dropped on table
     */
    public void tableCardExchange(List<Card> cards, boolean pickedFromTable) {
        try {
            JSONObject payload = getJson(currentLoggedInUser);

            String cardJson = new Gson().toJson(cards, new TypeToken<List<Card>>() {}.getType());
            payload.put(PARAM_CARDS, cardJson);

            payload.put(TABLE_PICKED, pickedFromTable);

            payload.put(COMMON_IDENTIFIER, PARSE_TABLE_CARD_EXCHANGE);
            sendBroadcastWithPayload(payload);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Card picked by current user OR placed on table by current user
     *
     * @param cards           which cards
     * @param pickedFromTable is the card picked from table
     */
    public void selfTableCardExchange(List<Card> cards, boolean pickedFromTable) {
        //tableCardExchange(cards, pickedFromTable);
    }

    //TODO
    public List<User> fetchAllTableCards() {
        return new ArrayList<>();
    }

}