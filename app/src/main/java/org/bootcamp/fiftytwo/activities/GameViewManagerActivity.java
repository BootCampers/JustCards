package org.bootcamp.fiftytwo.activities;

import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.parse.ParseException;
import com.parse.SaveCallback;

import org.bootcamp.fiftytwo.R;
import org.bootcamp.fiftytwo.application.FiftyTwoApplication;
import org.bootcamp.fiftytwo.fragments.CardsFragment;
import org.bootcamp.fiftytwo.fragments.ChatAndLogFragment;
import org.bootcamp.fiftytwo.fragments.DealerViewFragment;
import org.bootcamp.fiftytwo.fragments.DealingOptionsFragment;
import org.bootcamp.fiftytwo.fragments.PlayerFragment;
import org.bootcamp.fiftytwo.fragments.PlayerViewFragment;
import org.bootcamp.fiftytwo.interfaces.Observable;
import org.bootcamp.fiftytwo.interfaces.Observer;
import org.bootcamp.fiftytwo.models.Card;
import org.bootcamp.fiftytwo.models.ChatLog;
import org.bootcamp.fiftytwo.models.Game;
import org.bootcamp.fiftytwo.models.User;
import org.bootcamp.fiftytwo.network.ParseUtils;
import org.bootcamp.fiftytwo.utils.CardUtil;
import org.bootcamp.fiftytwo.utils.Constants;
import org.bootcamp.fiftytwo.utils.PlayerUtils;
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
import static org.bootcamp.fiftytwo.utils.AppUtils.getList;
import static org.bootcamp.fiftytwo.utils.AppUtils.isEmpty;
import static org.bootcamp.fiftytwo.utils.Constants.FRAGMENT_CHAT_TAG;
import static org.bootcamp.fiftytwo.utils.Constants.PARAM_CARDS;
import static org.bootcamp.fiftytwo.utils.Constants.PARAM_CURRENT_VIEW_PLAYER;
import static org.bootcamp.fiftytwo.utils.Constants.PARAM_GAME_NAME;
import static org.bootcamp.fiftytwo.utils.Constants.PARSE_DEAL_CARDS;
import static org.bootcamp.fiftytwo.utils.Constants.PARSE_DEAL_CARDS_TO_TABLE;
import static org.bootcamp.fiftytwo.utils.Constants.PARSE_NEW_PLAYER_ADDED;
import static org.bootcamp.fiftytwo.utils.Constants.PARSE_PLAYER_LEFT;
import static org.bootcamp.fiftytwo.utils.Constants.PLAYER_TAG;
import static org.bootcamp.fiftytwo.utils.Constants.TABLE_TAG;
import static org.bootcamp.fiftytwo.views.PlayerViewHelper.getPlayerFragmentTag;

