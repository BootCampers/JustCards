package org.bootcamp.fiftytwo.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import org.bootcamp.fiftytwo.R;
import org.bootcamp.fiftytwo.models.Card;
import org.bootcamp.fiftytwo.models.User;
import org.bootcamp.fiftytwo.utils.CardUtil;
import org.bootcamp.fiftytwo.utils.PlayerUtils;
import org.bootcamp.fiftytwo.views.PlayerViewHelper;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static org.bootcamp.fiftytwo.utils.AppUtils.getParcelable;
import static org.bootcamp.fiftytwo.utils.AppUtils.isEmpty;
import static org.bootcamp.fiftytwo.utils.CardUtil.draw;
import static org.bootcamp.fiftytwo.utils.Constants.DEALER_TAG;
import static org.bootcamp.fiftytwo.utils.Constants.PARAM_CARDS;
import static org.bootcamp.fiftytwo.utils.Constants.PARAM_PLAYERS;
import static org.bootcamp.fiftytwo.views.PlayerViewHelper.getPlayerFragmentTag;

public class DealerViewFragment extends Fragment {

    private List<Card> mCards = new ArrayList<>();
    private List<User> mPlayers = new ArrayList<>();
    private OnDealerListener mDealerListener;
    private Unbinder unbinder;

    @BindView(R.id.flDealerViewContainer) FrameLayout flDealerViewContainer;

    public interface OnDealerListener {
        void onDeal();
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
            mCards = isEmpty(cards) ? CardUtil.generateDeck(1, false) : cards;
            mPlayers = isEmpty(players) ? PlayerUtils.getPlayers(4) : players;
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
        Fragment dealerCardsFragment = CardsFragment.newInstance(mCards, DEALER_TAG);

        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.flDealerContainer, dealerCardsFragment, DEALER_TAG)
                .commit();

        PlayerViewHelper.addPlayers(this, R.id.flDealerViewContainer, mPlayers);
    }

    @OnClick(R.id.ibDeal)
    public void deal(View view) {
        int dealCount = 1;
        if (mCards.size() >= mPlayers.size() * dealCount) {
            for (User player : mPlayers) {
                String playerFragmentTag = getPlayerFragmentTag(player);
                Fragment playerFragment = getChildFragmentManager().findFragmentByTag(playerFragmentTag);
                if (playerFragment != null) {
                    List<Card> drawnCards = draw(mCards, dealCount, true);
                    if (!isEmpty(drawnCards)) {
                        ((PlayerFragment) playerFragment).dealCards(drawnCards);
                    }
                }
            }
        } else {
            Snackbar.make(view, "Not enough cards to deal", Snackbar.LENGTH_SHORT).show();
        }

        if (mDealerListener != null) {
            mDealerListener.onDeal();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnDealerListener) {
            mDealerListener = (OnDealerListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mDealerListener = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}