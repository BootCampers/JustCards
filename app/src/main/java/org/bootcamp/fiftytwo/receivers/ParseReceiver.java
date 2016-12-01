package org.bootcamp.fiftytwo.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.bootcamp.fiftytwo.application.FiftyTwoApplication;
import org.bootcamp.fiftytwo.models.User;
import org.json.JSONException;
import org.json.JSONObject;

import static org.bootcamp.fiftytwo.network.ParseUtils.isSelf;
import static org.bootcamp.fiftytwo.utils.Constants.COMMON_IDENTIFIER;
import static org.bootcamp.fiftytwo.utils.Constants.PARSE_DEAL_CARDS;
import static org.bootcamp.fiftytwo.utils.Constants.PARSE_DEAL_CARDS_TO_TABLE;
import static org.bootcamp.fiftytwo.utils.Constants.PARSE_EXCHANGE_CARD_WITH_TABLE;
import static org.bootcamp.fiftytwo.utils.Constants.PARSE_NEW_PLAYER_ADDED;
import static org.bootcamp.fiftytwo.utils.Constants.PARSE_PLAYER_LEFT;
import static org.bootcamp.fiftytwo.utils.Constants.PARSE_SWAP_CARD_WITHIN_TABLE;
import static org.bootcamp.fiftytwo.utils.Constants.PARSE_TOGGLE_CARDS_VISIBILITY;
import static org.bootcamp.fiftytwo.utils.Constants.TAG;

/**
 * Created by baphna on 11/18/2016.
 */
public class ParseReceiver extends BroadcastReceiver {

    private static final String intentAction = "com.parse.push.intent.RECEIVE";
    private FiftyTwoApplication application;

    @Override
    public void onReceive(Context context, Intent intent) {
        application = ((FiftyTwoApplication) context.getApplicationContext());
        Log.d(TAG, "onReceive");
        if (intent == null) {
            Log.d(TAG, "Receiver intent null");
        } else {
            processBroadcast(intent);
        }
    }

    private void processBroadcast(Intent intent) {
        String action = intent.getAction();

        if (action.equals(intentAction)) {
            try {
                JSONObject json = new JSONObject(intent.getExtras().getString("com.parse.Data"));
                JSONObject customData = new JSONObject(json.getString("customData"));

                String identifier = customData.getString(COMMON_IDENTIFIER);
                Log.d(TAG, identifier + "--" + customData.toString());
                User user = User.fromJson(customData);

                switch (identifier) {
                    case PARSE_NEW_PLAYER_ADDED:
                    case PARSE_PLAYER_LEFT:
                    case PARSE_DEAL_CARDS:
                    case PARSE_DEAL_CARDS_TO_TABLE:
                    case PARSE_TOGGLE_CARDS_VISIBILITY:
                        application.notifyObservers(identifier, customData);
                        break;
                    case PARSE_EXCHANGE_CARD_WITH_TABLE:
                    case PARSE_SWAP_CARD_WITHIN_TABLE:
                        // Process only if it's not from self/current user
                        if (!isSelf(user)) {
                            application.notifyObservers(identifier, customData);
                        }
                        break;
                    default:
                        Log.e(TAG, "Unknown identifier " + identifier);
                        break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}