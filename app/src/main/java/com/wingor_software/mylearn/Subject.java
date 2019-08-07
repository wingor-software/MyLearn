package com.wingor_software.mylearn;

/**
 * Klasa odpowiadająca za informacje o przedmiocie
 */
public class Subject
{
    private int subjectID;
    private String subjectName;
    private int color;
    private int examsTaken;
    private int examsPassed;

    public Subject(int subjectID, String subjectName, int color, int examsTaken, int examsPassed) {
        this.subjectID = subjectID;
        this.subjectName = subjectName;
        this.color = color;
        this.examsTaken = examsTaken;
        this.examsPassed = examsPassed;
    }

    public int getSubjectID() {
        return subjectID;
    }

    public void setSubjectID(int subjectID) {
        this.subjectID = subjectID;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getExamsTaken() {
        return examsTaken;
    }

    public void setExamsTaken(int examsTaken) {
        this.examsTaken = examsTaken;
    }

    public int getExamsPassed() {
        return examsPassed;
    }

    public void setExamsPassed(int examsPassed) {
        this.examsPassed = examsPassed;
    }
}
