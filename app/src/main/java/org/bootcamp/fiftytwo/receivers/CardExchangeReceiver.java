package org.bootcamp.fiftytwo.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.parse.ParseUser;

import org.bootcamp.fiftytwo.application.FiftyTwoApplication;
import org.bootcamp.fiftytwo.models.User;
import org.bootcamp.fiftytwo.utils.Constants;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by baphna on 11/18/2016.
 */
public class CardExchangeReceiver extends BroadcastReceiver{

    private static final String intentAction = "com.parse.push.intent.RECEIVE";
    private FiftyTwoApplication fiftyTwoApplication;

    @Override
    public void onReceive(Context context, Intent intent) {
        fiftyTwoApplication = ((FiftyTwoApplication)context.getApplicationContext());
        Log.d(Constants.TAG, "onReceive");
        if (intent == null) {
            Log.d(Constants.TAG, "Receiver intent null");
        } else {
            processBroadcast(context, intent);
        }

    }

    private void processBroadcast(Context context, Intent intent) {
        String action = intent.getAction();

        if (action.equals(intentAction)) {
            try {
                JSONObject json = new JSONObject(intent.getExtras().getString("com.parse.Data"));
                JSONObject customData = new JSONObject(json.getString("customData"));

                String identifier = customData.getString(Constants.COMMON_IDENTIFIER);
                User userFromJson = new User(customData);
                Log.d(Constants.TAG , identifier + "--" + customData.toString());

                if(identifier.equals(Constants.PARSE_NEW_PLAYER_ADDED)
                        || identifier.equals(Constants.PARSE_PLAYER_LEFT)) {
                    if (!userFromJson.getUserId().equals(ParseUser.getCurrentUser().getObjectId())) {
                        fiftyTwoApplication.notifyObservers(identifier.toString(), userFromJson);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


}
