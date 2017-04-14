package org.justcards.android.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import org.justcards.android.R;
import org.justcards.android.models.User;
import org.justcards.android.db.GameDB;
import org.justcards.android.utils.AnimationUtilsJC;
import org.justcards.android.utils.Constants;
import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static org.justcards.android.utils.AppUtils.loadRoundedImage;
import static org.justcards.android.utils.AppUtils.showSnackBar;
import static org.justcards.android.utils.Constants.DISPLAY_NAME;
import static org.justcards.android.utils.Constants.PARAM_USER;

public class SelectGameActivity extends AppCompatActivity implements GameDB.OnGameExistsListener {

    private User user = null;

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.tvWelcome) TextView tvWelcome;
    @BindView(R.id.tvAskUser) TextView tvAskUser;
    @BindView(R.id.ivAvatar) ImageView ivAvatar;
    @BindView(R.id.etGameName) EditText etGameName;
    @BindView(R.id.btnJoinGame) Button btnJoinGame;
    @BindView(R.id.btnCreateGame) Button btnCreateGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_game);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        if (getIntent() != null) {
            user = Parcels.unwrap(getIntent().getParcelableExtra(PARAM_USER));
        }
        if (user == null) {
            user = User.get(this);
        }

        assert user != null;
        tvWelcome.setText("Welcome " + user.getDisplayName() + "!");
        loadRoundedImage(this, ivAvatar, user.getAvatarUri());
        AnimationUtilsJC.animateCircularReveal(tvWelcome, 500);
        AnimationUtilsJC.animateCircularReveal(tvAskUser, 500);
        etGameName.clearFocus();
    }

    @OnClick(R.id.btnJoinGame)
    public void join(final View view) {
        if (etGameName.getText().toString().isEmpty()) {
            showSnackBar(getApplicationContext(), view, "Please enter ID of the game you would like to join...");
        } else {
            String gameName = etGameName.getText().toString();
            GameDB.checkExists(gameName, this);
        }
    }

    @OnClick(R.id.btnCreateGame)
    public void create() {
        Intent intent = new Intent(this, CreateGameActivity.class);
        intent.putExtra(DISPLAY_NAME, user.getDisplayName());
        startActivity(intent);
        AnimationUtilsJC.enterVineTransition(this);
    }

    @Override
    public void onGameExistsResult(boolean result) {
        if (result) {
            Intent gameViewManagerIntent = new Intent(this, GameViewManagerActivity.class);
            gameViewManagerIntent.putExtra(Constants.PARAM_GAME_NAME, etGameName.getText().toString().trim());
            gameViewManagerIntent.putExtra(Constants.PARAM_CURRENT_VIEW_PLAYER, true); // if false then it's dealer
            startActivity(gameViewManagerIntent);
            AnimationUtilsJC.enterZoomTransition(this);
        } else {
            new LovelyStandardDialog(this)
                    .setTopColorRes(R.color.colorPrimary)
                    .setButtonsColorRes(R.color.colorAccent)
                    .setIcon(R.drawable.ic_not_interested_36dp)
                    .setTitle("Game invalid")
                    .setMessage("This game id not found. Either create new game or enter a valid id.")
                    .setPositiveButton(R.string.msg_okay, v -> {
                    })
                    .show();
        }
    }
}