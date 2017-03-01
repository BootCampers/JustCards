package org.justcards.android.network;

import android.content.Context;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessaging;

import org.justcards.android.models.User;

import static org.justcards.android.utils.NetworkUtils.isNetworkAvailable;

/**
 * Author: agoenka
 * Created At: 2/28/2017
 * Version: 1.0
 */
public class FirebaseMessagingClient {

    public static final String TAG = FirebaseMessagingClient.class.getSimpleName();

    private Context mContext;
    private String mGameName;
    private User mCurrentUser;

    public FirebaseMessagingClient(Context context, String gameName, User currentUser) {
        this.mContext = context;
        this.mGameName = gameName;
        this.mCurrentUser = currentUser;
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
        /*JsonObject payload = getJson(mCurrentUser);
        if (joining) {
            payload.addProperty(COMMON_IDENTIFIER, PARSE_NEW_PLAYER_ADDED);
        } else {
            payload.addProperty(COMMON_IDENTIFIER, PARSE_PLAYER_LEFT);
        }
        sendBroadcast(payload);*/
    }

    /*private void sendBroadcast(final JsonObject payload) {
        if (isNetworkAvailable(mContext)) {
            HashMap<String, String> data = new HashMap<>();
            data.put("customData", payload.toString());
            data.put("channel", mGameName);
            ParseCloud.callFunctionInBackground(SERVER_FUNCTION_NAME, data, (object, e) -> {
                if (e == null) {
                    Log.d(TAG, "sendBroadcast: Succeeded! " + payload.toString());
                } else {
                    Log.e(TAG, "sendBroadcast: Failed: Message: " + e.getMessage() + ": Object: " + object);
                }
            });
        }
        //TODO: retry this operation if it's network failure..
    }*/


}