package org.bootcamp.fiftytwo.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.gson.Gson;
import com.plattysoft.leonids.ParticleSystem;
import com.plattysoft.leonids.modifiers.AlphaModifier;
import com.plattysoft.leonids.modifiers.ScaleModifier;

import org.bootcamp.fiftytwo.R;
import org.bootcamp.fiftytwo.application.FiftyTwoApplication;
import org.bootcamp.fiftytwo.fragments.CardsFragment;
import org.bootcamp.fiftytwo.fragments.ChatAndLogFragment;
import org.bootcamp.fiftytwo.fragments.DealerViewFragment;
import org.bootcamp.fiftytwo.fragments.DealingOptionsFragment;
import org.bootcamp.fiftytwo.fragments.PlayerFragment;
import org.bootcamp.fiftytwo.fragments.PlayerViewFragment;
import org.bootcamp.fiftytwo.fragments.ScoringFragment;
import org.bootcamp.fiftytwo.interfaces.Observable;
import org.bootcamp.fiftytwo.interfaces.Observer;
import org.bootcamp.fiftytwo.models.Card;
import org.bootcamp.fiftytwo.models.ChatLog;
import org.bootcamp.fiftytwo.models.Game;
import org.bootcamp.fiftytwo.models.User;
import org.bootcamp.fiftytwo.network.ParseUtils;
import org.bootcamp.fiftytwo.utils.Constants;
import org.bootcamp.fiftytwo.utils.MediaUtils;
import org.bootcamp.fiftytwo.views.PlayerViewHelper;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static org.bootcamp.fiftytwo.models.User.fromJson;
import static org.bootcamp.fiftytwo.network.ParseUtils.isSelf;
import static org.bootcamp.fiftytwo.utils.AppUtils.getList;
import static org.bootcamp.fiftytwo.utils.AppUtils.isEmpty;
import static org.bootcamp.fiftytwo.utils.Constants.FRAGMENT_CHAT_TAG;
import static org.bootcamp.fiftytwo.utils.Constants.FROM_POSITION;
import static org.bootcamp.fiftytwo.utils.Constants.PARAM_CARDS;
import static org.bootcamp.fiftytwo.utils.Constants.PARAM_CURRENT_VIEW_PLAYER;
import static org.bootcamp.fiftytwo.utils.Constants.PARAM_GAME_NAME;
import static org.bootcamp.fiftytwo.utils.Constants.PARSE_DEAL_CARDS;
import static org.bootcamp.fiftytwo.utils.Constants.PARSE_DEAL_CARDS_TO_TABLE;
import static org.bootcamp.fiftytwo.utils.Constants.PARSE_EXCHANGE_CARD_WITH_TABLE;
import static org.bootcamp.fiftytwo.utils.Constants.PARSE_NEW_PLAYER_ADDED;
import static org.bootcamp.fiftytwo.utils.Constants.PARSE_PLAYER_LEFT;
import static org.bootcamp.fiftytwo.utils.Constants.PARSE_SWAP_CARD_WITHIN_TABLE;
import static org.bootcamp.fiftytwo.utils.Constants.PARSE_TOGGLE_CARDS_VISIBILITY;
import static org.bootcamp.fiftytwo.utils.Constants.PLAYER_TAG;
import static org.bootcamp.fiftytwo.utils.Constants.TABLE_PICKED;
import static org.bootcamp.fiftytwo.utils.Constants.TABLE_TAG;
import static org.bootcamp.fiftytwo.views.PlayerViewHelper.getPlayerFragment;

