package com.wingor_software.mylearn;

import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;

public class CheckBoxStateListener implements CompoundButton.OnCheckedChangeListener
{
    private CheckBox cardsBox;
    private CheckBox quizBox;
    private SeekBar seekBar;
    private TextView questionsCountText;
    private DataBaseHelper dataBaseHelper;

    public CheckBoxStateListener(CheckBox cardsBox, CheckBox quizBox, SeekBar seekBar, TextView questionsCountText, DataBaseHelper dataBaseHelper)
    {
        this.cardsBox = cardsBox;
        this.quizBox = quizBox;
        this.seekBar = seekBar;
        this.questionsCountText = questionsCountText;
        this.dataBaseHelper = dataBaseHelper;
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b)
    {
        boolean cardsChecked = cardsBox.isChecked();
        boolean quizChecked = quizBox.isChecked();

        if(!cardsChecked && !quizChecked)   //zaden
        {
            seekBar.setMax(2);
            seekBar.setProgress(1);
            seekBar.setEnabled(false);
            questionsCountText.setText("Choose exam options");
        }
        else if(cardsChecked && quizChecked)    //oba
        {
            seekBar.setEnabled(true);
            try
            {
                int subID = MainActivity.getCurrentSubject().getSubjectID();
                seekBar.setMax(dataBaseHelper.getCardList(subID).size() + dataBaseHelper.getQuizList(subID).size());
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
        else if(cardsChecked)       //tylko fiszki
        {
            seekBar.setEnabled(true);
            try
            {
                int subID = MainActivity.getCurrentSubject().getSubjectID();
                seekBar.setMax(dataBaseHelper.getCardList(subID).size());
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
        else if(quizChecked)        //tylko pytania
        {
            seekBar.setEnabled(true);
            try
            {
                int subID = MainActivity.getCurrentSubject().getSubjectID();
                seekBar.setMax(dataBaseHelper.getQuizList(subID).size());
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
    }
}
