package org.bootcamp.fiftytwo.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import org.bootcamp.fiftytwo.R;
import org.bootcamp.fiftytwo.fragments.SelectCardsFragment;
import org.bootcamp.fiftytwo.models.Card;
import org.bootcamp.fiftytwo.utils.AnimationUtils;
import org.bootcamp.fiftytwo.utils.CardUtil;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static org.bootcamp.fiftytwo.utils.Constants.PARAM_CARDS;

public class SelectCardsActivity extends AppCompatActivity {

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.fabSaveSelection) FloatingActionButton fabSaveSelection;

    private SelectCardsFragment selectCardsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_cards);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        selectCardsFragment = new SelectCardsFragment();

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.flCardsContainer, selectCardsFragment)
                .commit();
    }

    @OnClick(R.id.fabSaveSelection)
    public void save(View view) {
        Intent data = new Intent();
        List<Card> cards = new ArrayList<>();
        if(selectCardsFragment != null && selectCardsFragment.isAdded()) {
            cards.addAll(CardUtil.getSelected(selectCardsFragment.getCards()));
        }
        data.putExtra(PARAM_CARDS, Parcels.wrap(cards));
        setResult(RESULT_OK, data);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        AnimationUtils.animateCircularReveal(fabSaveSelection);
    }
}