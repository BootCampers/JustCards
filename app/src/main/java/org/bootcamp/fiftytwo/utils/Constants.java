package org.bootcamp.fiftytwo.utils;

/**
 * Created by baphna on 11/13/2016.
 */
public class Constants {

    /** TAGS here */
    public static final String TAG = "FiftyTwo";
    public static final String FRAGMENT_CHAT_TAG = "chat";
    public static final String DEALER_TAG = "dealer";
    public static final String PLAYER_TAG = "player";
    public static final String TABLE_TAG = "table";
    public static final String USER_TAG = "user";
    public static final String DEALING_OPTIONS_TAG = "dealingOptions";

    /** Preference Keys here */
    /*DO NOT CHANGE ==  MUST match with member variables of User class*/
    public static final String USER_PREFS = "userPrefs";
    public static final String DISPLAY_NAME = "displayName";
    public static final String USER_AVATAR_URI = "avatarURI";
    public static final String USER_ID = "userId"; // this

    /** Request Codes Here */
    public static final int REQ_CODE_SELECT_CARDS = 1;

    /** Intent and Fragment Parameter here */
    public static final String PARAM_GAME_NAME = "gameName";
    public static final String PARAM_CURRENT_VIEW_PLAYER = "currentView";
    public static final String PARAM_CARDS = "cards";
    public static final String PARAM_PLAYERS = "players";
    public static final String PARAM_PLAYER = "player";
    public static final String PARAM_X = "PARAM_X";
    public static final String PARAM_Y = "PARAM_Y";
    public static final String PARAM_PLAYER_CARDS = "playerCards";
    public static final String PARAM_TABLE_CARDS = "tableCards";
    public static final String PARAM_LAYOUT_TYPE = "layoutType";
    public static final String ARG_PLAYER_COUNT = "playerCount";
    public static final String ARG_CARD_COUNT = "cardCount";
    public static final String ARG_COLUMN_COUNT = "columnCount";
    public static final String PARAMS_PLAYER_GAME = "playerGame";

    /**
     * Process broadcast based on identifier .. like new player added, chat message, card exchanged
     * User PARSE_<> naming convention for all identifiers
     */
    public static final String COMMON_IDENTIFIER = "commonIdentifier";
    public static final String SERVER_FUNCTION_NAME = "pushToChannel";
    public static final String TABLE_PICKED = "pickedFromTable";

    /** Parse Events Here */
    public static final String PARSE_NEW_PLAYER_ADDED = "newPlayerAdded";
    public static final String PARSE_PLAYER_LEFT = "playerLeft";
    public static final String PARSE_PLAYERS_EXCHANGE_CARDS = "playersExchangeCards";
    public static final String PARSE_TABLE_CARD_EXCHANGE = "tableCardExchange";

    /** Dealing Options */
    public static final String DO_CARD_COUNT = "cardsCount";
    public static final String DO_REMAINING_CARDS = "remainingCards";
    public static final String DO_DEAL_SELF = "dealSelf";
    public static final String DO_SHUFFLE = "shuffle";

    /** Avatar Values here */
    public static final String SELECTED_AVATAR = "selectedAvatar";
    public static final int PICK_IMAGE_REQUEST = 2987;

    /** Layout Type Codes here */
    public static final String LAYOUT_TYPE_STAGGERED_HORIZONTAL = "staggeredHorizontal";
    public static final String LAYOUT_TYPE_CIRCULAR = "circular";
}