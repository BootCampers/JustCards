package org.bootcamp.fiftytwo.fragments;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.bootcamp.fiftytwo.R;
import org.bootcamp.fiftytwo.adapters.CardsAdapter;
import org.bootcamp.fiftytwo.models.Card;

import java.util.ArrayList;

import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by baphna on 11/11/2016.
 */

public class CardsListFragment extends Fragment {

    @BindDrawable(R.drawable.back)
    Drawable cardBack;

    @BindView(R.id.rvCardsList)
    RecyclerView rvCardsList;
    private CardsAdapter cardsAdapter;
    private ArrayList<Card> cards = new ArrayList<>();
    private StaggeredGridLayoutManager staggeredLayoutManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public CardsListFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cards_list, container, false);
        ButterKnife.bind(this, view);

        cards.add(new Card("jack1"));
        cards.add(new Card("jack2"));
        cards.add(new Card("jack3"));
        cardsAdapter = new CardsAdapter(getActivity(), cards);
        staggeredLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL);
        rvCardsList.setLayoutManager(staggeredLayoutManager);
        rvCardsList.setAdapter(cardsAdapter);

        return view;
    }
}
