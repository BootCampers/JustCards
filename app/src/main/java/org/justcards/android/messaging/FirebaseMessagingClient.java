package org.justcards.android.messaging;

import android.content.Context;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;
import org.justcards.android.models.Card;
import org.justcards.android.models.User;

import java.io.UnsupportedEncodingException;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.client.cache.HeaderConstants;
import cz.msebera.android.httpclient.entity.StringEntity;

import static org.justcards.android.models.User.getJson;
import static org.justcards.android.utils.Constants.DATA;
import static org.justcards.android.utils.Constants.EVENT_CHAT_MESSAGE;
import static org.justcards.android.utils.Constants.EVENT_DEAL_CARDS;
import static org.justcards.android.utils.Constants.EVENT_DROP_CARD_TO_SINK;
import static org.justcards.android.utils.Constants.EVENT_END_ROUND;
import static org.justcards.android.utils.Constants.EVENT_EXCHANGE_CARD_WITH_TABLE;
import static org.justcards.android.utils.Constants.EVENT_IDENTIFIER;
import static org.justcards.android.utils.Constants.EVENT_NEW_PLAYER_ADDED;
import static org.justcards.android.utils.Constants.EVENT_PLAYER_LEFT;
import static org.justcards.android.utils.Constants.EVENT_ROUND_WINNERS;
import static org.justcards.android.utils.Constants.EVENT_SELECT_GAME_RULES;
import static org.justcards.android.utils.Constants.EVENT_SWAP_CARD_WITHIN_PLAYER;
import static org.justcards.android.utils.Constants.EVENT_TOGGLE_CARD;
import static org.justcards.android.utils.Constants.FROM_ADDRESS_PREFIX;
import static org.justcards.android.utils.Constants.FROM_POSITION;
import static org.justcards.android.utils.Constants.FROM_TAG;
import static org.justcards.android.utils.Constants.ON_TAG;
import static org.justcards.android.utils.Constants.PARAM_CARDS;
import static org.justcards.android.utils.Constants.PARAM_CHAT;
import static org.justcards.android.utils.Constants.PARAM_PLAYER;
import static org.justcards.android.utils.Constants.PARAM_PLAYERS;
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
 * Version: 1.1
 */
public class FirebaseMessagingClient {

    public static final String TAG = FirebaseMessagingClient.class.getSimpleName();
    private static final String API_URL = "https://fcm.googleapis.com/fcm/send";
    private static final String API_KEY = "key=AAAAxsSWPqI:APA91bGWXoizf9FGz4J4dMNyfoHjqrrDcAsp_XvDHA71bH0KGFhhfNW-_rYXR83C4ctnT850dnnWiizTj8f6FrOE4vCF6gh_EUmjZM7zwmUPR8S-ZF7SSUXvMf-iPeXVZzJHpmZPTdQu";

    private Context mContext;
    private String mGameName;

    public FirebaseMessagingClient(Context context, String gameName) {
        this.mContext = context;
        this.mGameName = gameName;
    }

    private User getCurrentUser() {
        return User.getCurrentUser(mContext);
    }

    private JsonObject initJson(final String eventId) {
        JsonObject json = getJson(getCurrentUser());
        json.addProperty(EVENT_IDENTIFIER, eventId);
        return json;
    }

    /**
     * @param payload the payload to broadcast
     * @see [https://firebase.google.com/docs/cloud-messaging/android/topic-messaging]
     * @see [https://firebase.google.com/docs/cloud-messaging/android/device-group]
     */
    private void broadcast(final JsonObject payload) {
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
                    Log.d(TAG, "broadcast: Succeeded: " + payload.toString());
                    super.onSuccess(statusCode, headers, response);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    Log.e(TAG, "broadcast: Failed: Message: " + throwable.getMessage() + ":Status Code: " + statusCode + ": Response: " + responseString, throwable);
                    super.onFailure(statusCode, headers, responseString, throwable);
                }
            });
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, "broadcast: error occurred in creating the http entity.", e);
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
        JsonObject payload = initJson(joining ? EVENT_NEW_PLAYER_ADDED : EVENT_PLAYER_LEFT);
        broadcast(payload);
    }

    /**
     * Dealer dealing cards to a particular user
     *
     * @param toUser to whom this is sent
     * @param card   which card
     */
    public void dealCards(final User toUser, final Card card) {
        JsonObject payload = initJson(EVENT_DEAL_CARDS);
        payload.add(PARAM_PLAYER, getJson(toUser));
        payload.add(PARAM_CARDS, new Gson().toJsonTree(card));
        broadcast(payload);
    }

    public void selectGameRules(String code, Object selection) {
        JsonObject payload = initJson(EVENT_SELECT_GAME_RULES);
        payload.addProperty(RULE_CODE, code);
        if (selection instanceof Boolean) {
            payload.addProperty(RULE_SELECTION, (Boolean) selection);
        }
        broadcast(payload);
    }

    public void sendChatMessage(final String message) {
        JsonObject payload = initJson(EVENT_CHAT_MESSAGE);
        payload.addProperty(PARAM_CHAT, message);
        broadcast(payload);
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
        JsonObject payload = initJson(EVENT_EXCHANGE_CARD_WITH_TABLE);
        payload.add(PARAM_CARDS, new Gson().toJsonTree(card));
        payload.addProperty(FROM_POSITION, fromPosition);
        payload.addProperty(TO_POSITION, toPosition);
        payload.addProperty(TABLE_PICKED, pickedFromTable);
        broadcast(payload);
    }

    /**
     * Player swaps a card within his/her hand from one position to another
     *
     * @param card         which card
     * @param fromPosition position of the card where it was picked from
     * @param toPosition   position of the card where it is dropped in
     */
    public void swapCardWithinPlayer(final Card card, final int fromPosition, final int toPosition) {
        JsonObject payload = initJson(EVENT_SWAP_CARD_WITHIN_PLAYER);
        payload.add(PARAM_CARDS, new Gson().toJsonTree(card));
        payload.addProperty(FROM_POSITION, fromPosition);
        payload.addProperty(TO_POSITION, toPosition);
        broadcast(payload);
    }

    /**
     * Player picks up a card from a stack of cards and drops them on to the Sink
     *
     * @param card         which card
     * @param fromTag      from which stack of cards
     * @param fromPosition from which position in the stack of cards
     */
    public void dropCardToSink(final Card card, final String fromTag, final int fromPosition) {
        JsonObject payload = initJson(EVENT_DROP_CARD_TO_SINK);
        payload.add(PARAM_CARDS, new Gson().toJsonTree(card));
        payload.addProperty(FROM_TAG, fromTag);
        payload.addProperty(FROM_POSITION, fromPosition);
        broadcast(payload);
    }

    public void toggleCard(final Card card, final int position, final String onTag) {
        JsonObject payload = initJson(EVENT_TOGGLE_CARD);
        payload.add(PARAM_CARDS, new Gson().toJsonTree(card));
        payload.addProperty(POSITION, position);
        payload.addProperty(ON_TAG, onTag);
        broadcast(payload);
    }

    public void declareRoundWinners(List<User> roundWinners) {
        JsonObject payload = initJson(EVENT_ROUND_WINNERS);
        payload.add(PARAM_PLAYERS, new Gson().toJsonTree(roundWinners));
        broadcast(payload);
    }

    public void endRound() {
        JsonObject payload = initJson(EVENT_END_ROUND);
        broadcast(payload);
    }

}