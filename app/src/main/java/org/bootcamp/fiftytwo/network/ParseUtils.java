package org.bootcamp.fiftytwo.network;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.parse.FindCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParsePush;
import com.parse.ParseQuery;

import org.bootcamp.fiftytwo.activities.GameViewManagerActivity;
import org.bootcamp.fiftytwo.models.Card;
import org.bootcamp.fiftytwo.models.Game;
import org.bootcamp.fiftytwo.models.User;
import org.bootcamp.fiftytwo.utils.Constants;
import org.bootcamp.fiftytwo.utils.NetworkUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import static org.bootcamp.fiftytwo.models.User.getJson;

/**
 * The code that processes this function is listed at:
 *
 * @link {https://github.com/rogerhu/parse-server-push-marker-example/blob/master/cloud/main.js}
 */
public class ParseUtils {

    private String gameName; //used for channel name
    private User currentLoggedInUser;
    private Context context;

    public ParseUtils(Context context, String gameName) {
        this.gameName = gameName;
        this.context = context;

        currentLoggedInUser = User.getCurrentUser(context);
    }

    public User getCurrentUser() {
        return currentLoggedInUser;
    }

    public void joinChannel() {
        if(NetworkUtils.isNetworkAvailable(context) == true) {
            ParsePush.subscribeInBackground(gameName);
        }
    }

    public void removeChannel() {
        ParsePush.unsubscribeInBackground(gameName);
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
                payload.put(Constants.COMMON_IDENTIFIER, Constants.PARSE_NEW_PLAYER_ADDED);
            } else {
                payload.put(Constants.COMMON_IDENTIFIER, Constants.PARSE_PLAYER_LEFT);
            }
            sendBroadcastWithPayload(payload);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * TODO: This API needs discussion
     * Current user passing card to particular user
     *
     * @param toUser to whom this is sent
     * @param card   which card
     */
    public void exchangeCard(User toUser, Card card) {
        try {
            JSONObject payload = getJson(currentLoggedInUser);

            JSONObject toUserJson = getJson(toUser);
            payload.put(Constants.PARAM_PLAYER, toUserJson);

            String cardJson = new Gson().toJson(card);
            payload.put(Constants.PARAM_CARDS, cardJson);

            payload.put(Constants.COMMON_IDENTIFIER, Constants.PARSE_PLAYERS_EXCHANGE_CARDS);
            sendBroadcastWithPayload(payload);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * TODO: This API needs discussion
     * Send card to or pick from table
     *
     * @param cards  which cards
     * @param pickedFromTable true if picked from table, false if dropped on table
     */
    public void tableCardExchange(List<Card> cards, boolean pickedFromTable) {
        try {
            JSONObject payload = getJson(currentLoggedInUser);

            String cardJson = new Gson().toJson(cards, new TypeToken<List<Card>>() {}.getType());
            payload.put(Constants.PARAM_CARDS, cardJson);

            payload.put(Constants.TABLE_PICKED, pickedFromTable);

            payload.put(Constants.COMMON_IDENTIFIER, Constants.PARSE_TABLE_CARD_EXCHANGE);
            sendBroadcastWithPayload(payload);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Card picked by current user OR placed on table by current user
     *
     * @param cards            which cards
     * @param pickedFromTable is the card picked from table
     */
    public void selfTableCardExchange(List<Card> cards, boolean pickedFromTable) {
        tableCardExchange(cards, pickedFromTable);
    }

    public void fetchPreviouslyJoinedUsers(final String gameName, final GameViewManagerActivity gameViewManagerActivity) {
        final HashSet<User> playersList = new HashSet<>();
        final Gson gson = new Gson();

        // Define the class we would like to query
        ParseQuery<Game> query = ParseQuery.getQuery(Game.class);
        // Define our query conditions
        query.whereEqualTo(Constants.PARAM_GAME_NAME, gameName);
        // Execute the find asynchronously
        query.findInBackground(new FindCallback<Game>() {
            public void done(List<Game> itemList, ParseException e) {
                if (e == null) {
                    Log.d("item", "Found list : " + itemList.size());

                    for (Game game: itemList) {
                        String playerString = (String) game.get(Constants.PARAMS_PLAYER_GAME);
                        User element = gson.fromJson(playerString, User.class);
                        playersList.add(element);
                    }

                    for (User player: playersList) {
                        gameViewManagerActivity.addNewPlayerToUI(player);
                    }
                } else {
                    Log.e("item", "Error: " + e.getMessage());
                }
            }
        });
    }

    //TODO
    public List<User> fetchAllTableCards() {
        return new ArrayList<>();
    }

    private void sendBroadcastWithPayload(JSONObject payload) {
        if(NetworkUtils.isNetworkAvailable(context) == true) {
            HashMap<String, String> data = new HashMap<>();
            data.put("customData", payload.toString());
            data.put("channel", gameName);
            ParseCloud.callFunctionInBackground(Constants.SERVER_FUNCTION_NAME, data);
        }
        //TODO: retry this operation if it's network failure..
    }

}