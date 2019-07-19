package com.wingor_software.mylearn;

public class Note
{
    private int ID;
    private String title;
    private String content;
    private int subjectID;

    public Note(int ID, String title, String content, int subjectID) {
        this.ID = ID;
        this.title = title;
        this.content = content;
        this.subjectID = subjectID;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getSubjectID() {
        return subjectID;
    }

    public void setSubjectID(int subjectID) {
        this.subjectID = subjectID;
    }
}
