package org.bootcamp.fiftytwo.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.plattysoft.leonids.ParticleSystem;
import com.plattysoft.leonids.modifiers.AlphaModifier;
import com.plattysoft.leonids.modifiers.ScaleModifier;

import org.bootcamp.fiftytwo.R;
import org.bootcamp.fiftytwo.adapters.CardsAdapter;
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
import org.bootcamp.fiftytwo.network.ParseDB;
import org.bootcamp.fiftytwo.network.ParseUtils;
import org.bootcamp.fiftytwo.utils.CardUtil;
import org.bootcamp.fiftytwo.utils.Constants;
import org.bootcamp.fiftytwo.utils.MediaUtils;
import org.bootcamp.fiftytwo.views.OnCardsDragListener;
import org.bootcamp.fiftytwo.views.PlayerViewHelper;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static org.bootcamp.fiftytwo.models.User.fromJson;
import static org.bootcamp.fiftytwo.utils.AppUtils.getList;
import static org.bootcamp.fiftytwo.utils.AppUtils.getVectorCompat;
import static org.bootcamp.fiftytwo.utils.AppUtils.isEmpty;
import static org.bootcamp.fiftytwo.utils.Constants.FRAGMENT_CHAT_TAG;
import static org.bootcamp.fiftytwo.utils.Constants.FROM_POSITION;
import static org.bootcamp.fiftytwo.utils.Constants.FROM_TAG;
import static org.bootcamp.fiftytwo.utils.Constants.PARAM_CARDS;
import static org.bootcamp.fiftytwo.utils.Constants.PARAM_CARD_COUNT;
import static org.bootcamp.fiftytwo.utils.Constants.PARAM_CURRENT_VIEW_PLAYER;
import static org.bootcamp.fiftytwo.utils.Constants.PARAM_GAME_NAME;
import static org.bootcamp.fiftytwo.utils.Constants.PARAM_PLAYER;
import static org.bootcamp.fiftytwo.utils.Constants.PARSE_DEAL_CARDS;
import static org.bootcamp.fiftytwo.utils.Constants.PARSE_DEAL_CARDS_TO_SINK;
import static org.bootcamp.fiftytwo.utils.Constants.PARSE_DEAL_CARDS_TO_TABLE;
import static org.bootcamp.fiftytwo.utils.Constants.PARSE_DROP_CARD_TO_SINK;
import static org.bootcamp.fiftytwo.utils.Constants.PARSE_EXCHANGE_CARD_WITH_TABLE;
import static org.bootcamp.fiftytwo.utils.Constants.PARSE_NEW_PLAYER_ADDED;
import static org.bootcamp.fiftytwo.utils.Constants.PARSE_PLAYER_LEFT;
import static org.bootcamp.fiftytwo.utils.Constants.PARSE_SCORE_UPDATED;
import static org.bootcamp.fiftytwo.utils.Constants.PARSE_SWAP_CARD_WITHIN_PLAYER;
import static org.bootcamp.fiftytwo.utils.Constants.PARSE_TOGGLE_CARDS_VISIBILITY;
import static org.bootcamp.fiftytwo.utils.Constants.PLAYER_TAG;
import static org.bootcamp.fiftytwo.utils.Constants.SINK_TAG;
import static org.bootcamp.fiftytwo.utils.Constants.TABLE_PICKED;
import static org.bootcamp.fiftytwo.utils.Constants.TABLE_TAG;
import static org.bootcamp.fiftytwo.utils.Constants.TO_POSITION;
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
    private List<Card> sinkCards = new ArrayList<>();
    private ParseUtils parseUtils;
    private MediaUtils mediaUtils;

    private boolean isCurrentViewPlayer = true;
    private boolean isShowingPlayerFragment = true; //false is showing dealer fragment
    private boolean isShowingChat = false;

    private PlayerViewFragment playerViewFragment;
    private DealerViewFragment dealerViewFragment;
    private ChatAndLogFragment chatAndLogFragment;

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.fab) FloatingActionMenu fab;
    @BindView(R.id.ibComment) ImageButton ibComment;
    @BindView(R.id.ibInfo) ImageButton ibInfo;
    @BindView(R.id.fabSwap) FloatingActionButton fabSwap;
    @BindView(R.id.fabExit) FloatingActionButton fabExit;
    @BindView(R.id.fabMute) FloatingActionButton fabMute;
    @BindView(R.id.fabToggleCardsVisibility) FloatingActionButton fabToggleCardsVisibility;
    @BindView(R.id.ivSink) ImageView ivSink;
    @BindView(R.id.tvSinkCardsCount) TextView tvSinkCardsCount;

    private static final String TAG = GameViewManagerActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_view_manager);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        fabSwap.setImageDrawable(getVectorCompat(this, R.drawable.ic_swap));
        fabMute.setImageDrawable(getVectorCompat(this, R.drawable.ic_not_interested));
        fabExit.setImageDrawable(getVectorCompat(this, R.drawable.ic_not_interested));
        fabToggleCardsVisibility.setImageDrawable(getVectorCompat(this, R.drawable.ic_visibility_on));

        mediaUtils = new MediaUtils(this);
        initGameParams();
        initFragments();
        initViews();

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
            ParseDB.findUsers(this, gameName, this::addPlayersToView);
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

    private void initViews() {
        ivSink.setOnDragListener(new OnCardsDragListener(new CardsAdapter.CardsListener() {
            @Override
            public void publish(String fromTag, String toTag, int fromPosition, int toPosition, Card card) {
                if (card != null) {
                    addCardsToSink(getList(card));
                    onCardExchange(fromTag, toTag, fromPosition, toPosition, card);
                }
            }

            @Override
            public void logActivity(String whoPosted, String fromAvatar, String details) {
                onNewLogEvent(whoPosted, fromAvatar, details);
            }

            @Override
            public void setEmptyList(boolean visibility) {
            }

            @Override
            public void cardCountChange(int newCount) {
            }
        }));
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

    @OnClick(R.id.fabExit)
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Exit Game")
                .setMessage("Are you sure you want to exit from game?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    parseUtils.removeChannel();
                    ParseDB.deleteGamesForUser(gameName, User.getCurrentUser(this));
                    if (User.getCurrentUser(this).isDealer()) {
                        ParseDB.deleteGame(gameName);
                    }
                    ((FiftyTwoApplication) getApplication()).removeAllObservers();
                    finish();
                })
                .setNegativeButton("No", null)
                .show();
    }

    @OnClick(R.id.ibInfo)
    public void showGameInfo(View view) {
        PopupWindow popup = new PopupWindow(GameViewManagerActivity.this);
        View layout = getLayoutInflater().inflate(R.layout.popup_gameid, null);
        Button btnGameId = (Button) layout.findViewById(R.id.btnGameId);
        btnGameId.setText("Game id " + gameName + " ");
        btnGameId.setOnClickListener(view1 -> {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.msg_share) + gameName);
            sendIntent.setType("text/plain");
            PackageManager manager = getPackageManager();
            List<ResolveInfo> info = manager.queryIntentActivities(sendIntent, 0);
            if (info.size() > 0) {
                startActivity(sendIntent);
            } else {
                Snackbar.make(view1, R.string.msg_no_app_sharing, Snackbar.LENGTH_LONG).show();
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
        //noinspection deprecation
        popup.setBackgroundDrawable(new BitmapDrawable());
        popup.showAsDropDown(view);
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
                parseUtils.dealCardsToTable(cards);
                return true;
            } else {
                parseUtils.dealCardsToSink(cards);
                return true;
            }
        }
        return false;
    }

    @Override
    public void onScoreFragmentInteraction(boolean saveClicked) {
        if (saveClicked) {
            parseUtils.updateUsersScore(mPlayers);
        }
    }

    @Override
    public void onCardExchange(String fromTag, String toTag, int fromPosition, int toPosition, Card card) {
        if (fromTag.equalsIgnoreCase(TABLE_TAG) && toTag.equalsIgnoreCase(PLAYER_TAG)) {
            parseUtils.exchangeCardWithTable(card, fromPosition, toPosition, true);
        } else if (fromTag.equalsIgnoreCase(PLAYER_TAG) && toTag.equalsIgnoreCase(TABLE_TAG)) {
            parseUtils.exchangeCardWithTable(card, fromPosition, toPosition, false);
        } else if (fromTag.equalsIgnoreCase(PLAYER_TAG) && toTag.equalsIgnoreCase(PLAYER_TAG)) {
            parseUtils.swapCardWithinPlayer(card, fromPosition, toPosition);
        } else if (toTag.equalsIgnoreCase(SINK_TAG)) {
            parseUtils.dropCardToSink(card, fromTag, fromPosition);
        }
    }

    @Override
    public void onNewLogEvent(String whoPosted, String fromAvatar, String details) {
        Log.d(Constants.TAG, GameViewManagerActivity.class.getSimpleName() + "--" + details + "--" + whoPosted);
        chatAndLogFragment.addNewLogEvent(whoPosted, fromAvatar, details);
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
    public synchronized void onUpdate(final Observable o, final Object identifier, final Object arg) {
        String event = identifier.toString();
        JsonObject json = (JsonObject) arg;
        Log.d(TAG, "onUpdate: " + event);

        switch (event) {
            case PARSE_NEW_PLAYER_ADDED:
                mediaUtils.playTingTone();
                runOnUiThread(() -> {
                    User user = User.fromJson(json);
                    addPlayersToView(getList(user));
                });
                break;
            case PARSE_PLAYER_LEFT:
                mediaUtils.playTingTone();
                runOnUiThread(() -> {
                    User user = User.fromJson(json);
                    removePlayersFromView(getList(user));
                });
                break;
            case PARSE_TOGGLE_CARDS_VISIBILITY:
                runOnUiThread(() -> {
                    User user = User.fromJson(json);
                    boolean toShow = json.get(PARSE_TOGGLE_CARDS_VISIBILITY).getAsBoolean();
                    toggleCardsVisibilityForPlayerView(user, toShow);
                });
                break;
            case PARSE_DEAL_CARDS:
                mediaUtils.playGlassBreakingTone();
                runOnUiThread(() -> {
                    User from = fromJson(json);
                    User to = fromJson(json.get(PARAM_PLAYER).getAsJsonObject());
                    Card card = new Gson().fromJson(json.get(PARAM_CARDS), Card.class);
                    handleDeal(card, from, to);
                });
                break;
            case PARSE_DEAL_CARDS_TO_TABLE:
                mediaUtils.playGlassBreakingTone();
                runOnUiThread(() -> {
                    User from = fromJson(json);
                    int cardCount = json.get(PARAM_CARD_COUNT).getAsInt();
                    List<Card> cards = new Gson().fromJson(json.get(PARAM_CARDS), new TypeToken<List<Card>>() {
                    }.getType());
                    handleDealTable(from, cards, cardCount);
                });
                break;
            case PARSE_DEAL_CARDS_TO_SINK:
                mediaUtils.playGlassBreakingTone();
                runOnUiThread(() -> {
                    User from = fromJson(json);
                    List<Card> cards = new Gson().fromJson(json.get(PARAM_CARDS), new TypeToken<List<Card>>() {
                    }.getType());
                    handleDealSink(from, cards);
                });
                break;
            case PARSE_EXCHANGE_CARD_WITH_TABLE:
                runOnUiThread(() -> {
                    User from = fromJson(json);
                    Card card = new Gson().fromJson(json.get(PARAM_CARDS), Card.class);
                    int fromPosition = json.get(FROM_POSITION).getAsInt();
                    int toPosition = json.get(TO_POSITION).getAsInt();
                    boolean pickedFromTable = json.get(TABLE_PICKED).getAsBoolean();
                    handleCardExchangeWithTable(from, card, fromPosition, toPosition, pickedFromTable);
                });
                break;
            case PARSE_SWAP_CARD_WITHIN_PLAYER:
                runOnUiThread(() -> {
                    User from = fromJson(json);
                    Card card = new Gson().fromJson(json.get(PARAM_CARDS), Card.class);
                    int fromPosition = json.get(FROM_POSITION).getAsInt();
                    int toPosition = json.get(TO_POSITION).getAsInt();
                    handleCardExchangeWithinPlayer(from, card, fromPosition, toPosition);
                });
                break;
            case PARSE_DROP_CARD_TO_SINK:
                runOnUiThread(() -> {
                    User from = fromJson(json);
                    Card card = new Gson().fromJson(json.get(PARAM_CARDS), Card.class);
                    String fromTag = json.get(FROM_TAG).getAsString();
                    int fromPosition = json.get(FROM_POSITION).getAsInt();
                    handleCardDropToSink(from, card, fromTag, fromPosition);
                });
                break;
            case PARSE_SCORE_UPDATED:
                runOnUiThread(() -> {
                    User from = fromJson(json);
                    List<User> users = new Gson().fromJson(json.get(Constants.USER_TAG_SCORE), new TypeToken<List<User>>() {
                    }.getType());
                    handleScoresUpdate(from, users);
                });
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

    public void handleDealTable(final User from, final List<Card> cards, final int cardCount) {
        if (from != null && from.isDealer() && cardCount > 0) {
            if (isEmpty(cards)) {
                ParseDB.getGameTableCards(gameName, cardCount, this::handleDealTable);
            } else {
                handleDealTable(cards);
            }
        }
    }

    public void handleDealTable(final List<Card> cards) {
        CardUtil.setShowingFront(cards);
        Fragment fragment = playerViewFragment.getChildFragmentManager().findFragmentByTag(TABLE_TAG);
        if (fragment != null) {
            ((CardsFragment) fragment).stackCards(cards);
        }
    }

    public void handleDealSink(final User from, final List<Card> cards) {
        if (from != null && from.isDealer() && !isEmpty(cards)) {
            CardUtil.setShowingFront(cards);
            addCardsToSink(cards);
        }
    }

    public void handleCardExchangeWithTable(final User from, final Card card, final int fromPosition, final int toPosition, final boolean pickedFromTable) {
        Log.d(TAG, "handleCardExchangeWithTable: Card: " + card +
                ", User: " + from +
                ", fromPosition: " + fromPosition +
                ", toPosition: " + toPosition +
                ", pickedFromTable: " + pickedFromTable);

        if (card != null && from != null) {
            Fragment playerFragment = getPlayerFragment(this, from);
            Fragment tableFragment = null;
            if (playerViewFragment != null) {
                tableFragment = playerViewFragment.getChildFragmentManager().findFragmentByTag(TABLE_TAG);
            }
            if (playerFragment != null && tableFragment != null) {
                Fragment source = pickedFromTable ? tableFragment : playerFragment;
                Fragment target = pickedFromTable ? playerFragment : tableFragment;
                exchangeCard(source, target, card, fromPosition, toPosition);
            }
        }
    }

    private void handleCardExchangeWithinPlayer(final User from, final Card card, final int fromPosition, final int toPosition) {
        Log.d(TAG, "handleCardExchangeWithinPlayer: Card: " + card +
                ", User: " + from +
                ", fromPosition: " + fromPosition +
                ", toPosition: " + toPosition);

        if (card != null && from != null) {
            Fragment fragment = getPlayerFragment(this, from);
            if (fragment != null) {
                exchangeCard(fragment, fragment, card, fromPosition, toPosition);
            }
        }
    }

    private void exchangeCard(Fragment source, Fragment target, Card card, int fromPosition, int toPosition) {
        boolean draw = ((CardsFragment) source).drawCard(fromPosition, card);
        Log.d(TAG, "exchangeCard: Draw Status: " + draw);
        if (draw) {
            boolean stack = ((CardsFragment) target).stackCard(card, toPosition);
            Log.d(TAG, "exchangeCard: Stack Status: " + stack);
        }
    }

    private void handleCardDropToSink(User from, Card card, String fromTag, int fromPosition) {
        Log.d(TAG, "handleCardDropToSink: Card: " + card +
                ", User: " + from +
                ", fromTag: " + fromTag +
                ", fromPosition: " + fromPosition);

        if (from != null && card != null && !TextUtils.isEmpty(fromTag)) {
            Fragment fragment = null;
            if (fromTag.equalsIgnoreCase(PLAYER_TAG)) {
                fragment = getPlayerFragment(this, from);
            } else if (fromTag.equalsIgnoreCase(TABLE_TAG) && playerViewFragment != null) {
                fragment = playerViewFragment.getChildFragmentManager().findFragmentByTag(TABLE_TAG);
            }

            if (fragment != null) {
                boolean draw = ((CardsFragment) fragment).drawCard(fromPosition, card);
                if (draw) {
                    addCardsToSink(getList(card));
                }
            } else {
                addCardsToSink(getList(card));
            }
        }
    }

    private void handleScoresUpdate(User from, List<User> users) {
        Log.d(TAG, "handleScoresUpdate: Score update received from: " + from);
        for (User user : users) {
            Fragment fragment = getPlayerFragment(this, user);
            if (fragment != null) {
                ((PlayerFragment) fragment).scoreChange(user.getScore());
            }
        }
        //TODO: log it
    }

    public void addCardsToSink(List<Card> cards) {
        sinkCards.addAll(cards);
        ivSink.setImageResource(R.drawable.ic_sink_full);
        tvSinkCardsCount.setText(String.valueOf(sinkCards.size()));
    }

    public void removeAllSinkCards() {
        sinkCards.clear();
        ivSink.setImageResource(R.drawable.ic_sink_empty);
    }

    public void toggleCardsVisibilityOfAllPlayers(boolean toShow) {
        for (User player : mPlayers) {
            toggleCardsVisibilityForPlayerView(player, toShow);
        }
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