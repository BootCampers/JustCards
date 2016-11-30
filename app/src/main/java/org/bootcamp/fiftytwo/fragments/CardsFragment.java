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
import android.widget.TextView;

import com.plattysoft.leonids.ParticleSystem;
import com.plattysoft.leonids.modifiers.AlphaModifier;
import com.plattysoft.leonids.modifiers.ScaleModifier;

import org.bootcamp.fiftytwo.R;
import org.bootcamp.fiftytwo.adapters.CardsAdapter;
import org.bootcamp.fiftytwo.models.Card;
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

    public interface OnCardExchangeLister {
        void onCardExchange(String fromTag, String toTag, int fromPosition, int toPosition, Card card);
    }

    public interface OnLogEventListener {
        void onNewLogEvent(String whoPosted, String fromAvatar, String detail);
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

    public void toggleCardsVisibility(boolean show) {
        flCardsContainer.setVisibility(show ? View.VISIBLE : View.GONE);
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
    public void publish(String fromTag, String toTag, int fromPosition, int toPosition, Card card) {
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

    public boolean stackCards(List<Card> cards) {
        if (!isEmpty(cards)) {
            mAdapter.addAll(cards);
            setEmptyList(mAdapter.getItemCount() == 0);

            return true;
        }
        return false;
    }

    public boolean drawCards(List<Card> cards) {
        if (!isEmpty(cards)) {
            mAdapter.removeAll(cards);
            setEmptyList(mAdapter.getItemCount() == 0);
            return true;
        }
        return false;
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
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCardExchangeLister = null;
        mLogEventListener = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null)
            unbinder.unbind();
    }

    private void setLayoutManager() {
        if (LAYOUT_TYPE_CIRCULAR.equalsIgnoreCase(layoutType)) {
            rvCardsList.addOnScrollListener(new CenterScrollListener());
            rvCardsList.setLayoutManager(new CircleLayoutManager(getContext())
                    .setFirstChildRotate(0)
                    .setIntervalAngle(13)
                    .setRadius(500)
                    .setRadialDistortionFactor(3)
                    .setContentOffsetX(-1)
                    .setContentOffsetY(-1)
                    .setDegreeRangeWillShow(-60, 60));
        } else if (LAYOUT_TYPE_SCROLL_ZOOM.equalsIgnoreCase(layoutType)) {
            rvCardsList.addOnScrollListener(new CenterScrollListener());
            rvCardsList.setLayoutManager(new ScrollZoomLayoutManager(getContext(), -30)
                    .setContentOffsetY(-1)
                    .setMaxScale(1.2f));
        } else {
            RecyclerView.ItemDecoration overlapDecoration = new OverlapDecoration(getContext(), -50, 0);
            rvCardsList.addItemDecoration(overlapDecoration);
            StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL);
            rvCardsList.setLayoutManager(staggeredGridLayoutManager);
        }
    }
}