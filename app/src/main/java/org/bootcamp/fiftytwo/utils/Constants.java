package org.bootcamp.fiftytwo.utils;

/**
 * Created by baphna on 11/13/2016.
 */
public class Constants {

    /** TAGS here */
    public static final String TAG = "FiftyTwo";
    public static final String FRAGMENT_CHAT_TAG = "chat";

    public static final String CURRENT_VIEW_PLAYER = "currentView";
    public static final String DEALER_TAG = "dealer";
    public static final String PLAYER_TAG = "player";
    public static final String TABLE_TAG = "table";
    public static final String USER_TAG = "user";

    /** Preference Keys here */
    public static final String USER_PREFS = "userPrefs";
    public static final String DISPLAY_NAME = "username";
    public static final String USER_AVATAR_URI = "avatarURI";

    public static final String GAME_NAME = "gameName";

    /** Request Codes Here */
    public static final int REQ_CODE_SELECT_CARDS = 1;

    /** Intent and Fragment Parameter here */
    public static final String PARAM_CARDS = "cards";
    public static final String PARAM_PLAYER = "player";
    public static final String X = "X";
    public static final String Y = "Y";

    /**
     * Process broadcast based on identifier .. like new player added, chat message, card exchanged
     * User PARSE_<> naming convention for all identifiers
     */

    public static final String COMMON_IDENTIFIER = "commonIdentifier";
    public static final String SERVER_FUNCTION_NAME = "pushToChannel";


    public static final String PARSE_NEW_PLAYER_ADDED = "newPlayerAdded";
    public static final String USER_ID = "userId";
    public static final String PARSE_PLAYER_LEFT = "playerLeft";
}