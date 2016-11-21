package org.bootcamp.fiftytwo.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.bootcamp.fiftytwo.R;
import org.bootcamp.fiftytwo.interfaces.Observable;
import org.bootcamp.fiftytwo.interfaces.Observer;
import org.bootcamp.fiftytwo.utils.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CreateJoinGameActivity extends AppCompatActivity
    implements Observer{

    @BindView(R.id.joinGameButton)
    Button joinGameButton;
    @BindView(R.id.createGameButton)
    Button createGameButton;
    @BindView(R.id.etGameName)
    EditText etGameName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_join_game);
        ButterKnife.bind(this);

        joinGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(etGameName.getText() == null){
                    Snackbar.make(view, "Please select a game from list first", Snackbar.LENGTH_LONG).show();
                } else {
                    Intent gameViewManagerIntent = new Intent(CreateJoinGameActivity.this, GameViewManagerActivity.class);
                    gameViewManagerIntent.putExtra(Constants.GAME_NAME, etGameName.getText().toString().trim());
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
    public void onUpdate(Observable o, Object arg) {
        String qualifier = (String)arg;
    }

}
