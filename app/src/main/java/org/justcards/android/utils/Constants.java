package org.justcards.android.utils;

/**
 * Created by baphna on 11/13/2016.
 */
public class Constants {

    /** TAGS here */
    public static final String TAG = "JustCards";
    public static final String FRAGMENT_CHAT_TAG = "chat";
    public static final String DEALER_TAG = "dealer";
    public static final String PLAYER_TAG = "player";
    public static final String TABLE_TAG = "table";
    public static final String SINK_TAG = "sink";
    public static final String DEALING_OPTIONS_TAG = "dealingOptions";
    public static final String SCORING_OPTIONS_TAG = "scoringOptions";

    /** Preference Keys here */
    /*DO NOT CHANGE ==  MUST match with member variables of User class*/
    public static final String USER_PREFS = "userPrefs";
    public static final String DISPLAY_NAME = "displayName";
    public static final String USER_AVATAR_URI = "avatarURI";
    public static final String USER_ID = "userId";
    public static final String IS_DEALER = "isDealer";
    public static final String IS_SHOWING_CARDS = "isShowingCards";
    public static final String IS_ACTIVE = "isActive";
    public static final String SCORE = "score";
    public static final String RULE_PREFS = "rulePrefs";
    public static final String RULE_VIEW_TABLE_CARD = "ruleViewTableCard";
    public static final String FCM_TOKEN = "FCMToken";
    public static final String SENT_TOKEN_TO_SERVER = "sentTokenToServer";
    public static final String GAME_PREFS = "gamePrefs";
    public static final String GAME_NAME = "gameName";

    /** Request Codes Here */
    public static final int PLAY_SERVICES_RESOLUTION_REQUEST = 900;
    public static final int REQ_CODE_SELECT_CARDS = 1;
    public static final int REQ_CODE_PICK_IMAGE = 2;

    /** Intent and Fragment Parameter here */
    public static final String PARAM_GAME_NAME = "gameName";
    public static final String PARAM_GAME_DATA = "gameData";
    public static final String PARAM_GAME_TABLE = "gameTable";
    public static final String PARAM_CURRENT_VIEW_PLAYER = "currentView";
    public static final String PARAM_CARDS = "cards";
    public static final String PARAM_CHAT = "chat";
    public static final String PARAM_PLAYERS = "players";
    public static final String PARAM_PLAYER = "player";
    public static final String PARAM_USER = "user";
    public static final String PARAM_X = "PARAM_X";
    public static final String PARAM_Y = "PARAM_Y";
    public static final String PARAM_PLAYER_CARDS = "playerCards";
    public static final String PARAM_TABLE_CARDS = "tableCards";
    public static final String PARAM_LAYOUT_TYPE = "layoutType";
    public static final String ARG_PLAYER_COUNT = "playerCount";
    public static final String ARG_CARD_COUNT = "cardCount";
    public static final String ARG_COLUMN_COUNT = "columnCount";
    public static final String PARAMS_PLAYER_GAME = "playerGame";
    public static final String PARAM_CARD_COUNT = "cardCount";
    public static final String SELECTED_AVATAR = "selectedAvatar";
    public static final String ARG_IS_SELF_ELIGIBLE = "isSelfEligible";
    public static final String ARG_FROM_PAGE = "fromPage";
    public static final String ARG_TO_PAGE = "toPage";
    public static final String FROM = "from";
    public static final String TO = "to";
    public static final String DATA = "data";

    /**
     * Process broadcast based on identifier .. like new player added, chat message, card exchanged
     * User PARSE_<> naming convention for all identifiers
     */
    public static final String FROM_ADDRESS_PREFIX = "/topics/";
    public static final String COMMON_IDENTIFIER = "commonIdentifier";
    public static final String SERVER_FUNCTION_NAME = "pushToChannel";
    public static final String TABLE_PICKED = "pickedFromTable";
    public static final String FROM_POSITION = "fromPosition";
    public static final String TO_POSITION = "toPosition";
    public static final String USER_TAG_SCORE = "userTag";
    public static final String FROM_TAG = "fromTag";
    public static final String POSITION = "position";
    public static final String ON_TAG = "onTag";
    public static final String RULE_CODE = "ruleCode";
    public static final String RULE_SELECTION = "ruleSelection";

    /** Notification Events Here */
    public static final String PARSE_NEW_PLAYER_ADDED = "newPlayerAdded";
    public static final String PARSE_PLAYER_LEFT = "playerLeft";
    public static final String PARSE_DEAL_CARDS = "dealCards";
    public static final String PARSE_EXCHANGE_CARD_WITH_TABLE = "exchangeCardWithTable";
    public static final String PARSE_SWAP_CARD_WITHIN_PLAYER = "swapCardWithinPlayer";
    public static final String PARSE_DROP_CARD_TO_SINK = "dropCardToSink";
    public static final String PARSE_TOGGLE_CARD = "toggleCard";
    public static final String PARSE_ROUND_WINNERS = "roundWinners";
    public static final String PARSE_END_ROUND = "endRound";
    public static final String PARSE_CHAT_MESSAGE = "chatMessage";
    public static final String PARSE_SELECT_GAME_RULES = "selectGameRules";

    /** Dealing Options */
    public static final String DO_CARD_COUNT = "cardsCount";
    public static final String DO_REMAINING_CARDS = "remainingCards";
    public static final String DO_DEAL_SELF = "dealSelf";
    public static final String DO_SHUFFLE = "shuffle";

    /** Layout Type Codes here */
    public static final String LAYOUT_TYPE_STAGGERED_HORIZONTAL = "staggeredHorizontal";
    public static final String LAYOUT_TYPE_CIRCULAR = "circular";
    public static final String LAYOUT_TYPE_SCROLL_ZOOM = "scrollZoom";

    /** View Properties here */
    public static final float CIRCULAR_BORDER_WIDTH = 6f;
}