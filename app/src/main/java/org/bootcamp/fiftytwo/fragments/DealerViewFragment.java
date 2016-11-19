package org.bootcamp.fiftytwo.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.bootcamp.fiftytwo.R;
import org.bootcamp.fiftytwo.utils.Constants;

public class DealerViewFragment extends Fragment {

    private OnDealerListener mListener;

    public interface OnDealerListener {
        void onDeal();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dealer_view, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();

        //Add player cards
        Fragment playersCardFragment = new CardsListFragment();
        Bundle playerBundle = new Bundle();
        playerBundle.putString(Constants.TAG, Constants.PLAYER_TAG);
        playersCardFragment.setArguments(playerBundle);
        transaction.replace(R.id.flPlayerContainer, playersCardFragment, Constants.PLAYER_TAG);

        //Add table cards
        Fragment tableCardsFragment = new CardsListFragment();
        Bundle tableBundle = new Bundle();
        tableBundle.putString(Constants.TAG, Constants.TABLE_TAG);
        tableCardsFragment.setArguments(tableBundle);
        transaction.replace(R.id.flTableContainer, tableCardsFragment, Constants.TABLE_TAG);

        transaction.commit();
    }

    private void initPlayers() {

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

}