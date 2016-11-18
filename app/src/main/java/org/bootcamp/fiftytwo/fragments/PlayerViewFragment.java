package org.bootcamp.fiftytwo.fragments;

import android.content.Context;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import org.bootcamp.fiftytwo.R;
import org.bootcamp.fiftytwo.models.User;
import org.bootcamp.fiftytwo.utils.Constants;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnPlayerFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PlayerViewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PlayerViewFragment extends CardsListFragment {
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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
        Fragment playerCardsFragment = new CardsListFragment();
        Bundle playerBundle = new Bundle();
        playerBundle.putString(Constants.TAG, Constants.PLAYER_TAG);
        playerCardsFragment.setArguments(playerBundle);
        transaction.replace(R.id.flPlayerContainer, playerCardsFragment, Constants.PLAYER_TAG);

        //Add table cards
        Fragment tableCardsFragment = new CardsListFragment();
        Bundle tableBundle = new Bundle();
        tableBundle.putString(Constants.TAG, Constants.TABLE_TAG);
        tableCardsFragment.setArguments(tableBundle);
        transaction.replace(R.id.flTableContainer, tableCardsFragment, Constants.TABLE_TAG);

        transaction.commit();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onPlayerFragmentInteraction(uri);
        }
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

    public void addNewPlayer(User user) {
        LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(LAYOUT_INFLATER_SERVICE);
        final LinearLayout userLayout = (LinearLayout) layoutInflater.inflate(R.layout.item_user, null);
        params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSPARENT);
        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.x = 0;
        params.y = 0;
        flPlayerViewContainer.addView(userLayout, params);

        userLayout.setOnTouchListener(new View.OnTouchListener() {
            float dX, dY;
            float newX, newY;
            @Override
            public boolean onTouch(View view, MotionEvent event) {

                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:

                        dX = view.getX() - event.getRawX();
                        dY = view.getY() - event.getRawY();
                        break;

                    case MotionEvent.ACTION_MOVE:

                        newX = event.getRawX() + dX;
                        newY = event.getRawY() + dY;

                        if(newX<0)
                            newX = 0;
                        if(newY<0)
                            newY = 0;

                        if (newX+view.getWidth() > flPlayerViewContainer.getWidth())
                            newX = flPlayerViewContainer.getWidth() - view.getWidth();
                        if (newY+view.getHeight() > flPlayerViewContainer.getHeight())
                            newY = flPlayerViewContainer.getHeight() - view.getHeight();


                        view.animate()
                                    .x(newX)
                                    .y(newY)
                                    .setDuration(0)
                                    .start();

                        break;
                    default:
                        return false;
                }
                return true;
            }

        });
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
    public interface OnPlayerFragmentInteractionListener {
        // TODO: Update argument type and name
        void onPlayerFragmentInteraction(Uri uri);
    }
}
