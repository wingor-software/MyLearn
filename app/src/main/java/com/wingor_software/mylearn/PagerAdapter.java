package com.wingor_software.mylearn;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class PagerAdapter extends FragmentStatePagerAdapter
{
    private MyFragment[] fragments;
    private DataBaseHelper dataBaseHelper;

    public PagerAdapter(FragmentManager fm, DataBaseHelper dataBaseHelper) {
        super(fm);
        this.dataBaseHelper = dataBaseHelper;
        fragments = new MyFragment[ExamActivity.getQuestionsCount()];
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return super.getPageTitle(position);
    }

    @Override
    public Fragment getItem(int position) {
        MyFragment myFragment = MyFragment.newInstance(position, dataBaseHelper);
        fragments[position] = myFragment;
        return myFragment;
    }

    @Override
    public int getCount() {
        return ExamActivity.getQuestionsCount();
    }

    public MyFragment[] getFragments()
    {
        return fragments;
    }
}
