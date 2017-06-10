package org.justcards.android.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import org.justcards.android.fragments.TutorialPageDetailFragment;

/**
 * Created by baphna on 12/7/2016.
 */

public class TutorialPagerAdapter extends FragmentPagerAdapter{

    private Context mContext;

    public TutorialPagerAdapter(FragmentManager fm, Context mContext) {
        super(fm);
        this.mContext = mContext;
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        return TutorialPageDetailFragment.newInstance(position + 1);
    }

    @Override
    public int getCount() {
        // Show 5 total pages.
        return 5;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return String.valueOf(position+1);
    }
}
