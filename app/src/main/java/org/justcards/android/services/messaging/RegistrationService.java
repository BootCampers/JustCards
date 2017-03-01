package org.justcards.android.services.messaging;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;

import org.justcards.android.R;

import static org.justcards.android.utils.Constants.FCM_TOKEN;
import static org.justcards.android.utils.Constants.SENT_TOKEN_TO_SERVER;

/**
 * Author: agoenka
 * Created At: 2/18/2017
 * Version: 1.0
 */
public class RegistrationService extends IntentService {

    private static final String TAG = RegistrationService.class.getSimpleName();

    public RegistrationService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Call to Instance API
        FirebaseInstanceId instanceID = FirebaseInstanceId.getInstance();
        String senderId = getResources().getString(R.string.gcm_defaultSenderId);
        Log.d(TAG, "Sender ID: " + senderId);

        try {
            // Request token that will be used by the server to send Push Notifications
            String token = instanceID.getToken();
            Log.d(TAG, "FCM Registration Token: " + token);

            // Save Token
            sharedPreferences.edit().putString(FCM_TOKEN, token).apply();
            // Pass along this data
            sendRegistrationToServer(token);
        } catch (Exception e) {
            Log.e(TAG, "Failed to complete token refresh", e);
            // If an exception occurs while fetching the new token or updating registration data on a third party server
            // This ensures that update will be attempted at a later time
            sharedPreferences.edit().putBoolean(SENT_TOKEN_TO_SERVER, false).apply();
        }
    }

    private void sendRegistrationToServer(final String token) {
        // Send Network Request
        // Add custom implementation, as needed.
        // There is no custom backend as of yet to manage user registration information
        // Hence no network request as of now.
        Log.d(TAG, "Token ID to send to server: " + token);

        // If registration was successful, store a boolean that indicates whether the generated token has been sent to the server
        PreferenceManager.getDefaultSharedPreferences(this)
                .edit()
                .putBoolean(SENT_TOKEN_TO_SERVER, true)
                .apply();
    }
}