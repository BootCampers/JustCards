package org.bootcamp.fiftytwo.activities;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.Toast;

import org.bootcamp.fiftytwo.R;
import org.bootcamp.fiftytwo.fragments.CardsListFragment;
import org.bootcamp.fiftytwo.fragments.ChatAndLogFragment;
import org.bootcamp.fiftytwo.fragments.DealerViewFragment;
import org.bootcamp.fiftytwo.fragments.PlayerViewFragment;
import org.bootcamp.fiftytwo.interfaces.Observable;
import org.bootcamp.fiftytwo.interfaces.Observer;
import org.bootcamp.fiftytwo.models.ChatLog;
import org.bootcamp.fiftytwo.models.User;
import org.bootcamp.fiftytwo.utils.Constants;

import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class GameViewManagerActivity extends AppCompatActivity implements
        PlayerViewFragment.OnPlayerFragmentInteractionListener,
        ChatAndLogFragment.OnListFragmentInteractionListener,
        DealerViewFragment.OnDealerFragmentInteractionListener,
        CardsListFragment.OnLogEventListener,
        Observer{

    @BindView(R.id.ibComment) ImageButton ibComment;
    @BindView(R.id.ibSettings) ImageButton ibSettings;
    @BindView(R.id.toolbar) Toolbar toolbar;

    @BindDrawable(R.drawable.ic_cancel) Drawable ic_cancel;
    @BindDrawable(R.drawable.ic_comment) Drawable ic_comment;

    PlayerViewFragment playerViewFragment;
    ChatAndLogFragment chatAndLogFragment;


    private boolean showingChat = false;
    private boolean showingPlayerFragment = true; //false is showing dealer fragment
    private DealerViewFragment dealerViewFragment;

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
        if(bundle != null) {
            isCurrentViewPlayer = bundle.getBoolean(Constants.CURRENT_VIEW_PLAYER);
            String gameName = bundle.getString(Constants.GAME_NAME);
            Toast.makeText(getApplicationContext(), "Joining " + gameName, Toast.LENGTH_SHORT).show();
        }
        //Set PlayerView as parent fragment
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if(isCurrentViewPlayer == true) {
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
        playerViewFragment.addNewPlayer(new User(true, "", "Ankit", 0));
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
    public void onDealerFragmentInteraction(Uri uri) {

    }

    @Override
    public void update(Observable o, Object arg) {
        String qualifier = (String)arg;
        Log.d(Constants.TAG, "GameViewManager " + qualifier);
    }
}