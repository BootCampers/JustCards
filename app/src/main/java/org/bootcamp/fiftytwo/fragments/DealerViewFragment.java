package org.bootcamp.fiftytwo.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import org.bootcamp.fiftytwo.R;
import org.bootcamp.fiftytwo.models.Card;
import org.bootcamp.fiftytwo.models.User;
import org.bootcamp.fiftytwo.utils.Constants;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static org.bootcamp.fiftytwo.utils.AppUtils.getParcelable;
import static org.bootcamp.fiftytwo.utils.AppUtils.isEmpty;
import static org.bootcamp.fiftytwo.utils.CardUtil.draw;
import static org.bootcamp.fiftytwo.utils.Constants.DEALER_TAG;
import static org.bootcamp.fiftytwo.utils.Constants.LAYOUT_TYPE_STAGGERED_HORIZONTAL;
import static org.bootcamp.fiftytwo.utils.Constants.PARAM_CARDS;
import static org.bootcamp.fiftytwo.utils.Constants.PARAM_PLAYERS;
import static org.bootcamp.fiftytwo.utils.Constants.TAG;

public class DealerViewFragment extends Fragment implements
        DealingOptionsFragment.OnDealOptionsListener,
        ScoringFragment.OnScoreFragmentListener {

    private List<Card> mCards = new ArrayList<>();
    private List<User> mPlayers = new ArrayList<>();
    private OnDealListener mDealListener;
    private ScoringFragment.OnScoreFragmentListener mScoreListener;
    private DealingOptionsFragment dealingOptionsFragment;
    private CardsFragment dealerCardsFragment;
    private ScoringFragment scoringFragment;
    private Unbinder unbinder;

    @BindView(R.id.flDealerViewContainer) FrameLayout flDealerViewContainer;
    @BindString(R.string.msg_send_to_table) String msgSendToTable;

    public interface OnDealListener {
        boolean onDeal(List<Card> cards, User player);
        boolean onDealTable(List<Card> cards, boolean toSink);
        void onDealerOptionsShowing(boolean isDealerOptionShowing);
    }

    public static DealerViewFragment newInstance(List<Card> cards, List<User> players) {
        DealerViewFragment fragment = new DealerViewFragment();
        Bundle args = new Bundle();
        if (!isEmpty(cards))
            args.putParcelable(PARAM_CARDS, getParcelable(cards));
        if (!isEmpty(players))
            args.putParcelable(PARAM_PLAYERS, getParcelable(players));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        if (bundle != null) {
            List<Card> cards = Parcels.unwrap(bundle.getParcelable(PARAM_CARDS));
            List<User> players = Parcels.unwrap(bundle.getParcelable(PARAM_PLAYERS));
            if (!isEmpty(cards))
                mCards = cards;
            if (!isEmpty(players))
                mPlayers = players;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dealer_view, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        dealerCardsFragment = CardsFragment.newInstance(mCards, DEALER_TAG, LAYOUT_TYPE_STAGGERED_HORIZONTAL);

        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.flDealerContainer, dealerCardsFragment, DEALER_TAG)
                .commit();
    }

    @OnClick({R.id.btnScore})
    public void score() {
        scoringFragment = ScoringFragment.newInstance(mPlayers);
        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.flDealerViewContainer, scoringFragment, Constants.SCORING_OPTIONS_TAG)
                .hide(dealerCardsFragment)
                .commit();
        mDealListener.onDealerOptionsShowing(true);

    }

    @Override
    public void onScore(boolean saveClicked) {
        getChildFragmentManager()
                .beginTransaction()
                .show(dealerCardsFragment)
                .remove(scoringFragment)
                .commit();
        mDealListener.onDealerOptionsShowing(false);
        mScoreListener.onScore(saveClicked);
    }

    @Override
    public void roundWinners(List<User> roundWinners) {
        mScoreListener.roundWinners(roundWinners);
    }

    @OnClick({R.id.btnDeal})
    public void deal() {
        List<Card> cards = dealerCardsFragment.getCards();

        dealingOptionsFragment = DealingOptionsFragment.newInstance(mPlayers.size(), cards.size());
        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.flDealerViewContainer, dealingOptionsFragment, Constants.DEALING_OPTIONS_TAG)
                .hide(dealerCardsFragment)
                .commit();

        mDealListener.onDealerOptionsShowing(true);
    }

    @Override
    public void onDealOptionSelected(Bundle bundle) {
        if (bundle != null) {
            Log.d(Constants.TAG, bundle.toString());
            int doCardCount = bundle.getInt(Constants.DO_CARD_COUNT);
            String doRemainingCards = bundle.getString(Constants.DO_REMAINING_CARDS);
            boolean doShuffle = bundle.getBoolean(Constants.DO_SHUFFLE);
            boolean doDealSelf = bundle.getBoolean(Constants.DO_DEAL_SELF);
            dealNow(doCardCount, doRemainingCards, doShuffle, doDealSelf);
        }

        getChildFragmentManager()
                .beginTransaction()
                .show(dealerCardsFragment)
                .remove(dealingOptionsFragment)
                .commit();
        mDealListener.onDealerOptionsShowing(false);
    }

    /**
     * @param dealCount        No of cards to deal to each player
     * @param doRemainingCards action that defines handling of the remaining cards
     * @param doShuffle        whether to shuffle the cards before dealing
     * @param doDealSelf       whether to deal to the dealer
     */
    private void dealNow(int dealCount, String doRemainingCards, boolean doShuffle, boolean doDealSelf) {
        if (mDealListener != null) {
            CardsFragment dealerFragment = (CardsFragment) getChildFragmentManager().findFragmentByTag(DEALER_TAG);
            List<Card> cards = dealerFragment.getCards();

            int numPlayers = mPlayers.size();
            if (!doDealSelf)
                numPlayers--;

            if (cards.size() >= numPlayers * dealCount) {
                if (doShuffle) {
                    cards = dealerFragment.shuffleCards();
                }
                for (User player : mPlayers) {
                    if (!doDealSelf) {
                        continue;
                    }

                    List<Card> drawnCards = draw(cards, dealCount, false);
                    if (!isEmpty(drawnCards)) {
                        boolean dealt = mDealListener.onDeal(drawnCards, player);
                        if (dealt) {
                            dealerFragment.drawCards(drawnCards);
                        }
                    }
                }

                // Handle Remaining Cards Here
                if (cards.size() > 0) {
                    Log.d(TAG, "dealNow: Remaining Cards Action Selected: " + doRemainingCards);
                    boolean toSink = !doRemainingCards.equals(msgSendToTable);
                    mDealListener.onDealTable(cards, toSink);
                }
            } else {
                Toast.makeText(getActivity(), "Not enough cards to deal", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void addPlayers(List<User> players) {
        if (!isEmpty(players)) {
            mPlayers.addAll(players);
        }
    }

    public boolean removePlayer(User player) {
        return mPlayers.remove(player);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnDealListener) {
            mDealListener = (OnDealListener) context;
        }
        if (context instanceof ScoringFragment.OnScoreFragmentListener) {
            mScoreListener = (ScoringFragment.OnScoreFragmentListener) context; //GameViewManagerActivity
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mDealListener = null;
        mScoreListener = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}