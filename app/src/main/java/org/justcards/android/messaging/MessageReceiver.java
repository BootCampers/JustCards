package org.justcards.android.messaging;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.justcards.android.application.JustCardsAndroidApplication;
import org.justcards.android.models.Game;
import org.justcards.android.models.User;

import java.util.HashMap;

import static org.justcards.android.models.User.getCurrentUser;
import static org.justcards.android.models.User.isSelf;
import static org.justcards.android.utils.Constants.EVENT_IDENTIFIER;
import static org.justcards.android.utils.Constants.PARAM_GAME_DATA;
import static org.justcards.android.utils.Constants.PARAM_GAME_NAME;
import static org.justcards.android.utils.Constants.EVENT_CHAT_MESSAGE;
import static org.justcards.android.utils.Constants.EVENT_DEAL_CARDS;
import static org.justcards.android.utils.Constants.EVENT_DROP_CARD_TO_SINK;
import static org.justcards.android.utils.Constants.EVENT_END_ROUND;
import static org.justcards.android.utils.Constants.EVENT_EXCHANGE_CARD_WITH_TABLE;
import static org.justcards.android.utils.Constants.EVENT_ROUND_WINNERS;
import static org.justcards.android.utils.Constants.EVENT_SELECT_GAME_RULES;
import static org.justcards.android.utils.Constants.EVENT_SWAP_CARD_WITHIN_PLAYER;
import static org.justcards.android.utils.Constants.EVENT_TOGGLE_CARD;
import static org.justcards.android.utils.Constants.TAG;

public class MessageReceiver extends BroadcastReceiver {

    private static final String intentAction = "org.justcards.push.intent.RECEIVE";
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

    @SuppressWarnings("unchecked")
    private void processBroadcast(Intent intent) {
        String action = intent.getAction();
        if (action.equals(intentAction)) {
            String gameName = intent.getStringExtra(PARAM_GAME_NAME);
            String savedGame = Game.getInstance(application).getName();
            if (savedGame == null || savedGame.equals(gameName)) {
                HashMap<String, String> gameData = (HashMap<String, String>) intent.getSerializableExtra(PARAM_GAME_DATA);
                String identifier = gameData.get(EVENT_IDENTIFIER);
                Log.d(TAG, identifier + "--" + gameData.toString());
                switch (identifier) {
                    case EVENT_DEAL_CARDS:
                    case EVENT_ROUND_WINNERS:
                    case EVENT_END_ROUND:
                    case EVENT_SELECT_GAME_RULES:
                        application.notifyObservers(identifier, gameData);
                        break;
                    case EVENT_EXCHANGE_CARD_WITH_TABLE:
                    case EVENT_SWAP_CARD_WITHIN_PLAYER:
                    case EVENT_DROP_CARD_TO_SINK:
                    case EVENT_TOGGLE_CARD:
                    case EVENT_CHAT_MESSAGE:
                        // Process only if it's not from self/current user
                        User from = User.fromMap(gameData);
                        Context context = application.getApplicationContext();
                        Log.d(TAG, "MessageReceiver: Saved User ID: " + getCurrentUser(context).getUserId() + " : Received User ID: " + from.getUserId());
                        if (!isSelf(from, context)) {
                            application.notifyObservers(identifier, gameData);
                        }
                        break;
                    default:
                        Log.e(TAG, "Unknown identifier " + identifier);
                        break;
                }
            }
        }
    }
}