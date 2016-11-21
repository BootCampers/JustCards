package org.bootcamp.fiftytwo.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.bootcamp.fiftytwo.application.FiftyTwoApplication;
import org.bootcamp.fiftytwo.utils.Constants;

/**
 * Created by baphna on 11/18/2016.
 */
public class CardExchangeReceiver extends BroadcastReceiver{


    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(Constants.TAG, "onReceive");
        FiftyTwoApplication fiftyTwoApplication = ((FiftyTwoApplication)context.getApplicationContext());
        fiftyTwoApplication.notifyObservers(Constants.NEW_PLAYER_ADDED);
    }


}
