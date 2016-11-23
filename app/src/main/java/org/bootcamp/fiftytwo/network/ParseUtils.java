package org.bootcamp.fiftytwo.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.parse.ParseCloud;
import com.parse.ParsePush;

import org.bootcamp.fiftytwo.models.Card;
import org.bootcamp.fiftytwo.models.User;
import org.bootcamp.fiftytwo.utils.Constants;
import org.bootcamp.fiftytwo.utils.PlayerUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static org.bootcamp.fiftytwo.utils.Constants.USER_PREFS;

// The code that processes this function is listed at:
// https://github.com/rogerhu/parse-server-push-marker-example/blob/master/cloud/main.js

public class ParseUtils {

    private Context mContext;
    private String gameName; //used for channel name
    private User currentLoggedinUser;

    public ParseUtils(Context mContext, String gameName) {
        this.mContext = mContext;
        this.gameName = gameName;

        SharedPreferences userPrefs = mContext.getSharedPreferences(USER_PREFS, MODE_PRIVATE);
        String displayName = userPrefs.getString(Constants.DISPLAY_NAME, "unknown");
        String profilePic = userPrefs.getString(Constants.USER_AVATAR_URI, "http://i.imgur.com/FLmEyXZ.jpg");
        String userId = userPrefs.getString(Constants.USER_ID, "usedIdUnknown");
        currentLoggedinUser = new User(profilePic, displayName, userId);
        Log.d(Constants.TAG, "Current user --"+displayName+"--"+profilePic+"--"+userId);
    }

    /**
     * Broadcast whether current user is joining the game or leaving
     * @param joining true if joining the game , false if leaving the game
     */
    public void changeGameParticipation(boolean joining){
        try {
            JSONObject payload = getPayloadFromUser(currentLoggedinUser);
            if(joining == true) {
                payload.put(Constants.COMMON_IDENTIFIER, Constants.PARSE_NEW_PLAYER_ADDED);
            } else {
                payload.put(Constants.COMMON_IDENTIFIER, Constants.PARSE_PLAYER_LEFT);
            }
            sendBroadcastWithPayload(payload);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void joinChannel() {
        ParsePush.subscribeInBackground(gameName);
    }
    public void removeChannel() {
        ParsePush.unsubscribeInBackground(gameName);
    }

    //TODO
    public List<User> fetchPreviouslyJoinedUsers(){
        return new ArrayList<>();
    }

    //TODO
    public List<User> fetchAllTableCards(){
        return new ArrayList<>();
    }

    /**
     * Current user passing card to particular user
     * @param toUser to whom this is sent
     * @param card which card
     */
    public void exchangeCard(User toUser, Card card){
        try {
            JSONObject payload = getPayloadFromUser(currentLoggedinUser);

            JSONObject toUserJson = getPayloadFromUser(toUser);

            payload.put(Constants.PARAM_PLAYER, toUserJson);

            Gson gson = new Gson();
            String cardJson = gson.toJson(card);
            payload.put(Constants.PARAM_CARDS, cardJson);

            payload.put(Constants.COMMON_IDENTIFIER, Constants.PARSE_PLAYERS_EXCHANGE_CARDS);
            sendBroadcastWithPayload(payload);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Helper function to convert user to json
     * @param user
     * @return convert user to json object to be sent as broadcast
     * @throws JSONException
     */
    private JSONObject getPayloadFromUser(User user) throws JSONException {

        JSONObject payload = new JSONObject();
        payload.put(Constants.DISPLAY_NAME, user.getDisplayName());
        payload.put(Constants.USER_AVATAR_URI, user.getAvatarUri());
        payload.put(Constants.USER_ID, user.getUserId());
        return payload;
    }

    private void sendBroadcastWithPayload(JSONObject payload){
        HashMap<String, String> data = new HashMap<>();
        data.put("customData", payload.toString());
        data.put("channel", gameName);
        ParseCloud.callFunctionInBackground(Constants.SERVER_FUNCTION_NAME, data);
    }

}
