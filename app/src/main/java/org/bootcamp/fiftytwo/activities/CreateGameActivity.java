package org.bootcamp.fiftytwo.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.bootcamp.fiftytwo.R;
import org.bootcamp.fiftytwo.models.Card;
import org.bootcamp.fiftytwo.utils.Constants;
import org.parceler.Parcels;

import java.util.List;
import java.util.Locale;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static org.bootcamp.fiftytwo.R.id.etGameName;
import static org.bootcamp.fiftytwo.utils.Constants.PARAM_CARDS;
import static org.bootcamp.fiftytwo.utils.Constants.REQ_CODE_SELECT_CARDS;

public class CreateGameActivity extends AppCompatActivity {

    @BindView(R.id.gameIDNumber)
    TextView gameIdNumber;
    @BindView(R.id.btnStartGame) Button btnStartGame;
    @BindView(R.id.btnSelectCards) Button btnSelectCards;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_game);
        ButterKnife.bind(this);

        int gameId = new Random().nextInt(99999);
        String gameIDString = String.format(Locale.getDefault(), "%05d", gameId);
        gameIdNumber.setText(gameIDString);
    }

    @OnClick(R.id.btnSelectCards)
    public void selectCards() {
        Intent intent = new Intent(this, SelectCardsActivity.class);
        startActivityForResult(intent, REQ_CODE_SELECT_CARDS);
    }

    @OnClick(R.id.btnStartGame)
    public void startGame(View view) {
        if(gameIdNumber.getText() != null) {

            Intent gameViewManagerIntent = new Intent(CreateGameActivity.this, GameViewManagerActivity.class);
            gameViewManagerIntent.putExtra(Constants.GAME_NAME, gameIdNumber.getText().toString());
            gameViewManagerIntent.putExtra(Constants.CURRENT_VIEW_PLAYER, false); //do to dealer view by default
            startActivity(gameViewManagerIntent);

            finish();

        } else {
            Snackbar.make(view, "Please enter game name first", Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE_SELECT_CARDS && resultCode == RESULT_OK) {
            List<Card> cards = Parcels.unwrap(data.getExtras().getParcelable(PARAM_CARDS));
            Toast.makeText(this, "got: " + cards.size(), Toast.LENGTH_SHORT).show();
        }
    }
}