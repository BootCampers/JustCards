package org.bootcamp.fiftytwo.fragments;

import android.content.Context;
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
import org.bootcamp.fiftytwo.views.OverlapDecoration;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static org.bootcamp.fiftytwo.utils.CardUtil.getParcelable;
import static org.bootcamp.fiftytwo.utils.Constants.PARAM_CARDS;
import static org.bootcamp.fiftytwo.utils.Constants.TAG;

/**
 * Created by baphna on 11/11/2016.
 */
public class CardsListFragment extends Fragment implements CardsAdapter.CardsListener {

    private OnLogEventListener listener;

    @BindView(R.id.rvCardsList) RecyclerView rvCardsList;
    @BindView(R.id.tvNoCards) TextView tvNoCards;

    public interface OnLogEventListener {
        void onNewLogEvent(String whoPosted, String detail);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnLogEventListener) {
            listener = (OnLogEventListener) context;
        } else {
            throw new ClassCastException(context.toString() + " must implement CardsListFragment.OnLogEventListener");
        }
    }

    public static CardsListFragment newInstance(final List<Card> cards, final String tag) {
        CardsListFragment fragment = new CardsListFragment();
        Bundle args = new Bundle();
        args.putParcelable(PARAM_CARDS, getParcelable(cards));
        args.putString(TAG, tag);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cards_list, container, false);
        ButterKnife.bind(this, view);

        Bundle bundle = getArguments();

        String tag = TAG;
        List<Card> cards = new ArrayList<>();

        if (bundle != null) {
            cards = Parcels.unwrap(bundle.getParcelable(PARAM_CARDS));
            tag = bundle.getString(TAG);
        }

        CardsAdapter adapter = new CardsAdapter(getActivity(), cards, this, tag);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL);
        RecyclerView.ItemDecoration overlapDecoration = new OverlapDecoration();
        rvCardsList.addItemDecoration(overlapDecoration);
        rvCardsList.setLayoutManager(layoutManager);
        setEmptyList(cards.size() == 0);
        rvCardsList.setAdapter(adapter);
        tvNoCards.setOnDragListener(adapter.getDragInstance());
        return view;
    }

    @Override
    public void setEmptyList(boolean visibility) {
        if (visibility) {
            tvNoCards.setVisibility(View.VISIBLE);
            rvCardsList.setVisibility(View.GONE);
        } else {
            tvNoCards.setVisibility(View.GONE);
            rvCardsList.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void logActivity(String whoPosted, String details) {
        Log.d(TAG, CardsListFragment.class.getSimpleName() + "--" + details + "--" + whoPosted);
        if (listener != null) {
            listener.onNewLogEvent(whoPosted, details);
        }
    }

}