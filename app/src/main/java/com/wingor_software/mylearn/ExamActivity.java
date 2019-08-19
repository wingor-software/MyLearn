package com.wingor_software.mylearn;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

public class ExamActivity extends AppCompatActivity {

//    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private PagerAdapter mPageAdapter;
    private static TextView pageCount;
    private static DataBaseHelper dataBaseHelper;
    private static Examable[] examables;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam);
        Toolbar toolbar = findViewById(R.id.toolbar);
        try
        {
            toolbar.setTitle("Exam: " + MainActivity.getCurrentSubject().getSubjectName());
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        setSupportActionBar(toolbar);
        mPageAdapter = new PagerAdapter(getSupportFragmentManager());
//        mTabLayout = (TabLayout) findViewById(R.id.tabLayout);
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mPageAdapter);
        pageCount = (TextView) findViewById(R.id.page_count);
        dataBaseHelper = new DataBaseHelper(this);
        examables = getExamableList(10);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                String str = (position + 1) + "/10";
                pageCount.setText(str);
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public static TextView getPageCount() {
        return pageCount;
    }

    public static Examable[] getExamableList(int size)
    {
        HashSet<Examable> examableHashSet = new HashSet<>();

        if(SubjectActivity.getExamType() == ExamType.CARDS || SubjectActivity.getExamType() == ExamType.ALL)
        {
            try
            {
                ArrayList<Card> cards = new ArrayList<>(dataBaseHelper.getCardList(MainActivity.getCurrentSubject().getSubjectID()));
                examableHashSet.addAll(cards);
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }

        if (SubjectActivity.getExamType() == ExamType.QUESTIONS || SubjectActivity.getExamType() == ExamType.ALL)
        {
            try
            {
                ArrayList<Quiz> quizzes = new ArrayList<>(dataBaseHelper.getQuizList(MainActivity.getCurrentSubject().getSubjectID()));
                examableHashSet.addAll(quizzes);
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }

        Examable[] examablesTab = new Examable[size];
        Iterator it = examableHashSet.iterator();
        for (int i = 0; i < size; i++) {
            if(it.hasNext())
                examablesTab[i] = (Examable) it.next();
            else
                break;
        }
        return examablesTab;
    }

    public static Examable[] getExamables() {
        return examables;
    }
}
