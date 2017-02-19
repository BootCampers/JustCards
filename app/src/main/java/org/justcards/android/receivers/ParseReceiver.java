package org.justcards.android.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.justcards.android.application.JustCardsAndroidApplication;
import org.justcards.android.models.User;

import static org.justcards.android.network.ParseUtils.isSelf;
import static org.justcards.android.utils.Constants.COMMON_IDENTIFIER;
import static org.justcards.android.utils.Constants.PARSE_CHAT_MESSAGE;
import static org.justcards.android.utils.Constants.PARSE_DEAL_CARDS;
import static org.justcards.android.utils.Constants.PARSE_DEAL_CARDS_TO_SINK;
import static org.justcards.android.utils.Constants.PARSE_DEAL_CARDS_TO_TABLE;
import static org.justcards.android.utils.Constants.PARSE_DROP_CARD_TO_SINK;
import static org.justcards.android.utils.Constants.PARSE_END_ROUND;
import static org.justcards.android.utils.Constants.PARSE_EXCHANGE_CARD_WITH_TABLE;
import static org.justcards.android.utils.Constants.PARSE_MUTE_PLAYER_FOR_ROUND;
import static org.justcards.android.utils.Constants.PARSE_NEW_PLAYER_ADDED;
import static org.justcards.android.utils.Constants.PARSE_PLAYER_LEFT;
import static org.justcards.android.utils.Constants.PARSE_ROUND_WINNERS;
import static org.justcards.android.utils.Constants.PARSE_SCORES_UPDATED;
import static org.justcards.android.utils.Constants.PARSE_SELECT_GAME_RULES;
import static org.justcards.android.utils.Constants.PARSE_SWAP_CARD_WITHIN_PLAYER;
import static org.justcards.android.utils.Constants.PARSE_TOGGLE_CARD;
import static org.justcards.android.utils.Constants.PARSE_TOGGLE_CARDS_LIST;
import static org.justcards.android.utils.Constants.TAG;

/**
 * Created by baphna on 11/18/2016.
 */
public class ParseReceiver extends BroadcastReceiver {

    private static final String intentAction = "com.parse.push.intent.RECEIVE";
    private JustCardsAndroidApplication application;

    @Override
    public void onReceive(Context context, Intent intent) {
        application = ((JustCardsAndroidApplication) context.getApplicationContext());
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
            JsonParser parser = new JsonParser();
            JsonObject data = (JsonObject) parser.parse(intent.getExtras().getString("com.parse.Data"));
            JsonObject customData = (JsonObject) parser.parse(data.get("customData").getAsString());

            String identifier = customData.get(COMMON_IDENTIFIER).getAsString();
            User from = User.fromJson(customData);
            Log.d(TAG, identifier + "--" + customData.toString());

            switch (identifier) {
                case PARSE_NEW_PLAYER_ADDED:
                case PARSE_PLAYER_LEFT:
                case PARSE_DEAL_CARDS:
                case PARSE_DEAL_CARDS_TO_TABLE:
                case PARSE_DEAL_CARDS_TO_SINK:
                case PARSE_TOGGLE_CARDS_LIST:
                case PARSE_MUTE_PLAYER_FOR_ROUND:
                case PARSE_SCORES_UPDATED:
                case PARSE_ROUND_WINNERS:
                case PARSE_END_ROUND:
                case PARSE_SELECT_GAME_RULES:
                    application.notifyObservers(identifier, customData);
                    break;
                case PARSE_EXCHANGE_CARD_WITH_TABLE:
                case PARSE_SWAP_CARD_WITHIN_PLAYER:
                case PARSE_DROP_CARD_TO_SINK:
                case PARSE_TOGGLE_CARD:
                case PARSE_CHAT_MESSAGE:
                    // Process only if it's not from self/current user
                    if (!isSelf(from)) {
                        application.notifyObservers(identifier, customData);
                    }
                    break;
                default:
                    Log.e(TAG, "Unknown identifier " + identifier);
                    break;
            }
        }
    }
}