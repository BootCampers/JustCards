package org.justcards.android.fragments;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import org.justcards.android.R;
import org.justcards.android.adapters.ScoreAdapter;
import org.justcards.android.models.User;
import org.justcards.android.utils.AppUtils;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static org.justcards.android.utils.AppUtils.isEmpty;
import static org.justcards.android.utils.Constants.PARAM_PLAYERS;

public class ScoringFragment extends Fragment {

    private List<User> users = new ArrayList<>();
    private OnScoreFragmentListener mListener;
    private Unbinder unbinder;

    @BindView(R.id.rvPlayersScores) RecyclerView rvPlayersScores;
    @BindView(R.id.btnSave) Button btnSave;
    @BindView(R.id.ibCancel) ImageButton ibCancel;

    private HashMap<User, Integer> savedScoreMap = new HashMap<>();
    private List<User> roundWinners = new ArrayList<>();
    private int roundHighScore = 0;

    public interface OnScoreFragmentListener {
        void onScore(boolean saveClicked);

        void roundWinners(List<User> roundWinners);
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
            for (User user : users) {
                savedScoreMap.put(user, user.getScore());
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scoring, container, false);
        unbinder = ButterKnife.bind(this, view);

        StaggeredGridLayoutManager staggeredLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        ScoreAdapter scoringAdapter = new ScoreAdapter(users);
        rvPlayersScores.setLayoutManager(staggeredLayoutManager);
        rvPlayersScores.setAdapter(scoringAdapter);
        return view;
    }

    @OnClick(R.id.btnSave)
    public void saveScore() {
        if (mListener != null) {
            mListener.onScore(true);
        }
        //Scores of all users is already updated to latest in Score adapter
        if (users.size() >= 2) {
            for (User user : users) {
                int scoreDelta = user.getScore() - savedScoreMap.get(user);
                if (scoreDelta > roundHighScore) {
                    roundWinners.clear();
                    roundWinners.add(user);
                    roundHighScore = scoreDelta;
                } else if (scoreDelta == roundHighScore) {
                    roundWinners.add(user);
                    roundHighScore = scoreDelta;
                }
            }
            if (mListener != null) {
                mListener.roundWinners(roundWinners);
            }
        }
    }

    @OnClick(R.id.ibCancel)
    public void onCancelPressed() {
        if (mListener != null) {
            mListener.onScore(false);
        }
        //Go back to original score
        for (User user : users) {
            user.setScore(savedScoreMap.get(user));
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnScoreFragmentListener) {
            mListener = (OnScoreFragmentListener) getParentFragment();
        } else {
            throw new RuntimeException(context.toString() + " must implement OnScoreFragmentListener");
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