package com.wingor_software.mylearn;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DataBaseHelper extends SQLiteOpenHelper
{
    private static final String DATABASENAME = "MyLearn_DB";

    //PRZEDMIOTY---------------------------------------------------
    private static final String SUBJECT_TABLE_NAME = "subjects";
    private static final String SUBJECT_COL1 = "ID";
    private static final String SUBJECT_COL2 = "Subject";
    private static final String SUBJECT_COL3 = "Color";

    //FISZKI----------------------------------------------------------
    private static final String CARD_TABLE_NAME = "cards";
    private static final String CARD_COL1 = "ID";
    private static final String CARD_COL2 = "Word";
    private static final String CARD_COL3 = "Answer";
    private static final String CARD_COL4 = "SubjectID";

    //NOTATKI--------------------------------------------------------
    private static final String NOTE_TABLE_NAME = "notes";
    private static final String NOTE_COL1 = "ID";
    private static final String NOTE_COL2 = "Title";
    private static final String NOTE_COL3 = "Content";
    private static final String NOTE_COL4 = "SubjectID";
    private static final String NOTE_COL5 = "Color";


    public DataBaseHelper(Context context) {
        super(context, DATABASENAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String createTableSubject = "CREATE TABLE " + SUBJECT_TABLE_NAME + " ( " + SUBJECT_COL1 + " INTEGER PRIMARY KEY AUTOINCREMENT, " + SUBJECT_COL2 + " TEXT, " + SUBJECT_COL3 + " INTEGER);";
        String createTableCard = "CREATE TABLE " + CARD_TABLE_NAME + " ( " + CARD_COL1 + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + CARD_COL2 + " TEXT, " + CARD_COL3 + " TEXT, " + CARD_COL4 + " INTEGER)";
        String createTableNote = "CREATE TABLE " + NOTE_TABLE_NAME + " ( " + NOTE_COL1 + " INTEGER PRIMARY KEY AUTOINCREMENT, " + NOTE_COL2 + " TEXT, " + NOTE_COL3 + " TEXT, " + NOTE_COL4 + " INTEGER, " + NOTE_COL5 + " INTEGER);";

        sqLiteDatabase.execSQL(createTableSubject);
        sqLiteDatabase.execSQL(createTableCard);
        sqLiteDatabase.execSQL(createTableNote);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + SUBJECT_TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + CARD_TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + NOTE_TABLE_NAME);
    }

    //SUBJECT METHODS------------------------------------

    public boolean addSubjectData(String item, int color)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(SUBJECT_COL2, item);
        contentValues.put(SUBJECT_COL3, color);
        Log.d("DataBase", "addData : Adding " + item + " to " + SUBJECT_TABLE_NAME);
        long result = db.insert(SUBJECT_TABLE_NAME, null, contentValues);
        return (result != -1);
    }

    public Cursor getSubjectData()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + SUBJECT_TABLE_NAME;
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    public List<Subject> getSubjectsList() throws EmptyDataBaseException
    {
        Cursor data = this.getSubjectData();
        List<Subject> subjects = new ArrayList<>();
        while(data.moveToNext())
        {
            subjects.add(new Subject(data.getInt(0), data.getString(1), data.getInt(2)));
        }
        if (subjects.size() == 0) throw new EmptyDataBaseException();
        return subjects;
    }

    public void dropSubject(int ID)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + SUBJECT_TABLE_NAME+ " WHERE " + SUBJECT_COL1 + " = " + ID;
        db.execSQL(query);
    }

    public Subject getLatelyAddedSubject() throws EmptyDataBaseException
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + SUBJECT_TABLE_NAME+ " ORDER BY " + SUBJECT_COL1+ " DESC LIMIT 1";
        Cursor cursor = db.rawQuery(query, null);
        if (cursor == null) throw new EmptyDataBaseException();
        cursor.moveToNext();
        Subject subject = new Subject(cursor.getInt(0), cursor.getString(1), cursor.getInt(2));
        cursor.close();
        return subject;
    }

    public void dropSubjectTable()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + SUBJECT_TABLE_NAME+ " WHERE 1=1";

        db.execSQL(query);
    }

    //---------------------------------------------------

    //CARD METHODS----------------------------------------

    public boolean addCardData(String word, String answer, int subjectID)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CARD_COL2, word);
        contentValues.put(CARD_COL3, answer);
        contentValues.put(CARD_COL4, subjectID);
        Log.d("Card DataBase", "addData : Adding " + word + ", " + answer + ", " + subjectID);
        long result = db.insert(CARD_TABLE_NAME, null, contentValues);
        return (result != -1);
    }

    public Cursor getCardData()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + CARD_TABLE_NAME;
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    public List<Card> getCardList() throws EmptyDataBaseException
    {
        Cursor data = this.getCardData();
        List<Card> cards = new ArrayList<>();
        while(data.moveToNext())
        {
            cards.add(new Card(data.getInt(0), data.getString(1), data.getString(2), data.getInt(3)));
        }
        if (cards.size() == 0) throw new EmptyDataBaseException();
        return cards;
    }

    public List<Card> getCardList(int subjectID) throws EmptyDataBaseException
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + CARD_TABLE_NAME + " WHERE " + CARD_COL4 + " = " + subjectID;
        Cursor data = db.rawQuery(query, null);
        ArrayList<Card> cards = new ArrayList<>();
        while(data.moveToNext())
        {
            cards.add(new Card(data.getInt(0), data.getString(1), data.getString(2), data.getInt(3)));
        }
        if(cards.size() == 0) throw new EmptyDataBaseException();
        data.close();
        return cards;
    }

    public void dropCardByID(int ID)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + CARD_TABLE_NAME + " WHERE " + CARD_COL1 + " = " + ID;
        db.execSQL(query);
    }

    public void dropCardsBySubjectID(int subjectID)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + CARD_TABLE_NAME + " WHERE " + CARD_COL4 + " = " + subjectID;
        db.execSQL(query);
    }

    public void dropCardTable()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + CARD_TABLE_NAME + " WHERE 1=1";
        db.execSQL(query);
    }

    public Card getLatelyAddedCard() throws EmptyDataBaseException
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + CARD_TABLE_NAME + " ORDER BY " + CARD_COL1 + " DESC LIMIT 1";
        Cursor data = db.rawQuery(query, null);
        if (data == null) throw new EmptyDataBaseException();
        data.moveToNext();
        Card card = new Card(data.getInt(0), data.getString(1), data.getString(2), data.getInt(3));
        data.close();
        return card;
    }

    //-------------------------------------------------------

    //NOTE METHODS-------------------------------------------

    public boolean addNoteData(String title, String content, int subjectID, int color)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(NOTE_COL5, color);
        contentValues.put(NOTE_COL4, subjectID);
        contentValues.put(NOTE_COL3, content);
        contentValues.put(NOTE_COL2, title);
        Log.d("DataBase", "addData : Adding " + title + ", " + subjectID + ", " + " to " + NOTE_TABLE_NAME);
        long result = db.insert(NOTE_TABLE_NAME, null, contentValues);
        return (result != -1);
    }

    public Cursor getNoteData()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + NOTE_TABLE_NAME;
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    public List<Note> getNoteList() throws EmptyDataBaseException
    {
        Cursor data = this.getNoteData();
        List<Note> notes = new ArrayList<>();
        while(data.moveToNext())
        {
            notes.add(new Note(data.getInt(0), data.getString(1), data.getString(2), data.getInt(3), data.getInt(4)));
        }
        if (notes.size() == 0) throw new EmptyDataBaseException();
        return notes;
    }

    public List<Note> getNoteList(int subjectID) throws EmptyDataBaseException
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + NOTE_TABLE_NAME + " WHERE " + NOTE_COL4 + " = " + subjectID;
        Cursor data = db.rawQuery(query, null);
        ArrayList<Note> notes = new ArrayList<>();
        while(data.moveToNext())
        {
            notes.add(new Note(data.getInt(0), data.getString(1), data.getString(2), data.getInt(3), data.getInt(4)));
        }
        if(notes.size() == 0) throw new EmptyDataBaseException();
        data.close();
        return notes;
    }

    public void dropNoteByID(int ID)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + NOTE_TABLE_NAME + " WHERE " + NOTE_COL1 + " = " + ID;
        db.execSQL(query);
    }

    public void dropNotesBySubjectID(int subjectID)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + NOTE_TABLE_NAME + " WHERE " + NOTE_COL4 + " = " + subjectID;
        db.execSQL(query);
    }

    public void dropNoteTable()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + NOTE_TABLE_NAME + " WHERE 1=1";
        db.execSQL(query);
    }

    public Note getLatelyAddedNote() throws EmptyDataBaseException
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + NOTE_TABLE_NAME + " ORDER BY " + NOTE_COL1 + " DESC LIMIT 1";
        Cursor data = db.rawQuery(query, null);
        if (data == null) throw new EmptyDataBaseException();
        data.moveToNext();
        Note note = new Note(data.getInt(0), data.getString(1), data.getString(2), data.getInt(3), data.getInt(4));
        data.close();
        return note;
    }

    public void updateNoteContent(int noteID, String newContent)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " + NOTE_TABLE_NAME + " SET " + NOTE_COL3 + " = '" + newContent + "' WHERE " + NOTE_COL1 + " = " + noteID + ";";
        db.execSQL(query);
    }

    //-------------------------------------------------------
}
