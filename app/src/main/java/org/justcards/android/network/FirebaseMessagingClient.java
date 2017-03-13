package org.justcards.android.network;

import android.content.Context;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;
import org.justcards.android.models.Card;
import org.justcards.android.models.GameTable;
import org.justcards.android.models.User;

import java.io.UnsupportedEncodingException;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.client.cache.HeaderConstants;
import cz.msebera.android.httpclient.entity.StringEntity;

import static org.justcards.android.models.User.getJson;
import static org.justcards.android.utils.Constants.COMMON_IDENTIFIER;
import static org.justcards.android.utils.Constants.DATA;
import static org.justcards.android.utils.Constants.FROM_ADDRESS_PREFIX;
import static org.justcards.android.utils.Constants.FROM_POSITION;
import static org.justcards.android.utils.Constants.FROM_TAG;
import static org.justcards.android.utils.Constants.ON_TAG;
import static org.justcards.android.utils.Constants.PARAM_CARDS;
import static org.justcards.android.utils.Constants.PARAM_CARD_COUNT;
import static org.justcards.android.utils.Constants.PARAM_CHAT;
import static org.justcards.android.utils.Constants.PARAM_PLAYER;
import static org.justcards.android.utils.Constants.PARAM_PLAYERS;
import static org.justcards.android.utils.Constants.PARSE_CHAT_MESSAGE;
import static org.justcards.android.utils.Constants.PARSE_DEAL_CARDS;
import static org.justcards.android.utils.Constants.PARSE_DEAL_CARDS_TO_SINK;
import static org.justcards.android.utils.Constants.PARSE_DEAL_CARDS_TO_TABLE;
import static org.justcards.android.utils.Constants.PARSE_DROP_CARD_TO_SINK;
import static org.justcards.android.utils.Constants.PARSE_END_ROUND;
import static org.justcards.android.utils.Constants.PARSE_EXCHANGE_CARD_WITH_TABLE;
import static org.justcards.android.utils.Constants.PARSE_NEW_PLAYER_ADDED;
import static org.justcards.android.utils.Constants.PARSE_PLAYER_LEFT;
import static org.justcards.android.utils.Constants.PARSE_ROUND_WINNERS;
import static org.justcards.android.utils.Constants.PARSE_SELECT_GAME_RULES;
import static org.justcards.android.utils.Constants.PARSE_SWAP_CARD_WITHIN_PLAYER;
import static org.justcards.android.utils.Constants.PARSE_TOGGLE_CARD;
import static org.justcards.android.utils.Constants.POSITION;
import static org.justcards.android.utils.Constants.RULE_CODE;
import static org.justcards.android.utils.Constants.RULE_SELECTION;
import static org.justcards.android.utils.Constants.TABLE_PICKED;
import static org.justcards.android.utils.Constants.TO;
import static org.justcards.android.utils.Constants.TO_POSITION;
import static org.justcards.android.utils.NetworkUtils.isNetworkAvailable;

/**
 * Author: agoenka
 * Created At: 2/28/2017
 * Version: 1.0
 */
public class FirebaseMessagingClient {

    public static final String TAG = FirebaseMessagingClient.class.getSimpleName();
    private static final String API_URL = "https://fcm.googleapis.com/fcm/send";
    private static final String API_KEY = "key=AAAAxsSWPqI:APA91bGWXoizf9FGz4J4dMNyfoHjqrrDcAsp_XvDHA71bH0KGFhhfNW-_rYXR83C4ctnT850dnnWiizTj8f6FrOE4vCF6gh_EUmjZM7zwmUPR8S-ZF7SSUXvMf-iPeXVZzJHpmZPTdQu";

    private Context mContext;
    private String mGameName;
    private User mCurrentUser;

    public FirebaseMessagingClient(Context context, String gameName) {
        this.mContext = context;
        this.mGameName = gameName;
        mCurrentUser = User.getCurrentUser(context);
    }

    public User getCurrentUser() {
        return mCurrentUser;
    }

    public void saveCurrentUserIsDealer(boolean isDealer) {
        mCurrentUser.setDealer(isDealer);
        mCurrentUser.save(mContext);
    }

    public void saveCurrentUserScore(final int score) {
        mCurrentUser.setScore(score);
        mCurrentUser.save(mContext);
    }

    public void resetCurrentUserForRound() {
        mCurrentUser.setShowingCards(false);
        mCurrentUser.setActive(true);
        mCurrentUser.save(mContext);
    }

    public void resetCurrentUser() {
        mCurrentUser.setDealer(false);
        mCurrentUser.setShowingCards(false);
        mCurrentUser.setActive(true);
        mCurrentUser.setScore(0);
        mCurrentUser.save(mContext);
    }

    public void saveCurrentUserIsShowingCards(boolean isShowingCards) {
        mCurrentUser.setShowingCards(isShowingCards);
        mCurrentUser.save(mContext);
    }

    public void saveCurrentUserIsActive(boolean isActive) {
        mCurrentUser.setActive(isActive);
        mCurrentUser.save(mContext);
    }

