package org.justcards.android.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.gson.Gson;
import com.plattysoft.leonids.ParticleSystem;
import com.plattysoft.leonids.modifiers.AlphaModifier;
import com.plattysoft.leonids.modifiers.ScaleModifier;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import org.justcards.android.R;
import org.justcards.android.adapters.CardsAdapter;
import org.justcards.android.application.JustCardsAndroidApplication;
import org.justcards.android.db.GameDB;
import org.justcards.android.db.TableDB;
import org.justcards.android.db.UsersDB;
import org.justcards.android.fragments.CardsFragment;
import org.justcards.android.fragments.ChatAndLogFragment;
import org.justcards.android.fragments.DealerViewFragment;
import org.justcards.android.fragments.DealingOptionsFragment;
import org.justcards.android.fragments.PlayerFragment;
import org.justcards.android.fragments.PlayerViewFragment;
import org.justcards.android.fragments.RoundWinnersFragment;
import org.justcards.android.fragments.ScoringFragment;
import org.justcards.android.fragments.TutorialFragment;
import org.justcards.android.interfaces.Observable;
import org.justcards.android.interfaces.Observer;
import org.justcards.android.messaging.FirebaseMessagingClient;
import org.justcards.android.models.Card;
import org.justcards.android.models.ChatLog;
import org.justcards.android.models.Game;
import org.justcards.android.models.GameRules;
import org.justcards.android.models.User;
import org.justcards.android.utils.AnimationUtilsJC;
import org.justcards.android.utils.CardUtil;
import org.justcards.android.utils.Constants;
import org.justcards.android.utils.MediaUtils;
import org.justcards.android.utils.PlayerUtils;
import org.justcards.android.views.OnCardsDragListener;
import org.justcards.android.views.OnTouchMoveListener;
import org.justcards.android.views.PlayerViewHelper;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static org.justcards.android.models.GameRules.getRuleDescription;
import static org.justcards.android.models.User.fromJson;
import static org.justcards.android.models.User.getCurrentUser;
import static org.justcards.android.models.User.isSelf;
import static org.justcards.android.models.User.resetForRound;
import static org.justcards.android.utils.AppUtils.getList;
import static org.justcards.android.utils.AppUtils.getUsersType;
import static org.justcards.android.utils.AppUtils.getVectorCompat;
import static org.justcards.android.utils.AppUtils.isEmpty;
import static org.justcards.android.utils.Constants.DEALER_TAG;
import static org.justcards.android.utils.Constants.EVENT_CHAT_MESSAGE;
import static org.justcards.android.utils.Constants.EVENT_DEAL_CARDS;
import static org.justcards.android.utils.Constants.EVENT_DROP_CARD_TO_SINK;
import static org.justcards.android.utils.Constants.EVENT_END_ROUND;
import static org.justcards.android.utils.Constants.EVENT_EXCHANGE_CARD_WITH_TABLE;
import static org.justcards.android.utils.Constants.EVENT_ROUND_WINNERS;
import static org.justcards.android.utils.Constants.EVENT_SELECT_GAME_RULES;
import static org.justcards.android.utils.Constants.EVENT_SWAP_CARD_WITHIN_PLAYER;
import static org.justcards.android.utils.Constants.EVENT_TOGGLE_CARD;
import static org.justcards.android.utils.Constants.FRAGMENT_CHAT_TAG;
import static org.justcards.android.utils.Constants.FROM_POSITION;
import static org.justcards.android.utils.Constants.FROM_TAG;
import static org.justcards.android.utils.Constants.ON_TAG;
import static org.justcards.android.utils.Constants.PARAM_CARDS;
import static org.justcards.android.utils.Constants.PARAM_CHAT;
import static org.justcards.android.utils.Constants.PARAM_CURRENT_VIEW_PLAYER;
import static org.justcards.android.utils.Constants.PARAM_GAME_NAME;
import static org.justcards.android.utils.Constants.PARAM_PLAYER;
import static org.justcards.android.utils.Constants.PARAM_PLAYERS;
import static org.justcards.android.utils.Constants.PLAYER_TAG;
import static org.justcards.android.utils.Constants.POSITION;
import static org.justcards.android.utils.Constants.RULE_CODE;
import static org.justcards.android.utils.Constants.RULE_SELECTION;
import static org.justcards.android.utils.Constants.RULE_VIEW_TABLE_CARD;
import static org.justcards.android.utils.Constants.SINK_TAG;
import static org.justcards.android.utils.Constants.TABLE_PICKED;
import static org.justcards.android.utils.Constants.TABLE_TAG;
import static org.justcards.android.utils.Constants.TO_POSITION;
import static org.justcards.android.views.PlayerViewHelper.getPlayerFragment;

