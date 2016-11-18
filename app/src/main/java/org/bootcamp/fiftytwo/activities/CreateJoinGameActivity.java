package org.bootcamp.fiftytwo.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.Button;

import org.bootcamp.fiftytwo.R;
import org.bootcamp.fiftytwo.adapters.GamesListAdapter;
import org.bootcamp.fiftytwo.application.ChatApplication;
import org.bootcamp.fiftytwo.interfaces.Observable;
import org.bootcamp.fiftytwo.interfaces.Observer;
import org.bootcamp.fiftytwo.services.AllJoynService;
import org.bootcamp.fiftytwo.utils.Constants;
import org.bootcamp.fiftytwo.utils.DividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CreateJoinGameActivity extends AppCompatActivity
    implements Observer{

    @BindView(R.id.joinGameButton)
    Button joinGameButton;
    @BindView(R.id.createGameButton)
    Button createGameButton;
    @BindView(R.id.rvGamesAroundList)
    RecyclerView rvGamesAroundList;

    List<String> gamesList = new ArrayList<>();
    private StaggeredGridLayoutManager staggeredLayoutManager;
    private GamesListAdapter gamesListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_join_game);
        ButterKnife.bind(this);

        fetchChannelList();

        gamesListAdapter = new GamesListAdapter(this, gamesList);
        staggeredLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        rvGamesAroundList.setLayoutManager(staggeredLayoutManager);
        rvGamesAroundList.setAdapter(gamesListAdapter);
        RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST);
        rvGamesAroundList.addItemDecoration(itemDecoration);

        joinGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //startActivity(new Intent(CreateJoinGameActivity.this, GameViewManagerActivity.class));
                if(gamesListAdapter.getSelectedChannelName() == null){
                    Snackbar.make(view, "Please select a game from list first", Snackbar.LENGTH_LONG).show();
                } else {
                    String gameName = gamesListAdapter.getSelectedChannelName();

                    Intent gameViewManagerIntent = new Intent(CreateJoinGameActivity.this, GameViewManagerActivity.class);
                    gameViewManagerIntent.putExtra(Constants.GAME_NAME, gameName);
                    gameViewManagerIntent.putExtra(Constants.CURRENT_VIEW_PLAYER, true); //if false then it's dealer
                    startActivity(gameViewManagerIntent);
                }
            }
        });

        createGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CreateJoinGameActivity.this, CreateGameActivity.class));
            }
        });
    }

    @Override
    public void update(Observable o, Object arg) {
        String qualifier = (String)arg;

        if(qualifier.equals(Constants.CHANNEL_LIST_CHANGED)){
            fetchChannelList();
            gamesListAdapter.notifyDataSetChanged();
        }
    }

    private void fetchChannelList(){
        List<String> channels = ((ChatApplication)getApplication()).getFoundChannels();
        for (String channel : channels) {
            if (channel.length() > AllJoynService.NAME_PREFIX.length() + 1) {
                gamesList.add(channel.substring(AllJoynService.NAME_PREFIX.length() + 1));
            }
        }
    }
}
