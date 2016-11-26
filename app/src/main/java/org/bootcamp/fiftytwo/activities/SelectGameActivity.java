package org.bootcamp.fiftytwo.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;

import org.bootcamp.fiftytwo.R;
import org.bootcamp.fiftytwo.interfaces.Observable;
import org.bootcamp.fiftytwo.interfaces.Observer;
import org.bootcamp.fiftytwo.utils.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;

import static org.bootcamp.fiftytwo.utils.Constants.DISPLAY_NAME;
import static org.bootcamp.fiftytwo.utils.Constants.USER_AVATAR_URI;

public class SelectGameActivity extends AppCompatActivity implements Observer {

    @BindView(R.id.joinGameButton) Button joinGameButton;
    @BindView(R.id.createGameButton) Button createGameButton;
    @BindView(R.id.etGameName) EditText etGameName;
    @BindView(R.id.welcomeText) TextView welcomeText;
    @BindView(R.id.userAvatar) ImageView avatarImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_game);
        ButterKnife.bind(this);

        Bundle extras = getIntent().getExtras();

        welcomeText.setText("Welcome " + extras.get(DISPLAY_NAME) + "!");
        Glide.with(this).load(extras.get(USER_AVATAR_URI))
                .asBitmap()
                .centerCrop()
                .into(new BitmapImageViewTarget(avatarImageView) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable =
                                RoundedBitmapDrawableFactory.create(getResources(), resource);
                        circularBitmapDrawable.setCircular(true);
                        avatarImageView.setImageDrawable(circularBitmapDrawable);
                    }
                });

        joinGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etGameName.getText().toString().isEmpty()) {
                    Snackbar.make(view, "Please enter ID of the game you would like to join...", Snackbar.LENGTH_LONG).show();
                } else {
                    Intent gameViewManagerIntent = new Intent(SelectGameActivity.this, GameViewManagerActivity.class);
                    gameViewManagerIntent.putExtra(Constants.PARAM_GAME_NAME, etGameName.getText().toString().trim());
                    gameViewManagerIntent.putExtra(Constants.PARAM_CURRENT_VIEW_PLAYER, true); //if false then it's dealer
                    startActivity(gameViewManagerIntent);
                }
            }
        });

        createGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SelectGameActivity.this, CreateGameActivity.class));
            }
        });
    }

    @Override
    public void onUpdate(Observable o, Object arg, Object arg1) {
        String qualifier = (String) arg;
    }

}