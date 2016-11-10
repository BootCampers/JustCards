package org.bootcamp.fiftytwo.fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.bootcamp.fiftytwo.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class GameViewManagerActivityFragment extends Fragment {

    public GameViewManagerActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_game_view_manager, container, false);
    }
}
