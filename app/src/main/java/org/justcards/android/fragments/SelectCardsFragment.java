package org.justcards.android.fragments;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.justcards.android.R;
import org.justcards.android.adapters.SelectCardsAdapter;
import org.justcards.android.models.Card;
import org.justcards.android.utils.CardUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * A placeholder fragment containing a simple view.
 */
public class SelectCardsFragment extends Fragment {

    public SelectCardsAdapter mAdapter;
    private Unbinder unbinder;

    @BindView(R.id.rvCards) RecyclerView rvCards;
    @BindView(R.id.btnLoadMore) Button btnLoadMore;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new SelectCardsAdapter(getContext(), new ArrayList<>());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select_cards, container, false);
        unbinder = ButterKnife.bind(this, view);

        List<Card> cards = CardUtil.selectDefaults(CardUtil.generateDeck(1, true));

        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 6);
        rvCards.setAdapter(mAdapter);
        rvCards.setLayoutManager(layoutManager);
        mAdapter.addAll(cards);

        return view;
    }

    @OnClick(R.id.btnLoadMore)
    public void loadMore() {
        mAdapter.addAll(CardUtil.selectDefaults(CardUtil.generateDeck(1, true)));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public List<Card> getCards() {
        return mAdapter.getCards();
    }
}