public class GameViewManagerActivity extends AppCompatActivity implements
        PlayerViewFragment.onPlayListener,
        DealerViewFragment.OnDealListener,
        DealingOptionsFragment.OnDealOptionsListener,
        ChatAndLogFragment.OnChatAndLogListener,
        CardsFragment.OnLogEventListener,
        Observer {

    private List<User> mPlayers = new ArrayList<>();
    private List<Card> mCards;
    private ParseUtils parseUtils;

    private boolean isCurrentViewPlayer = true;
    private boolean showingPlayerFragment = true; //false is showing dealer fragment
    private boolean showingChat = false;

    private PlayerViewFragment playerViewFragment;
    private DealerViewFragment dealerViewFragment;
    private ChatAndLogFragment chatAndLogFragment;

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.fab) FloatingActionButton fab;
    @BindView(R.id.ibComment) ImageButton ibComment;
    @BindView(R.id.ibSettings) ImageButton ibSettings;
    @BindDrawable(R.drawable.ic_comment) Drawable ic_comment;
    @BindDrawable(R.drawable.ic_cancel) Drawable ic_cancel;

    private static final String TAG = GameViewManagerActivity.class.getSimpleName();
    private String gameName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_view_manager);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        initGameParams();
        initFragments();

        ((FiftyTwoApplication) getApplication()).addObserver(this);
    }

    private void initGameParams() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            gameName = bundle.getString(PARAM_GAME_NAME);
            isCurrentViewPlayer = bundle.getBoolean(PARAM_CURRENT_VIEW_PLAYER);
            mCards = Parcels.unwrap(bundle.getParcelable(PARAM_CARDS));
            Toast.makeText(getApplicationContext(), "Joining " + gameName, Toast.LENGTH_SHORT).show();

            //Add myself to game
            Game game = new Game();
            game.setGameName(gameName);
            game.addPlayer(User.getCurrentUser(GameViewManagerActivity.this));
            game.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        Log.d(Constants.TAG, "Passed");
                    } else {
                        Log.d(Constants.TAG, "Null " + e.getMessage());
                    }
                }
            });

            //Join channel for updates
            parseUtils = new ParseUtils(this, gameName);
            parseUtils.joinChannel();
            parseUtils.changeGameParticipation(true);

            //Get previously joined players
            parseUtils.fetchPreviouslyJoinedUsers(gameName, GameViewManagerActivity.this);
        }
    }

    private void initFragments() {
        // TODO: This custom data generation is temporary and for testing purposes only
        List<Card> cards = CardUtil.generateDeck(1, false).subList(0, 10);
        if (isEmpty(mPlayers)) {
            mPlayers = PlayerUtils.getPlayers(4);
        }

        playerViewFragment = PlayerViewFragment.newInstance(cards, cards);
        dealerViewFragment = DealerViewFragment.newInstance(mCards, null);
        chatAndLogFragment = ChatAndLogFragment.newInstance(1);

        if (isCurrentViewPlayer) {
            fab.setVisibility(View.GONE);
            replaceContainerFragment(playerViewFragment);
        } else {
            replaceContainerFragment(dealerViewFragment);
        }

        final View rootView = getWindow().getDecorView().getRootView();
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                PlayerViewHelper.addPlayers(GameViewManagerActivity.this, R.id.flGameContainer, mPlayers);
                if (dealerViewFragment != null) dealerViewFragment.addPlayers(mPlayers);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    rootView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    rootView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
            }
        });
    }

    private void replaceContainerFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.flGameContainer, fragment)
                .commit();
    }

    @OnClick(R.id.fab)
    public void switchView(View view) {
        if (showingPlayerFragment) {
            replaceContainerFragment(dealerViewFragment);
            showingPlayerFragment = false;
        } else {
            replaceContainerFragment(playerViewFragment);
            showingPlayerFragment = true;
        }
    }

    @OnClick(R.id.ibSettings)
    public void addNewPlayer() {
        // TODO: change for new player addition rather than for Settings
    }

    @OnClick(R.id.ibComment)
    public void toggleChatAndLogView(View v) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (!showingChat) {
            fragmentTransaction.replace(R.id.flLogContainer, chatAndLogFragment, FRAGMENT_CHAT_TAG);
            ibComment.setImageDrawable(ic_cancel);
            showingChat = true;
        } else {
            fragmentTransaction.remove(chatAndLogFragment);
            ibComment.setImageDrawable(ic_comment);
            showingChat = false;
        }
        fragmentTransaction.commit();
    }

    // TODO: may be use Dialog fragment and reuse that with other fragment when user leave??
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Exiting from game")
                .setMessage("Are you sure you want to exit from game?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        parseUtils.changeGameParticipation(false);
                        parseUtils.removeChannel();
                        parseUtils.deleteUserFromDb(gameName, User.getCurrentUser(GameViewManagerActivity.this));
                        ((FiftyTwoApplication) getApplication()).removeAllObservers();
                        finish();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    @Override
    public void onDealerOptionsShowing(boolean isDealerOptionShowing) {
        //Show and hide all players when dealer option is showing to un-clutter the view
        for (User player : mPlayers) {
            String playerTag = getPlayerFragmentTag(player);
            Fragment playerFragmentByTag = getSupportFragmentManager().findFragmentByTag(playerTag);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            if (isDealerOptionShowing) {
                transaction.hide(playerFragmentByTag);
            } else {
                transaction.show(playerFragmentByTag);
            }
            transaction.commit();
        }
    }

    @Override
    public boolean onDeal(List<Card> cards, User player) {
        Fragment playerFragment = getSupportFragmentManager().findFragmentByTag(getPlayerFragmentTag(player));
        if (playerFragment != null && !isEmpty(cards)) {
            boolean result = ((PlayerFragment) playerFragment).stackCards(cards);
            if (result) {
                parseUtils.dealCards(player, cards);
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
                // TODO: Handle Drop to Sink here
                return true;
            }
        }
        return false;
    }

    public void handleDeal(List<Card> cards, User from, User to) {
        // TODO: Need to add a condition to check whether the from player is a dealer
        if (!isEmpty(cards) && from != null && to != null) {
            if (isCurrentViewPlayer) {
                Fragment playerFragment = getSupportFragmentManager().findFragmentByTag(getPlayerFragmentTag(to));
                if (playerFragment != null) {
                    ((PlayerFragment) playerFragment).stackCards(cards);
                }
            }
            if (parseUtils.getCurrentUser().getUserId().equals(to.getUserId()) && playerViewFragment != null) {
                Fragment fragment = playerViewFragment.getChildFragmentManager().findFragmentByTag(PLAYER_TAG);
                if (fragment != null) {
                    ((CardsFragment) fragment).stackCards(cards);
                }
            }
        }
    }

    public void handleDealTable(User from, List<Card> cards) {
        // TODO: Need to add a condition to check whether the player is a dealer
        if (!isEmpty(cards) && from != null) {
            Fragment fragment = playerViewFragment.getChildFragmentManager().findFragmentByTag(TABLE_TAG);
            if (fragment != null) {
                ((CardsFragment) fragment).stackCards(cards);
            }
        }
    }

    @Override
    public void onNewLogEvent(String whoPosted, String details) {
        Log.d(Constants.TAG, GameViewManagerActivity.class.getSimpleName() + "--" + details + "--" + whoPosted);
        chatAndLogFragment.addNewLogEvent(whoPosted, details);
    }

    @Override
    public synchronized void onUpdate(final Observable o, final Object identifier, final Object arg) {
        String event = identifier.toString();
        Log.d(TAG, "onUpdate: " + event);

        switch (event) {
            case PARSE_NEW_PLAYER_ADDED:
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        User player = (User) arg;
                        addNewPlayerToUI(player);
                    }
                });
                break;
            case PARSE_PLAYER_LEFT:
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        User player = (User) arg;
                        String userViewTag = getPlayerFragmentTag(player);
                        Fragment userFragment = getSupportFragmentManager().findFragmentByTag(userViewTag);
                        if (userFragment != null) {
                            getSupportFragmentManager().beginTransaction().remove(userFragment).commitNow();
                        } else {
                            Log.e(TAG, "NULL Failed to remove view for " + userViewTag);
                        }
                        if (dealerViewFragment != null) dealerViewFragment.removePlayer((User) arg);
                        onNewLogEvent(player.getDisplayName(), player.getDisplayName() + " left.");
                    }
                });
                break;
            case PARSE_DEAL_CARDS:
                try {
                    JSONObject details = (JSONObject) arg;
                    User from = fromJson(details);
                    JSONObject toUserDetails = details.getJSONObject(PLAYER_TAG);
                    User to = fromJson(toUserDetails);
                    Gson gson = new Gson();
                    String cardString = gson.toJson(details.getString(PARAM_CARDS));
                    Log.d(TAG, "cardExchanged is -- " + cardString);
                    List<Card> cards = gson.fromJson(cardString, new TypeToken<List<Card>>() {}.getType());
                    handleDeal(cards, from, to);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case PARSE_DEAL_CARDS_TO_TABLE:
                try {
                    JSONObject details = (JSONObject) arg;
                    User from = fromJson(details);
                    Gson gson = new Gson();
                    String cardsString = gson.toJson(details.getString(PARAM_CARDS));
                    Log.d(TAG, "cardExchanged is--" + cardsString);
                    List<Card> cards = gson.fromJson(cardsString, new TypeToken<List<Card>>() {}.getType());
                    handleDealTable(from, cards);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    public void addNewPlayerToUI(User player) {
        PlayerViewHelper.addPlayer(GameViewManagerActivity.this, R.id.flGameContainer, player);
        if (dealerViewFragment != null) {
            dealerViewFragment.addPlayers(getList(player));
        }
        onNewLogEvent(player.getDisplayName(), player.getDisplayName() + " joined.");
    }

    @Override
    public void onPlay() {
        // Do Nothing
    }

    @Override
    public void onDealOptionSelected(Bundle bundle) {
        // Do Nothing
    }

    @Override
    public void onChat(ChatLog item) {
        // Do Nothing
    }
}