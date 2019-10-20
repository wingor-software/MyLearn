package com.wingor_software.mylearn.ui.main;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.wingor_software.mylearn.R;
import com.wingor_software.mylearn.tutorial.FragmentTutorial1;
import com.wingor_software.mylearn.tutorial.FragmentTutorial2;
import com.wingor_software.mylearn.tutorial.FragmentTutorial3;
import com.wingor_software.mylearn.tutorial.FragmentTutorial4;
import com.wingor_software.mylearn.tutorial.FragmentTutorial5;
import com.wingor_software.mylearn.tutorial.FragmentTutorial6;
import com.wingor_software.mylearn.tutorial.FragmentTutorial7;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    @StringRes
    private static final int[] TAB_TITLES = new int[]{
            R.string.tab_text_1,
            R.string.tab_text_2,
            R.string.tab_text_3,
            R.string.tab_text_4,
            R.string.tab_text_5,
            R.string.tab_text_6,
            R.string.tab_text_7
    };
    private final Context mContext;

    public SectionsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
//        return PlaceholderFragment.newInstance(position + 1);
        switch (position)
        {
            case 0:
            {
                FragmentTutorial1 tab1 = new FragmentTutorial1();
                return tab1;
            }
            case 1:
            {
                FragmentTutorial2 tab2 = new FragmentTutorial2();
                return tab2;
            }
            case 2:
            {
                FragmentTutorial3 tab3 = new FragmentTutorial3();
                return tab3;
            }
            case 3:
            {
                FragmentTutorial4 tab4 = new FragmentTutorial4();
                return tab4;
            }
            case 4:
            {
                FragmentTutorial5 tab5 = new FragmentTutorial5();
                return tab5;
            }
            case 5:
            {
                FragmentTutorial6 tab6 = new FragmentTutorial6();
                return tab6;
            }
            case 6:
            {
                FragmentTutorial7 tab7 = new FragmentTutorial7();
                return tab7;
            }
            default:
            {
                return null;
            }
        }
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }

    @Override
    public int getCount() {
        // Show 7 total pages.
        return 7;
    }
}