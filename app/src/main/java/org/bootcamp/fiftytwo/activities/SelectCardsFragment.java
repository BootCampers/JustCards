package org.bootcamp.fiftytwo.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.bootcamp.fiftytwo.R;
import org.bootcamp.fiftytwo.adapters.SelectCardsAdapter;
import org.bootcamp.fiftytwo.models.Card;
import org.bootcamp.fiftytwo.utils.CardUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A placeholder fragment containing a simple view.
 */
public class SelectCardsFragment extends Fragment {

    @BindView(R.id.rvCards) RecyclerView rvCards;
    private SelectCardsAdapter mAdapter;
    private List<Card> mCards;
    private GridLayoutManager layoutManager;

    public SelectCardsFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select_cards, container, false);
        ButterKnife.bind(this, view);

        mCards = CardUtil.generateDeck(1, true);
        mAdapter = new SelectCardsAdapter(getContext(), mCards);
        layoutManager = new GridLayoutManager(getContext(), 6, LinearLayoutManager.VERTICAL, false);
        rvCards.setLayoutManager(layoutManager);
        rvCards.setAdapter(mAdapter);

        return view;
    }
}