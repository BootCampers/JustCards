package org.bootcamp.fiftytwo.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import org.bootcamp.fiftytwo.R;
import org.bootcamp.fiftytwo.adapters.ScoreAdapter;
import org.bootcamp.fiftytwo.models.User;
import org.bootcamp.fiftytwo.utils.AppUtils;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static org.bootcamp.fiftytwo.utils.AppUtils.isEmpty;
import static org.bootcamp.fiftytwo.utils.Constants.PARAM_PLAYERS;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnScoreFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ScoringFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ScoringFragment extends Fragment {

    private OnScoreFragmentInteractionListener mListener;
    private Unbinder unbinder;

    @BindView(R.id.rvPlayersScores)
    RecyclerView rvPlayersScores;
    @BindView(R.id.btnSave)
    Button btnSave;
    @BindView(R.id.ibCancel)
    ImageButton ibCancel;

    private StaggeredGridLayoutManager staggeredLayoutManager;


    List<User> users = new ArrayList<>();
    private ScoreAdapter scoringAdapter;

    public ScoringFragment() {
        // Required empty public constructor
    }

    public static ScoringFragment newInstance(List<User> users) {
        ScoringFragment fragment = new ScoringFragment();
        Bundle args = new Bundle();
        args.putParcelable(PARAM_PLAYERS, AppUtils.getParcelable(users));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            users = Parcels.unwrap(getArguments().getParcelable(PARAM_PLAYERS));
            if (isEmpty(users)) {
                users = new ArrayList<>();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_scoring, container, false);
        unbinder = ButterKnife.bind(this, view);

        staggeredLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        scoringAdapter = new ScoreAdapter(users);
        rvPlayersScores.setLayoutManager(staggeredLayoutManager);
        rvPlayersScores.setAdapter(scoringAdapter);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnScoreFragmentInteractionListener) {
            mListener = (OnScoreFragmentInteractionListener) getParentFragment();
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnScoreFragmentInteractionListener");
        }
    }

    @OnClick(R.id.btnSave)
    public void saveScore(View view){
        //TODO : save score
        if (mListener != null) {
            mListener.onScoreFragmentInteraction(true);
        }
    }

    @OnClick(R.id.ibCancel)
    public void onCancelPressed() {
        if (mListener != null) {
            mListener.onScoreFragmentInteraction(false);
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
    public interface OnScoreFragmentInteractionListener {
        void onScoreFragmentInteraction(boolean saveClicked);
    }
}
