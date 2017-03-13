package org.justcards.android.network;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.parse.ParseCloud;
import com.parse.ParsePush;

import org.justcards.android.models.Card;
import org.justcards.android.models.User;

import java.util.HashMap;
import java.util.List;

import static org.justcards.android.models.User.getJson;
import static org.justcards.android.utils.Constants.COMMON_IDENTIFIER;
import static org.justcards.android.utils.Constants.FROM_POSITION;
import static org.justcards.android.utils.Constants.FROM_TAG;
import static org.justcards.android.utils.Constants.ON_TAG;
import static org.justcards.android.utils.Constants.PARAM_CARDS;
import static org.justcards.android.utils.Constants.PARAM_CHAT;
import static org.justcards.android.utils.Constants.PARAM_PLAYERS;
import static org.justcards.android.utils.Constants.PARSE_CHAT_MESSAGE;
import static org.justcards.android.utils.Constants.PARSE_DROP_CARD_TO_SINK;
import static org.justcards.android.utils.Constants.PARSE_END_ROUND;
import static org.justcards.android.utils.Constants.PARSE_EXCHANGE_CARD_WITH_TABLE;
import static org.justcards.android.utils.Constants.PARSE_MUTE_PLAYER_FOR_ROUND;
import static org.justcards.android.utils.Constants.PARSE_NEW_PLAYER_ADDED;
import static org.justcards.android.utils.Constants.PARSE_PLAYER_LEFT;
import static org.justcards.android.utils.Constants.PARSE_ROUND_WINNERS;
import static org.justcards.android.utils.Constants.PARSE_SCORES_UPDATED;
import static org.justcards.android.utils.Constants.PARSE_SELECT_GAME_RULES;
import static org.justcards.android.utils.Constants.PARSE_SWAP_CARD_WITHIN_PLAYER;
import static org.justcards.android.utils.Constants.PARSE_TOGGLE_CARD;
import static org.justcards.android.utils.Constants.PARSE_TOGGLE_CARDS_LIST;
import static org.justcards.android.utils.Constants.POSITION;
import static org.justcards.android.utils.Constants.RULE_CODE;
import static org.justcards.android.utils.Constants.RULE_SELECTION;
import static org.justcards.android.utils.Constants.SERVER_FUNCTION_NAME;
import static org.justcards.android.utils.Constants.TABLE_PICKED;
import static org.justcards.android.utils.Constants.TAG;
import static org.justcards.android.utils.Constants.TO_MUTE;
import static org.justcards.android.utils.Constants.TO_POSITION;
import static org.justcards.android.utils.Constants.TO_SHOW;
import static org.justcards.android.utils.Constants.USER_TAG_SCORE;
import static org.justcards.android.utils.NetworkUtils.isNetworkAvailable;

/**
 * The code that processes this function is listed at:
 *
 * @link {https://github.com/rogerhu/parse-server-push-marker-example/blob/master/cloud/main.js}
 */

@SuppressWarnings("unused")
@Deprecated
public class ParseUtils {

    private Context context;
    private String gameName; //used for channel name
    private User currentLoggedInUser;

    public ParseUtils(final Context context, final String gameName) {
        this.gameName = gameName;
        this.context = context;
        currentLoggedInUser = User.getCurrentUser(context);
    }

    public User getCurrentUser() {
        return currentLoggedInUser;
    }

    public void saveCurrentUserIsDealer(boolean isDealer) {
        currentLoggedInUser.setDealer(isDealer);
        currentLoggedInUser.save(context);
    }

    public void saveCurrentUserIsShowingCards(boolean isShowingCards) {
        currentLoggedInUser.setShowingCards(isShowingCards);
        currentLoggedInUser.save(context);
    }

    public void saveCurrentUserIsActive(boolean isActive) {
        currentLoggedInUser.setActive(isActive);
        currentLoggedInUser.save(context);
    }

    public void saveCurrentUserScore(final int score) {
        currentLoggedInUser.setScore(score);
        currentLoggedInUser.save(context);
    }

    public void resetCurrentUserForRound() {
        currentLoggedInUser.setShowingCards(false);
        currentLoggedInUser.setActive(true);
        currentLoggedInUser.save(context);
    }

