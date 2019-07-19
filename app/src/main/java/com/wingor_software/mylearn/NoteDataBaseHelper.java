package com.wingor_software.mylearn;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class NoteDataBaseHelper extends SQLiteOpenHelper
{
    private static final String TABLE_NAME = "notes";
    private static final String COL1 = "ID";
    private static final String COL2 = "Title";
    private static final String COL3 = "Content";
    private static final String COL4 = "SubjectID";
    private static final String COL5 = "Color";

    public NoteDataBaseHelper(Context context)
    {
        super(context, TABLE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " ( " + COL1 + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COL2 + " TEXT, " + COL3 + " TEXT, " + COL4 + " INTEGER, " + COL5 + " INTEGER);";
        sqLiteDatabase.execSQL(createTable);
        Log.d("tag test", createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
    }

    public boolean addData(String title, String content, int subjectID, int color)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL5, color);
        contentValues.put(COL4, subjectID);
        contentValues.put(COL3, content);
        contentValues.put(COL2, title);
        Log.d("DataBase", "addData : Adding " + title + ", " + subjectID + ", " + " to " + TABLE_NAME);
        long result = db.insert(TABLE_NAME, null, contentValues);
        return (result != -1);
    }

    public Cursor getData()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME;
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    public List<Note> getNoteList() throws EmptyDataBaseException
    {
        Cursor data = this.getData();
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
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COL4 + " = " + subjectID;
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
        String query = "DELETE FROM " + TABLE_NAME + " WHERE " + COL1 + " = " + ID;
        db.execSQL(query);
    }

    public void dropNotesBySubjectID(int subjectID)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + TABLE_NAME + " WHERE " + COL4 + " = " + subjectID;
        db.execSQL(query);
    }

    public void dropTable()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + TABLE_NAME + " WHERE 1=1";
        db.execSQL(query);
    }

    public Note getLatelyAddedNote() throws EmptyDataBaseException
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " ORDER BY " + COL1 + " DESC LIMIT 1";
        Cursor data = db.rawQuery(query, null);
        if (data == null) throw new EmptyDataBaseException();
        data.moveToNext();
        Note note = new Note(data.getInt(0), data.getString(1), data.getString(2), data.getInt(3), data.getInt(4));
        data.close();
        return note;
    }
}
