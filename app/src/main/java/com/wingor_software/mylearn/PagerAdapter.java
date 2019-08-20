package com.wingor_software.mylearn;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class PagerAdapter extends FragmentStatePagerAdapter
{
    private MyFragment[] fragments = new MyFragment[10];

    public PagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return super.getPageTitle(position);
    }

    @Override
    public Fragment getItem(int position) {
        MyFragment myFragment = MyFragment.newInstance(position);
        fragments[position] = myFragment;
        return myFragment;
    }

    @Override
    public int getCount() {
        return 10;
    }

    public MyFragment[] getFragments()
    {
        return fragments;
    }
}
