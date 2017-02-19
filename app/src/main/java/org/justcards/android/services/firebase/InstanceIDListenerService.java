package org.justcards.android.services.firebase;

import android.content.Intent;

import com.google.firebase.iid.FirebaseInstanceIdService;

import org.justcards.android.services.RegistrationService;

/**
 * Author: agoenka
 * Created At: 2/18/2017
 * Version: 1.0
 */
public class InstanceIDListenerService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        // Fetch updated Instance ID token and notify changes
        Intent intent = new Intent(this, RegistrationService.class);
        startService(intent);
    }
}