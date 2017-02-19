package org.justcards.android.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import org.justcards.android.fragments.TutorialPageDetailFragment;

/**
 * Created by baphna on 12/7/2016.
 */

public class TutorialPagerAdapter extends FragmentPagerAdapter{

    public TutorialPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        return TutorialPageDetailFragment.newInstance(position + 1);
    }

    @Override
    public int getCount() {
        // Show 3 total pages.
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return String.valueOf(position+1);
    }
}
