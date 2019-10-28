package com.wingor_software.mylearn;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.getbase.floatingactionbutton.FloatingActionButton;

public class SubjectMenuPagerAdapter extends FragmentStatePagerAdapter
{
    private int numberOfTabs;
    private Context context;
    private DataBaseHelper dataBaseHelper;

    private String[] pageTitles = new String[]{"Cards", "Quiz", "Notes", "Exams"};
    private Fragment[] currentFragments;

    public SubjectMenuPagerAdapter(FragmentManager fm, int numberOfTabs, Context context, DataBaseHelper dataBaseHelper)
    {
        super(fm);
        this.numberOfTabs = numberOfTabs;
        this.context = context;
        this.dataBaseHelper = dataBaseHelper;
        currentFragments = new Fragment[4];
    }

    @Override
    public Fragment getItem(int position) {
        switch(position)
        {
            case 0:
            {
                CardListViewFragment cardListViewFragment = new CardListViewFragment(context, dataBaseHelper);
                currentFragments[0] = cardListViewFragment;
                return cardListViewFragment;
            }
            case 1:
            {
                QuizListViewFragment quizListViewFragment = new QuizListViewFragment(context, dataBaseHelper);
                currentFragments[1] = quizListViewFragment;
                return quizListViewFragment;
            }
            case 2:
            {
                NoteListViewFragment noteListViewFragment = new NoteListViewFragment(context, dataBaseHelper);
                currentFragments[2] = noteListViewFragment;
                return noteListViewFragment;
            }
            case 3:
            {
                ExamFragment examFragment = new ExamFragment(context, dataBaseHelper);
                currentFragments[3] = examFragment;
                return examFragment;
            }
            default:
                return null;
        }
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return pageTitles[position];
    }

    @Override
    public int getCount() {
        return numberOfTabs;
    }

    public Fragment getCurrentFragment(int position)
    {
        return currentFragments[position];
    }
}
