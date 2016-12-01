package org.bootcamp.fiftytwo.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.bootcamp.fiftytwo.R;
import org.bootcamp.fiftytwo.models.Card;
import org.bootcamp.fiftytwo.models.User;
import org.parceler.Parcels;

import java.util.List;

import butterknife.BindString;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static org.bootcamp.fiftytwo.utils.AppUtils.getParcelable;
import static org.bootcamp.fiftytwo.utils.AppUtils.isEmpty;
import static org.bootcamp.fiftytwo.utils.Constants.LAYOUT_TYPE_CIRCULAR;
import static org.bootcamp.fiftytwo.utils.Constants.LAYOUT_TYPE_SCROLL_ZOOM;
import static org.bootcamp.fiftytwo.utils.Constants.PARAM_PLAYER_CARDS;
import static org.bootcamp.fiftytwo.utils.Constants.PARAM_TABLE_CARDS;
import static org.bootcamp.fiftytwo.utils.Constants.PLAYER_TAG;
import static org.bootcamp.fiftytwo.utils.Constants.TABLE_TAG;

public class PlayerViewFragment extends Fragment {

    private onPlayListener mListener;
    private Unbinder unbinder;
    private List<Card> mPlayerCards;
    private List<Card> mTableCards;

    @BindString(R.string.msg_hide) String msgHide;
    @BindString(R.string.msg_show) String msgShow;

    public interface onPlayListener {
        void onCardsVisibility(final boolean toShow);
    }

    public static PlayerViewFragment newInstance(List<Card> playerCards, List<Card> tableCards) {
        PlayerViewFragment fragment = new PlayerViewFragment();
        Bundle args = new Bundle();
        if (!isEmpty(playerCards))
            args.putParcelable(PARAM_PLAYER_CARDS, getParcelable(playerCards));
        if (!isEmpty(tableCards))
            args.putParcelable(PARAM_TABLE_CARDS, getParcelable(tableCards));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        if (bundle != null) {
            List<Card> playerCards = Parcels.unwrap(bundle.getParcelable(PARAM_PLAYER_CARDS));
            List<Card> tableCards = Parcels.unwrap(bundle.getParcelable(PARAM_TABLE_CARDS));
            if (!isEmpty(playerCards))
                mPlayerCards = playerCards;
            if (!isEmpty(tableCards))
                mTableCards = tableCards;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_player_view, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Fragment playerCardsFragment = CardsFragment.newInstance(mPlayerCards, PLAYER_TAG, LAYOUT_TYPE_CIRCULAR);
        Fragment tableCardsFragment = CardsFragment.newInstance(mTableCards, TABLE_TAG, LAYOUT_TYPE_SCROLL_ZOOM);

        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.flPlayerContainer, playerCardsFragment, PLAYER_TAG)
                .replace(R.id.flTableContainer, tableCardsFragment, TABLE_TAG)
                .commit();
    }


    //@OnClick(R.id.btnToggleCardsFragment)
    public void toggleMyCardsToAll() {
        User self = User.getCurrentUser(getContext());
        boolean isShowing = self.isShowingCards();

        self.setShowingCards(!isShowing);
        //btnToggleCardsFragment.setText(isShowing ? msgShow : msgHide);
        if (mListener != null) {
            mListener.onCardsVisibility(!isShowing);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof onPlayListener) {
            mListener = (onPlayListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement onPlayListener");
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