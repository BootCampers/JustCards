package org.bootcamp.fiftytwo.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import org.bootcamp.fiftytwo.R;

public class CreateJoinGameActivity extends AppCompatActivity {

    Button joinGameButton;
    Button createGameButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_join_game);

        joinGameButton = (Button) findViewById(R.id.joinGameButton);
        createGameButton = (Button) findViewById(R.id.createGameButton);

        joinGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CreateJoinGameActivity.this, GameViewManagerActivity.class));
            }
        });

        createGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CreateJoinGameActivity.this, CreateGameActivity.class));
            }
        });
    }
}
