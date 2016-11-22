package org.bootcamp.fiftytwo.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import org.bootcamp.fiftytwo.R;
import org.bootcamp.fiftytwo.models.Card;
import org.bootcamp.fiftytwo.models.User;
import org.bootcamp.fiftytwo.utils.CardUtil;
import org.bootcamp.fiftytwo.views.PlayerViewHelper;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static org.bootcamp.fiftytwo.utils.CardUtil.getParcelable;
import static org.bootcamp.fiftytwo.utils.Constants.DEALER_TAG;
import static org.bootcamp.fiftytwo.utils.Constants.PARAM_CARDS;

public class DealerViewFragment extends Fragment {

    private Unbinder unbinder;
    private OnDealerListener mListener;
    private List<Card> mCards = new ArrayList<>();

    @BindView(R.id.flDealerViewContainer) FrameLayout flDealerViewContainer;

    public interface OnDealerListener {
        void onDeal();
    }

    public static DealerViewFragment newInstance(List<Card> cards) {
        DealerViewFragment fragment = new DealerViewFragment();
        Bundle args = new Bundle();
        if (cards != null)
            args.putParcelable(PARAM_CARDS, getParcelable(cards));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            // Add table cards
            List<Card> cards = Parcels.unwrap(bundle.getParcelable(PARAM_CARDS));
            if (cards == null || cards.size() == 0) {
                mCards = CardUtil.generateDeck(1, false);
            } else {
                mCards = cards;
            }
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

        PlayerViewHelper.addPlayers(this, R.id.flDealerViewContainer, User.getPlayers(4));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnDealerListener) {
            mListener = (OnDealerListener) context;
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
        unbinder.unbind();
    }
}