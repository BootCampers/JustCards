package org.bootcamp.fiftytwo.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import org.bootcamp.fiftytwo.R;
import org.bootcamp.fiftytwo.fragments.SelectCardsFragment;
import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

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
        /** This is just a hack for now, will fix it later */
        data.putExtra("cards", Parcels.wrap(selectCardsFragment.mAdapter.get()));
        setResult(RESULT_OK, data);
        finish();
    }
}