    /**
     * @param payload the payload to broadcast
     * @see [https://firebase.google.com/docs/cloud-messaging/android/topic-messaging]
     * @see [https://firebase.google.com/docs/cloud-messaging/android/device-group]
     */
    private void sendBroadcast(final JsonObject payload) {
        try {
            JsonObject json = new JsonObject();
            json.addProperty(TO, FROM_ADDRESS_PREFIX + mGameName);
            json.add(DATA, payload);
            HttpEntity entity = new StringEntity(json.toString());

            AsyncHttpClient client = new AsyncHttpClient();
            client.addHeader(HeaderConstants.AUTHORIZATION, API_KEY);
            client.post(mContext, API_URL, entity, "application/json", new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    Log.d(TAG, "sendBroadcast: Succeeded: " + payload.toString());
                    super.onSuccess(statusCode, headers, response);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    Log.e(TAG, "sendBroadcast: Failed: Message: " + throwable.getMessage() + ":Status Code: " + statusCode + ": Response: " + responseString, throwable);
                    super.onFailure(statusCode, headers, responseString, throwable);
                }
            });
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, "sendBroadcast: error occurred in creating the http entity.", e);
        }
        // TODO: retry this operation if it's network failure..
    }

    public void joinGame() {
        if (isNetworkAvailable(mContext)) {
            Log.d(TAG, "Joining Game through subscribing to the game feed.");
            FirebaseMessaging.getInstance().subscribeToTopic(mGameName);
            changeGameParticipation(true);
        }
    }

    public void leaveGame() {
        if (isNetworkAvailable(mContext)) {
            Log.d(TAG, "Leave Game through un-subscribing from the game feed.");
            FirebaseMessaging.getInstance().unsubscribeFromTopic(mGameName);
            changeGameParticipation(false);
        }
    }

    /**
     * Broadcast whether current user is joining the game or leaving
     *
     * @param joining true if joining the game , false if leaving the game
     */
    private void changeGameParticipation(boolean joining) {
        // Send to Upstream Server
        JsonObject payload = getJson(mCurrentUser);
        payload.addProperty(COMMON_IDENTIFIER, joining ? PARSE_NEW_PLAYER_ADDED : PARSE_PLAYER_LEFT);
        sendBroadcast(payload);
    }

    /**
     * Dealer dealing cards to a particular user
     *
     * @param toUser to whom this is sent
     * @param card   which card
     */
    public void dealCards(final User toUser, final Card card) {
        JsonObject payload = getJson(mCurrentUser);
        payload.add(PARAM_PLAYER, getJson(toUser));
        payload.add(PARAM_CARDS, new Gson().toJsonTree(card));
        payload.addProperty(COMMON_IDENTIFIER, PARSE_DEAL_CARDS);
        sendBroadcast(payload);
    }

    /**
     * Dealer moving cards to table
     *
     * @param cards which cards
     */
    public void dealCardsToTable(final List<Card> cards) {
        ParseDB.deleteGameTables(mGameName, () -> {
            GameTable.save(mGameName, cards, false);
            JsonObject payload = getJson(mCurrentUser);
            payload.addProperty(PARAM_CARD_COUNT, cards.size());
            payload.addProperty(COMMON_IDENTIFIER, PARSE_DEAL_CARDS_TO_TABLE);
            sendBroadcast(payload);
        });
    }

    public void dealCardsToSink(final List<Card> cards) {
        JsonObject payload = getJson(mCurrentUser);
        payload.add(PARAM_CARDS, new Gson().toJsonTree(cards));
        payload.addProperty(COMMON_IDENTIFIER, PARSE_DEAL_CARDS_TO_SINK);
        sendBroadcast(payload);
    }

    public void sendChatMessage(final String message) {
        JsonObject payload = getJson(mCurrentUser);
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
        JsonObject payload = getJson(mCurrentUser);
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
        JsonObject payload = getJson(mCurrentUser);
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
        JsonObject payload = getJson(mCurrentUser);
        payload.add(PARAM_CARDS, new Gson().toJsonTree(card));
        payload.addProperty(FROM_TAG, fromTag);
        payload.addProperty(FROM_POSITION, fromPosition);
        payload.addProperty(COMMON_IDENTIFIER, PARSE_DROP_CARD_TO_SINK);
        sendBroadcast(payload);
    }

    public void toggleCard(final Card card, final int position, final String onTag) {
        JsonObject payload = getJson(mCurrentUser);
        payload.add(PARAM_CARDS, new Gson().toJsonTree(card));
        payload.addProperty(POSITION, position);
        payload.addProperty(ON_TAG, onTag);
        payload.addProperty(COMMON_IDENTIFIER, PARSE_TOGGLE_CARD);
        sendBroadcast(payload);
    }

    public void declareRoundWinners(List<User> roundWinners) {
        JsonObject payload = getJson(mCurrentUser);
        payload.add(PARAM_PLAYERS, new Gson().toJsonTree(roundWinners));
        payload.addProperty(COMMON_IDENTIFIER, PARSE_ROUND_WINNERS);
        sendBroadcast(payload);
    }

    public void endRound() {
        JsonObject payload = getJson(mCurrentUser);
        payload.addProperty(COMMON_IDENTIFIER, PARSE_END_ROUND);
        sendBroadcast(payload);
    }

    public void selectGameRules(String code, Object selection) {
        JsonObject payload = getJson(mCurrentUser);
        payload.addProperty(RULE_CODE, code);
        if (selection instanceof Boolean) {
            payload.addProperty(RULE_SELECTION, (Boolean) selection);
        }
        payload.addProperty(COMMON_IDENTIFIER, PARSE_SELECT_GAME_RULES);
        sendBroadcast(payload);
    }

}