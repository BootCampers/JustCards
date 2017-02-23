package org.justcards.android.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.justcards.android.R;
import org.justcards.android.models.Card;
import org.justcards.android.network.FirebaseDB;
import org.justcards.android.utils.AnimationUtils;
import org.justcards.android.utils.Constants;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static org.justcards.android.utils.AppUtils.getParcelable;
import static org.justcards.android.utils.AppUtils.isEmpty;
import static org.justcards.android.utils.AppUtils.showSnackBar;
import static org.justcards.android.utils.Constants.PARAM_CARDS;
import static org.justcards.android.utils.Constants.REQ_CODE_SELECT_CARDS;

public class CreateGameActivity extends AppCompatActivity implements FirebaseDB.OnGameExistsListener {

    private List<Card> mCards = new ArrayList<>();
    private String gameNumberString;

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.tvGameNumber) TextView tvGameNumber;
    @BindView(R.id.fabShareGameId) FloatingActionButton fabShareGameId;
    @BindView(R.id.rlSelectedCards) RelativeLayout rlSelectedCards;
    @BindView(R.id.tvSelectedCards) TextView tvSelectedCards;
    @BindView(R.id.btnCreateGame) Button btnCreateGame;

    @BindString(R.string.select_cards) String msgSelectCards;
    @BindString(R.string.start_game) String msgStartGame;
    @BindString(R.string.msg_selected_cards_count) String msgSelectedCardsCount;
    @BindString(R.string.msg_enter_game_number) String msgEnterGameNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_game);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initializeWidgets();
    }

    private void initializeWidgets() {
        int gameNumber = new Random().nextInt(99999);
        gameNumberString = String.format(Locale.getDefault(), "%05d", gameNumber);
        FirebaseDB.checkGameExists(gameNumberString, this);
        tvGameNumber.setText(gameNumberString);
    }

    @Override
    protected void onResume() {
        super.onResume();
        AnimationUtils.animateCircularReveal(fabShareGameId);
    }

    @OnClick(R.id.fabShareGameId)
    public void shareGameId(View view) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.msg_share) + gameNumberString);
        sendIntent.setType("text/plain");
        PackageManager manager = getPackageManager();
        List<ResolveInfo> info = manager.queryIntentActivities(sendIntent, 0);
        if (info.size() > 0) {
            startActivity(sendIntent);
        } else {
            Snackbar.make(view, R.string.msg_no_app_sharing, Snackbar.LENGTH_LONG).show();
        }
    }

    @OnClick(R.id.btnCreateGame)
    public void startGame(View view) {
        if (mCards == null || mCards.size() == 0) {
            Intent intent = new Intent(this, SelectCardsActivity.class);
            startActivityForResult(intent, REQ_CODE_SELECT_CARDS);
            AnimationUtils.enterVineTransition(this);
        } else {
            if (TextUtils.isEmpty(tvGameNumber.getText())) {
                showSnackBar(getApplicationContext(), view, msgEnterGameNumber);
            } else {
                Intent gameViewManagerIntent = new Intent(this, GameViewManagerActivity.class);
                gameViewManagerIntent.putExtra(Constants.PARAM_GAME_NAME, tvGameNumber.getText().toString());
                gameViewManagerIntent.putExtra(Constants.PARAM_CARDS, getParcelable(mCards));
                gameViewManagerIntent.putExtra(Constants.PARAM_CURRENT_VIEW_PLAYER, false); // go to dealer view by default
                startActivity(gameViewManagerIntent);
                AnimationUtils.enterZoomTransition(this);
                finish();
            }
        }
    }

    @OnClick(R.id.btnReselectCards)
    public void selectCards() {
        Intent intent = new Intent(this, SelectCardsActivity.class);
        startActivityForResult(intent, REQ_CODE_SELECT_CARDS);
        AnimationUtils.enterVineTransition(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE_SELECT_CARDS && resultCode == RESULT_OK) {
            mCards = Parcels.unwrap(data.getExtras().getParcelable(PARAM_CARDS));
            rlSelectedCards.setVisibility(View.VISIBLE);
            tvSelectedCards.setText(String.format(msgSelectedCardsCount, mCards.size()));
        }

        //Change select cards to start game
        btnCreateGame.setText(isEmpty(mCards) ? msgSelectCards : msgStartGame);
    }

    @Override
    public void onGameExistsResult(final boolean result) {
        if (result) {
            int gameNumber = new Random().nextInt(99999);
            String gameNumberString = String.format(Locale.getDefault(), "%05d", gameNumber);
            FirebaseDB.checkGameExists(gameNumberString, this);
            tvGameNumber.setText(gameNumberString);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            AnimationUtils.exitVineTransition(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AnimationUtils.exitVineTransition(this);
    }
}