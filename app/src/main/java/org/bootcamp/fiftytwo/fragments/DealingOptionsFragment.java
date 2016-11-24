package org.bootcamp.fiftytwo.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.bootcamp.fiftytwo.R;
import org.bootcamp.fiftytwo.utils.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnDealOptionListener} interface
 * to handle interaction events.
 * Use the {@link DealingOptionsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DealingOptionsFragment extends Fragment {
    private static final String ARG_PLAYER_COUNT = "playerCount";
    private static final String ARG_CARD_COUNT = "cardCount";

    @BindView(R.id.spnrCardsFace)
    Spinner spnrCardsFace;
    @BindView(R.id.spnrRemainingCards)
    Spinner spnrRemainingCards;
    @BindView(R.id.ibCancel)
    ImageButton ibCancel;
    @BindView(R.id.btnDealNow)
    Button btnDealNow;
    @BindView(R.id.switchShuffle)
    Switch switchShuffle;
    @BindView(R.id.tvCardsToDeal)
    TextView tvCardsToDeal;
    @BindView(R.id.ibAdd)
    ImageButton ibAdd;
    @BindView(R.id.ibReduce)
    ImageButton ibReduce;

    private int playerCount;
    private int cardCount;
    private int maxAllowed = 0;
    private int currentDealingCount = 0;

    private OnDealOptionListener mListener;
    private Unbinder unbinder;

    public DealingOptionsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param playerCount Parameter 1.
     * @param cardCount Parameter 2.
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
            if(playerCount == 0){
                Toast.makeText(getActivity(), "Please let some players to join first", Toast.LENGTH_LONG).show();
            } else if (playerCount > cardCount){
                Toast.makeText(getActivity(), "More players than cards in game. Please deal cards manually.", Toast.LENGTH_LONG).show();
            } else {
                maxAllowed = cardCount/playerCount; //rounded off to floor
                currentDealingCount = maxAllowed;
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
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
            bundle.putString(Constants.DO_CARD_FACE, spnrCardsFace.getSelectedItem().toString());
            bundle.putBoolean(Constants.DO_SHUFFLE, switchShuffle.isChecked());
            mListener.onDealOptionSelected(bundle);
        }
    }

    @OnClick(R.id.ibCancel)
    public void onCancelPressed(){
        if (mListener != null) {
            mListener.onDealOptionSelected(null);
        }
    }

    @OnClick(R.id.ibAdd)
    public synchronized void increaseCards(View view){
        if(currentDealingCount == maxAllowed) {
            Snackbar.make(view, "Already max cards per player added", Snackbar.LENGTH_LONG).show();
        } else{
            currentDealingCount++;
            tvCardsToDeal.setText(String.valueOf(currentDealingCount));
        }
    }

    @OnClick(R.id.ibReduce)
    public synchronized void decreaseCards(View view){
        if(currentDealingCount == 1){
            Snackbar.make(view, "Can not deal less than 1 card per player", Snackbar.LENGTH_LONG).show();
        } else{
            currentDealingCount--;
            tvCardsToDeal.setText(String.valueOf(currentDealingCount));
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnDealOptionListener) {
            mListener = (OnDealOptionListener) getParentFragment();
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnDealOptionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnDealOptionListener {
        void onDealOptionSelected(Bundle bundle);
    }
}
