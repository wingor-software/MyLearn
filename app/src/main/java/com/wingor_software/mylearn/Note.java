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

    private final boolean isPhotoNote;

    private String content;
    private String filePath;

    public Note(int ID, String title, String content, int subjectID, int color) {
        this.ID = ID;
        this.title = title;
        this.subjectID = subjectID;
        this.color = color;

        this.isPhotoNote = false;

        this.content = content;
        this.filePath = null;
    }

    public Note(int ID, String title, int subjectID, int color, String filePath) {
        this.ID = ID;
        this.title = title;
        this.subjectID = subjectID;
        this.color = color;

        this.isPhotoNote = true;

        this.content = null;
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

    public String getContent() throws NotThisTypeOfNoteException {
        if(!isPhotoNote)
            return content;
        else
            throw new NotThisTypeOfNoteException();
    }

    public void setContent(String content) throws NotThisTypeOfNoteException {
        if(!isPhotoNote)
            this.content = content;
        else
            throw new NotThisTypeOfNoteException();
    }

    public String getFilePath() throws NotThisTypeOfNoteException{
        if(!isPhotoNote)
            return filePath;
        else
            throw new NotThisTypeOfNoteException();
    }

    public void setFilePath(String filePath) throws NotThisTypeOfNoteException{
        if(!isPhotoNote)
            this.filePath = filePath;
        else
            throw new NotThisTypeOfNoteException();
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

    public boolean isPhotoNote()
    {
        return isPhotoNote;
    }
}
