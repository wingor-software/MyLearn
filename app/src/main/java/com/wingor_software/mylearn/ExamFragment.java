package com.wingor_software.mylearn;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.CompoundButtonCompat;
import androidx.fragment.app.Fragment;

public class ExamFragment extends Fragment
{
    private Context context;
    private DataBaseHelper dataBaseHelper;

    public ExamFragment(Context context, DataBaseHelper dataBaseHelper)
    {
        this.context = context;
        this.dataBaseHelper = dataBaseHelper;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.subject_exam_fragment, null);
        ConstraintLayout scoreLayout = view.findViewById(R.id.scoreLayout);

        ProgressBar progressBar = scoreLayout.findViewById(R.id.scoreBar);

        int light = getResources().getColor(R.color.colorPrimary);
        int dark = getResources().getColor(R.color.white);

        scoreLayout.setBackgroundColor((dataBaseHelper.getDisplayMode() == DisplayMode.LIGHT) ? getResources().getColor(R.color.white) : getResources().getColor(R.color.colorDarkModeBackground));

        TextView scoreText = scoreLayout.findViewById(R.id.scoreText);
        scoreText.setTextColor((dataBaseHelper.getDisplayMode() == DisplayMode.LIGHT) ? light : dark);

        Button examStartButton = (Button) view.findViewById(R.id.examStartButton);
        examStartButton.setTextColor((dataBaseHelper.getDisplayMode() == DisplayMode.LIGHT) ? light : dark);

        final CheckBox checkBoxCards = (CheckBox) view.findViewById(R.id.checkBoxCards);
        checkBoxCards.setTextColor((dataBaseHelper.getDisplayMode() == DisplayMode.LIGHT) ? light : dark);
        checkBoxCards.setChecked(false);

        final CheckBox checkBoxQuiz = (CheckBox) view.findViewById(R.id.checkBoxQuestions);
        checkBoxQuiz.setTextColor((dataBaseHelper.getDisplayMode() == DisplayMode.LIGHT) ? light : dark);
        checkBoxQuiz.setChecked(false);

        SeekBar seekBar = (SeekBar) view.findViewById(R.id.questionCountSeekBar);
        seekBar.setMax(2);
        seekBar.setEnabled(false);
        seekBar.setProgress(1);

        final TextView questionsCount = (TextView) view.findViewById(R.id.questionsCountText);
        questionsCount.setText("Choose exam options");

        examStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startExam(checkBoxCards, checkBoxQuiz);
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                questionsCount.setText("" + i);
                SubjectActivity.setQuestionsCountToExam(i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });

        checkBoxCards.setOnCheckedChangeListener(new CheckBoxStateListener(checkBoxCards, checkBoxQuiz, seekBar, questionsCount, dataBaseHelper));
        checkBoxQuiz.setOnCheckedChangeListener(new CheckBoxStateListener(checkBoxCards, checkBoxQuiz, seekBar, questionsCount, dataBaseHelper));

        dark = getResources().getColor(R.color.colorLightPrimary);

        CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkBoxQuestions);
        for (int i = 0; i < 2; i++) {
            if (Build.VERSION.SDK_INT < 21) {
                CompoundButtonCompat.setButtonTintList(checkBox, ColorStateList.valueOf((dataBaseHelper.getDisplayMode() == DisplayMode.LIGHT) ? light : dark));//Use android.support.v4.widget.CompoundButtonCompat when necessary else
            } else {
                checkBox.setButtonTintList(ColorStateList.valueOf((dataBaseHelper.getDisplayMode() == DisplayMode.LIGHT) ? light : dark));//setButtonTintList is accessible directly on API>19
            }
            checkBox = (CheckBox) view.findViewById(R.id.checkBoxCards);
        }

        int examsTaken = 0;
        int examsPassed = 0;
        try
        {
            examsTaken = MainActivity.getCurrentSubject().getExamsTaken();
            examsPassed = MainActivity.getCurrentSubject().getExamsPassed();
            progressBar.setMax(examsTaken);
            progressBar.setProgress(examsPassed);
            scoreText.setText(new String(examsPassed + "/" + examsTaken));
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        return view;
    }

    public void startExam(CheckBox cards, CheckBox questions) {
        Intent intent;

        int cardsCount = 0;
        int quizzesCount = 0;
        try
        {
            cardsCount = dataBaseHelper.getCardList(MainActivity.getCurrentSubject().getSubjectID()).size();
            quizzesCount = dataBaseHelper.getQuizList(MainActivity.getCurrentSubject().getSubjectID()).size();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        if(SubjectActivity.getQuestionsCountToExam() == 0)
        {
            Toast.makeText(context, "Please, select exam size", Toast.LENGTH_LONG).show();
        }
        else if(cards.isChecked() && questions.isChecked() && cardsCount + quizzesCount > 0)
        {
            SubjectActivity.setExamType(ExamType.ALL);
            Toast.makeText(context, "Cards and questions exam", Toast.LENGTH_LONG).show();
            intent = new Intent(context, ExamActivity.class);
            startActivity(intent);
        }
        else if(cards.isChecked() && cardsCount > 0)
        {
            SubjectActivity.setExamType(ExamType.CARDS);
            Toast.makeText(context, "Cards exam", Toast.LENGTH_LONG).show();
            intent = new Intent(context, ExamActivity.class);
            startActivity(intent);
        }
        else if(questions.isChecked() && quizzesCount > 0)
        {
            SubjectActivity.setExamType(ExamType.QUESTIONS);
            Toast.makeText(context, "Questions exam", Toast.LENGTH_LONG).show();
            intent = new Intent(context, ExamActivity.class);
            startActivity(intent);
        }
        else if(!cards.isChecked() && !questions.isChecked())
        {
            Toast.makeText(context, "Please, select exam type", Toast.LENGTH_LONG).show();
        }
        else
        {
            Toast.makeText(context, "Looks like you don't have any cards or questions", Toast.LENGTH_LONG).show();
        }
    }
}
