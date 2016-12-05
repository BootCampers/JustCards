package org.bootcamp.fiftytwo.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.bootcamp.fiftytwo.R;
import org.bootcamp.fiftytwo.adapters.RoundWinnersAdapter;
import org.bootcamp.fiftytwo.models.User;
import org.bootcamp.fiftytwo.utils.AppUtils;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static org.bootcamp.fiftytwo.utils.AppUtils.isEmpty;
import static org.bootcamp.fiftytwo.utils.Constants.PARAM_PLAYERS;

public class RoundWinnersFragment extends DialogFragment {

    private List<User> winners = new ArrayList<>();
    private Unbinder unbinder;

    @BindView(R.id.rvWinners)
    RecyclerView rvWinners;

    public RoundWinnersFragment() {
        // Required empty public constructor
    }

    public static RoundWinnersFragment newInstance(List<User> winners) {
        RoundWinnersFragment fragment = new RoundWinnersFragment();
        Bundle args = new Bundle();
        args.putParcelable(PARAM_PLAYERS, AppUtils.getParcelable(winners));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            winners = Parcels.unwrap(getArguments().getParcelable(PARAM_PLAYERS));
            if (isEmpty(winners)) {
                winners = new ArrayList<>();
            }
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_round_winners, container, false);
        unbinder = ButterKnife.bind(this, view);

        StaggeredGridLayoutManager staggeredLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        RoundWinnersAdapter roundWinnersAdapter = new RoundWinnersAdapter(winners);
        rvWinners.setLayoutManager(staggeredLayoutManager);
        rvWinners.setAdapter(roundWinnersAdapter);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}