package com.wingor_software.mylearn;

public class CalendarEvent {
    private int ID;
    private String date;
    private String content;

    public CalendarEvent(int ID, String date, String content) {
        this.ID = ID;
        this.date = date;
        this.content = content;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
