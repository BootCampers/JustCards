package org.bootcamp.fiftytwo.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.bootcamp.fiftytwo.R;
import org.bootcamp.fiftytwo.application.ChatApplication;
import org.bootcamp.fiftytwo.models.Card;
import org.parceler.Parcels;

import java.util.List;
import java.util.Locale;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static org.bootcamp.fiftytwo.utils.Constants.PARAM_CARDS;
import static org.bootcamp.fiftytwo.utils.Constants.REQ_CODE_SELECT_CARDS;

public class CreateGameActivity extends AppCompatActivity {

    @BindView(R.id.gameIDNumber) TextView gameIdNumber;
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

        //Set game name and do init
        ((ChatApplication)getApplication()).hostSetChannelName("ankitbaphna");
        ((ChatApplication)getApplication()).hostInitChannel();
    }

    @OnClick(R.id.btnSelectCards)
    public void selectCards() {
        Intent intent = new Intent(this, SelectCardsActivity.class);
        startActivityForResult(intent, REQ_CODE_SELECT_CARDS);
    }

    @OnClick(R.id.btnStartGame)
    public void startGame() {
        //start hosting
        ((ChatApplication)getApplication()).hostStartChannel();
        startActivity(new Intent(CreateGameActivity.this, GameViewManagerActivity.class));
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