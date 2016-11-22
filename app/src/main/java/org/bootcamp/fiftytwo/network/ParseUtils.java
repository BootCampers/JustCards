package org.bootcamp.fiftytwo.network;

import android.content.Context;

import com.parse.ParseCloud;
import com.parse.ParsePush;

import org.bootcamp.fiftytwo.models.User;
import org.bootcamp.fiftytwo.utils.Constants;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

// The code that processes this function is listed at:
// https://github.com/rogerhu/parse-server-push-marker-example/blob/master/cloud/main.js

public class ParseUtils {

    private Context mContext;
    private String gameName; //used for channel name

    //TODO: temporary till login screeens are done
    User tempUser;

    public ParseUtils(Context mContext, String gameName) {
        this.mContext = mContext;
        this.gameName = gameName;
        tempUser = User.getPlayers(1).get(0);
    }

    public void addNewPlayer(User user){
        try {

            //TODO: change tempUser to user
            JSONObject payload = getPayloadFromUser(tempUser);
            payload.put(Constants.COMMON_IDENTIFIER, Constants.PARSE_NEW_PLAYER_ADDED);

            HashMap<String, String> data = new HashMap<>();
            data.put("customData", payload.toString());
            data.put("channel", gameName);
            ParseCloud.callFunctionInBackground(Constants.SERVER_FUNCTION_NAME, data);
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


    private static JSONObject getPayloadFromUser(User user) throws JSONException {

        JSONObject payload = new JSONObject();
        payload.put(Constants.DISPLAY_NAME, user.getDisplayName());
        payload.put(Constants.USER_AVATAR_URI, user.getAvatarUri());
        payload.put("userId", user.getUserId());
        return payload;
    }

    public void exitFromGame(User user) {
        try {
            //TODO: change tempUser to user
            JSONObject payload = getPayloadFromUser(tempUser);
            payload.put(Constants.COMMON_IDENTIFIER, Constants.PARSE_PLAYER_LEFT);

            HashMap<String, String> data = new HashMap<>();
            data.put("customData", payload.toString());
            data.put("channel", gameName);
            ParseCloud.callFunctionInBackground(Constants.SERVER_FUNCTION_NAME, data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //TODO
    public List<User> fetchPreviouslyJoinedUsers(){
        return new ArrayList<>();
    }
}
