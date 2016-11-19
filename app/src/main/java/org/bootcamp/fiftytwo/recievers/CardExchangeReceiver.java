package org.bootcamp.fiftytwo.recievers;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.parse.ParsePushBroadcastReceiver;

import org.bootcamp.fiftytwo.activities.ShowPopUp;
import org.bootcamp.fiftytwo.utils.Constants;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

/**
 * Created by baphna on 11/18/2016.
 */
public class CardExchangeReceiver extends ParsePushBroadcastReceiver {
    @Override
    public void onPushReceive(Context context, Intent intent) {
        try {
            if (intent == null)
            {
                Log.d(Constants.TAG, "Receiver intent null");
            }
            else
            {
                String action = intent.getAction();
                Log.d(Constants.TAG, "got action " + action );
                if (action.equals("org.bootcamp.fiftytwo.UPDATE"))
                {
                    String channel = intent.getExtras().getString("com.parse.Channel");
                    JSONObject json = new JSONObject(intent.getExtras().getString("com.parse.Data"));
                    Log.d(Constants.TAG, "got action " + action + " on channel " + channel + " with:");
                    Iterator itr = json.keys();
                    while (itr.hasNext()) {
                        String key = (String) itr.next();
                        if (key.equals("customdata"))
                        {
                            Intent pupInt = new Intent(context, ShowPopUp.class);
                            pupInt.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
                            context.getApplicationContext().startActivity(pupInt);
                        }
                        Log.d(Constants.TAG, "..." + key + " => " + json.getString(key));
                    }
                }
            }
        } catch (JSONException e) {
            Log.d(Constants.TAG, "JSONException: " + e.getMessage());
        }
    }
}
