package org.bootcamp.fiftytwo.activities;

import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.Toast;

import org.bootcamp.fiftytwo.R;
import org.bootcamp.fiftytwo.fragments.CardsFragment;
import org.bootcamp.fiftytwo.fragments.ChatAndLogFragment;
import org.bootcamp.fiftytwo.fragments.DealerViewFragment;
import org.bootcamp.fiftytwo.fragments.PlayerViewFragment;
import org.bootcamp.fiftytwo.interfaces.Observable;
import org.bootcamp.fiftytwo.interfaces.Observer;
import org.bootcamp.fiftytwo.models.ChatLog;
import org.bootcamp.fiftytwo.models.User;
import org.bootcamp.fiftytwo.network.ParseUtils;
import org.bootcamp.fiftytwo.utils.CardUtil;
import org.bootcamp.fiftytwo.utils.Constants;

import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class GameViewManagerActivity extends AppCompatActivity implements
        PlayerViewFragment.OnPlayerFragmentInteractionListener,
        ChatAndLogFragment.OnListFragmentInteractionListener,
        DealerViewFragment.OnDealerListener,
        CardsFragment.OnLogEventListener,
        Observer {

    @BindView(R.id.ibComment) ImageButton ibComment;
    @BindView(R.id.ibSettings) ImageButton ibSettings;
    @BindView(R.id.toolbar) Toolbar toolbar;

    @BindDrawable(R.drawable.ic_cancel) Drawable ic_cancel;
    @BindDrawable(R.drawable.ic_comment) Drawable ic_comment;

    private PlayerViewFragment playerViewFragment;
    private DealerViewFragment dealerViewFragment;
    private ChatAndLogFragment chatAndLogFragment;

    private boolean showingChat = false;
    private boolean showingPlayerFragment = true; //false is showing dealer fragment
    private String gameName;

    private ParseUtils parseUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_view_manager);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        //TODO: check if he is a dealer or not and hide fab accordingly
        playerViewFragment = new PlayerViewFragment();
        chatAndLogFragment = new ChatAndLogFragment();
        dealerViewFragment = new DealerViewFragment();

        boolean isCurrentViewPlayer = true;
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            isCurrentViewPlayer = bundle.getBoolean(Constants.CURRENT_VIEW_PLAYER);
            gameName = bundle.getString(Constants.GAME_NAME);
            Toast.makeText(getApplicationContext(), "Joining " + gameName, Toast.LENGTH_SHORT).show();
            parseUtils = new ParseUtils(this, gameName);
            parseUtils.joinChannel();
            //TODO get self details
            parseUtils.changeGameParticipation(true);
        }
        //Set PlayerView as parent fragment
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (isCurrentViewPlayer) {
            fragmentTransaction.replace(R.id.flGameContainer, playerViewFragment);
        } else {
            fragmentTransaction.replace(R.id.flGameContainer, dealerViewFragment);
        }
        fragmentTransaction.commit();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (showingPlayerFragment) {
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.flGameContainer, dealerViewFragment);
                    fragmentTransaction.commit();
                    showingPlayerFragment = false;
                } else {
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.flGameContainer, playerViewFragment);
                    fragmentTransaction.commit();
                    showingPlayerFragment = true;
                }
            }
        });
    }

    @Override
    public void onPlayerFragmentInteraction(Uri uri) {

    }

    //TODO: change for new player addition rather than for Settings
    @OnClick(R.id.ibSettings)
    public void addNewPlayer() {
        //playerViewFragment.changeGameParticipation(User.getDummyPlayer());
        //if(parseUtils != null){
          //  parseUtils.changeGameParticipation(User.getDummyPlayers(1).get(0));
        //}
    }

    @OnClick(R.id.ibComment)
    public void toggleChatAndLogView(View v) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (!showingChat) {
            fragmentTransaction.replace(R.id.flLogContainer, chatAndLogFragment, Constants.FRAGMENT_CHAT_TAG);
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
    public void onNewLogEvent(String whoPosted, String details) {
        Log.d(Constants.TAG, GameViewManagerActivity.class.getSimpleName() + "--" + details + "--" + whoPosted);
        chatAndLogFragment.addNewLogEvent(whoPosted, details);
    }

    @Override
    public void onListFragmentInteraction(ChatLog item) {

    }

    @Override
    public void onDeal() {
        // Do Nothing for now
    }

    @Override
    public void onBackPressed() {
        parseUtils.exchangeCard(User.getDummyPlayers(1).get(0), CardUtil.generateDeck(1, false).get(0));
        //TODO: may be use Dailog fragment and resue that with other fragment when user leave??
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Exiting from game")
                .setMessage("Are you sure you want to exit from game?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        parseUtils.changeGameParticipation(false);
                        parseUtils.removeChannel();
                        finish();
                    }

                })
                .setNegativeButton("No", null)
                .show();
    }

    @Override
    public void onUpdate(Observable o, Object identifier, Object arg) {
        String qualifier = (String) arg.toString();
        Log.d(Constants.TAG, "GameViewManager-onUpdate-" + identifier);
    }
}