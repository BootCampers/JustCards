package org.justcards.android.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.justcards.android.R;
import org.justcards.android.adapters.TutorialPagerAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import github.chenupt.springindicator.SpringIndicator;

import static org.justcards.android.utils.Constants.ARG_FROM_PAGE;
import static org.justcards.android.utils.Constants.ARG_TO_PAGE;

public class TutorialFragment extends Fragment {

    @BindView(R.id.btn_tutorial_done)
    Button btnTutorialDone;
    private TutorialPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private int fromPage;
    private int toPage;
    private Unbinder unbinder;

    public TutorialFragment() {
        // Required empty public constructor
    }

    public static TutorialFragment newInstance(int fromPage, int toPage) {
        TutorialFragment fragment = new TutorialFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_FROM_PAGE, fromPage);
        args.putInt(ARG_TO_PAGE, toPage);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            fromPage = getArguments().getInt(ARG_FROM_PAGE);
            toPage = getArguments().getInt(ARG_TO_PAGE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tutorial, container, false);
        unbinder = ButterKnife.bind(this, view);

        mSectionsPagerAdapter = new TutorialPagerAdapter(getChildFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) view.findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        SpringIndicator springIndicator = (SpringIndicator) view.findViewById(R.id.indicator);
        springIndicator.setViewPager(mViewPager);

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @OnClick(R.id.btn_tutorial_done)
    public void doneTutorial(View view){
        getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        unbinder.unbind();
    }

}
