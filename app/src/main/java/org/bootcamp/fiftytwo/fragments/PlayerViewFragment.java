package org.bootcamp.fiftytwo.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.google.gson.Gson;

import org.bootcamp.fiftytwo.R;
import org.bootcamp.fiftytwo.application.FiftyTwoApplication;
import org.bootcamp.fiftytwo.interfaces.Observable;
import org.bootcamp.fiftytwo.interfaces.Observer;
import org.bootcamp.fiftytwo.models.Card;
import org.bootcamp.fiftytwo.models.User;
import org.bootcamp.fiftytwo.receivers.CardExchangeReceiver;
import org.bootcamp.fiftytwo.utils.CardUtil;
import org.bootcamp.fiftytwo.utils.Constants;
import org.json.JSONException;
import org.json.JSONObject;
import org.bootcamp.fiftytwo.views.PlayerViewHelper;

import java.util.List;

import static org.bootcamp.fiftytwo.utils.Constants.PLAYER_TAG;
import static org.bootcamp.fiftytwo.utils.Constants.TABLE_TAG;
import static org.bootcamp.fiftytwo.views.PlayerViewHelper.getPlayerFragmentTag;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnPlayerFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PlayerViewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PlayerViewFragment extends Fragment
        implements Observer {

    CardExchangeReceiver cardExchangeReceiver;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnPlayerFragmentInteractionListener mListener;
    private int REQUEST_CODE = 999;

    FrameLayout flPlayerViewContainer;
    private WindowManager.LayoutParams params;
    private FiftyTwoApplication fiftyTwoApp;
    private View rootView;

    public PlayerViewFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PlayerViewFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PlayerViewFragment newInstance(String param1, String param2) {
        PlayerViewFragment fragment = new PlayerViewFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        fiftyTwoApp = (FiftyTwoApplication) getActivity().getApplication();
        fiftyTwoApp.addObserver(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_player_view, container, false);
        flPlayerViewContainer = (FrameLayout) rootView.findViewById(R.id.flPlayerViewContainer);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        insertNestedFragments();
    }

    /*http://guides.codepath.com/android/Creating-and-Using-Fragments#nesting-fragments-within-fragments*/
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
            throw new RuntimeException(context.toString()
                    + " must implement OnPlayerFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onUpdate(Observable o, final Object identifier, final Object arg) {
        String whatUpdated = identifier.toString();
        Log.d(Constants.TAG, PlayerViewFragment.class.getSimpleName() + "-onUpdate-" + whatUpdated);

        if (whatUpdated.equals(Constants.PARSE_NEW_PLAYER_ADDED)) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    PlayerViewHelper.addPlayer(getActivity(), flPlayerViewContainer.getId(), (User) arg);
                    //TODO: Add to the log
                }
            });
        } else if (whatUpdated.equals(Constants.PARSE_PLAYER_LEFT)) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String userViewTag = getPlayerFragmentTag((User) arg);
                    Fragment userFragment = getChildFragmentManager().findFragmentByTag(userViewTag);
                    if (userFragment != null) {
                        getChildFragmentManager().beginTransaction().remove(userFragment).commit();
                    } else {
                        Log.e(Constants.TAG, "NULL Failed to remove view for " + userViewTag);
                    }
                    //TODO: Add to the log
                }
            });
        } else if (whatUpdated.equals(Constants.PARSE_PLAYERS_EXCHANGE_CARDS)){

            try {
                JSONObject details = (JSONObject) arg;
                User fromUser = userFromJson(details);
                JSONObject toUserDetails = details.getJSONObject(Constants.PLAYER_TAG);
                User toUser = userFromJson(toUserDetails);
                Gson gson = new Gson();
                String cardString = null;
                cardString = gson.toJson(details.getString(Constants.PARAM_CARDS));
                Log.d(Constants.TAG, "cardExchanged is--"+ cardString);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }  else if (whatUpdated.equals(Constants.PARSE_TABLE_CARD_EXCHANGE)){

            try {
                JSONObject details = (JSONObject) arg;
                User fromUser = userFromJson(details);
                JSONObject toUserDetails = details.getJSONObject(Constants.PLAYER_TAG);
                User toUser = userFromJson(toUserDetails);
                Gson gson = new Gson();
                String cardString = null;
                cardString = gson.toJson(details.getString(Constants.PARAM_CARDS));
                boolean pickedOrPlacedOnTable = details.getBoolean(Constants.TABLE_PICKED);
                Log.d(Constants.TAG, "cardExchanged is--"+ cardString);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private User userFromJson(JSONObject jsonObject){
         try {
            String displayName = jsonObject.getString(Constants.DISPLAY_NAME);
            String profilePic = jsonObject.getString(Constants.USER_AVATAR_URI);
            String userId = jsonObject.getString(Constants.USER_ID);
            Log.d(Constants.TAG, "userFromJson--"+displayName+"--"+profilePic+"--"+userId);
             return new User(profilePic, displayName, userId);
         } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public interface OnPlayerFragmentInteractionListener {
        // TODO: Update argument type and name
        void onPlayerFragmentInteraction(Uri uri);
    }
}