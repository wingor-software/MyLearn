package com.wingor_software.mylearn;

/**
 * Klasa odpowiadająca za informacje o notatce
 */
public class Note
{
    private int ID;
    private String title;
    private int subjectID;
    private int color;

    private String content;
    private String filePath;

    public Note(int ID, String title, String content, int subjectID, int color, String filePath) {
        this.ID = ID;
        this.title = title;
        this.subjectID = subjectID;
        this.color = color;

        this.content = content;
        this.filePath = filePath;
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

    public String getFilePath(){
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
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
}
