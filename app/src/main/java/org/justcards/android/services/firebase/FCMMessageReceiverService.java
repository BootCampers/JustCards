package org.justcards.android.services.firebase;

import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.firebase.messaging.RemoteMessage.Notification;

import org.justcards.android.R;

import java.util.Map;

import static org.justcards.android.utils.Constants.TAG;

/**
 * Author: agoenka
 * Created At: 2/18/2017
 * Version: ${VERSION}
 */

public class FCMMessageReceiverService extends FirebaseMessagingService {

    private static final int MESSAGE_NOTIFICATION_ID = 435345;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        String from = remoteMessage.getFrom();
        Map<String, String> data = remoteMessage.getData();

        // Broadcast the data and from
        Log.d(TAG, "Message received from : " + from);
        Log.d(TAG, "Data received : " + data);

        Notification notification = remoteMessage.getNotification();
        createNotification(notification);
    }

    private void createNotification(Notification notification) {
        Context context = getBaseContext();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(notification.getTitle())
                .setContentText(notification.getBody());
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(MESSAGE_NOTIFICATION_ID, builder.build());
    }
}