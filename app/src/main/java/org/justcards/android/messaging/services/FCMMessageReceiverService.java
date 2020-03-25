package org.justcards.android.messaging.services;

import android.content.Intent;
import android.content.IntentFilter;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.justcards.android.messaging.MessageReceiver;

import java.util.HashMap;
import java.util.Map;

import static android.text.TextUtils.isEmpty;
import static org.justcards.android.utils.Constants.FROM;
import static org.justcards.android.utils.Constants.FROM_ADDRESS_PREFIX;
import static org.justcards.android.utils.Constants.PARAM_GAME_DATA;
import static org.justcards.android.utils.Constants.PARAM_GAME_NAME;
import static org.justcards.android.utils.Constants.TO;

/**
 * Author: agoenka
 * Created At: 2/18/2017
 * Version: 1.0
 */
public class FCMMessageReceiverService extends FirebaseMessagingService {

    public static final String TAG = FCMMessageReceiverService.class.getSimpleName();
    public static final String ACTION = "org.justcards.push.intent.RECEIVE";
    private MessageReceiver mMessageReceiver;

    @Override
    public void onCreate() {
        super.onCreate();
        mMessageReceiver = new MessageReceiver();
        // Register the receiver to send the broadcast intent to
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter(ACTION));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
    }

    /**
     * There are two types of messages data messages and notification messages.
     * Data messages are handled here in onMessageReceived whether the app is in the foreground or background.
     * Data messages are the type traditionally used with GCM.
     * Notification messages are only received here in onMessageReceived when the app is in the foreground.
     * When the app is in the background an automatically generated notification is displayed.
     * When the user taps on the notification they are returned to the app.
     * Messages containing both notification and data payloads are treated as notification messages.
     * The Firebase console always sends notification messages.
     * For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // Check if message contains the from information
        String from = remoteMessage.getFrom();
        // Log the from
        Log.d(TAG, "Message received from: " + from);

        String to = remoteMessage.getTo();
        // Log the to
        Log.d(TAG, "Message received to: " + to);

        // Check if message contains a data payload
        Map<String, String> data = remoteMessage.getData();
        if (data.size() > 0) {
            Log.d(TAG, "Message data payload: " + data);
        }

        // Check if message contains a notification payload
        RemoteMessage.Notification notification = remoteMessage.getNotification();
        if (notification != null) {
            Log.d(TAG, "Message notification title: " + notification.getTitle() + ". Message notification body: " + notification.getBody());
        }

        if (!isEmpty(from) && from.startsWith(FROM_ADDRESS_PREFIX)) {
            String gameName = from.replace(FROM_ADDRESS_PREFIX, "");

            HashMap<String, String> gameData = new HashMap<>();
            for (Map.Entry<String, String> entry : data.entrySet()) {
                gameData.put(entry.getKey(), entry.getValue());
            }

            // Construct an Intent tying it to the ACTION on the application namespace
            Intent intent = new Intent(ACTION);
            intent.putExtra(FROM, from); // From is usually the topic name. e.g. '/topics/199'
            intent.putExtra(TO, to); // To is usually null
            intent.putExtra(PARAM_GAME_NAME, gameName);
            intent.putExtra(PARAM_GAME_DATA, gameData); // Game Data is usually the game data saved in payload in the upstream message

            // Broadcast the received intent to be handled by the application
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        }
    }

    @Override
    public void onMessageSent(String s) {
        Log.d(TAG, "onMessageSent: " + s);
        super.onMessageSent(s);
    }

    @Override
    public void onSendError(String s, Exception e) {
        Log.e(TAG, "onSendError: " + s, e);
        super.onSendError(s, e);
    }

}