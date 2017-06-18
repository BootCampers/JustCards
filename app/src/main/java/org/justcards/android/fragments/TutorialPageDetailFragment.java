package org.justcards.android.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;

import org.justcards.android.R;

/**
 * Created by baphna on 12/7/2016.
 */

public class TutorialPageDetailFragment extends Fragment {

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    public TutorialPageDetailFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static TutorialPageDetailFragment newInstance(int sectionNumber) {
        TutorialPageDetailFragment fragment = new TutorialPageDetailFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.item_tutorial, container, false);
        TextView textView = (TextView) rootView.findViewById(R.id.tvTitle);
        String[] tutorialMessages = getResources().getStringArray(R.array.array_tutorial_strings);
        int currentIndex = getArguments().getInt(ARG_SECTION_NUMBER)-1;
        textView.setText(tutorialMessages[currentIndex]);
        ImageView imageView = (ImageView) rootView.findViewById(R.id.ivGif);
        GlideDrawableImageViewTarget imageViewTarget = new GlideDrawableImageViewTarget(imageView);
        switch (currentIndex) {
            case 0:
                Glide.with(this).load(R.raw.start_game).into(imageViewTarget);
                break;
            case 1:
                Glide.with(this).load(R.raw.player_join).into(imageViewTarget);
                break;
            case 2:
                Glide.with(this).load(R.raw.deal_options).into(imageViewTarget);
                break;
            case 3:
                Glide.with(this).load(R.raw.toggle_chat).into(imageViewTarget);
                break;
            case 4:
                Glide.with(this).load(R.raw.switch_dealer_player).into(imageViewTarget);
                break;
        }
        return rootView;
    }
}
