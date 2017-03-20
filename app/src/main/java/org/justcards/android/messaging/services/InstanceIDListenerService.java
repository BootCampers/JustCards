package org.justcards.android.messaging.services;

import android.content.Intent;

import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Author: agoenka
 * Created At: 2/18/2017
 * Version: 1.0
 */
public class InstanceIDListenerService extends FirebaseInstanceIdService {

    /**
     * Called if InstanceID token is updated.
     * This may occur if the security of the previous token had been compromised.
     * Note that this is called when the InstanceID token is initially generated,
     * So this is where you would retrieve the token.
     */
    @Override
    public void onTokenRefresh() {
        // Fetch updated Instance ID token and notify changes
        Intent intent = new Intent(this, RegistrationService.class);
        startService(intent);
    }
}