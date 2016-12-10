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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.bootcamp.fiftytwo.R;
import org.bootcamp.fiftytwo.adapters.CardsAdapter;
import org.bootcamp.fiftytwo.models.Card;
import org.bootcamp.fiftytwo.utils.AnimationUtils;
import org.bootcamp.fiftytwo.utils.CardUtil;
import org.bootcamp.fiftytwo.views.CenterScrollListener;
import org.bootcamp.fiftytwo.views.CircleLayoutManager;
import org.bootcamp.fiftytwo.views.OverlapDecoration;
import org.bootcamp.fiftytwo.views.ScrollZoomLayoutManager;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static org.bootcamp.fiftytwo.utils.AppUtils.getParcelable;
import static org.bootcamp.fiftytwo.utils.AppUtils.isEmpty;
import static org.bootcamp.fiftytwo.utils.Constants.LAYOUT_TYPE_CIRCULAR;
import static org.bootcamp.fiftytwo.utils.Constants.LAYOUT_TYPE_SCROLL_ZOOM;
import static org.bootcamp.fiftytwo.utils.Constants.PARAM_CARDS;
import static org.bootcamp.fiftytwo.utils.Constants.PARAM_LAYOUT_TYPE;
import static org.bootcamp.fiftytwo.utils.Constants.TAG;

/**
 * Created by baphna on 11/11/2016.
 */
public class CardsFragment extends Fragment implements CardsAdapter.CardsListener {

    protected CardsAdapter mAdapter;
    protected Unbinder unbinder;
    protected String tag = TAG;
    protected String layoutType;

    @BindView(R.id.rvCardsList) RecyclerView rvCardsList;
    @BindView(R.id.tvNoCards) TextView tvNoCards;
    @BindView(R.id.flCardsContainer) FrameLayout flCardsContainer;

    private OnCardExchangeLister mCardExchangeLister;
    private OnLogEventListener mLogEventListener;
    private OnToggleCardListener mOnToggleCardListener;

    public interface OnCardExchangeLister {
        void onCardExchange(String fromTag, String toTag, int fromPosition, int toPosition, Card card);
    }


    public interface OnLogEventListener {
        void onNewLogEvent(String whoPosted, String fromAvatar, String detail);
    }

    public interface OnToggleCardListener {
        void onToggleCard(Card card, int position, String onTag);
    }

