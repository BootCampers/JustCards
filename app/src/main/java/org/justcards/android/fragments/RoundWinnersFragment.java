package org.justcards.android.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.bumptech.glide.Glide;

import org.justcards.android.R;
import org.justcards.android.adapters.RoundWinnersAdapter;
import org.justcards.android.models.User;
import org.justcards.android.utils.AppUtils;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static org.justcards.android.utils.AppUtils.isEmpty;
import static org.justcards.android.utils.Constants.PARAM_PLAYERS;

public class RoundWinnersFragment extends DialogFragment {

    private List<User> winners = new ArrayList<>();
    private Unbinder unbinder;

    @BindView(R.id.rvWinners) RecyclerView rvWinners;
    @BindView(R.id.ivFireworks) ImageView ivFireworks;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_round_winners, container, false);
        unbinder = ButterKnife.bind(this, view);

        StaggeredGridLayoutManager staggeredLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        RoundWinnersAdapter roundWinnersAdapter = new RoundWinnersAdapter(winners);
        rvWinners.setLayoutManager(staggeredLayoutManager);
        rvWinners.setAdapter(roundWinnersAdapter);

        Glide.with(getActivity())
                .asGif()
                .load(R.drawable.fireworks)
                .into(ivFireworks);

        return view;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);

        // request a window without the title
        if (dialog.getWindow() != null) {
            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }

        return dialog;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}