package com.wingor_software.mylearn;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
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
        mViewPager.setOffscreenPageLimit(10);
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
    
    public LinearLayout[] getExamableLayouts()
    {
        LinearLayout[] examableLayouts = new LinearLayout[10];
        for (int i = 0; i < mPageAdapter.getFragments().length; i++) {
            examableLayouts[i] = mPageAdapter.getFragments()[i].getExamableLayout();
        }
        return examableLayouts;
    }

    public void checkAnswers(View view)
    {
        LinearLayout[] examableLayouts = getExamableLayouts();
        int all = 10;
        int correct = 0;
        for (int i = 0; i < examableLayouts.length; i++) {
            if(examableLayouts[i] == null)
            {
                all--;
                continue;
            }
            switch(examableLayouts[i].getTag().toString())
            {
                case "card":
                {
                    if(isCorrectCardAnswer(examableLayouts[i])) correct++;
                    break;
                }
                case "quiz":
                {
                    if(isCorrectQuizAnswer(examableLayouts[i])) correct++;
                    break;
                }
            }
        }

        if (((float) correct / (float) all >= 0.5)) {
            SubjectActivity.addPassedExam();
        } else {
            SubjectActivity.addNotPassedExam();
        }

        showExamResultPopup(correct, all);
    }

    private void showExamResultPopup(int correct, int all)
    {
        final Dialog myDialog = new Dialog(ExamActivity.this);
        myDialog.setContentView(R.layout.popup_result_card);
        Button okButton = myDialog.findViewById(R.id.okCardButton);
        TextView result = myDialog.findViewById(R.id.resultText);
        TextView correctAnswer = myDialog.findViewById(R.id.correctAnswer);
        result.setText(correct + "/" + all);
        result.setTextColor(Color.GREEN);
        correctAnswer.setVisibility(View.INVISIBLE);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myDialog.dismiss();
                finish();
            }
        });
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();
    }

    public boolean isCorrectCardAnswer(LinearLayout layout)
    {
        EditText editText = (EditText) layout.getChildAt(1);
        return (editText.getText().toString().equals(editText.getTag().toString()));
    }

    public boolean isCorrectQuizAnswer(LinearLayout layout)
    {
        for (int i = 1; i < layout.getChildCount(); i++) {
            CheckBox box = (CheckBox) layout.getChildAt(i);
            if(!((box.isChecked() && box.getTag().toString().equals("correct")) || (!box.isChecked() && box.getTag().toString().equals("incorrect"))))
            {
                return false;
            }
        }
        return true;
    }
}
