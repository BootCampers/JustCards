package org.bootcamp.fiftytwo.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import org.bootcamp.fiftytwo.R;
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
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();

        // TODO: We may need this if we plan to put players in a draggable/re-ordering recyclerview
        // TODO: Let's hold off for that now
        //Add player cards
        /*Fragment playersCardFragment = new CardsListFragment();
        Bundle playerBundle = new Bundle();
        playerBundle.putString(Constants.TAG, Constants.PLAYER_TAG);
        playersCardFragment.setArguments(playerBundle);
        transaction.replace(R.id.flPlayersContainer, playersCardFragment, Constants.PLAYER_TAG);*/

        //Add table cards
        Fragment dealerCardsFragment = CardsListFragment.newInstance(CardUtil.generateDeck(1, false).subList(0, 1), DEALER_TAG);
        transaction.replace(R.id.flDealerContainer, dealerCardsFragment, DEALER_TAG);
        transaction.commit();

        initPlayers(User.getDummyPlayers(4));
    }

    private void initPlayers(List<User> players) {
        Player.addPlayers(getActivity(), flDealerViewContainer, players, R.layout.item_player_with_cards);

        /*for (User player : players) {
            Player.addPlayer(getActivity(), flDealerViewContainer, player);
        }*/
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