package org.bootcamp.fiftytwo.activities;

import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
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

import org.bootcamp.fiftytwo.R;
import org.bootcamp.fiftytwo.application.FiftyTwoApplication;
import org.bootcamp.fiftytwo.fragments.CardsFragment;
import org.bootcamp.fiftytwo.fragments.ChatAndLogFragment;
import org.bootcamp.fiftytwo.fragments.DealerViewFragment;
import org.bootcamp.fiftytwo.fragments.PlayerFragment;
import org.bootcamp.fiftytwo.fragments.PlayerViewFragment;
import org.bootcamp.fiftytwo.interfaces.Observable;
import org.bootcamp.fiftytwo.interfaces.Observer;
import org.bootcamp.fiftytwo.models.Card;
import org.bootcamp.fiftytwo.models.ChatLog;
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
import static org.bootcamp.fiftytwo.utils.AppUtils.isEmpty;
import static org.bootcamp.fiftytwo.utils.Constants.*;
import static org.bootcamp.fiftytwo.utils.Constants.PARAM_CARDS;
import static org.bootcamp.fiftytwo.utils.Constants.PARAM_GAME_NAME;
import static org.bootcamp.fiftytwo.utils.Constants.PARSE_NEW_PLAYER_ADDED;
import static org.bootcamp.fiftytwo.utils.Constants.PARSE_PLAYERS_EXCHANGE_CARDS;
import static org.bootcamp.fiftytwo.utils.Constants.PARSE_PLAYER_LEFT;
import static org.bootcamp.fiftytwo.utils.Constants.PARSE_TABLE_CARD_EXCHANGE;
import static org.bootcamp.fiftytwo.utils.Constants.PLAYER_TAG;
import static org.bootcamp.fiftytwo.views.PlayerViewHelper.getPlayerFragmentTag;

public class GameViewManagerActivity extends AppCompatActivity implements
        PlayerViewFragment.OnPlayerFragmentInteractionListener,
        ChatAndLogFragment.OnListFragmentInteractionListener,
        DealerViewFragment.OnDealListener,
        CardsFragment.OnLogEventListener,
        Observer {

    private List<User> mPlayers = new ArrayList<>();
    private List<Card> mCards;
    private ParseUtils parseUtils;
    private FiftyTwoApplication application;

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

        application = (FiftyTwoApplication) getApplication();
        application.addObserver(this);
    }

    private void initGameParams() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String gameName = bundle.getString(PARAM_GAME_NAME);
            isCurrentViewPlayer = bundle.getBoolean(PARAM_CURRENT_VIEW_PLAYER);
            mCards = Parcels.unwrap(bundle.getParcelable(PARAM_CARDS));
            Toast.makeText(getApplicationContext(), "Joining " + gameName, Toast.LENGTH_SHORT).show();

            parseUtils = new ParseUtils(this, gameName);
            parseUtils.joinChannel();
            // TODO get self details
            parseUtils.changeGameParticipation(true);
        }
    }

    private void initFragments() {
        playerViewFragment = new PlayerViewFragment();
        dealerViewFragment = DealerViewFragment.newInstance(mCards, mPlayers);
        chatAndLogFragment = new ChatAndLogFragment();

        if (isCurrentViewPlayer) {
            fab.setVisibility(View.GONE);
            replaceContainerFragment(playerViewFragment);
        } else {
            replaceContainerFragment(dealerViewFragment);
        }

        // TODO: This is only for temporary purposes.
        if (isEmpty(mPlayers)) {
            mPlayers = PlayerUtils.getPlayers(4);
        }

        final View rootView = getWindow().getDecorView().getRootView();
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                /*PlayerViewHelper.addPlayers(GameViewManagerActivity.this, R.id.flGameContainer, mPlayers);
                if (dealerViewFragment != null) dealerViewFragment.addPlayers(mPlayers);*/
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

    @Override
    public void onBackPressed() {
        parseUtils.tableCardExchange(new User("", "dummyName", "dummyId"), CardUtil.generateDeck(1, false).get(0), false);
        // TODO: may be use Dialog fragment and reuse that with other fragment when user leave??
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Exiting from game")
                .setMessage("Are you sure you want to exit from game?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        parseUtils.changeGameParticipation(false);
                        parseUtils.removeChannel();
                        ((FiftyTwoApplication) getApplication()).removeAllObservers();
                        finish();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    @Override
    public boolean onDeal(List<Card> cards, User player) {
        // TODO: Fire Off Parse Push Here
        Fragment playerFragment = getSupportFragmentManager().findFragmentByTag(getPlayerFragmentTag(player));
        return playerFragment != null && !isEmpty(cards) && ((PlayerFragment) playerFragment).stackCards(cards);
    }

    @Override
    public void onNewLogEvent(String whoPosted, String details) {
        Log.d(Constants.TAG, GameViewManagerActivity.class.getSimpleName() + "--" + details + "--" + whoPosted);
        chatAndLogFragment.addNewLogEvent(whoPosted, details);
    }

    @Override
    public void onPlayerFragmentInteraction(Uri uri) {
    }

    @Override
    public void onListFragmentInteraction(ChatLog item) {
    }

    @Override
    public void onUpdate(final Observable o, final Object identifier, final Object arg) {
        String event = identifier.toString();
        Log.d(TAG, "onUpdate: " + event);

        switch (event) {
            case PARSE_NEW_PLAYER_ADDED:
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        PlayerViewHelper.addPlayer(GameViewManagerActivity.this, R.id.flGameContainer, (User) arg);
                        if (dealerViewFragment != null) dealerViewFragment.addPlayers(mPlayers);
                        //TODO: Add to the log
                    }
                });
                break;
            case PARSE_PLAYER_LEFT:
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String userViewTag = getPlayerFragmentTag((User) arg);
                        Fragment userFragment = getSupportFragmentManager().findFragmentByTag(userViewTag);
                        if (userFragment != null) {
                            getSupportFragmentManager().beginTransaction().remove(userFragment).commitNow();
                        } else {
                            Log.e(TAG, "NULL Failed to remove view for " + userViewTag);
                        }
                        //TODO: Add to the log
                    }
                });
                break;
            case PARSE_PLAYERS_EXCHANGE_CARDS:
                try {
                    JSONObject details = (JSONObject) arg;
                    User fromUser = fromJson(details);
                    JSONObject toUserDetails = details.getJSONObject(PLAYER_TAG);
                    User toUser = fromJson(toUserDetails);
                    Gson gson = new Gson();
                    String cardString;
                    cardString = gson.toJson(details.getString(PARAM_CARDS));
                    Log.d(TAG, "cardExchanged is--" + cardString);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case PARSE_TABLE_CARD_EXCHANGE:
                try {
                    JSONObject details = (JSONObject) arg;
                    User fromUser = fromJson(details);
                    JSONObject toUserDetails = details.getJSONObject(PLAYER_TAG);
                    User toUser = fromJson(toUserDetails);
                    Gson gson = new Gson();
                    String cardString;
                    cardString = gson.toJson(details.getString(PARAM_CARDS));
                    boolean pickedOrPlacedOnTable = details.getBoolean(TABLE_PICKED);
                    Log.d(TAG, "cardExchanged is--" + cardString);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

}