public class GameViewManagerActivity extends AppCompatActivity implements
        DealerViewFragment.OnDealListener,
        DealingOptionsFragment.OnDealOptionsListener,
        ChatAndLogFragment.OnChatAndLogListener,
        CardsFragment.OnCardExchangeLister,
        CardsFragment.OnToggleCardListener,
        CardsFragment.OnLogEventListener,
        ScoringFragment.OnScoreFragmentListener,
        TutorialFragment.OnFragmentInteractionListener,
        UsersDB.OnEventListener,
        TableDB.OnEventListener,
        Observer {

    private static final String TAG = GameViewManagerActivity.class.getSimpleName();

    private String mGameName;
    private List<User> mPlayers = new ArrayList<>();
    private List<Card> mCards;
    private List<Card> sinkCards = new ArrayList<>();
    private boolean mIsCurrentViewPlayer = true;
    private boolean mIsShowingPlayerFragment = true; //false is showing dealer fragment
    private boolean mIsShowingChat = false;

    private MediaUtils mMediaUtils;
    private Gson gson = new Gson();

    // Firebase
    private FirebaseMessagingClient messagingClient;
    private TableDB mTableDb;
    private UsersDB mUsersDb;

    private PlayerViewFragment mPlayerViewFragment;
    private DealerViewFragment mDealerViewFragment;
    private ChatAndLogFragment mChatAndLogFragment;

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.clGameLayout) ViewGroup container;
    @BindView(R.id.flLogContainer) FrameLayout flLogContainer;
    @BindView(R.id.llSink) ViewGroup llSink;
    @BindView(R.id.ibChat) ImageButton ibChat;
    @BindView(R.id.ibInfo) ImageButton ibInfo;
    @BindView(R.id.ibHelp) ImageButton ibHelp;
    @BindView(R.id.ivSink) ImageView ivSink;
    @BindView(R.id.tvSinkCardsCount) TextView tvSinkCardsCount;
    @BindView(R.id.fabExit) FloatingActionButton fabExit;
    @BindView(R.id.fabSwap) FloatingActionButton fabSwap;
    @BindView(R.id.fabMute) FloatingActionButton fabMute;
    @BindView(R.id.fabShow) FloatingActionButton fabShow;
    @BindView(R.id.fabMenu) FloatingActionMenu fabMenu;

    @BindString(R.string.msg_show_dealer_view) String msgDealerSide;
    @BindString(R.string.msg_show_player_view) String msgPlayerSide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_view_manager);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {

            ButterKnife.bind(this);

            setSupportActionBar(toolbar);

            mMediaUtils = new MediaUtils(this);
            initGameParams();
            initFragments();
            initViews();

            ((JustCardsAndroidApplication) getApplication()).addObserver(this);
        }
    }

    private void initGameParams() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mGameName = bundle.getString(PARAM_GAME_NAME);
            mCards = Parcels.unwrap(bundle.getParcelable(PARAM_CARDS));
            mIsCurrentViewPlayer = bundle.getBoolean(PARAM_CURRENT_VIEW_PLAYER);
            Toast.makeText(getApplicationContext(), "Joining Game: " + mGameName, Toast.LENGTH_SHORT).show();

            // Save Game Name
            Game.getInstance(this).setName(mGameName);
            User currentUser = User.getCurrentUser(this).saveIsDealer(!mIsCurrentViewPlayer, this);

            mUsersDb = UsersDB.getInstance(mGameName).observeOn(this);
            mTableDb = TableDB.getInstance(mGameName).observeOn(this);
            messagingClient = new FirebaseMessagingClient(this, mGameName);
            messagingClient.joinGame(); // Join channel for updates

            // Add current user to game
            mUsersDb.save(currentUser);

            // Dummy players for testing
            if (!mIsCurrentViewPlayer) {
                mUsersDb.save(PlayerUtils.getPlayers(2));
            }
        }
    }

    private void initFragments() {
        // Instantiating all the child fragments for game view
        mPlayerViewFragment = PlayerViewFragment.newInstance(null, null);
        mChatAndLogFragment = ChatAndLogFragment.newInstance(1);

        //Insert chat and log fragment
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.flLogContainer, mChatAndLogFragment, FRAGMENT_CHAT_TAG)
                .commit();
        showChatAndLogView();

        // Controlling the fragments for display based on player's role
        if (mIsCurrentViewPlayer) {
            fabSwap.setVisibility(View.GONE);
            fabSwap.setLabelText(msgDealerSide);
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.flGameContainer, mPlayerViewFragment)
                    .commit();
        } else {
            mDealerViewFragment = DealerViewFragment.newInstance(mCards, null);
            fabSwap.setLabelText(msgPlayerSide);
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.flGameContainer, mPlayerViewFragment)
                    .add(R.id.flGameContainer, mDealerViewFragment)
                    .hide(mPlayerViewFragment)
                    .commit();
        }

        // Set the current view state (player vs dealer)
        mIsShowingPlayerFragment = mIsCurrentViewPlayer;
    }

    private void initViews() {
        fabMenu.setClosedOnTouchOutside(true);
        fabExit.setImageDrawable(getVectorCompat(this, R.drawable.ic_power));
        fabSwap.setImageDrawable(getVectorCompat(this, R.drawable.ic_swap));
        fabMute.setImageDrawable(getVectorCompat(this, R.drawable.ic_not_interested));
        fabShow.setImageDrawable(getVectorCompat(this, R.drawable.ic_visibility_on));

        ivSink.setOnDragListener(new OnCardsDragListener(new CardsAdapter.CardsListener() {
            @Override
            public void exchange(String fromTag, String toTag, int fromPosition, int toPosition, Card card) {
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

            @Override
            public void toggleCard(Card card, int position, String onTag) {
            }
        }));
        llSink.setOnTouchListener(new OnTouchMoveListener(container));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            AnimationUtilsJC.animateCircularReveal(fabMenu.getMenuIconView());
        }
    }

    @OnClick(R.id.fabSwap)
    public void switchView() {
        fabMenu.close(true);
        if (mIsCurrentViewPlayer || mIsShowingPlayerFragment) {
            fabSwap.setLabelText(msgPlayerSide);
        } else {
            fabSwap.setLabelText(msgDealerSide);
        }
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(
                        mIsShowingPlayerFragment ? R.anim.slide_in_left : R.anim.slide_in_right,
                        mIsShowingPlayerFragment ? R.anim.slide_out_right : R.anim.slide_out_left)
                .hide(mIsShowingPlayerFragment ? mPlayerViewFragment : mDealerViewFragment)
                .show(mIsShowingPlayerFragment ? mDealerViewFragment : mPlayerViewFragment)
                .commit();
        this.mIsShowingPlayerFragment ^= true;
        toggleSelfPlayerView();
    }

    /**
     * Hide self view if it's in player fragment. Show in dealer.
     */
    private void toggleSelfPlayerView() {
        togglePlayerView(getCurrentUser(this));
    }

    private void togglePlayerView(User player) {
        Fragment playerFragment = getPlayerFragment(this, player);
        if (playerFragment != null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            if (mIsShowingPlayerFragment) {
                transaction.hide(playerFragment);
            } else {
                transaction.show(playerFragment);
            }
            transaction.commit();
        }
    }

    @OnClick(R.id.fabShow)
    public void onShow(View view) {
        if (fabShow.getTag() == null || !((boolean) fabShow.getTag())) {
            boolean hasCards = true;
            if (mPlayerViewFragment != null) {
                Fragment fragment = mPlayerViewFragment.getChildFragmentManager().findFragmentByTag(PLAYER_TAG);
                if (fragment != null) {
                    hasCards = ((CardsFragment) fragment).getCards().size() > 0;
                }
            }
            if (hasCards) {
                new LovelyStandardDialog(this)
                        .setTopColorRes(R.color.colorPrimary)
                        .setButtonsColorRes(R.color.colorAccent)
                        .setIcon(R.drawable.ic_visibility_on_36dp)
                        .setTitle("Show Cards to Everyone")
                        .setMessage("Are you sure you want to show your cards to all players in the game? Once shown, cards cannot be hidden back in this round!")
                        .setPositiveButton("Yes Show", v -> {
                            fabShow.setTag(true);
                            fabShow.setEnabled(false);
                            fabMenu.close(true);

                            User currentUser = User.getCurrentUser(this).flipIsShowingCards(this);
                            mUsersDb.setShowingCards(currentUser);
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .show();
            } else {
                Toast.makeText(this, "You've no cards to show!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "You're showing your cards already and cannot hide them once shown!", Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.fabMute)
    public void onMute(View view) {
        if (fabMute.getTag() == null || !((boolean) fabMute.getTag())) {
            new LovelyStandardDialog(this)
                    .setTopColorRes(R.color.colorPrimary)
                    .setButtonsColorRes(R.color.colorAccent)
                    .setIcon(R.drawable.ic_not_interested_36dp)
                    .setTitle("Mute for current round")
                    .setMessage("Are you sure you want to mute yourself for this round? Once muted, you cannot play in this round any more!")
                    .setPositiveButton("Yes Mute", v -> {
                        fabMute.setTag(true);
                        fabMenu.close(true);
                        fabMute.setEnabled(false);

                        User currentUser = User.getCurrentUser(this).flipIsActive(this);
                        mUsersDb.setActive(currentUser);
                    })
                    .setNegativeButton(android.R.string.no, null)
                    .show();
        } else {
            Toast.makeText(this, "You're already on mute for this round!", Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.fabExit)
    @Override
    public void onBackPressed() {
        fabMenu.close(true);

        new LovelyStandardDialog(this)
                .setTopColorRes(R.color.colorPrimary)
                .setButtonsColorRes(R.color.colorAccent)
                .setIcon(R.drawable.ic_power_36dp)
                .setTitle("Exit Game")
                .setMessage("Are you sure you want to exit from game?")
                .setPositiveButton("Yes Exit", v -> {
                    messagingClient.leaveGame();
                    mUsersDb.observeOff();
                    mTableDb.observeOff();
                    GameDB.deleteGamesForUser(mGameName, getCurrentUser(GameViewManagerActivity.this));
                    GameDB.delete(mGameName, this);
                    ((JustCardsAndroidApplication) getApplication()).removeAllObservers();
                    User.getCurrentUser(this).reset(this);
                    finish();
                    AnimationUtilsJC.exitZoomTransition(this);
                })
                .setNegativeButton(android.R.string.no, null)
                .show();
    }

    @OnClick(R.id.ibInfo)
    public void showGameInfo(View view) {
        PopupWindow popup = new PopupWindow(GameViewManagerActivity.this);
        View layout = getLayoutInflater().inflate(R.layout.popup_gameid, container, false);
        Button btnGameId = (Button) layout.findViewById(R.id.btnGameId);
        btnGameId.setText("Game id " + mGameName + " ");
        btnGameId.setOnClickListener(view1 -> {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.msg_share) + mGameName);
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

    @OnClick(R.id.ibChat)
    public void toggleChatAndLogView(View v) {
        if (!mIsShowingChat) {
            showChatAndLogView();
        } else {
            hideChatAndLogView();
        }
    }

    private void showChatAndLogView() {
        mIsShowingChat = true;
        ibChat.setImageResource(R.drawable.ic_cancel);
        AnimationUtilsJC.bounceAnimation(this, ibChat);
        AnimationUtilsJC.animateCornerReveal(flLogContainer);
    }

    private void hideChatAndLogView() {
        mIsShowingChat = false;
        ibChat.setImageResource(R.drawable.ic_comment);
        AnimationUtilsJC.bounceAnimation(this, ibChat);
        AnimationUtilsJC.animateCornerUnReveal(flLogContainer, new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                flLogContainer.setVisibility(View.GONE);
                fabMenu.showMenuButton(true);
            }
        });
    }

    @OnClick(R.id.ibHelp)
    public void showTutorial(View view) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        TutorialFragment tutorialFragment = TutorialFragment.newInstance(0,0);
        fragmentTransaction.add(R.id.flGameContainer, tutorialFragment, "Tutorial");
        fragmentTransaction.commit();
        //startActivity(new Intent(GameViewManagerActivity.this, TutorialActivity.class));
    }

    @Override
    public void onDealerOptionsShowing(boolean isDealerOptionShowing) {
        // Toggle Chat and Log visibility
        if (isDealerOptionShowing) {
            hideChatAndLogView();
        } else {
            showChatAndLogView();
        }

        // Show and hide all players when dealer option is showing to un-clutter the view
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
    public void onSelectGameRule(String code, Object selection) {
        messagingClient.selectGameRules(code, selection);
    }

    @Override
    public boolean onDeal(List<Card> cards, User player) {
        Fragment fragment = getPlayerFragment(this, player);
        if (fragment != null && !isEmpty(cards)) {
            boolean result = ((CardsFragment) fragment).stackCards(cards);
            if (result) {
                for (Card card : cards) {
                    messagingClient.dealCards(player, card);
                }
                User self = getCurrentUser(this);
                onNewLogEvent(self.getDisplayName(), self.getAvatarUri(), "Dealing " + cards.size() + " cards to everyone");
            }
            return result;
        }
        return false;
    }

    @Override
    public boolean onDealTable(List<Card> cards, boolean toSink) {
        if (!isEmpty(cards)) {
            mTableDb.pushCards(cards, toSink); // Push Cards to firebase db
            if (!toSink) {
                User self = getCurrentUser(this);
                onNewLogEvent(self.getDisplayName(), self.getAvatarUri(), cards.size() + " cards are being moved to the Table");
                return true;
            } else {
                User self = getCurrentUser(this);
                onNewLogEvent(self.getDisplayName(), self.getAvatarUri(), cards.size() + " cards are being moved to the Sink");
                return true;
            }
        }
        return false;
    }

    @Override
    public void onCardExchange(String fromTag, String toTag, int fromPosition, int toPosition, Card card) {
        if (fromTag.equalsIgnoreCase(TABLE_TAG) && toTag.equalsIgnoreCase(PLAYER_TAG)) {
            messagingClient.exchangeCardWithTable(card, fromPosition, toPosition, true);
        } else if (fromTag.equalsIgnoreCase(PLAYER_TAG) && toTag.equalsIgnoreCase(TABLE_TAG)) {
            messagingClient.exchangeCardWithTable(card, fromPosition, toPosition, false);
        } else if (fromTag.equalsIgnoreCase(PLAYER_TAG) && toTag.equalsIgnoreCase(PLAYER_TAG)) {
            messagingClient.swapCardWithinPlayer(card, fromPosition, toPosition);
        } else if (toTag.equalsIgnoreCase(SINK_TAG)) {
            messagingClient.dropCardToSink(card, fromTag, fromPosition);
        }
    }

    @Override
    public void onToggleCard(Card card, int position, String onTag) {
        if (card != null) {
            messagingClient.toggleCard(card, position, onTag);
        }
    }

    @Override
    public void onScore(boolean saveClicked) {
        if (saveClicked) {
            for (User player : mPlayers) {
                mUsersDb.setScore(player);
            }
            messagingClient.endRound();
        }
    }

    @Override
    public void roundWinners(List<User> roundWinners) {
        messagingClient.declareRoundWinners(roundWinners);
    }

    @Override
    public void onNewLogEvent(String whoPosted, String fromAvatar, String details) {
        Log.d(Constants.TAG, GameViewManagerActivity.class.getSimpleName() + "--" + details + "--" + whoPosted);
        mChatAndLogFragment.addNewLogEvent(whoPosted, fromAvatar, details);
    }

    @Override
    public void onDealOptionSelected(Bundle bundle) {
        // Do nothing
    }

    @Override
    public void onChat(ChatLog item) {
        messagingClient.sendChatMessage(item.getDetails());
    }

    @Override
    public void onChatScroll(int dy) {
        if (dy > 0) {
            fabMenu.hideMenuButton(true);
        } else if (dy < 0) {
            fabMenu.showMenuButton(true);
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        //Do nothing
    }

    @SuppressWarnings("unchecked")
    @Override
    public synchronized void onUpdate(final Observable o, final Object identifier, final Object arg) {
        String event = identifier.toString();
        Log.d(TAG, "onUpdate: " + event);
        HashMap<String, String> gameData = (HashMap<String, String>) arg;
        User from = User.fromMap(gameData);
        Card card;
        int fromPosition;
        int toPosition;

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE
                && !this.isDestroyed()) {

            switch (event) {
                case EVENT_DEAL_CARDS:
                    User to = fromJson(gameData.get(PARAM_PLAYER));
                    card = gson.fromJson(gameData.get(PARAM_CARDS), Card.class);
                    handleDeal(card, from, to);
                    break;
                case EVENT_EXCHANGE_CARD_WITH_TABLE:
                    card = gson.fromJson(gameData.get(PARAM_CARDS), Card.class);
                    fromPosition = Integer.valueOf(gameData.get(FROM_POSITION));
                    toPosition = Integer.valueOf(gameData.get(TO_POSITION));
                    boolean pickedFromTable = Boolean.valueOf(gameData.get(TABLE_PICKED));
                    handleCardExchangeWithTable(from, card, fromPosition, toPosition, pickedFromTable);
                    break;
                case EVENT_SWAP_CARD_WITHIN_PLAYER:
                    card = gson.fromJson(gameData.get(PARAM_CARDS), Card.class);
                    fromPosition = Integer.valueOf(gameData.get(FROM_POSITION));
                    toPosition = Integer.valueOf(gameData.get(TO_POSITION));
                    handleCardExchangeWithinPlayer(from, card, fromPosition, toPosition);
                    break;
                case EVENT_DROP_CARD_TO_SINK:
                    card = gson.fromJson(gameData.get(PARAM_CARDS), Card.class);
                    String fromTag = gameData.get(FROM_TAG);
                    fromPosition = Integer.valueOf(gameData.get(FROM_POSITION));
                    handleCardDropToSink(from, card, fromTag, fromPosition);
                    break;
                case EVENT_TOGGLE_CARD:
                    card = gson.fromJson(gameData.get(PARAM_CARDS), Card.class);
                    int position = Integer.valueOf(gameData.get(POSITION));
                    String onTag = gameData.get(ON_TAG);
                    handleToggleCard(from, card, position, onTag);
                    break;
                case EVENT_ROUND_WINNERS:
                    List<User> roundWinners = gson.fromJson(gameData.get(PARAM_PLAYERS), getUsersType());
                    handleRoundWinners(roundWinners, from);
                    break;
                case EVENT_END_ROUND:
                    handleEndRound(from);
                    break;
                case EVENT_SELECT_GAME_RULES:
                    String code = gameData.get(RULE_CODE);
                    Object selection = null;
                    String selectionElement = gameData.get(RULE_SELECTION);
                    if (!TextUtils.isEmpty(selectionElement)) {
                        selection = Boolean.valueOf(selectionElement);
                    }
                    handleGameRules(from, code, selection);
                    break;
                case EVENT_CHAT_MESSAGE:
                    onNewLogEvent(from.getDisplayName(), from.getAvatarUri(), gameData.get(PARAM_CHAT));
                    break;
            }
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // ignore orientation/keyboard change
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void handlePlayersAdded(final List<User> players) {
        mPlayers.addAll(players);
        if (mDealerViewFragment != null) {
            mDealerViewFragment.addPlayers(players);
        }

        PlayerViewHelper.addPlayers(GameViewManagerActivity.this, container.getId(), players);
        for (User player : players) {
            if (player.equals(getCurrentUser(this))) {
                togglePlayerView(player);
            }
            handleToggleCardsListForPlayerView(player, false);
            onNewLogEvent(player.getDisplayName(), player.getAvatarUri(), player.getDisplayName() + " joined.");
        }
    }

    @Override
    public void handlePlayersRemoved(final List<User> players) {
        for (User player : players) {
            mPlayers.remove(player);
            if (mDealerViewFragment != null) {
                mDealerViewFragment.removePlayer(player);
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

    @Override
    public void handlePlayerChanged(final User player) {
        // evaluate the fields changed
        for (User mPlayer : mPlayers) {
            if (mPlayer.equals(player)) {
                //figure out what changed
                if (player.isActive() != mPlayer.isActive()) {
                    Log.d(TAG, "is active changed");
                    handleMutePlayerForRound(player, player.isActive());
                }

                if (player.isShowingCards() != mPlayer.isShowingCards()) {
                    Log.d(TAG, "isShowingCards changed");
                    handleToggleCardsListForPlayerView(player, player.isShowingCards());
                }

                if (player.getScore() != mPlayer.getScore()) {
                    Log.d(TAG, "Score changed");
                    handleUpdateScores(getList(player));
                }

                if (!player.getCards().equals(mPlayer.getCards())) {
                    Log.d(TAG, "Cards in hand changed");
                }
            }
        }
    }

    @Override
    public void handleDealTable(final List<Card> cards) {
        mMediaUtils.playGlassBreakingTone();
        CardUtil.setShowingFront(cards);
        Fragment fragment = mPlayerViewFragment.getChildFragmentManager().findFragmentByTag(TABLE_TAG);
        if (fragment != null) {
            boolean stacked = ((CardsFragment) fragment).stackCards(cards);
            if (stacked && User.getCurrentUser(this).isDealer() && mDealerViewFragment != null) {
                mDealerViewFragment.drawDealerCards(cards);
            }
        }
    }

    @Override
    public void handleDealSink(final List<Card> cards) {
        mMediaUtils.playGlassBreakingTone();
        if (!isEmpty(cards)) {
            CardUtil.setShowingFront(cards);
            addCardsToSink(cards);
            if (User.getCurrentUser(this).isDealer() && mDealerViewFragment != null) {
                mDealerViewFragment.drawDealerCards(cards);
            }
        }
    }

    public void handleDeal(final Card card, final User from, final User to) {
        if (card != null && from != null && from.isDealer() && to != null) {
            Log.d(TAG, "handleDeal: from: " + from.getUserId() + " : to: " + to.getUserId() + ": Card: " + card.getName());
            card.setShowingFront(false);
            if (mIsCurrentViewPlayer) {
                Fragment playerFragment = getPlayerFragment(this, to);
                if (playerFragment != null) {
                    ((CardsFragment) playerFragment).stackCards(getList(card));
                }
            }
            if (User.getCurrentUser(this).equals(to) && mPlayerViewFragment != null) {
                Fragment fragment = mPlayerViewFragment.getChildFragmentManager().findFragmentByTag(PLAYER_TAG);
                if (fragment != null) {
                    ((CardsFragment) fragment).stackCards(getList(card));
                }
            }

            // animation deal event on the player view
            animateDeal();
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
            if (mPlayerViewFragment != null) {
                tableFragment = mPlayerViewFragment.getChildFragmentManager().findFragmentByTag(TABLE_TAG);
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
            if (PLAYER_TAG.equalsIgnoreCase(fromTag)) {
                fragment = getPlayerFragment(this, from);
            } else if (TABLE_TAG.equalsIgnoreCase(fromTag) && mPlayerViewFragment != null) {
                fragment = mPlayerViewFragment.getChildFragmentManager().findFragmentByTag(TABLE_TAG);
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

    private void handleToggleCard(User from, Card card, int position, String onTag) {
        Log.d(TAG, "handleToggleCard: Card: " + card +
                ", User: " + from +
                ", position: " + position +
                ", onTag: " + onTag);

        if (from != null && card != null && !TextUtils.isEmpty(onTag)) {
            Fragment fragment = null;
            if (onTag.equalsIgnoreCase(TABLE_TAG) && mPlayerViewFragment != null) {
                fragment = mPlayerViewFragment.getChildFragmentManager().findFragmentByTag(TABLE_TAG);
                mChatAndLogFragment.addNewLogEvent(from.getDisplayName(), from.getAvatarUri(),
                        from.getDisplayName() + " flipped " + card.getName().toLowerCase().replace("_", " ") + " on table");
            } else if (onTag.equalsIgnoreCase(PLAYER_TAG)) {
                fragment = getPlayerFragment(this, from);
            }

            if (fragment != null) {
                ((CardsFragment) fragment).toggleCard(card, position);
            }
        }
    }

    /**
     * Show or hide the user's cards fragment
     *
     * @param player which player
     * @param toShow true if want to show, false for hiding
     */
    private void handleToggleCardsListForPlayerView(final User player, final boolean toShow) {
        if (player != null) {
            if (mPlayers.contains(player)) {
                mPlayers.get(mPlayers.indexOf(player)).setShowingCards(toShow);
            }

            if (mDealerViewFragment != null) {
                List<User> players = mDealerViewFragment.getPlayers();
                if (!isEmpty(players) && players.contains(player)) {
                    players.get(players.indexOf(player)).setShowingCards(toShow);
                }
            }

            player.setShowingCards(toShow);

            Fragment fragment = getPlayerFragment(this, player);
            if (fragment != null) {
                ((CardsFragment) fragment).toggleCardsList(toShow);
            }

            if (player.isShowingCards()) {
                if (isSelf(player, this)) {
                    Toast.makeText(this, "You are showing your cards now!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, player.getDisplayName() + " is currently showing cards!", Toast.LENGTH_SHORT).show();
                }
                onNewLogEvent(player.getDisplayName(), player.getAvatarUri(), player.getDisplayName() + " is showing cards.");
            }
        }
    }

    private void handleMutePlayerForRound(final User player, final boolean toMute) {
        if (player != null) {
            if (mPlayers.contains(player)) {
                mPlayers.get(mPlayers.indexOf(player)).setActive(!toMute);
            }

            if (mDealerViewFragment != null) {
                List<User> players = mDealerViewFragment.getPlayers();
                if (!isEmpty(players) && players.contains(player)) {
                    players.get(players.indexOf(player)).setActive(!toMute);
                }
            }

            player.setActive(!toMute);

            if (!player.isActive()) {
                if (isSelf(player, this)) {
                    Toast.makeText(this, "You are on mute now!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, player.getDisplayName() + " is on mute!", Toast.LENGTH_SHORT).show();
                }
                onNewLogEvent(player.getDisplayName(), player.getAvatarUri(), player.getDisplayName() + " is on mute.");
            }
        }
    }

    private void handleUpdateScores(final List<User> players) {
        if (!isEmpty(players)) {
            for (User player : players) {
                if (mPlayers.contains(player)) {
                    mPlayers.get(mPlayers.indexOf(player)).setScore(player.getScore());
                }

                if (mDealerViewFragment != null) {
                    List<User> dPlayers = mDealerViewFragment.getPlayers();
                    if (!isEmpty(dPlayers) && dPlayers.contains(player)) {
                        dPlayers.get(dPlayers.indexOf(player)).setScore(player.getScore());
                    }
                }

                if (isSelf(player, this)) {
                    User.getCurrentUser(this).saveScore(player.getScore(), this);
                }

                Fragment fragment = getPlayerFragment(this, player);
                if (fragment != null) {
                    ((PlayerFragment) fragment).scoreChange(player.getScore());
                }
            }
        }
    }

    public void handleRoundWinners(final List<User> roundWinners, final User from) {
        //Hide the chat view to show full screen
        hideChatAndLogView();

        Log.i(Constants.TAG, "Winners are " + roundWinners.toString());
        if (!isEmpty(roundWinners) && from != null && from.isDealer()) {
            mMediaUtils.playClapsTone();
            FragmentManager fm = getSupportFragmentManager();
            RoundWinnersFragment winnerDialog = RoundWinnersFragment.newInstance(roundWinners);
            winnerDialog.show(fm, "winnerDialog");
        }
    }

    private void handleEndRound(final User from) {
        if (from != null && from.isDealer()) {
            onNewLogEvent(from.getDisplayName(), from.getAvatarUri(), "Ending the current round now.");

            // Reset Button States
            fabMute.setEnabled(true);
            fabMute.setTag(false);
            fabShow.setEnabled(true);
            fabShow.setTag(false);

            // Reset player statuses in game manager and dealer view fragment
            resetForRound(mPlayers);
            if (mDealerViewFragment != null) {
                List<User> players = mDealerViewFragment.getPlayers();
                resetForRound(players);
            }

            // Reset current player status and pushCards
            User.getCurrentUser(this).resetForRound(this);

            // Clear leftover cards from player's hand and table
            Fragment fragment;
            if (mPlayerViewFragment != null) {
                fragment = mPlayerViewFragment.getChildFragmentManager().findFragmentByTag(PLAYER_TAG);
                if (fragment != null) {
                    ((CardsFragment) fragment).clearCards();
                }
                fragment = mPlayerViewFragment.getChildFragmentManager().findFragmentByTag(TABLE_TAG);
                if (fragment != null) {
                    ((CardsFragment) fragment).clearCards();
                    mTableDb.clear(false, this);
                }
            }

            // Clear leftover cards from dealer's stack and re-stack with selected cards for the game
            if (mDealerViewFragment != null) {
                fragment = mDealerViewFragment.getChildFragmentManager().findFragmentByTag(DEALER_TAG);
                if (fragment != null) {
                    ((CardsFragment) fragment).clearCards();
                    mDealerViewFragment.setCards(mCards);
                    ((CardsFragment) fragment).stackCards(mCards);
                }
            }

            // Clear leftover cards from floating players and toggle the cards list
            for (User player : mPlayers) {
                fragment = getPlayerFragment(this, player);
                if (fragment != null) {
                    ((CardsFragment) fragment).clearCards();
                    ((CardsFragment) fragment).toggleCardsList(false);
                }
            }

            // Clear cards from the sink
            clearSink();
        }
    }

    public void addCardsToSink(List<Card> cards) {
        sinkCards.addAll(cards);
        ivSink.setImageResource(R.drawable.ic_sink_full);
        tvSinkCardsCount.setText(String.valueOf(sinkCards.size()));
    }

    public void clearSink() {
        sinkCards.clear();
        ivSink.setImageResource(R.drawable.ic_sink_empty);
        tvSinkCardsCount.setText(String.valueOf(sinkCards.size()));
        mTableDb.clear(true, this);
    }

    private void handleGameRules(final User from, final String code, final Object selection) {
        Log.d(TAG, "handleGameRules: from: " + from +
                ", code: " + code +
                ", selection: " + selection);

        if (from != null && from.isDealer() && !TextUtils.isEmpty(code) && selection != null) {
            if (RULE_VIEW_TABLE_CARD.equalsIgnoreCase(code)) {
                GameRules.get(this)
                        .setViewTableCardAllowed((boolean) selection)
                        .save(this);
                onNewLogEvent(from.getDisplayName(), from.getAvatarUri(),
                        "Rule set for the round: " + getRuleDescription(RULE_VIEW_TABLE_CARD, this) + ": " + ((boolean) selection ? "Yes" : "No"));
            }
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