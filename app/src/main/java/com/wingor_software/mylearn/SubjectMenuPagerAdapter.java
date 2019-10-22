package com.wingor_software.mylearn;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.getbase.floatingactionbutton.FloatingActionButton;

public class SubjectMenuPagerAdapter extends FragmentStatePagerAdapter
{
    private int numberOfTabs;
    private Context context;
    private DataBaseHelper dataBaseHelper;
    private FloatingActionButton addingFabButton;

    public SubjectMenuPagerAdapter(FragmentManager fm, int numberOfTabs, Context context, DataBaseHelper dataBaseHelper)
    {
        super(fm);
        this.numberOfTabs = numberOfTabs;
        this.context = context;
        this.dataBaseHelper = dataBaseHelper;
    }

    @Override
    public Fragment getItem(int position) {
        switch(position)
        {
            case 0:
            {
                return new CardListViewFragment(context, dataBaseHelper);
            }
            case 1:
            {
                return new QuizListViewFragment(context, dataBaseHelper);
            }
            case 2:
            {
                return new NoteListViewFragment(context, dataBaseHelper);
            }
            case 3:
            {
                return new ExamFragment(context, dataBaseHelper);
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
