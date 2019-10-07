package com.wingor_software.mylearn;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;

public class QuizPagerAdapter extends PagerAdapter
{
    private Context context;
    private DataBaseHelper dataBaseHelper;
    private Dialog myDialog;

    public QuizPagerAdapter(DataBaseHelper dataBaseHelper, Context context, Dialog dialog)
    {
        this.dataBaseHelper = dataBaseHelper;
        this.context = context;
        this.myDialog = dialog;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull final ViewGroup container, int position) {
        try
        {
            TextView question = new TextView(context);
            final Quiz quiz = dataBaseHelper.getQuizList(MainActivity.getCurrentSubject().getSubjectID()).get(position);
            question.setText(quiz.getQuestion());
            question.setGravity(Gravity.CENTER);
            question.setTextColor(context.getResources().getColor(R.color.colorDarkPurple));
            question.setTextSize(20);
            question.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    myDialog.dismiss();
                    final Dialog answerDialog = new Dialog(context);
                    answerDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                    answerDialog.setContentView(R.layout.popup_scrolling_open_quiz_answer);

                    LinearLayout linearLayout = (LinearLayout) answerDialog.findViewById(R.id.openQuizAnswerLinearLayout);
                    linearLayout.setOrientation(LinearLayout.VERTICAL);
                    preperingAnswers(linearLayout, quiz.getGoodAnswers(), true);
                    preperingAnswers(linearLayout, quiz.getBadAnswers(), false);

                    Button button = answerDialog.findViewById(R.id.openQuizAnswerButton);
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            answerDialog.dismiss();
                            myDialog.show();
                        }
                    });

                    answerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    answerDialog.show();
                    return false;
                }
            });
            ((ViewPager) container).addView(question);
            return question;
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    private void preperingAnswers(LinearLayout linearLayout ,ArrayList<String> answers, boolean correct)
    {
        for (int i = 0; i < answers.size(); i++) {
            LinearLayout nestedLayout = new LinearLayout(context);
            nestedLayout.setOrientation(LinearLayout.HORIZONTAL);

            CheckBox checkBox = new CheckBox(context);
            if(correct)
                checkBox.setChecked(true);
            checkBox.setEnabled(false);
            nestedLayout.addView(checkBox);

            TextView textView = new TextView(context);
            textView.setTextSize(15);
            textView.setTextColor(context.getResources().getColor(R.color.colorDarkPurple));
            textView.setText(answers.get(i));
            nestedLayout.addView(textView);

            linearLayout.addView(nestedLayout);
        }
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup collection, int position, @NonNull Object object) {
        ((ViewPager) collection).removeView((View) object);
    }

    @Override
    public int getCount()
    {
        try
        {
            return dataBaseHelper.getQuizList(MainActivity.getCurrentSubject().getSubjectID()).size();
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return 0;
        }
    }
}
