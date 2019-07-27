package com.wingor_software.mylearn;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Klasa odpowiadająca za informacje o fiszce
 */

public class Card
{
    private int ID;
    private String word;
    private String answer;
    private int subjectID;
    private ArrayList<Integer> notesList;

    public Card(int ID, String word, String answer, int subjectID) {
        this.ID = ID;
        this.word = word;
        this.answer = answer;
        this.subjectID = subjectID;
        this.notesList = new ArrayList<>();
    }

    public Card(int ID, String word, String answer, int subjectID, Integer[] attachedNotes)
    {
        this.ID = ID;
        this.word = word;
        this.answer = answer;
        this.subjectID = subjectID;
        this.notesList = new ArrayList<>(Arrays.asList(attachedNotes));
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public int getSubjectID() {
        return subjectID;
    }

    public void setSubjectID(int subjectID) {
        this.subjectID = subjectID;
    }

    public void attachNote(int noteID)
    {
        notesList.add(noteID);
    }

    public void deleteNoteAttachment(int noteID)
    {
        notesList.remove(noteID);
    }

    public String notesListToString()
    {
        StringBuilder stringBuilder = new StringBuilder();
        for (Integer noteID : notesList)
        {
            if(stringBuilder.length() != 0)
                stringBuilder.append("\n");
            stringBuilder.append(noteID);
        }
        return stringBuilder.toString();
    }
}
