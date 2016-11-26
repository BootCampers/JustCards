package org.bootcamp.fiftytwo.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.bootcamp.fiftytwo.R;
import org.bootcamp.fiftytwo.utils.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.OnItemSelected;
import butterknife.Unbinder;

import static org.bootcamp.fiftytwo.utils.Constants.ARG_CARD_COUNT;
import static org.bootcamp.fiftytwo.utils.Constants.ARG_PLAYER_COUNT;
import static org.bootcamp.fiftytwo.utils.Constants.TAG;

public class
DealingOptionsFragment extends Fragment {

    @BindView(R.id.tvCardsToDeal) TextView tvCardsToDeal;
    @BindView(R.id.ibAdd) ImageButton ibAdd;
    @BindView(R.id.ibReduce) ImageButton ibReduce;
    @BindView(R.id.spnrRemainingCards) Spinner spnrRemainingCards;
    @BindView(R.id.switchShuffle) Switch switchShuffle;
    @BindView(R.id.switchDealSelf) Switch switchDealSelf;
    @BindView(R.id.btnDealNow) Button btnDealNow;
    @BindView(R.id.ibCancel) ImageButton ibCancel;

    private int playerCount;
    private int cardCount;
    private boolean dealToSelf = true;
    private int maxAllowed = 0;
    private int currentDealingCount = 0;

    private OnDealOptionsListener mListener;
    private Unbinder unbinder;

    public interface OnDealOptionsListener {
        void onDealOptionSelected(Bundle bundle);
    }

    /**
     * Use this factory method to create a new instance of this fragment using the provided parameters.
     *
     * @param playerCount Parameter 1.
     * @param cardCount   Parameter 2.
     * @return A new instance of fragment DealingOptionsFragment.
     */
    public static DealingOptionsFragment newInstance(int playerCount, int cardCount) {
        DealingOptionsFragment fragment = new DealingOptionsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PLAYER_COUNT, playerCount);
        args.putInt(ARG_CARD_COUNT, cardCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            playerCount = getArguments().getInt(ARG_PLAYER_COUNT);
            cardCount = getArguments().getInt(ARG_CARD_COUNT);
            if (playerCount == 0) {
                Toast.makeText(getActivity(), "Please let some players to join first", Toast.LENGTH_LONG).show();
            } else if (playerCount > cardCount) {
                Toast.makeText(getActivity(), "More players than cards in game. Please deal cards manually.", Toast.LENGTH_LONG).show();
            } else {
                maxAllowed = cardCount / playerCount; //rounded off to floor
                currentDealingCount = maxAllowed;
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dealing_options, container, false);
        unbinder = ButterKnife.bind(this, view);
        tvCardsToDeal.setText(String.valueOf(currentDealingCount));
        return view;
    }

    @OnClick(R.id.btnDealNow)
    public void onDealNowPressed() {
        if (mListener != null) {
            Bundle bundle = new Bundle();
            bundle.putInt(Constants.DO_CARD_COUNT, Integer.parseInt(tvCardsToDeal.getText().toString()));
            bundle.putString(Constants.DO_REMAINING_CARDS, spnrRemainingCards.getSelectedItem().toString());
            bundle.putBoolean(Constants.DO_SHUFFLE, switchShuffle.isChecked());
            bundle.putBoolean(Constants.DO_DEAL_SELF, switchDealSelf.isChecked());
            mListener.onDealOptionSelected(bundle);
        }
    }

    @OnClick(R.id.ibCancel)
    public void onCancelPressed() {
        if (mListener != null) {
            mListener.onDealOptionSelected(null);
        }
    }

    @OnClick(R.id.ibAdd)
    public synchronized void increaseCards(View view) {
        if (currentDealingCount == maxAllowed) {
            Snackbar.make(view, "Already max cards per player added", Snackbar.LENGTH_LONG).show();
        } else {
            currentDealingCount++;
            tvCardsToDeal.setText(String.valueOf(currentDealingCount));
        }
    }

    @OnClick(R.id.ibReduce)
    public synchronized void decreaseCards(View view) {
        if (currentDealingCount == 1) {
            Snackbar.make(view, "Can not deal less than 1 card per player", Snackbar.LENGTH_LONG).show();
        } else {
            currentDealingCount--;
            tvCardsToDeal.setText(String.valueOf(currentDealingCount));
        }
    }

    @OnItemSelected(R.id.spnrRemainingCards)
    public void onSpinner(AdapterView<?> parent) {
        ((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE);
    }

    @OnCheckedChanged(R.id.switchDealSelf)
    public void dealSelf(CompoundButton button, boolean isChecked) {
        Log.d(TAG, "dealSelf: Deal Self Switch Clicked: " + isChecked);
        if(!isChecked && dealToSelf) {
            if (playerCount <= 1) {
                Snackbar.make(button, "No other players to deal to", Snackbar.LENGTH_LONG).show();
            } else {
                dealToSelf = false;
                playerCount --;
                maxAllowed = cardCount / playerCount;
            }
        } else if (isChecked && !dealToSelf) {
            dealToSelf = true;
            playerCount ++;
            maxAllowed = cardCount / playerCount;
            if (currentDealingCount > maxAllowed) {
                currentDealingCount = maxAllowed;
                tvCardsToDeal.setText(String.valueOf(currentDealingCount));
                Snackbar.make(button, "Number of cards to deal is reduced for the selected options.", Snackbar.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnDealOptionsListener) {
            mListener = (OnDealOptionsListener) getParentFragment();
        } else {
            throw new RuntimeException(context.toString() + " must implement OnDealOptionsListener");
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