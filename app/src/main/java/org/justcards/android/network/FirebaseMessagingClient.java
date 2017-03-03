package org.justcards.android.network;

import android.content.Context;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessaging;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;
import org.justcards.android.models.User;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.client.cache.HeaderConstants;
import cz.msebera.android.httpclient.entity.StringEntity;

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

    public void joinGame() {
        if (isNetworkAvailable(mContext)) {
            Log.d(TAG, "Joining Game through subscribing to the game feed.");
            FirebaseMessaging.getInstance().subscribeToTopic(mGameName);
            changeGameParticipation(true);
        }
    }

    /**
     * Broadcast whether current user is joining the game or leaving
     *
     * @param joining true if joining the game , false if leaving the game
     */
    private void changeGameParticipation(boolean joining) {
        // Send to Upstream Server

        // https://firebase.google.com/docs/cloud-messaging/android/topic-messaging
        // https://firebase.google.com/docs/cloud-messaging/android/device-group

        try {
            String payload = "{\n" +
                    "  \"to\": \"/topics/" + mGameName + "\",\n" +
                    "  \"data\": {\n" +
                    "    \"message\": \"This is a Firebase Cloud Messaging Topic Message!\",\n" +
                    "   }\n" +
                    "}";

            String contentType = "application/json";

            AsyncHttpClient client = new AsyncHttpClient();
            client.addHeader(HeaderConstants.AUTHORIZATION, API_KEY);
            HttpEntity entity = new StringEntity(payload);

            client.post(mContext, API_URL, entity, contentType, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    Log.d(TAG, "onSuccess: ");
                    super.onSuccess(statusCode, headers, response);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    Log.e(TAG, "onFailure: ", throwable);
                    super.onFailure(statusCode, headers, responseString, throwable);
                }
            });
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

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

    public void leaveGame() {
        if (isNetworkAvailable(mContext)) {
            Log.d(TAG, "Leave Game through un-subscribing from the game feed.");
            FirebaseMessaging.getInstance().unsubscribeFromTopic(mGameName);
            changeGameParticipation(false);
        }
    }

}