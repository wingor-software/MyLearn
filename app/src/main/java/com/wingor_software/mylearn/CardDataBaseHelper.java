package com.wingor_software.mylearn;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class CardDataBaseHelper extends SQLiteOpenHelper
{
    private static final String TABLE_NAME = "cards";
    private static final String COL1 = "ID";
    private static final String COL2 = "Word";
    private static final String COL3 = "Answer";
    private static final String COL4 = "SubjectID";

    public CardDataBaseHelper(Context context)
    {
        super(context, TABLE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " ( " + COL1 + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL2 + " TEXT, " + COL3 + " TEXT, " + COL4 + " INTEGER)";
        sqLiteDatabase.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
    }

    public boolean addData(String word, String answer, int subjectID)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL2, word);
        contentValues.put(COL3, answer);
        contentValues.put(COL4, subjectID);
        Log.d("Card DataBase", "addData : Adding " + word + ", " + answer + ", " + subjectID);
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

    public List<Card> getCardList() throws EmptyDataBaseException
    {
        Cursor data = this.getData();
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
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COL4 + " = " + subjectID;
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
        String query = "DELETE FROM " + TABLE_NAME + " WHERE " + COL1 + " = " + ID;
        db.execSQL(query);
    }

    public void dropCardsBySubjectID(int subjectID)
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

    public Card getLatelyAddedCard() throws EmptyDataBaseException
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " ORDER BY " + COL1 + " DESC LIMIT 1";
        Cursor data = db.rawQuery(query, null);
        if (data == null) throw new EmptyDataBaseException();
        data.moveToNext();
        Card card = new Card(data.getInt(0), data.getString(1), data.getString(2), data.getInt(3));
        data.close();
        return card;
    }
}