public class GameViewManagerActivity extends AppCompatActivity implements
        PlayerViewFragment.onPlayListener,
        DealerViewFragment.OnDealListener,
        DealingOptionsFragment.OnDealOptionsListener,
        ChatAndLogFragment.OnChatAndLogListener,
        CardsFragment.OnCardExchangeLister,
        CardsFragment.OnLogEventListener,
        ScoringFragment.OnScoreFragmentInteractionListener,
        Observer {

    private List<User> mPlayers = new ArrayList<>();
    private List<Card> mCards;
    private String gameName;
    private ParseUtils parseUtils;

    private boolean isCurrentViewPlayer = true;
    private boolean isShowingPlayerFragment = true; //false is showing dealer fragment
    private boolean isShowingChat = false;

    private PlayerViewFragment playerViewFragment;
    private DealerViewFragment dealerViewFragment;
    private ChatAndLogFragment chatAndLogFragment;

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.fab)
    FloatingActionMenu fab;
    @BindView(R.id.ibComment) ImageButton ibComment;
    @BindView(R.id.ibInfo) ImageButton ibInfo;
    @BindView(R.id.fabSwap)
    FloatingActionButton fabSwap;
    @BindView(R.id.fabExit)
    FloatingActionButton fabExit;
    @BindView(R.id.fabMute)
    FloatingActionButton fabMute;
    @BindView(R.id.fabToggleCardsVisibility)
    FloatingActionButton fabToggleCardsVisibility;
    @BindView(R.id.ivSink)
    ImageView ivSink;

    @BindDrawable(R.drawable.ic_power)
    Drawable icPower;
    @BindDrawable(R.drawable.ic_swap)
    Drawable icSwap;
    @BindDrawable(R.drawable.ic_not_interested)
    Drawable icNotInterested;
    @BindDrawable(R.drawable.ic_visibility_on)
    Drawable icVisibilityOn;
    @BindDrawable(R.drawable.ic_visibility_off)
    Drawable icVisibilityOff;
    @BindDrawable(R.drawable.ic_sink_empty)
    Drawable icSinkEmpty;
    @BindDrawable (R.drawable.ic_sink_full)
    Drawable icSinkFull;

    private static final String TAG = GameViewManagerActivity.class.getSimpleName();

    List<Card> sinkCards = new ArrayList<>();
    MediaUtils mediaUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_view_manager);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        fabSwap.setImageDrawable(icSwap);
        fabMute.setImageDrawable(icNotInterested);
        fabExit.setImageDrawable(icPower);
        fabToggleCardsVisibility.setImageDrawable(icVisibilityOn);

        mediaUtils = new MediaUtils(GameViewManagerActivity.this);
        initGameParams();
        initFragments();

        ((FiftyTwoApplication) getApplication()).addObserver(this);
    }

    private void initGameParams() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            gameName = bundle.getString(PARAM_GAME_NAME);
            mCards = Parcels.unwrap(bundle.getParcelable(PARAM_CARDS));
            isCurrentViewPlayer = bundle.getBoolean(PARAM_CURRENT_VIEW_PLAYER);
            Toast.makeText(getApplicationContext(), "Joining Game: " + gameName, Toast.LENGTH_SHORT).show();

            //Join channel for updates
            parseUtils = new ParseUtils(this, gameName);
            parseUtils.saveCurrentUser(!isCurrentViewPlayer);

            //Get previously joined players
            parseUtils.fetchPreviouslyJoinedUsers(gameName, this);
            parseUtils.joinChannel();

            // Add myself to game
            Game.save(gameName, User.getCurrentUser(this));
        }
    }

    private void initFragments() {
        // Instantiating all the child fragments for game view
        playerViewFragment = PlayerViewFragment.newInstance(null, null);
        dealerViewFragment = DealerViewFragment.newInstance(mCards, null);
        chatAndLogFragment = ChatAndLogFragment.newInstance(1);

        // Controlling the fragments for display based on player's role
        if (isCurrentViewPlayer) {
            fab.setVisibility(View.GONE);
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.flGameContainer, playerViewFragment)
                    .commit();
        } else {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.flGameContainer, playerViewFragment)
                    .add(R.id.flGameContainer, dealerViewFragment)
                    .hide(playerViewFragment)
                    .commit();
        }

        // Set the current view state (player vs dealer)
        isShowingPlayerFragment = isCurrentViewPlayer;
    }

    @OnClick(R.id.fabSwap)
    public void switchView() {
        getSupportFragmentManager()
                .beginTransaction()
                .hide(isShowingPlayerFragment ? playerViewFragment : dealerViewFragment)
                .show(isShowingPlayerFragment ? dealerViewFragment : playerViewFragment)
                .commit();
        this.isShowingPlayerFragment ^= true;
        toggleSelfPlayerView();
    }

    /**
     * Hide self view if it's in player fragment. Show in dealer.
     */
    private void toggleSelfPlayerView() {
        togglePlayerView(User.getCurrentUser(this));
    }

    private void togglePlayerView(User player) {
        Fragment playerFragment = getPlayerFragment(this, player);
        if (playerFragment != null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            if (isShowingPlayerFragment) {
                transaction.hide(playerFragment);
            } else {
                transaction.show(playerFragment);
            }
            transaction.commit();
        }
    }

    @SuppressWarnings("unused")
    public void toggleCardsVisibilityOfAllPlayers(boolean toShow) {
        for (User player : mPlayers) {
            toggleCardsVisibilityForPlayerView(player, toShow);
        }
    }

    /**
     * Show or hide the user's cards fragment
     *
     * @param player which player
     * @param toShow true if want to show, false for hiding
     */
    private void toggleCardsVisibilityForPlayerView(final User player, final boolean toShow) {
        Fragment playerFragment = getPlayerFragment(this, player);
        if (playerFragment != null) {
            ((PlayerFragment) playerFragment).toggleCardsVisibility(toShow);
        }
    }

    @Override
    public void onCardsVisibility(boolean toShow) {
        parseUtils.toggleCardsVisibility(toShow);
    }

    public void addCardToSink(Card card){
        sinkCards.add(card);
        ivSink.setImageDrawable(icSinkFull);
    }

    public void removeCardFromSink(Card card){
        sinkCards.remove(card);
        if(sinkCards.size() == 0){
            ivSink.setImageDrawable(icSinkEmpty);
        }
    }

    public void removeAllSinkCards(){
        sinkCards.clear();
        ivSink.setImageDrawable(icSinkEmpty);
    }

    @OnClick(R.id.ibInfo)
    public void showGameInfo(View view){
        PopupWindow popup = new PopupWindow(GameViewManagerActivity.this);
        View layout = getLayoutInflater().inflate(R.layout.popup_gameid, null);
        Button btnGameId= (Button) layout.findViewById(R.id.btnGameId);
        btnGameId.setText("Game id " + gameName + " ");
        btnGameId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.msg_share) + gameName);
                sendIntent.setType("text/plain");
                PackageManager manager = getPackageManager();
                List<ResolveInfo> info = manager.queryIntentActivities(sendIntent, 0);
                if (info.size() > 0) {
                    startActivity(sendIntent);
                } else {
                    Snackbar.make(view, R.string.msg_no_app_sharing, Snackbar.LENGTH_LONG).show();
                }
            }
        });
        popup.setContentView(layout);
        // Set content width and height
        popup.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popup.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        // Closes the popup window when touch outside of it - when looses focus
        popup.setOutsideTouchable(true);
        popup.setFocusable(true);
        // Show anchored to button
        popup.setBackgroundDrawable(new BitmapDrawable());
        popup.showAsDropDown(view);
    }

    @OnClick(R.id.fabExit)
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Exit Game")
                .setMessage("Are you sure you want to exit from game?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    parseUtils.removeChannel();
                    parseUtils.deleteUserFromDb(gameName, User.getCurrentUser(this));
                    if (User.getCurrentUser(this).isDealer()) {
                        parseUtils.deleteGameFromServer(gameName);
                    }
                    ((FiftyTwoApplication) getApplication()).removeAllObservers();
                    finish();
                })
                .setNegativeButton("No", null)
                .show();
    }

    @OnClick(R.id.ibComment)
    public void toggleChatAndLogView(View v) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (!isShowingChat) {
            fragmentTransaction.replace(R.id.flLogContainer, chatAndLogFragment, FRAGMENT_CHAT_TAG);
            ibComment.setImageResource(R.drawable.ic_cancel);
            isShowingChat = true;
        } else {
            fragmentTransaction.remove(chatAndLogFragment);
            ibComment.setImageResource(R.drawable.ic_comment);
            isShowingChat = false;
        }
        fragmentTransaction.commit();
    }

    @Override
    public void onDealerOptionsShowing(boolean isDealerOptionShowing) {
        //Show and hide all players when dealer option is showing to un-clutter the view
        for (User player : mPlayers) {
            Fragment playerFragment = getPlayerFragment(this, player);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            if (isDealerOptionShowing) {
                transaction.hide(playerFragment);
            } else {
                transaction.show(playerFragment);
            }
            transaction.commit();
        }
    }

    @Override
    public boolean onDeal(List<Card> cards, User player) {
        Fragment fragment = getPlayerFragment(this, player);
        if (fragment != null && !isEmpty(cards)) {
            boolean result = ((CardsFragment) fragment).stackCards(cards);
            if (result) {
                for (Card card : cards) {
                    parseUtils.dealCards(player, card);
                }
            }
            return result;
        }
        return false;
    }

    @Override
    public boolean onDealTable(List<Card> cards, boolean toSink) {
        if (!isEmpty(cards)) {
            if (!toSink) {
                for (int i=0; i<cards.size(); i++) {
                    Card card = cards.get(i);
                    parseUtils.dealCardsToTable(card, i);
                }
                return true;
            } else {
                // TODO: Handle Drop to Sink here
                return true;
            }
        }
        return false;
    }

    /**
     * Card Exchange Rules:
     * <p>
     * ------          Dealer	Player  Table   Player View
     * Dealer	        X	    Y(D)	Y(DT)	Y(D)
     * Player	        NP	    X	    Y(CT)	NA
     * Table	        NP	    Y(CT)	Y(T)	NA
     * Player View	    NA	    NA	    NA	    NA
     * <p>
     * Legends:
     * X	No Broadcast
     * Y	Broadcast
     * NA	Move Not Allowed
     * NP	Move Not Possible
     * <p>
     * Values in parentheses describe the corresponding APIs in use with the legends being:
     * D   Deal
     * DT  Deal Table
     * CT  Card Table
     * T   Within Table
     * <p>
     * Tags for different Card Fragments:
     * 1.DEALER_TAG
     * 2.PLAYER_TAG
     * 3.TABLE_TAG
     * 4.Custom Player View Tag (player.getDisplayName() + "_" + player.getUserId())
     */
    @Override
    public void onCardExchange(String fromTag, String toTag, int fromPosition, int toPosition, Card card) {
        if (fromTag.equalsIgnoreCase(TABLE_TAG) && toTag.equalsIgnoreCase(PLAYER_TAG)) {
            parseUtils.exchangeCardWithTable(card, fromPosition, toPosition, true);
        } else if (fromTag.equalsIgnoreCase(PLAYER_TAG) && toTag.equalsIgnoreCase(TABLE_TAG)) {
            parseUtils.exchangeCardWithTable(card, fromPosition, toPosition, false);
        } else if (fromTag.equalsIgnoreCase(TABLE_TAG) && toTag.equalsIgnoreCase(TABLE_TAG)) {
            parseUtils.swapCardWithinTable(card, fromPosition, toPosition);
        }
    }

    @Override
    public void onNewLogEvent(String whoPosted, String fromAvatar, String details) {
        Log.d(Constants.TAG, GameViewManagerActivity.class.getSimpleName() + "--" + details + "--" + whoPosted);
        chatAndLogFragment.addNewLogEvent(whoPosted, fromAvatar, details);
    }

    @Override
    public synchronized void onUpdate(final Observable o, final Object identifier, final Object arg) {
        String event = identifier.toString();
        Log.d(TAG, "onUpdate: " + event);

        switch (event) {
            case PARSE_NEW_PLAYER_ADDED:
                mediaUtils.playTingTone();
                runOnUiThread(() -> {
                    User user = User.fromJson((JSONObject) arg);
                    addPlayersToView(getList(user));
                });
                break;
            case PARSE_PLAYER_LEFT:
                mediaUtils.playTingTone();
                runOnUiThread(() -> {
                    User user = User.fromJson((JSONObject) arg);
                    removePlayersFromView(getList(user));
                });
                break;
            case PARSE_DEAL_CARDS:
                mediaUtils.playGlassBreakingTone();
                try {
                    JSONObject details = (JSONObject) arg;
                    User from = fromJson(details);
                    JSONObject toUserDetails = details.getJSONObject(PLAYER_TAG);
                    User to = fromJson(toUserDetails);
                    Card card = new Gson().fromJson(details.getString(PARAM_CARDS), Card.class);
                    handleDeal(card, from, to);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case PARSE_DEAL_CARDS_TO_TABLE:
                try {
                    JSONObject details = (JSONObject) arg;
                    User from = fromJson(details);
                    Card card = new Gson().fromJson(details.getString(PARAM_CARDS), Card.class);
                    int position =  details.getInt(Constants.PARAM_POSITION);
                    handleDealTable(from, card, position);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case PARSE_EXCHANGE_CARD_WITH_TABLE:
                try {
                    JSONObject details = (JSONObject) arg;
                    User from = fromJson(details);
                    Card card = new Gson().fromJson(details.getString(PARAM_CARDS), Card.class);
                    int fromPosition = details.getInt(FROM_POSITION);
                    int toPosition = details.getInt(FROM_POSITION);
                    boolean pickedFromTable = details.getBoolean(TABLE_PICKED);
                    handleCardExchangeWithTable(from, card, fromPosition, toPosition, pickedFromTable);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case PARSE_SWAP_CARD_WITHIN_TABLE:
                try {
                    JSONObject details = (JSONObject) arg;
                    User from = fromJson(details);
                    Card card = new Gson().fromJson(details.getString(PARAM_CARDS), Card.class);
                    int fromPosition = details.getInt(FROM_POSITION);
                    int toPosition = details.getInt(FROM_POSITION);
                    handleCardExchangeWithinTable(from, card, fromPosition, toPosition);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case PARSE_TOGGLE_CARDS_VISIBILITY:
                try {
                    User user = User.fromJson((JSONObject) arg);
                    boolean toShow = ((JSONObject) arg).getBoolean(Constants.PARSE_TOGGLE_CARDS_VISIBILITY);
                    toggleCardsVisibilityForPlayerView(user, toShow);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    public void addPlayersToView(final List<User> players) {
        mPlayers.addAll(players);
        if (dealerViewFragment != null) {
            dealerViewFragment.addPlayers(players);
        }

        PlayerViewHelper.addPlayers(this, R.id.clGameLayout, players);
        for (User player : players) {
            if (player.equals(User.getCurrentUser(this))) {
                togglePlayerView(player);
            }
            toggleCardsVisibilityForPlayerView(player, false);
            onNewLogEvent(player.getDisplayName(), player.getAvatarUri(), player.getDisplayName() + " joined.");
        }
    }

    public void removePlayersFromView(final List<User> players) {
        for (User player : players) {
            mPlayers.remove(player);
            if (dealerViewFragment != null) {
                dealerViewFragment.removePlayer(player);
            }
            Fragment playerFragment = getPlayerFragment(this, player);
            if (playerFragment != null) {
                getSupportFragmentManager().beginTransaction().remove(playerFragment).commitNow();
            } else {
                Log.e(TAG, "NULL Failed to remove view for " + player);
            }
            onNewLogEvent(player.getDisplayName(), player.getAvatarUri(), player.getDisplayName() + " left.");
        }
    }

    public void handleDeal(final Card card, final User from, final User to) {
        if (card != null && from != null && from.isDealer() && to != null) {
            card.setShowingFront(false);
            if (isCurrentViewPlayer) {
                Fragment playerFragment = getPlayerFragment(this, to);
                if (playerFragment != null) {
                    ((CardsFragment) playerFragment).stackCards(getList(card));
                }
            }
            if (parseUtils.getCurrentUser().equals(to) && playerViewFragment != null) {
                Fragment fragment = playerViewFragment.getChildFragmentManager().findFragmentByTag(PLAYER_TAG);
                if (fragment != null) {
                    ((CardsFragment) fragment).stackCards(getList(card));
                }
            }

            // animation deal event on the player view
            animateDeal();
        }
    }

    public void handleDealTable(final User from, final Card card, final int position) {
        if (card != null && from != null && from.isDealer()) {
            card.setShowingFront(false);
            Fragment fragment = playerViewFragment.getChildFragmentManager().findFragmentByTag(TABLE_TAG);
            if (fragment != null) {
                ((CardsFragment) fragment).stackCard(card, position);
            }
        }
    }

    public void handleCardExchangeWithTable(final User from, final Card card, final int fromPosition, final int toPosition, final boolean pickedFromTable) {
        Log.d(TAG, "handleCardExchangeWithTable: Card: " + card +
                ", User: " + from +
                ", fromPosition: " + fromPosition +
                ", toPosition: " + toPosition +
                ", pickedFromTable: " + pickedFromTable);

        if (card != null && from != null && !isSelf(from)) {
            Fragment playerFragment = getPlayerFragment(this, from);
            Fragment tableFragment = null;
            if (playerViewFragment != null) {
                tableFragment = playerViewFragment.getChildFragmentManager().findFragmentByTag(TABLE_TAG);
            }
            if (playerFragment != null && tableFragment != null) {
                Fragment source = pickedFromTable ? tableFragment : playerFragment;
                Fragment target = pickedFromTable ? playerFragment : tableFragment;

                boolean draw = ((CardsFragment) source).drawCard(fromPosition, card);
                Log.d(TAG, "handleCardExchangeWithTable: Draw Status: " + draw);
                if (draw) {
                    boolean stack = ((CardsFragment) target).stackCard(card, toPosition);
                    Log.d(TAG, "handleCardExchangeWithTable: Stack Status: " + stack);
                }
            }
        }
    }

    private void handleCardExchangeWithinTable(final User from, final Card card, final int fromPosition, final int toPosition) {
        Log.d(TAG, "handleCardExchangeWithinTable: Card: " + card +
                ", User: " + from +
                ", fromPosition: " + fromPosition +
                ", toPosition: " + toPosition);

        // TODO

        if (card != null && from != null && !isSelf(from) && playerViewFragment != null) {
            Fragment tableFragment = playerViewFragment.getChildFragmentManager().findFragmentByTag(TABLE_TAG);
            if (tableFragment != null) {
                boolean draw = ((CardsFragment) tableFragment).drawCard(fromPosition, card);
                Log.d(TAG, "handleCardExchangeWithinTable: Draw Status: " + draw);
                if (draw) {
                    boolean stack = ((CardsFragment) tableFragment).stackCard(card, toPosition);
                    Log.d(TAG, "handleCardExchangeWithinTable: Stack Status: " + stack);
                }
            }
        }
    }

    @Override
    public void onDealOptionSelected(Bundle bundle) {
        // Do Nothing
    }

    @Override
    public void onChat(ChatLog item) {
        // Do Nothing
    }

    @Override
    public void onScoreFragmentInteraction(boolean saveClicked) {
        //Do Nothing
    }

    private void animateDeal() {
        new ParticleSystem(this, 4, R.drawable.dust, 300)
                .setSpeedByComponentsRange(-0.025f, 0.025f, -0.06f, -0.08f)
                .setAcceleration(0.000001f, 30)
                .setInitialRotationRange(0, 360)
                .addModifier(new AlphaModifier(255, 0, 1000, 3000))
                .addModifier(new ScaleModifier(0.5f, 2f, 0, 1000))
                .oneShot(findViewById(R.id.flPlayerContainer), 4);
    }

}