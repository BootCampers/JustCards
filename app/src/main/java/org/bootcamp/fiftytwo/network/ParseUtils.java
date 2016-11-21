package org.bootcamp.fiftytwo.network;

import android.content.Context;

import com.parse.ParseCloud;
import com.parse.ParsePush;
import com.parse.ParseUser;

import org.bootcamp.fiftytwo.models.User;
import org.bootcamp.fiftytwo.utils.Constants;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

// The code that processes this function is listed at:
// https://github.com/rogerhu/parse-server-push-marker-example/blob/master/cloud/main.js

public class ParseUtils {

    private Context mContext;
    private String gameName; //used for channel name
    private final String SERVER_FUNCTION_NAME = "pushToChannel";

    public ParseUtils(Context mContext, String gameName) {
        this.mContext = mContext;
        this.gameName = gameName;
    }

    public void addNewPlayer(User user){
        try {
            JSONObject payload = getPayloadFromUser(user);
            payload.put(Constants.COMMON_IDENTIFIER, Constants.PARSE_NEW_PLAYER_ADDED);

            HashMap<String, String> data = new HashMap<>();
            data.put("customData", payload.toString());
            data.put("channel", gameName);
            ParseCloud.callFunctionInBackground(SERVER_FUNCTION_NAME, data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void joinChannel() {
        ParsePush.subscribeInBackground(gameName);
    }

    private static JSONObject getPayloadFromUser(User user) throws JSONException {

        JSONObject payload = new JSONObject();
        payload.put(Constants.USERNAME, user.getName());
        payload.put(Constants.USER_AVATAR_URI, user.getAvatarUri());

        payload.put("userId", ParseUser.getCurrentUser().getObjectId());
        return payload;
    }
}
