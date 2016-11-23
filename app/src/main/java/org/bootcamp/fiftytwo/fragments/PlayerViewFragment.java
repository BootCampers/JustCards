package org.bootcamp.fiftytwo.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.bootcamp.fiftytwo.R;
import org.bootcamp.fiftytwo.interfaces.Observable;
import org.bootcamp.fiftytwo.interfaces.Observer;
import org.bootcamp.fiftytwo.models.Card;
import org.bootcamp.fiftytwo.receivers.CardExchangeReceiver;
import org.bootcamp.fiftytwo.utils.CardUtil;

import java.util.List;

import static org.bootcamp.fiftytwo.utils.Constants.PLAYER_TAG;
import static org.bootcamp.fiftytwo.utils.Constants.TABLE_TAG;

public class PlayerViewFragment extends Fragment implements Observer {

    private CardExchangeReceiver cardExchangeReceiver;
    private OnPlayerFragmentInteractionListener mListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_player_view, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        insertNestedFragments();
    }

    private void insertNestedFragments() {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();

        //Add player cards
        List<Card> cards = CardUtil.generateDeck(1, false);
        Fragment playerCardsFragment = CardsFragment.newInstance(cards.subList(0, 6), PLAYER_TAG);
        transaction.replace(R.id.flPlayerContainer, playerCardsFragment, PLAYER_TAG);

        //Add table cards
        Fragment tableCardsFragment = CardsFragment.newInstance(cards.subList(0, 6), TABLE_TAG);
        transaction.replace(R.id.flTableContainer, tableCardsFragment, TABLE_TAG);

        transaction.commit();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnPlayerFragmentInteractionListener) {
            mListener = (OnPlayerFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnPlayerFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onUpdate(Observable o, final Object identifier, final Object arg) {
        // Nothing to do as of now
    }

    public interface OnPlayerFragmentInteractionListener {
        // TODO: Update argument type and name
        void onPlayerFragmentInteraction(Uri uri);
    }
}