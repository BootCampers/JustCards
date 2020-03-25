package org.justcards.android.adapters;

import android.content.Context;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import org.justcards.android.R;
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
        // Show 6 total pages.
        return mContext.getResources().getStringArray(R.array.array_tutorial_strings).length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return String.valueOf(position+1);
    }
}
