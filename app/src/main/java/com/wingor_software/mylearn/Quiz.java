package com.wingor_software.mylearn;

import android.content.Context;
import android.view.Gravity;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;

public class Quiz implements Serializable, Examable
{
    private int ID;
    private String question;
    private ArrayList<String> goodAnswers;
    private ArrayList<String> badAnswers;
    private int subjectID;
    private ArrayList<Integer> attachedNotes;
    private int color;

    public Quiz(int ID, String question, ArrayList<String> goodAnswers, ArrayList<String> badAnswers, int subjectID, ArrayList<Integer> attachedNotes, int color) {
        this.ID = ID;
        this.question = question;
        this.goodAnswers = goodAnswers;
        this.badAnswers = badAnswers;
        this.subjectID = subjectID;
        this.attachedNotes = attachedNotes;
        this.color = color;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public ArrayList<String> getGoodAnswers() {
        return goodAnswers;
    }

    public void setGoodAnswers(ArrayList<String> goodAnswers) {
        this.goodAnswers = goodAnswers;
    }

    public ArrayList<String> getBadAnswers() {
        return badAnswers;
    }

    public void setBadAnswers(ArrayList<String> badAnswers) {
        this.badAnswers = badAnswers;
    }

    public ArrayList<Integer> getAttachedNotes() {
        return attachedNotes;
    }

    public void setAttachedNotes(ArrayList<Integer> attachedNotes) {
        this.attachedNotes = attachedNotes;
    }

    public int getSubjectID() {
        return subjectID;
    }

    public void setSubjectID(int subjectID) {
        this.subjectID = subjectID;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String attachedNotesToString()
    {
        StringBuilder stringBuilder = new StringBuilder();
        for (Integer noteID : attachedNotes)
        {
            if(stringBuilder.length() != 0)
                stringBuilder.append("\n");
            stringBuilder.append(noteID);
        }
        return stringBuilder.toString();
    }

    public String goodAnwersToString()
    {
        StringBuilder stringBuilder = new StringBuilder();
        if(goodAnswers == null) return "";
        for (String answer: goodAnswers)
        {
            if(stringBuilder.length() != 0)
                stringBuilder.append("\n");
            stringBuilder.append(answer);
        }
        return stringBuilder.toString();
    }

    public String badAnwersToString()
    {
        StringBuilder stringBuilder = new StringBuilder();
        if(badAnswers == null) return "";
        for (String answer: badAnswers)
        {
            if(stringBuilder.length() != 0)
                stringBuilder.append("\n");
            stringBuilder.append(answer);
        }
        return stringBuilder.toString();
    }

    public void deleteNoteAttachment(int noteID)
    {
        attachedNotes.remove(noteID);
    }

    @Override
    public LinearLayout getLayoutToDisplay(Context context) {
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setTag("quiz");

        TextView question = new TextView(context);
        question.setText(this.question);
        question.setGravity(Gravity.CENTER);
        linearLayout.addView(question);

        HashSet<Answer> answersSet = new HashSet<>();
        for (int i = 0; i < getGoodAnswers().size(); i++) {
            answersSet.add(new Answer(getGoodAnswers().get(i), true));
        }
        for (int i = 0; i < getBadAnswers().size(); i++) {
            answersSet.add(new Answer(getBadAnswers().get(i), false));
        }

        for(Answer answer : answersSet)
        {
            CheckBox checkBox = new CheckBox(context);
            checkBox.setText(answer.getAnswer());
            checkBox.setGravity(Gravity.CENTER);
            if(answer.isCorrect()) checkBox.setTag("correct");
            else checkBox.setTag("incorrect");
            linearLayout.addView(checkBox);
        }

        return linearLayout;
    }
}
