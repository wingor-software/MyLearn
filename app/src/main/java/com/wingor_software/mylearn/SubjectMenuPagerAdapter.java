package com.wingor_software.mylearn;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class SubjectMenuPagerAdapter extends FragmentStatePagerAdapter
{
    private int numberOfTabs;

    public SubjectMenuPagerAdapter(FragmentManager fm, int numberOfTabs)
    {
        super(fm);
        this.numberOfTabs = numberOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch(position)
        {
            case 0:
            {

            }
            case 1:
            {

            }
            case 2:
            {

            }
            case 3:
            {

            }
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return numberOfTabs;
    }
}
