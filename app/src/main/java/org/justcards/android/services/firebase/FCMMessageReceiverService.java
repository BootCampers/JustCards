package org.justcards.android.services.firebase;

import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.justcards.android.receivers.MessageReceiver;

import java.util.Map;

import static org.justcards.android.utils.Constants.TAG;

/**
 * Author: agoenka
 * Created At: 2/18/2017
 * Version: ${VERSION}
 */
public class FCMMessageReceiverService extends FirebaseMessagingService {

    public static final String ACTION = "org.justcards.push.intent.RECEIVE";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // Fetch data passed into the intent
        String from = remoteMessage.getFrom();
        Map<String, String> data = remoteMessage.getData();

        // Log the data and from
        Log.d(TAG, "Message received from : " + from);
        Log.d(TAG, "Data received : " + data);

        // Construct an Intent tying it to the ACTION on the application namespace
        Intent intent = new Intent(ACTION);
        intent.putExtra("from", from);
        intent.putExtra("data", data.get("gameName"));

        // Broadcast the received intent to be handled by the application
        broadCast(intent);
    }

    private void broadCast(final Intent intent) {
        MessageReceiver messageReceiver = new MessageReceiver();
        IntentFilter intentFilter = new IntentFilter(ACTION);

        // Register the receiver to send the broadcast intent to
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, intentFilter);
        // Fire the broadcast with intent
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

}