package com.wingor_software.mylearn;

import java.util.ArrayList;

public class Quiz
{
    private int ID;
    private String question;
    private ArrayList<String> goodAnswers;
    private ArrayList<String> badAnswers;
    private ArrayList<Integer> attachedNotes;
    private int subjectID;

    public Quiz(int ID, String question, ArrayList<String> goodAnswers, ArrayList<String> badAnswers, ArrayList<Integer> attachedNotes, int subjectID) {
        this.ID = ID;
        this.question = question;
        this.goodAnswers = goodAnswers;
        this.badAnswers = badAnswers;
        this.attachedNotes = attachedNotes;
        this.subjectID = subjectID;
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
}
