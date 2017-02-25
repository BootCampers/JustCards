package org.justcards.android.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.plattysoft.leonids.ParticleSystem;
import com.plattysoft.leonids.modifiers.AlphaModifier;
import com.plattysoft.leonids.modifiers.ScaleModifier;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import org.justcards.android.R;
import org.justcards.android.adapters.CardsAdapter;
import org.justcards.android.application.JustCardsAndroidApplication;
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
import org.justcards.android.models.Card;
import org.justcards.android.models.ChatLog;
import org.justcards.android.models.Game;
import org.justcards.android.models.GameRules;
import org.justcards.android.models.User;
import org.justcards.android.network.FirebaseDB;
import org.justcards.android.network.ParseDB;
import org.justcards.android.network.ParseUtils;
import org.justcards.android.utils.AnimationUtils;
import org.justcards.android.utils.CardUtil;
import org.justcards.android.utils.Constants;
import org.justcards.android.utils.MediaUtils;
import org.justcards.android.utils.PlayerUtils;
import org.justcards.android.views.OnCardsDragListener;
import org.justcards.android.views.OnTouchMoveListener;
import org.justcards.android.views.PlayerViewHelper;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static org.justcards.android.models.GameRules.getRuleDescription;
import static org.justcards.android.models.User.fromJson;
import static org.justcards.android.models.User.getCurrentUser;
import static org.justcards.android.models.User.resetForRound;
import static org.justcards.android.network.ParseUtils.isSelf;
import static org.justcards.android.utils.AppUtils.getCardsType;
import static org.justcards.android.utils.AppUtils.getList;
import static org.justcards.android.utils.AppUtils.getUsersType;
import static org.justcards.android.utils.AppUtils.getVectorCompat;
import static org.justcards.android.utils.AppUtils.isEmpty;
import static org.justcards.android.utils.Constants.DEALER_TAG;
import static org.justcards.android.utils.Constants.FRAGMENT_CHAT_TAG;
import static org.justcards.android.utils.Constants.FROM_POSITION;
import static org.justcards.android.utils.Constants.FROM_TAG;
import static org.justcards.android.utils.Constants.ON_TAG;
import static org.justcards.android.utils.Constants.PARAM_CARDS;
import static org.justcards.android.utils.Constants.PARAM_CARD_COUNT;
import static org.justcards.android.utils.Constants.PARAM_CHAT;
import static org.justcards.android.utils.Constants.PARAM_CURRENT_VIEW_PLAYER;
import static org.justcards.android.utils.Constants.PARAM_GAME_NAME;
import static org.justcards.android.utils.Constants.PARAM_PLAYER;
import static org.justcards.android.utils.Constants.PARAM_PLAYERS;
import static org.justcards.android.utils.Constants.PARSE_CHAT_MESSAGE;
import static org.justcards.android.utils.Constants.PARSE_DEAL_CARDS;
import static org.justcards.android.utils.Constants.PARSE_DEAL_CARDS_TO_SINK;
import static org.justcards.android.utils.Constants.PARSE_DEAL_CARDS_TO_TABLE;
import static org.justcards.android.utils.Constants.PARSE_DROP_CARD_TO_SINK;
import static org.justcards.android.utils.Constants.PARSE_END_ROUND;
import static org.justcards.android.utils.Constants.PARSE_EXCHANGE_CARD_WITH_TABLE;
import static org.justcards.android.utils.Constants.PARSE_MUTE_PLAYER_FOR_ROUND;
import static org.justcards.android.utils.Constants.PARSE_NEW_PLAYER_ADDED;
import static org.justcards.android.utils.Constants.PARSE_PLAYER_LEFT;
import static org.justcards.android.utils.Constants.PARSE_ROUND_WINNERS;
import static org.justcards.android.utils.Constants.PARSE_SCORES_UPDATED;
import static org.justcards.android.utils.Constants.PARSE_SELECT_GAME_RULES;
import static org.justcards.android.utils.Constants.PARSE_SWAP_CARD_WITHIN_PLAYER;
import static org.justcards.android.utils.Constants.PARSE_TOGGLE_CARD;
import static org.justcards.android.utils.Constants.PARSE_TOGGLE_CARDS_LIST;
import static org.justcards.android.utils.Constants.PLAYER_TAG;
import static org.justcards.android.utils.Constants.POSITION;
import static org.justcards.android.utils.Constants.RULE_CODE;
import static org.justcards.android.utils.Constants.RULE_SELECTION;
import static org.justcards.android.utils.Constants.RULE_VIEW_TABLE_CARD;
import static org.justcards.android.utils.Constants.SINK_TAG;
import static org.justcards.android.utils.Constants.TABLE_PICKED;
import static org.justcards.android.utils.Constants.TABLE_TAG;
import static org.justcards.android.utils.Constants.TO_MUTE;
import static org.justcards.android.utils.Constants.TO_POSITION;
import static org.justcards.android.utils.Constants.TO_SHOW;
import static org.justcards.android.utils.Constants.USER_TAG_SCORE;
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
        Observer {

    private List<User> mPlayers = new ArrayList<>();
    private List<Card> mCards;
    private String gameName;
    private List<Card> sinkCards = new ArrayList<>();
    private ParseUtils parseUtils;
    private MediaUtils mediaUtils;
    private Gson gson = new Gson();

    private boolean isCurrentViewPlayer = true;
    private boolean isShowingPlayerFragment = true; //false is showing dealer fragment
    private boolean isShowingChat = false;

    private PlayerViewFragment playerViewFragment;
    private DealerViewFragment dealerViewFragment;
    private ChatAndLogFragment chatAndLogFragment;

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.clGameLayout) ViewGroup container;
    @BindView(R.id.llSink) ViewGroup llSink;
    @BindView(R.id.ibComment) ImageButton ibComment;
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

    private static final String TAG = GameViewManagerActivity.class.getSimpleName();

    //Firebase
    DatabaseReference gameDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_view_manager);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        mediaUtils = new MediaUtils(this);
        initGameParams();
        initFragments();
        initViews();

        ((JustCardsAndroidApplication) getApplication()).addObserver(this);
    }

    private void initGameParams() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            gameName = bundle.getString(PARAM_GAME_NAME);
            mCards = Parcels.unwrap(bundle.getParcelable(PARAM_CARDS));
            isCurrentViewPlayer = bundle.getBoolean(PARAM_CURRENT_VIEW_PLAYER);
            Toast.makeText(getApplicationContext(), "Joining Game: " + gameName, Toast.LENGTH_SHORT).show();

            gameDatabaseReference = FirebaseDatabase.getInstance().getReference(gameName);

            // Add myself to game
            User currentUser = User.getCurrentUser(this);
            String uId = gameDatabaseReference.push().getKey();
            if(isCurrentViewPlayer == false){
                currentUser.setDealer(true);
            } else {
                currentUser.setDealer(false);
            }
            currentUser.setUserId(uId);
            currentUser.save(getApplicationContext());
            gameDatabaseReference.child(uId).setValue(currentUser);

            //***   DUmmy code for testing ****///
                List<User> dummyPlayers = PlayerUtils.getPlayers(2);
                for (User dummyPlayer : dummyPlayers) {
                    Game.save(gameName, dummyPlayer);
                    String dummyUserId = gameDatabaseReference.push().getKey();
                    dummyPlayer.setUserId(dummyUserId);
                    gameDatabaseReference.child(dummyUserId).setValue(dummyPlayer);
                }
            //***   DUmmy code for testing ****///

            //Join channel for updates
            parseUtils = new ParseUtils(this, gameName);
            parseUtils.saveCurrentUserIsDealer(!isCurrentViewPlayer);

            //Get previously joined players
            gameDatabaseReference.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String previousKey) {
                    User user = dataSnapshot.getValue(User.class);
                    Log.d(TAG,"onChildAdded " + user.getDisplayName() + " " + dataSnapshot.getKey());
                    addPlayersToView(getList(user));
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String previousKey) {
                    User user = dataSnapshot.getValue(User.class);
                    Log.d(TAG,"onChildChanged " + user.getDisplayName() + " " + dataSnapshot.getKey());
                    for(User player : mPlayers){
                        if(player.getUserId().equals(user.getUserId()) /*userid is not gonna change*/){
                            //figure out what changed
                            if(user.isActive() != player.isActive()){
                                Log.d(TAG,"is active changed");
                                handleMutePlayerForRound(user, user.isActive());
                            }
                            if(user.getScore() != player.getScore()) {
                                Log.d(TAG,"Score changed");
                                handleScoresUpdate(user, getList(user));
                            }
                            if(user.isShowingCards() != player.isShowingCards()){
                                Log.d(TAG,"isShowingCards changed");
                                toggleCardsListForPlayerView(user, user.isShowingCards());
                            }
                        }
                    }
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    Log.d(TAG,"onChildRemoved " + user.getDisplayName() + "--" + dataSnapshot.getKey());
                    removePlayersFromView(getList(user));
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String previousKey) {
                    User user = dataSnapshot.getValue(User.class);
                    Log.d(TAG,"onChildMoved " + user.getDisplayName() + " " + dataSnapshot.getKey());
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            parseUtils.joinChannel();
        }
    }

    private void initFragments() {
        // Instantiating all the child fragments for game view
        playerViewFragment = PlayerViewFragment.newInstance(null, null);
        dealerViewFragment = DealerViewFragment.newInstance(mCards, null);
        chatAndLogFragment = ChatAndLogFragment.newInstance(1);

        // Controlling the fragments for display based on player's role
        if (isCurrentViewPlayer) {
            fabSwap.setVisibility(View.GONE);
            fabSwap.setLabelText(msgDealerSide);
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.flGameContainer, playerViewFragment)
                    .commit();
        } else {
            fabSwap.setLabelText(msgPlayerSide);
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
        AnimationUtils.animateCircularReveal(fabMenu);
    }

    @OnClick(R.id.fabSwap)
    public void switchView() {
        fabMenu.close(true);
        if (isCurrentViewPlayer || isShowingPlayerFragment) {
            fabSwap.setLabelText(msgPlayerSide);
        } else {
            fabSwap.setLabelText(msgDealerSide);
        }
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(
                        isShowingPlayerFragment ? R.anim.slide_in_left : R.anim.slide_in_right,
                        isShowingPlayerFragment ? R.anim.slide_out_right : R.anim.slide_out_left)
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
        togglePlayerView(getCurrentUser(this));
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

    @OnClick(R.id.fabShow)
    public void onShow(View view) {
        if (fabShow.getTag() == null || !((boolean) fabShow.getTag())) {
            boolean hasCards = true;
            if (playerViewFragment != null) {
                Fragment fragment = playerViewFragment.getChildFragmentManager().findFragmentByTag(PLAYER_TAG);
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
                            User self = parseUtils.getCurrentUser();
                            parseUtils.saveCurrentUserIsShowingCards(!self.isShowingCards());
                            parseUtils.toggleCardsList(true);
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
                        User self = parseUtils.getCurrentUser();
                        parseUtils.saveCurrentUserIsActive(!self.isActive());
                        parseUtils.mutePlayerForRound(true);
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
                    parseUtils.removeChannel();
                    FirebaseDB.deleteGamesForUser(gameName, getCurrentUser(GameViewManagerActivity.this));
                    if (getCurrentUser(GameViewManagerActivity.this).isDealer()) {
                        FirebaseDB.deleteGame(gameName);
                    }
                    ((JustCardsAndroidApplication) getApplication()).removeAllObservers();
                    parseUtils.resetCurrentUser();
                    finish();
                    AnimationUtils.exitZoomTransition(this);
                })
                .setNegativeButton(android.R.string.no, null)
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

    @OnClick(R.id.ibHelp)
    public void showTutorial(View view) {
        /*FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        TutorialFragment tutorialFragment = TutorialFragment.newInstance(0,0);
        fragmentTransaction.add(R.id.flGameContainer, tutorialFragment, "Tutorial");
        fragmentTransaction.commit();*/
        startActivity(new Intent(GameViewManagerActivity.this, TutorialActivity.class));
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
    public void onSelectGameRule(String code, Object selection) {
        parseUtils.selectGameRules(code, selection);
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
            if (!toSink) {
                parseUtils.dealCardsToTable(cards);
                User self = getCurrentUser(this);
                onNewLogEvent(self.getDisplayName(), self.getAvatarUri(), cards.size() + " cards are being moved to the table");
                return true;
            } else {
                parseUtils.dealCardsToSink(cards);
                return true;
            }
        }
        return false;
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
    public void onToggleCard(Card card, int position, String onTag) {
        if (card != null) {
            parseUtils.toggleCard(card, position, onTag);
        }
    }

    @Override
    public void onScore(boolean saveClicked) {
        if (saveClicked) {
            parseUtils.updateUsersScore(mPlayers);
            parseUtils.endRound();
        }
    }

    @Override
    public void roundWinners(List<User> roundWinners) {
        parseUtils.declareRoundWinners(roundWinners);
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
        parseUtils.sendChatMessage(item.getDetails());
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        //Do nothing
    }

    @Override
    public synchronized void onUpdate(final Observable o, final Object identifier, final Object arg) {
        String event = identifier.toString();
        JsonObject json = (JsonObject) arg;
        User from = User.fromJson(json);
        Log.d(TAG, "onUpdate: " + event);

        switch (event) {
            case PARSE_NEW_PLAYER_ADDED:
                mediaUtils.playTingTone();
                runOnUiThread(() -> addPlayersToView(getList(from)));
                break;
            case PARSE_PLAYER_LEFT:
                mediaUtils.playTingTone();
                runOnUiThread(() -> removePlayersFromView(getList(from)));
                break;
            case PARSE_DEAL_CARDS:
                runOnUiThread(() -> {
                    User to = fromJson(json.get(PARAM_PLAYER).getAsJsonObject());
                    Card card = gson.fromJson(json.get(PARAM_CARDS), Card.class);
                    handleDeal(card, from, to);
                });
                break;
            case PARSE_DEAL_CARDS_TO_TABLE:
                mediaUtils.playGlassBreakingTone();
                runOnUiThread(() -> {
                    int cardCount = json.get(PARAM_CARD_COUNT).getAsInt();
                    List<Card> cards = gson.fromJson(json.get(PARAM_CARDS), getCardsType());
                    handleDealTable(from, cards, cardCount);
                });
                break;
            case PARSE_DEAL_CARDS_TO_SINK:
                mediaUtils.playGlassBreakingTone();
                runOnUiThread(() -> {
                    List<Card> cards = gson.fromJson(json.get(PARAM_CARDS), getCardsType());
                    handleDealSink(from, cards);
                });
                break;
            case PARSE_EXCHANGE_CARD_WITH_TABLE:
                runOnUiThread(() -> {
                    Card card = gson.fromJson(json.get(PARAM_CARDS), Card.class);
                    int fromPosition = json.get(FROM_POSITION).getAsInt();
                    int toPosition = json.get(TO_POSITION).getAsInt();
                    boolean pickedFromTable = json.get(TABLE_PICKED).getAsBoolean();
                    handleCardExchangeWithTable(from, card, fromPosition, toPosition, pickedFromTable);
                });
                break;
            case PARSE_SWAP_CARD_WITHIN_PLAYER:
                runOnUiThread(() -> {
                    Card card = gson.fromJson(json.get(PARAM_CARDS), Card.class);
                    int fromPosition = json.get(FROM_POSITION).getAsInt();
                    int toPosition = json.get(TO_POSITION).getAsInt();
                    handleCardExchangeWithinPlayer(from, card, fromPosition, toPosition);
                });
                break;
            case PARSE_DROP_CARD_TO_SINK:
                runOnUiThread(() -> {
                    Card card = gson.fromJson(json.get(PARAM_CARDS), Card.class);
                    String fromTag = json.get(FROM_TAG).getAsString();
                    int fromPosition = json.get(FROM_POSITION).getAsInt();
                    handleCardDropToSink(from, card, fromTag, fromPosition);
                });
                break;
            case PARSE_TOGGLE_CARD:
                runOnUiThread(() -> {
                    Card card = gson.fromJson(json.get(PARAM_CARDS), Card.class);
                    int position = json.get(POSITION).getAsInt();
                    String onTag = json.get(ON_TAG).getAsString();
                    handleToggleCard(from, card, position, onTag);
                });
                break;
            case PARSE_TOGGLE_CARDS_LIST:
                runOnUiThread(() -> {
                    boolean toShow = json.get(TO_SHOW).getAsBoolean();
                    toggleCardsListForPlayerView(from, toShow);
                });
                break;
            case PARSE_MUTE_PLAYER_FOR_ROUND:
                runOnUiThread(() -> {
                    boolean toMute = json.get(TO_MUTE).getAsBoolean();
                    handleMutePlayerForRound(from, toMute);
                });
                break;
            case PARSE_SCORES_UPDATED:
                runOnUiThread(() -> {
                    List<User> players = gson.fromJson(json.get(USER_TAG_SCORE), getUsersType());
                    handleScoresUpdate(from, players);
                });
                break;
            case PARSE_ROUND_WINNERS:
                runOnUiThread(() -> {
                    List<User> roundWinners = gson.fromJson(json.get(PARAM_PLAYERS), getUsersType());
                    handleRoundWinners(roundWinners, from);
                });
                break;
            case PARSE_END_ROUND:
                runOnUiThread(() -> handleEndRound(from));
                break;
            case PARSE_SELECT_GAME_RULES:
                runOnUiThread(() -> {
                    String code = json.get(RULE_CODE).getAsString();
                    Object selection = null;
                    JsonElement selectionElement = json.get(RULE_SELECTION);
                    if (selectionElement.isJsonPrimitive()) {
                        if (selectionElement.getAsJsonPrimitive().isBoolean()) {
                            selection = selectionElement.getAsJsonPrimitive().getAsBoolean();
                        }
                    }
                    handleGameRules(from, code, selection);
                });
                break;
            case PARSE_CHAT_MESSAGE:
                runOnUiThread(() -> onNewLogEvent(from.getDisplayName(), from.getAvatarUri(), json.get(PARAM_CHAT).getAsString()));
                break;
        }
    }

    public void addPlayersToView(final List<User> players) {
        mPlayers.addAll(players);
        if (dealerViewFragment != null) {
            dealerViewFragment.addPlayers(players);
        }

        PlayerViewHelper.addPlayers(this, container.getId(), players);
        for (User player : players) {
            if (player.equals(getCurrentUser(this))) {
                togglePlayerView(player);
            }
            toggleCardsListForPlayerView(player, false);
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
            boolean stacked = ((CardsFragment) fragment).stackCards(cards);
            if (stacked && parseUtils.getCurrentUser().isDealer() && dealerViewFragment != null) {
                dealerViewFragment.drawDealerCards(cards);
            }
        }
    }

    public void handleDealSink(final User from, final List<Card> cards) {
        if (from != null && from.isDealer() && !isEmpty(cards)) {
            CardUtil.setShowingFront(cards);
            addCardsToSink(cards);
            if (parseUtils.getCurrentUser().isDealer() && dealerViewFragment != null) {
                dealerViewFragment.drawDealerCards(cards);
            }
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
            if (PLAYER_TAG.equalsIgnoreCase(fromTag)) {
                fragment = getPlayerFragment(this, from);
            } else if (TABLE_TAG.equalsIgnoreCase(fromTag) && playerViewFragment != null) {
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

    private void handleToggleCard(User from, Card card, int position, String onTag) {
        Log.d(TAG, "handleToggleCard: Card: " + card +
                ", User: " + from +
                ", position: " + position +
                ", onTag: " + onTag);

        if (from != null && card != null && !TextUtils.isEmpty(onTag)) {
            Fragment fragment = null;
            if (onTag.equalsIgnoreCase(TABLE_TAG) && playerViewFragment != null) {
                fragment = playerViewFragment.getChildFragmentManager().findFragmentByTag(TABLE_TAG);
                chatAndLogFragment.addNewLogEvent(from.getDisplayName(), from.getAvatarUri(),
                        from.getDisplayName() + " flipped " + card.getName().toLowerCase().replace("_", " ") +" on table");
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
    private void toggleCardsListForPlayerView(final User player, final boolean toShow) {
        if (player != null) {
            if (mPlayers.contains(player)) {
                mPlayers.get(mPlayers.indexOf(player)).setShowingCards(toShow);
            }

            if (dealerViewFragment != null) {
                List<User> players = dealerViewFragment.getPlayers();
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
                if (isSelf(player)) {
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

            if (dealerViewFragment != null) {
                List<User> players = dealerViewFragment.getPlayers();
                if (!isEmpty(players) && players.contains(player)) {
                    players.get(players.indexOf(player)).setActive(!toMute);
                }
            }

            player.setActive(!toMute);

            if (!player.isActive()) {
                if (isSelf(player)) {
                    Toast.makeText(this, "You are on mute now!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, player.getDisplayName() + " is on mute!", Toast.LENGTH_SHORT).show();
                }
                onNewLogEvent(player.getDisplayName(), player.getAvatarUri(), player.getDisplayName() + " is on mute.");
            }
        }
    }

    private void handleScoresUpdate(final User from, final List<User> players) {
        Log.d(TAG, "handleScoresUpdate: Score update received from: " + from);
        if (from != null && from.isDealer() && !isEmpty(players)) {
            for (User player : players) {
                if (mPlayers.contains(player)) {
                    mPlayers.get(mPlayers.indexOf(player)).setScore(player.getScore());
                }

                if (dealerViewFragment != null) {
                    List<User> dPlayers = dealerViewFragment.getPlayers();
                    if (!isEmpty(dPlayers) && dPlayers.contains(player)) {
                        dPlayers.get(dPlayers.indexOf(player)).setScore(player.getScore());
                    }
                }

                if (isSelf(player)) {
                    parseUtils.saveCurrentUserScore(player.getScore());
                }

                Fragment fragment = getPlayerFragment(this, player);
                if (fragment != null) {
                    ((PlayerFragment) fragment).scoreChange(player.getScore());
                }
            }
            onNewLogEvent(from.getDisplayName(), from.getAvatarUri(), "Scores are updated now.");
        }
    }

    public void handleRoundWinners(final List<User> roundWinners, final User from) {
        Log.i(Constants.TAG, "Winners are " + roundWinners.toString());
        if (!isEmpty(roundWinners) && from != null && from.isDealer()) {
            mediaUtils.playClapsTone();
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
            if (dealerViewFragment != null) {
                List<User> players = dealerViewFragment.getPlayers();
                resetForRound(players);
            }

            // Reset current player status and save
            parseUtils.resetCurrentUserForRound();

            // Clear leftover cards from player's hand and table
            Fragment fragment;
            if (playerViewFragment != null) {
                fragment = playerViewFragment.getChildFragmentManager().findFragmentByTag(PLAYER_TAG);
                if (fragment != null) {
                    ((CardsFragment) fragment).clearCards();
                }
                fragment = playerViewFragment.getChildFragmentManager().findFragmentByTag(TABLE_TAG);
                if (fragment != null) {
                    ((CardsFragment) fragment).clearCards();
                }
            }

            // Clear leftover cards from dealer's stack and re-stack with selected cards for the game
            if (dealerViewFragment != null) {
                fragment = dealerViewFragment.getChildFragmentManager().findFragmentByTag(DEALER_TAG);
                if (fragment != null) {
                    ((CardsFragment) fragment).clearCards();
                    dealerViewFragment.setCards(mCards);
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