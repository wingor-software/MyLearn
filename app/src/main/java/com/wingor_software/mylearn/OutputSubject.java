package com.wingor_software.mylearn;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class OutputSubject implements Serializable
{
    private String subjectName;
    private int subjectColor;

    private ArrayList<Card> cards;

    private ArrayList<Quiz> quizzes;

    private ArrayList<Note> notes;

    private ArrayList<ArrayList<byte[]>> photos;

    public OutputSubject(String subjectName, int subjectColor, List<Card> cards, List<Quiz> quizzes, List<Note> notes, ArrayList<ArrayList<byte[]>> photos)
    {
        this.subjectName = subjectName;
        this.subjectColor = subjectColor;
        this.cards = new ArrayList<>(cards);
        this.quizzes = new ArrayList<>(quizzes);
        this.notes = new ArrayList<>(notes);
        this.photos = photos;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public int getSubjectColor() {
        return subjectColor;
    }

    public ArrayList<Card> getCards() {
        return cards;
    }

    public ArrayList<Quiz> getQuizzes() {
        return quizzes;
    }

    public ArrayList<Note> getNotes() {
        return notes;
    }

    public ArrayList<ArrayList<byte[]>> getPhotos() {
        return photos;
    }
}
