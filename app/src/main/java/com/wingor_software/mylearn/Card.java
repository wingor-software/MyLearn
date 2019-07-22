package com.wingor_software.mylearn;

/**
 * Klasa odpowiadająca za informacje o fiszce
 */

public class Card
{
    private int ID;
    private String word;
    private String answer;
    private int subjectID;

    public Card(int ID, String word, String answer, int subjectID) {
        this.ID = ID;
        this.word = word;
        this.answer = answer;
        this.subjectID = subjectID;
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
}
