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
import butterknife.Unbinder;

import static org.bootcamp.fiftytwo.utils.CardUtil.getParcelable;
import static org.bootcamp.fiftytwo.utils.Constants.PARAM_CARDS;
import static org.bootcamp.fiftytwo.utils.Constants.TAG;

/**
 * Created by baphna on 11/11/2016.
 */
public class CardsFragment extends Fragment implements CardsAdapter.CardsListener {

    protected CardsAdapter mAdapter;
    protected List<Card> mCards = new ArrayList<>();
    private OnLogEventListener mListener;
    protected Unbinder unbinder;
    protected String tag = TAG;

    @BindView(R.id.rvCardsList) RecyclerView rvCardsList;
    @BindView(R.id.tvNoCards) TextView tvNoCards;

    public interface OnLogEventListener {
        void onNewLogEvent(String whoPosted, String detail);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnLogEventListener) {
            mListener = (OnLogEventListener) context;
        } else {
            throw new ClassCastException(context.toString() + " must implement CardsFragment.OnLogEventListener");
        }
    }

    public static CardsFragment newInstance(final List<Card> cards, final String tag) {
        CardsFragment fragment = new CardsFragment();
        Bundle args = new Bundle();
        args.putParcelable(PARAM_CARDS, getParcelable(cards));
        args.putString(TAG, tag);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            mCards = Parcels.unwrap(bundle.getParcelable(PARAM_CARDS));
            tag = bundle.getString(TAG);
        }
        mAdapter = new CardsAdapter(getContext(), mCards, this, tag);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cards_list, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        initCards();
    }

    protected void initCards() {
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL);
        RecyclerView.ItemDecoration overlapDecoration = new OverlapDecoration(getContext(), -50, 0);
        rvCardsList.addItemDecoration(overlapDecoration);
        rvCardsList.setLayoutManager(layoutManager);
        rvCardsList.setAdapter(mAdapter);
        tvNoCards.setOnDragListener(mAdapter.getDragInstance());
        setEmptyList(mCards.size() == 0);
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
        Log.d(TAG, this.getClass().getSimpleName() + "--" + details + "--" + whoPosted);
        if (mListener != null) {
            mListener.onNewLogEvent(whoPosted, details);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(unbinder != null)
            unbinder.unbind();
    }
}