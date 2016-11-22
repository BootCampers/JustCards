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
import org.bootcamp.fiftytwo.views.Player;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static org.bootcamp.fiftytwo.utils.Constants.DEALER_TAG;

public class DealerViewFragment extends Fragment {

    private Unbinder unbinder;
    private OnDealerListener mListener;

    @BindView(R.id.flDealerViewContainer) FrameLayout flDealerViewContainer;

    public interface OnDealerListener {
        void onDeal();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dealer_view, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        // Add table cards
        List<Card> cards = CardUtil.generateDeck(1, false);
        Fragment dealerCardsFragment = CardsFragment.newInstance(cards.subList(0, 6), DEALER_TAG);

        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.flDealerContainer, dealerCardsFragment, DEALER_TAG)
                .commit();

        Player.addPlayers(this, R.id.flDealerViewContainer, User.getDummyPlayers(4));
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