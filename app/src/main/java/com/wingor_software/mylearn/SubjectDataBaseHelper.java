package com.wingor_software.mylearn;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class SubjectDataBaseHelper extends SQLiteOpenHelper {

    private static final String TABLE_NAME = "subjects";
    private static final String COL1 = "ID";
    private static final String COL2 = "Subject";

    public SubjectDataBaseHelper(Context context)
    {
        super(context, TABLE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " + COL2 + " TEXT)";
        sqLiteDatabase.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
    }

    public boolean addData(String item)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL2, item);
        Log.d("DataBase", "addData : Adding " + item + " to " + TABLE_NAME);
        long result = db.insert(TABLE_NAME, null, contentValues);
//        if(result ==  -1)
//            return false;
//        else
//            return true;
//      jesli jakis blad to zwraca -1
        return (result != -1);
    }

    public Cursor getData()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME;
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    public List<Subject> getSubjectsList() throws EmptyDataBaseException
    {
        Cursor data = this.getData();
        List<Subject> subjects = new ArrayList<>();
        while(data.moveToNext())
        {
            subjects.add(new Subject(data.getInt(0), data.getString(1)));
        }
        if (subjects.size() == 0) throw new EmptyDataBaseException();
        return subjects;
    }

    public void dropSubject(int ID)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + TABLE_NAME + " WHERE " + COL1 + " = " + ID;
        db.execSQL(query);
    }

    public Subject getLatelyAddedSubject() throws EmptyDataBaseException
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " ORDER BY " + COL1 + " DESC LIMIT 1";
        Cursor cursor = db.rawQuery(query, null);
        if (cursor == null) throw new EmptyDataBaseException();
        Subject subject = new Subject(cursor.getInt(0), cursor.getString(1));
        cursor.close();
        return subject;
    }

    public void dropTable()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + TABLE_NAME + " WHERE 1=1";
        db.execSQL(query);
    }
}