    public void resetCurrentUser() {
        currentLoggedInUser.setDealer(false);
        currentLoggedInUser.setShowingCards(false);
        currentLoggedInUser.setActive(true);
        currentLoggedInUser.setScore(0);
        currentLoggedInUser.save(context);
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

    private void sendBroadcast(final JsonObject payload) {
        if (isNetworkAvailable(context)) {
            HashMap<String, String> data = new HashMap<>();
            data.put("customData", payload.toString());
            data.put("channel", gameName);
            ParseCloud.callFunctionInBackground(SERVER_FUNCTION_NAME, data, (object, e) -> {
                if (e == null) {
                    Log.d(TAG, "sendBroadcast: Succeeded! " + payload.toString());
                } else {
                    Log.e(TAG, "sendBroadcast: Failed: Message: " + e.getMessage() + ": Object: " + object);
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
    private void changeGameParticipation(boolean joining) {
        JsonObject payload = getJson(currentLoggedInUser);
        if (joining) {
            payload.addProperty(COMMON_IDENTIFIER, PARSE_NEW_PLAYER_ADDED);
        } else {
            payload.addProperty(COMMON_IDENTIFIER, PARSE_PLAYER_LEFT);
        }
        sendBroadcast(payload);
    }

    public void sendChatMessage(final String message) {
        JsonObject payload = getJson(currentLoggedInUser);
        payload.addProperty(PARAM_CHAT, message);
        payload.addProperty(COMMON_IDENTIFIER, PARSE_CHAT_MESSAGE);
        sendBroadcast(payload);
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
        sendBroadcast(payload);
    }

    /**
     * Player swaps a card within his/her hand from one position to another
     *
     * @param card         which card
     * @param fromPosition position of the card where it was picked from
     * @param toPosition   position of the card where it is dropped in
     */
    public void swapCardWithinPlayer(final Card card, final int fromPosition, final int toPosition) {
        JsonObject payload = getJson(currentLoggedInUser);
        payload.add(PARAM_CARDS, new Gson().toJsonTree(card));
        payload.addProperty(FROM_POSITION, fromPosition);
        payload.addProperty(TO_POSITION, toPosition);
        payload.addProperty(COMMON_IDENTIFIER, PARSE_SWAP_CARD_WITHIN_PLAYER);
        sendBroadcast(payload);
    }

    /**
     * Player picks up a card from a stack of cards and drops them on to the Sink
     *
     * @param card         which card
     * @param fromTag      from which stack of cards
     * @param fromPosition from which position in the stack of cards
     */
    public void dropCardToSink(final Card card, final String fromTag, final int fromPosition) {
        JsonObject payload = getJson(currentLoggedInUser);
        payload.add(PARAM_CARDS, new Gson().toJsonTree(card));
        payload.addProperty(FROM_TAG, fromTag);
        payload.addProperty(FROM_POSITION, fromPosition);
        payload.addProperty(COMMON_IDENTIFIER, PARSE_DROP_CARD_TO_SINK);
        sendBroadcast(payload);
    }

    public void toggleCard(final Card card, final int position, final String onTag) {
        JsonObject payload = getJson(currentLoggedInUser);
        payload.add(PARAM_CARDS, new Gson().toJsonTree(card));
        payload.addProperty(POSITION, position);
        payload.addProperty(ON_TAG, onTag);
        payload.addProperty(COMMON_IDENTIFIER, PARSE_TOGGLE_CARD);
        sendBroadcast(payload);
    }

    public void toggleCardsList(boolean toShow) {
        JsonObject payload = getJson(currentLoggedInUser);
        payload.addProperty(TO_SHOW, toShow);
        payload.addProperty(COMMON_IDENTIFIER, PARSE_TOGGLE_CARDS_LIST);
        sendBroadcast(payload);
    }

    public void mutePlayerForRound(boolean toMute) {
        JsonObject payload = getJson(currentLoggedInUser);
        payload.addProperty(TO_MUTE, toMute);
        payload.addProperty(COMMON_IDENTIFIER, PARSE_MUTE_PLAYER_FOR_ROUND);
        sendBroadcast(payload);
    }

    public void updateUsersScore(final List<User> players) {
        JsonObject payload = getJson(currentLoggedInUser);
        payload.add(USER_TAG_SCORE, new Gson().toJsonTree(players));
        payload.addProperty(COMMON_IDENTIFIER, PARSE_SCORES_UPDATED);
        sendBroadcast(payload);
    }

    public void declareRoundWinners(List<User> roundWinners) {
        JsonObject payload = getJson(currentLoggedInUser);
        payload.add(PARAM_PLAYERS, new Gson().toJsonTree(roundWinners));
        payload.addProperty(COMMON_IDENTIFIER, PARSE_ROUND_WINNERS);
        sendBroadcast(payload);
    }

    public void endRound() {
        JsonObject payload = getJson(currentLoggedInUser);
        payload.addProperty(COMMON_IDENTIFIER, PARSE_END_ROUND);
        sendBroadcast(payload);
    }

    public void selectGameRules(String code, Object selection) {
        JsonObject payload = getJson(currentLoggedInUser);
        payload.addProperty(RULE_CODE, code);
        if (selection instanceof Boolean) {
            payload.addProperty(RULE_SELECTION, (Boolean) selection);
        }
        payload.addProperty(COMMON_IDENTIFIER, PARSE_SELECT_GAME_RULES);
        sendBroadcast(payload);
    }
}