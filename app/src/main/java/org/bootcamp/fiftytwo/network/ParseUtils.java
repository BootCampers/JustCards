package org.bootcamp.fiftytwo.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.parse.ParseCloud;
import com.parse.ParsePush;

import org.bootcamp.fiftytwo.models.Card;
import org.bootcamp.fiftytwo.models.User;
import org.bootcamp.fiftytwo.utils.Constants;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static org.bootcamp.fiftytwo.models.User.getJson;
import static org.bootcamp.fiftytwo.utils.Constants.USER_PREFS;

/**
 * The code that processes this function is listed at:
 *
 * @link {https://github.com/rogerhu/parse-server-push-marker-example/blob/master/cloud/main.js}
 */
public class ParseUtils {

    private String gameName; //used for channel name
    private User currentLoggedInUser;

    public ParseUtils(Context context, String gameName) {
        this.gameName = gameName;

        SharedPreferences userPrefs = context.getSharedPreferences(USER_PREFS, MODE_PRIVATE);
        String displayName = userPrefs.getString(Constants.DISPLAY_NAME, "unknown");
        String profilePic = userPrefs.getString(Constants.USER_AVATAR_URI, "http://i.imgur.com/FLmEyXZ.jpg");
        String userId = userPrefs.getString(Constants.USER_ID, "usedIdUnknown");
        currentLoggedInUser = new User(profilePic, displayName, userId);

        Log.d(Constants.TAG, "Current user -- " + displayName + " -- " + profilePic + " -- " + userId);
    }

    public User getCurrentUser() {
        return currentLoggedInUser;
    }

    public void joinChannel() {
        ParsePush.subscribeInBackground(gameName);
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

    //TODO
    public List<User> fetchPreviouslyJoinedUsers() {
        return new ArrayList<>();
    }

    //TODO
    public List<User> fetchAllTableCards() {
        return new ArrayList<>();
    }

    private void sendBroadcastWithPayload(JSONObject payload) {
        HashMap<String, String> data = new HashMap<>();
        data.put("customData", payload.toString());
        data.put("channel", gameName);
        ParseCloud.callFunctionInBackground(Constants.SERVER_FUNCTION_NAME, data);
    }

}