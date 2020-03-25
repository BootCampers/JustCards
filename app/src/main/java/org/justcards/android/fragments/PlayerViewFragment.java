package org.justcards.android.fragments;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.justcards.android.R;
import org.justcards.android.models.Card;
import org.parceler.Parcels;

import java.util.List;

import butterknife.BindString;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static org.justcards.android.utils.AppUtils.getParcelable;
import static org.justcards.android.utils.AppUtils.isEmpty;
import static org.justcards.android.utils.Constants.LAYOUT_TYPE_CIRCULAR;
import static org.justcards.android.utils.Constants.LAYOUT_TYPE_SCROLL_ZOOM;
import static org.justcards.android.utils.Constants.PARAM_PLAYER_CARDS;
import static org.justcards.android.utils.Constants.PARAM_TABLE_CARDS;
import static org.justcards.android.utils.Constants.PLAYER_TAG;
import static org.justcards.android.utils.Constants.TABLE_TAG;

public class PlayerViewFragment extends Fragment {

    private Unbinder unbinder;
    private List<Card> mPlayerCards;
    private List<Card> mTableCards;

    @BindString(R.string.msg_hide) String msgHide;
    @BindString(R.string.msg_show) String msgShow;

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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

}