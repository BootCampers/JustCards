package org.bootcamp.fiftytwo.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.bootcamp.fiftytwo.R;
import org.bootcamp.fiftytwo.models.User;
import org.bootcamp.fiftytwo.utils.Constants;
import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static org.bootcamp.fiftytwo.utils.AppUtils.loadRoundedImage;
import static org.bootcamp.fiftytwo.utils.Constants.PARAM_USER;

public class SelectGameActivity extends AppCompatActivity {

    @BindView(R.id.btnJoinGame) Button btnJoinGame;
    @BindView(R.id.btnCreateGame) Button btnCreateGame;
    @BindView(R.id.etGameName) EditText etGameName;
    @BindView(R.id.tvWelcome) TextView tvWelcome;
    @BindView(R.id.ivAvatar) ImageView ivAvatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_game);
        ButterKnife.bind(this);

        User user = Parcels.unwrap(getIntent().getParcelableExtra(PARAM_USER));
        tvWelcome.setText("Welcome " + user.getDisplayName() + "!");
        loadRoundedImage(this, ivAvatar, user.getAvatarUri());
    }

    @OnClick(R.id.btnJoinGame)
    public void join(final View view) {
        if (etGameName.getText().toString().isEmpty()) {
            Snackbar.make(view, "Please enter ID of the game you would like to join...", Snackbar.LENGTH_LONG).show();
        } else {
            Intent gameViewManagerIntent = new Intent(SelectGameActivity.this, GameViewManagerActivity.class);
            gameViewManagerIntent.putExtra(Constants.PARAM_GAME_NAME, etGameName.getText().toString().trim());
            gameViewManagerIntent.putExtra(Constants.PARAM_CURRENT_VIEW_PLAYER, true); // if false then it's dealer
            startActivity(gameViewManagerIntent);
        }
    }

    @OnClick(R.id.btnCreateGame)
    public void create() {
        Intent intent = new Intent(this, CreateGameActivity.class);
        startActivity(intent);
    }

}