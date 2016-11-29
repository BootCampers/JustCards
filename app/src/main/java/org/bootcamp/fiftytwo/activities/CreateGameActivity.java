package org.bootcamp.fiftytwo.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.bootcamp.fiftytwo.R;
import org.bootcamp.fiftytwo.models.Card;
import org.bootcamp.fiftytwo.network.ParseUtils;
import org.bootcamp.fiftytwo.utils.Constants;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static org.bootcamp.fiftytwo.utils.AppUtils.getParcelable;
import static org.bootcamp.fiftytwo.utils.AppUtils.showSnackBar;
import static org.bootcamp.fiftytwo.utils.Constants.DISPLAY_NAME;
import static org.bootcamp.fiftytwo.utils.Constants.PARAM_CARDS;
import static org.bootcamp.fiftytwo.utils.Constants.REQ_CODE_SELECT_CARDS;

public class CreateGameActivity extends AppCompatActivity implements ParseUtils.OnGameExistsListener {

    private List<Card> mCards = new ArrayList<>();
    private ParseUtils parseUtils;

    @BindView(R.id.tvGameNumber) TextView tvGameNumber;
    @BindView(R.id.tvGameNumberLabel) TextView tvGameNumberLabel;
    @BindView(R.id.btnGameOptions) Button btnGameOptions;
    @BindView(R.id.btnShareId)
    FloatingActionButton btnShareId;

    @BindString(R.string.select_cards) String str_select_cards;
    @BindString(R.string.start_game) String str_start_game;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_game);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);

        initializeWidgets();
    }

    private void initializeWidgets() {
        int gameNumber = new Random().nextInt(99999);
        String gameNumberString = String.format(Locale.getDefault(), "%05d", gameNumber);
        parseUtils = new ParseUtils(this, gameNumberString);
        parseUtils.checkGameExists(gameNumberString, this);
        tvGameNumber.setText(gameNumberString);
        tvGameNumberLabel.setText(getIntent().getStringExtra(DISPLAY_NAME) + ", here is your Game ID:");
    }

   @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE_SELECT_CARDS && resultCode == RESULT_OK) {
            mCards = Parcels.unwrap(data.getExtras().getParcelable(PARAM_CARDS));
            Toast.makeText(this, "Selected Total: " + mCards.size() + " Cards", Toast.LENGTH_SHORT).show();
        }

       //Change select cards to start game
        if (mCards == null || mCards.size() == 0){
            btnGameOptions.setText(str_select_cards);
        } else {
            btnGameOptions.setText(str_start_game);
        }
    }

    @OnClick(R.id.btnGameOptions)
    public void startGame(View view) {
        if (mCards == null || mCards.size() == 0) {
            Intent intent = new Intent(this, SelectCardsActivity.class);
            startActivityForResult(intent, REQ_CODE_SELECT_CARDS);
        } else {
            if (TextUtils.isEmpty(tvGameNumber.getText())) {
                showSnackBar(getApplicationContext(), view, "Please enter game name first");
            } else {
                Intent gameViewManagerIntent = new Intent(this, GameViewManagerActivity.class);
                gameViewManagerIntent.putExtra(Constants.PARAM_GAME_NAME, tvGameNumber.getText().toString());
                gameViewManagerIntent.putExtra(Constants.PARAM_CARDS, getParcelable(mCards));
                gameViewManagerIntent.putExtra(Constants.PARAM_CURRENT_VIEW_PLAYER, false); // go to dealer view by default
                startActivity(gameViewManagerIntent);
                finish();
            }
        }
    }

    @Override
    public void onGameExistsResult(final boolean result) {
        if (result) {
            int gameNumber = new Random().nextInt(99999);
            String gameNumberString = String.format(Locale.getDefault(), "%05d", gameNumber);
            parseUtils.checkGameExists(gameNumberString, this);
            tvGameNumber.setText(gameNumberString);
        }
    }
}