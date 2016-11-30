package org.bootcamp.fiftytwo.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.bootcamp.fiftytwo.R;
import org.bootcamp.fiftytwo.models.User;
import org.bootcamp.fiftytwo.network.ParseUtils;
import org.bootcamp.fiftytwo.utils.Constants;
import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static org.bootcamp.fiftytwo.utils.AppUtils.loadRoundedImage;
import static org.bootcamp.fiftytwo.utils.Constants.DISPLAY_NAME;
import static org.bootcamp.fiftytwo.utils.Constants.PARAM_USER;

public class SelectGameActivity extends AppCompatActivity implements ParseUtils.OnGameExistsListener {

    @BindView(R.id.btnJoinGame) Button btnJoinGame;
    @BindView(R.id.btnCreateGame) Button btnCreateGame;
    @BindView(R.id.etGameName) EditText etGameName;
    @BindView(R.id.tvWelcome) TextView tvWelcome;
    @BindView(R.id.ivAvatar) ImageView ivAvatar;
    User user = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_game);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);

        user = Parcels.unwrap(getIntent().getParcelableExtra(PARAM_USER));
        tvWelcome.setText("Welcome " + user.getDisplayName() + "!");
        loadRoundedImage(this, ivAvatar, user.getAvatarUri());
    }

    @OnClick(R.id.btnJoinGame)
    public void join(final View view) {
        /*if (etGameName.getText().toString().isEmpty()) {
            showSnackBar(getApplicationContext(), view, "Please enter ID of the game you would like to join...");
        } else {
            String gameName = etGameName.getText().toString();
            ParseUtils parseUtils = new ParseUtils(this, gameName);
            parseUtils.checkGameExists(gameName, this);
        }*/
        startActivity(new Intent(SelectGameActivity.this, TutorialActivity.class));
    }

    @OnClick(R.id.btnCreateGame)
    public void create() {
        Intent intent = new Intent(this, CreateGameActivity.class);
        intent.putExtra(DISPLAY_NAME, user.getDisplayName());
        startActivity(intent);
    }

    @Override
    public void onGameExistsResult(boolean result) {
        if (result) {
            Intent gameViewManagerIntent = new Intent(this, GameViewManagerActivity.class);
            gameViewManagerIntent.putExtra(Constants.PARAM_GAME_NAME, etGameName.getText().toString().trim());
            gameViewManagerIntent.putExtra(Constants.PARAM_CURRENT_VIEW_PLAYER, true); // if false then it's dealer
            startActivity(gameViewManagerIntent);
        } else {
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Game invalid")
                    .setMessage("This game id not found. Either create new game or enter a valid id.")
                    .setPositiveButton("Okay", (dialog, which) -> {
                    })
                    .show();
        }
    }
}