    public static CardsFragment newInstance(final List<Card> cards, final String tag, final String layoutType) {
        CardsFragment fragment = new CardsFragment();
        Bundle args = new Bundle();
        args.putParcelable(PARAM_CARDS, getParcelable(cards));
        args.putString(TAG, tag);
        args.putString(PARAM_LAYOUT_TYPE, layoutType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();

        List<Card> cards = new ArrayList<>();
        if (bundle != null) {
            cards = Parcels.unwrap(bundle.getParcelable(PARAM_CARDS));
            if (isEmpty(cards)) {
                cards = new ArrayList<>();
            }
            tag = bundle.getString(TAG);
            layoutType = bundle.getString(PARAM_LAYOUT_TYPE);
        }
        mAdapter = new CardsAdapter(getContext(), cards, this, tag);
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
        setLayoutManager();
        rvCardsList.setAdapter(mAdapter);
        tvNoCards.setOnDragListener(mAdapter.getDragInstance());
        setEmptyList(mAdapter.getItemCount() == 0);
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
    public void exchange(String fromTag, String toTag, int fromPosition, int toPosition, Card card) {
        if (mCardExchangeLister != null) {
            mCardExchangeLister.onCardExchange(fromTag, toTag, fromPosition, toPosition, card);
        }
    }

    @Override
    public void logActivity(String whoPosted, String avatarUri, String details) {
        Log.d(TAG, this.getClass().getSimpleName() + "--" + details + "--" + whoPosted);
        if (mLogEventListener != null) {
            mLogEventListener.onNewLogEvent(whoPosted, avatarUri, details);
        }
    }

    @Override
    public synchronized void cardCountChange(int newCount) {
        Log.d(TAG, CardsFragment.class.getSimpleName() + "--newCount--" + newCount);
        if(getParentFragment() instanceof  PlayerFragment) {
            ((PlayerFragment) getParentFragment()).cardCountChange(newCount);
        }
    }

    @Override
    public void toggleCard(Card card, int position, String onTag) {
        Log.d(TAG, CardsFragment.class.getSimpleName() + " -- toggleCard: " + card + ", Tag: " + onTag + ", position: " + position);
        if (mOnToggleCardListener != null) {
            mOnToggleCardListener.onToggleCard(card, position, onTag);
        }
    }

    public void toggleCardsList(boolean show) {
        flCardsContainer.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    public void toggleCard(Card card, int position) {
        RecyclerView.ViewHolder holder = rvCardsList.findViewHolderForAdapterPosition(position);
        if (holder != null && holder.getAdapterPosition() != RecyclerView.NO_POSITION) {
            ImageView ivCard = ((CardsAdapter.ViewHolder) holder).ivCard;
            Card holderCard = mAdapter.getCards().get(position);
            if (card.equals(holderCard)) {
                AnimationUtils.animateFlip(mAdapter.getContext(), ivCard, () -> {
                    Glide.with(mAdapter.getContext())
                            .load(card.isShowingFront() ? holderCard.getDrawable(mAdapter.getContext()) : holderCard.getDrawableBack())
                            .crossFade()
                            .into(ivCard);
                    holderCard.setShowingFront(card.isShowingFront());
                });
            } else {
                Log.d(TAG, "Problem found in toggleCard: " + "Received: " + card + ", but Found: " + holderCard);
            }
        }
    }

    public List<Card> getCards() {
        return mAdapter.getCards();
    }

    /**
     * This is a mutable operation and
     *
     * @return the shuffled cards
     */
    public List<Card> shuffleCards() {
        List<Card> cards = CardUtil.shuffleDeck(mAdapter.getCards());
        mAdapter.setCards(cards);
        return mAdapter.getCards();
    }

    public boolean drawCard(int position, Card card) {
        if (card != null && position < mAdapter.getItemCount()) {
            Log.d(TAG, "drawCard: Drawing card: " + mAdapter.getCards().get(position));
            return mAdapter.remove(position) != null;
        }
        return false;
    }

    public boolean stackCard(Card card, int position) {
        if (card != null) {
            mAdapter.add(card, position);
            return true;
        }
        return false;
    }

    public boolean drawCards(List<Card> cards) {
        if (!isEmpty(cards)) {
            mAdapter.removeAll(cards);
            return true;
        }
        return false;
    }

    public boolean stackCards(List<Card> cards) {
        if (!isEmpty(cards)) {
            mAdapter.addAll(cards);
            return true;
        }
        return false;
    }

    public void clearCards() {
        mAdapter.clear();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnCardExchangeLister) {
            mCardExchangeLister = (OnCardExchangeLister) context;
        } else {
            throw new ClassCastException(context.toString() + " must implement CardsFragment.OnCardExchangeLister");
        }

        if (context instanceof OnLogEventListener) {
            mLogEventListener = (OnLogEventListener) context;
        } else {
            throw new ClassCastException(context.toString() + " must implement CardsFragment.OnLogEventListener");
        }

        if (context instanceof OnToggleCardListener) {
            mOnToggleCardListener = (OnToggleCardListener) context;
        } else {
            throw new ClassCastException(context.toString() + " must implement CardsFragment.OnToggleCardListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCardExchangeLister = null;
        mLogEventListener = null;
        mOnToggleCardListener = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null)
            unbinder.unbind();
    }

    private void setLayoutManager() {
        RecyclerView.LayoutManager mLayoutManager;
        if (LAYOUT_TYPE_CIRCULAR.equalsIgnoreCase(layoutType)) {
            rvCardsList.addOnScrollListener(new CenterScrollListener());
            mLayoutManager = new CircleLayoutManager(getContext())
                    .setFirstChildRotate(0)
                    .setIntervalAngle(13)
                    .setRadius(500)
                    .setRadialDistortionFactor(3)
                    .setContentOffsetX(-1)
                    .setContentOffsetY(-1)
                    .setDegreeRangeWillShow(-60, 60);
        } else if (LAYOUT_TYPE_SCROLL_ZOOM.equalsIgnoreCase(layoutType)) {
            rvCardsList.addOnScrollListener(new CenterScrollListener());
            mLayoutManager = new ScrollZoomLayoutManager(getContext(), -30)
                    .setContentOffsetY(-1)
                    .setMaxScale(1.2f);
        } else {
            RecyclerView.ItemDecoration overlapDecoration = new OverlapDecoration(getContext(), -50, 0);
            rvCardsList.addItemDecoration(overlapDecoration);
            mLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL);
        }
        rvCardsList.setLayoutManager(mLayoutManager);
    }
}