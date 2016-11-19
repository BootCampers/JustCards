package org.bootcamp.fiftytwo.fragments;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.bootcamp.fiftytwo.R;
import org.bootcamp.fiftytwo.adapters.CardsAdapter;
import org.bootcamp.fiftytwo.models.Card;
import org.bootcamp.fiftytwo.models.CardRank;
import org.bootcamp.fiftytwo.models.CardSuit;
import org.bootcamp.fiftytwo.utils.Constants;

import java.util.ArrayList;

import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by baphna on 11/11/2016.
 */

public class CardsListFragment extends Fragment implements CardsAdapter.CardsListener {

    @BindDrawable(R.drawable.back)
    Drawable cardBack;

    @BindView(R.id.rvCardsList)
    RecyclerView rvCardsList;
    private CardsAdapter cardsAdapter;
    private ArrayList<Card> cards = new ArrayList<>();
    private StaggeredGridLayoutManager staggeredLayoutManager;
    @BindView(R.id.tvNoCards)
    TextView tvNoCards;
    private OnLogEventListener listener;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnLogEventListener) {
            listener = (OnLogEventListener) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement CardsListFragment.OnLogEventListener");
        }
    }

    public CardsListFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cards_list, container, false);
        ButterKnife.bind(this, view);

        String tag = Constants.TAG;

        Bundle bundle = getArguments();
        if(bundle != null) {
            tag = bundle.getString(Constants.TAG);
        }
        cards.add(new Card(CardSuit.SPADES, CardRank.JACK));
        cards.add(new Card(CardSuit.SPADES, CardRank.QUEEN));
        cards.add(new Card(CardSuit.SPADES, CardRank.KING));
        cardsAdapter = new CardsAdapter(getActivity(), cards, this, tag);
        staggeredLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL);
        rvCardsList.setLayoutManager(staggeredLayoutManager);
        setEmptyList(false);
        rvCardsList.setAdapter(cardsAdapter);

        tvNoCards.setOnDragListener(cardsAdapter.getDragInstance());
        return view;
    }

    @Override
    public void setEmptyList(boolean visibility) {
        if(visibility){
            tvNoCards.setVisibility(View.VISIBLE);
            rvCardsList.setVisibility(View.GONE);
        } else {
            tvNoCards.setVisibility(View.GONE);
            rvCardsList.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void logActivity(String whoPosted, String details) {
        Log.d(Constants.TAG, CardsListFragment.class.getSimpleName()
                + "--" + details + "--" + whoPosted);
        if(listener != null){
            listener.onNewLogEvent(whoPosted, details);
        }
    }

    public interface OnLogEventListener {
        public void onNewLogEvent(String whoPosted, String detail);
    }
}
