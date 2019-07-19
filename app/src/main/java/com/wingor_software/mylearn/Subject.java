package com.wingor_software.mylearn;

public class Subject
{
    private int subjectID;
    private String subjectName;
    private int color;

    public Subject(int subjectID, String subjectName, int color) {
        this.subjectID = subjectID;
        this.subjectName = subjectName;
        this.color = color;
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
}
