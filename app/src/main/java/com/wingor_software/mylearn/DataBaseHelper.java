package com.wingor_software.mylearn;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Klasa odpowiadająca za połączenie z bazą danych SQLite
 */

public class DataBaseHelper extends SQLiteOpenHelper
{

    private static final String DATABASE_NAME = "MyLearn_DB";
    private static final int DATABASE_VERSION = 2;

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
    private static final String CARD_COL5 = "AttachedNoteIDs";
    private static final String CARD_COL6 = "Color";

    //NOTATKI--------------------------------------------------------
    private static final String NOTE_TABLE_NAME = "notes";
    private static final String NOTE_COL1 = "ID";
    private static final String NOTE_COL2 = "Title";
    private static final String NOTE_COL3 = "Content";
    private static final String NOTE_COL4 = "SubjectID";
    private static final String NOTE_COL5 = "Color";
    private static final String NOTE_COL6 = "PhotoPath";

    //QUIZ----------------------------------------------------------
    private static final String QUIZ_TABLE_NAME = "quiz";
    private static final String QUIZ_COL1 = "ID";
    private static final String QUIZ_COL2 = "Question";
    private static final String QUIZ_COL3 = "GoodAnswers";
    private static final String QUIZ_COL4 = "BadAnswers";
    private static final String QUIZ_COL5 = "SubjectID";
    private static final String QUIZ_COL6 = "AttachedNoteIDs";
    private static final String QUIZ_COL7 = "Color";


    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Metoda która tworzy baze danych wraz z tabelami
     * @param sqLiteDatabase jest odnosnikiem do bazy SQLite
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String createTableSubject = "CREATE TABLE " + SUBJECT_TABLE_NAME + " ( " + SUBJECT_COL1 + " INTEGER PRIMARY KEY AUTOINCREMENT, " + SUBJECT_COL2 + " TEXT, " + SUBJECT_COL3 + " INTEGER);";

        String createTableCard = "CREATE TABLE " + CARD_TABLE_NAME + " ( " + CARD_COL1 + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + CARD_COL2 + " TEXT, " + CARD_COL3 + " TEXT, " + CARD_COL4 + " INTEGER, " + CARD_COL5 + " TEXT, " + CARD_COL6 + " INTEGER);";

        String createTableNote = "CREATE TABLE " + NOTE_TABLE_NAME + " ( " + NOTE_COL1 + " INTEGER PRIMARY KEY AUTOINCREMENT, " + NOTE_COL2 + " TEXT, "
                + NOTE_COL3 + " TEXT, " + NOTE_COL4 + " INTEGER, " + NOTE_COL5 + " INTEGER, " + NOTE_COL6 + " TEXT);";

        String createTableQuiz = "CREATE TABLE " + QUIZ_TABLE_NAME + " ( " + QUIZ_COL1 + " INTEGER PRIMARY KEY AUTOINCREMENT, " + QUIZ_COL2 + " TEXT, " + QUIZ_COL3 + " TEXT, "
                + QUIZ_COL4 + " TEXT, " + QUIZ_COL5 + " INTEGER, " + QUIZ_COL6 + " TEXT, " + QUIZ_COL7 + " INTEGER);";

        sqLiteDatabase.execSQL(createTableSubject);
        sqLiteDatabase.execSQL(createTableCard);
        sqLiteDatabase.execSQL(createTableNote);
        sqLiteDatabase.execSQL(createTableQuiz);
    }

    /**
     * Metoda dropujaca tabele jeśli istnieja
     * @param sqLiteDatabase  jest odnosnikiem do bazy SQLite
     * @param i nwm
     * @param i1 nwm
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + SUBJECT_TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + CARD_TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + NOTE_TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + QUIZ_TABLE_NAME);

        onCreate(sqLiteDatabase);
    }

    //SUBJECT METHODS------------------------------------

    /**
     * Metoda dodająca wpis "Przedmiotu" do odpowiedniej tabeli
     * @param item informuje o nazwie przedmiotu
     * @param color informuje o kolorze przedmiotu
     * @return zwraca true jeżeli operacja sie powiodłą
     */
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

    /**
     * Metoda pobierająca nazwe przedmiotu
     * @return zwraca nazwe przedmiotu
     */

    public Cursor getSubjectData()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + SUBJECT_TABLE_NAME;
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    /**
     * Metoda pobierająca liste wszystkich przedmiotów
     * @return zwraca liste przedmiotów
     * @throws EmptyDataBaseException w przypadku gdy tabela z przedmiotami jest pusta
     */

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

    /**
     * Metoda usuwa wpis przedmiotu z tabeli
     * @param ID odnosi sie do ID usuwanego przedmiotu
     */

    public void dropSubject(int ID)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + SUBJECT_TABLE_NAME+ " WHERE " + SUBJECT_COL1 + " = " + ID;
        db.execSQL(query);
    }

    /**
     * Metoda zwraca ostatnio dodany przedmiot
     * @return zwraca dany przedmiot
     * @throws EmptyDataBaseException w przypadku pustej tabeli
     */

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

    /**
     * Metoda usuwa tabele przedmiotow
     */

    public void dropSubjectTable()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + SUBJECT_TABLE_NAME+ " WHERE 1=1";

        db.execSQL(query);
    }

    //---------------------------------------------------

    //CARD METHODS----------------------------------------

    /**
     * Metoda dodająca fiszke do tabeli
     * @param word informuje o słowie głównym
     * @param answer informuje o odpowiedzi na słowo główne
     * @param subjectID informuje o ID przedmiotu do którego jest przypisana fiszka
     * @return zwraca true jesli dodano poprawnie
     */

    public boolean addCardData(String word, String answer, int subjectID, String noteIDs, int color)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CARD_COL2, word);
        contentValues.put(CARD_COL3, answer);
        contentValues.put(CARD_COL4, subjectID);
        contentValues.put(CARD_COL5, noteIDs);
        contentValues.put(CARD_COL6, color);
        Log.d("Card DataBase", "addData : Adding " + word + ", " + answer + ", " + subjectID);
        long result = db.insert(CARD_TABLE_NAME, null, contentValues);
        return (result != -1);
    }

    /**
     * Metoda pobierajaca fiszki
     * @return zwraca fiszki
     */

    public Cursor getCardData()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + CARD_TABLE_NAME;
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    /**
     * Metoda zwracajaca liste fiszek
     * @return lista fiszek
     * @throws EmptyDataBaseException jesli baza jest pusta
     */

    public List<Card> getCardList() throws EmptyDataBaseException
    {
        Cursor data = this.getCardData();
        List<Card> cards = new ArrayList<>();
        while(data.moveToNext())
        {
            try
            {
                cards.add(new Card(data.getInt(0), data.getString(1), data.getString(2), data.getInt(3), attachedNotesToArrayList(data.getString(4)), data.getInt(5)));
            }
            catch(Exception e)
            {
                cards.add(new Card(data.getInt(0), data.getString(1), data.getString(2), data.getInt(3), data.getInt(5)));
            }
        }
        if (cards.size() == 0) throw new EmptyDataBaseException();
        return cards;
    }

    private Integer[] attachedNotesToArrayList(String notes) throws Exception
    {
        if(!notes.equals(""))
        {
            String[] notesStr = notes.split("\n");
            Integer[] noteIDs = new Integer[notesStr.length];
            for (int i = 0; i < notesStr.length; i++) {
                noteIDs[i] = Integer.parseInt(notesStr[i]);
            }
            return noteIDs;
        }
        throw new Exception();
    }

    /**
     * Metoda zwracająca fiszki nalezace do danego przedmiotu
     * @param subjectID informuje o ID przedmiotu z któego chcemy pobrac fiszki
     * @return fiszki przedmoitu
     * @throws EmptyDataBaseException jesli baza pusta
     */

    public List<Card> getCardList(int subjectID) throws EmptyDataBaseException
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + CARD_TABLE_NAME + " WHERE " + CARD_COL4 + " = " + subjectID;
        Cursor data = db.rawQuery(query, null);
        ArrayList<Card> cards = new ArrayList<>();
        while(data.moveToNext())
        {
            try
            {
                cards.add(new Card(data.getInt(0), data.getString(1), data.getString(2), data.getInt(3), attachedNotesToArrayList(data.getString(4)), data.getInt(5)));
            }
            catch(Exception e)
            {
                cards.add(new Card(data.getInt(0), data.getString(1), data.getString(2), data.getInt(3), data.getInt(5)));
            }
        }
        if(cards.size() == 0) throw new EmptyDataBaseException();
        data.close();
        return cards;
    }

    /**
     * Metoda usuwa fiszke z tabeli
     * @param ID Id usuwanej fiszki
     */

    public void dropCardByID(int ID)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + CARD_TABLE_NAME + " WHERE " + CARD_COL1 + " = " + ID;
        db.execSQL(query);
    }

    /**
     * Metoda usuwa fiszki powiązane z danym przedmiotem
     * @param subjectID id przedmiotu z ktorego maja byc usuniete fiszki
     */
    public void dropCardsBySubjectID(int subjectID)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + CARD_TABLE_NAME + " WHERE " + CARD_COL4 + " = " + subjectID;
        db.execSQL(query);
    }

    /**
     * Metoda usuwa tabele z fiszkami
     */
    public void dropCardTable()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + CARD_TABLE_NAME + " WHERE 1=1";
        db.execSQL(query);
    }

    /**
     * Metoda zwraca ostatnio dodaną fiszke
     * @return zwraca tą fiszke
     * @throws EmptyDataBaseException gdy pusta tabela fiszek
     */
    public Card getLatelyAddedCard() throws EmptyDataBaseException
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + CARD_TABLE_NAME + " ORDER BY " + CARD_COL1 + " DESC LIMIT 1";
        Cursor data = db.rawQuery(query, null);
        if (data == null) throw new EmptyDataBaseException();
        data.moveToNext();
        Card card;
        try
        {
            card = new Card(data.getInt(0), data.getString(1), data.getString(2), data.getInt(3), attachedNotesToArrayList(data.getString(4)), data.getInt(5));
        }
        catch(Exception e)
        {
            card = new Card(data.getInt(0), data.getString(1), data.getString(2), data.getInt(3), data.getInt(5));
        }
        data.close();
        return card;
    }

    public void updateNotesAttachedToCard(Integer[] noteIDs)
    {

    }

    //-------------------------------------------------------

    //NOTE METHODS-------------------------------------------

    /**
     * Metoda dodająca notatke do tabeli
     * @param title tytul notatki
     * @param content zawartosc notatki
     * @param subjectID id przedmiotu do którego jest przypisana notatka
     * @param color color notatki
     * @return true jesli poprawnie dodano
     */

    public boolean addNoteData(String title, String content, int subjectID, int color, String filePath)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(NOTE_COL6, filePath);
        contentValues.put(NOTE_COL5, color);
        contentValues.put(NOTE_COL4, subjectID);
        contentValues.put(NOTE_COL3, content);
        contentValues.put(NOTE_COL2, title);
        Log.d("DataBase", "addData : Adding " + title + ", " + subjectID + ", " + " to " + NOTE_TABLE_NAME);
        long result = db.insert(NOTE_TABLE_NAME, null, contentValues);
        return (result != -1);
    }

    /**
     * Metoda zwracajaca notatke
     * @return zwraca dane notatki
     */

    public Cursor getNoteData()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + NOTE_TABLE_NAME;
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    /**
     * Metoda zwracajaca liste notatek z bazy danych
     * @return zwraca liste notatek
     * @throws EmptyDataBaseException jesli baza pusta
     */

    public List<Note> getNoteList() throws EmptyDataBaseException
    {
        Cursor data = this.getNoteData();
        List<Note> notes = new ArrayList<>();
        while(data.moveToNext())
        {
            notes.add(new Note(data.getInt(0), data.getString(1), data.getString(2), data.getInt(3), data.getInt(4), data.getString(5)));
        }
        if (notes.size() == 0) throw new EmptyDataBaseException();
        return notes;
    }

    /**
     * Metoda zwracajaca liste notatek nalezacych do danego przedmiotu
     * @param subjectID informuje o id przedmotu
     * @return zwraca liste notatek
     * @throws EmptyDataBaseException gdy przedmot nie ma notatek
     */

    public List<Note> getNoteList(int subjectID) throws EmptyDataBaseException
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + NOTE_TABLE_NAME + " WHERE " + NOTE_COL4 + " = " + subjectID;
        Cursor data = db.rawQuery(query, null);
        ArrayList<Note> notes = new ArrayList<>();
        while(data.moveToNext())
        {
            notes.add(new Note(data.getInt(0), data.getString(1), data.getString(2), data.getInt(3), data.getInt(4), data.getString(5)));
        }
        if(notes.size() == 0) throw new EmptyDataBaseException();
        data.close();
        return notes;
    }

    /**
     * Metoda usuwajaca notatke
     * @param ID id usuwanej notatki
     */

    public void dropNoteByID(int ID)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + NOTE_TABLE_NAME + " WHERE " + NOTE_COL1 + " = " + ID;
        db.execSQL(query);
    }

    /**
     * Metoda usuwająca notatki należace do danego przedmiotu
     * @param subjectID id przedmiotu z ktorego maja byc usuniete notatki
     */

    public void dropNotesBySubjectID(int subjectID)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + NOTE_TABLE_NAME + " WHERE " + NOTE_COL4 + " = " + subjectID;
        db.execSQL(query);
    }

    /**
     * Metoda usuwajaca tabele notatek
     */

    public void dropNoteTable()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + NOTE_TABLE_NAME + " WHERE 1=1";
        db.execSQL(query);
    }

    /**
     * Metoda zwracająca ostatnio dodana notatke
     * @return zwraca dana notatke
     * @throws EmptyDataBaseException gdy nie ma notatek w tabeli
     */

    public Note getLatelyAddedNote() throws EmptyDataBaseException
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + NOTE_TABLE_NAME + " ORDER BY " + NOTE_COL1 + " DESC LIMIT 1";
        Cursor data = db.rawQuery(query, null);
        if (data == null) throw new EmptyDataBaseException();
        data.moveToNext();
        Note note = new Note(data.getInt(0), data.getString(1), data.getString(2), data.getInt(3), data.getInt(4), data.getString(5));
        data.close();
        return note;
    }

    /**
     * Metoda zmieniająca zawartość notatki
     * @param noteID id modyfikowanej notatki
     * @param newContent nowa zawartosc notatki
     */
    public void updateNoteContent(int noteID, String newContent)
    {
        Note note = getNoteByID(noteID);
        if(note == null)
        {
            Log.d("NoteUpdate", "Blad przy aktualizowaniu contentu notatki");
            return;
        }
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " + NOTE_TABLE_NAME + " SET " + NOTE_COL3 + " = '" + newContent + "' WHERE " + NOTE_COL1 + " = " + noteID + ";";
        db.execSQL(query);
    }

    private Note getNoteByID(int noteID)
    {
        Cursor data = getNoteData();
        while(data.moveToNext())
        {
            if(data.getInt(0) == noteID)
            {
                return new Note(data.getInt(0), data.getString(1), data.getString(2), data.getInt(3), data.getInt(4), data.getString(5));
            }
        }
        return null;
    }

    public void updateNotePhotosByID(int noteID, String photosPaths)
    {
        Note note = getNoteByID(noteID);
        if(note == null)
        {
            Log.d("NoteUpdate", "Blad przy aktualizowaniu contentu notatki");
            return;
        }
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " + NOTE_TABLE_NAME + " SET " + NOTE_COL6 + " = '" + photosPaths + "' WHERE " + NOTE_COL1 + " = " + noteID + ";";
        db.execSQL(query);
    }

    //-------------------------------------------------------
    //QUIZ METHODS ------------------------------------------

    public boolean addQuizData(String question, String goodAnswers, String badAnswers, int subjectID, String attachedNotes, int color)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(QUIZ_COL2, question);
        contentValues.put(QUIZ_COL3, goodAnswers);
        contentValues.put(QUIZ_COL4, badAnswers);
        contentValues.put(QUIZ_COL5, subjectID);
        contentValues.put(QUIZ_COL6, attachedNotes);
        contentValues.put(QUIZ_COL7, color);
        Log.d("DataBase", "addData : Adding " + question + ", " + subjectID + ", " + " to " + QUIZ_TABLE_NAME);
        long result = db.insert(QUIZ_TABLE_NAME, null, contentValues);
        return (result != -1);
    }

    public Cursor getQuizData()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + QUIZ_TABLE_NAME;
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    public List<Quiz> getQuizList(int subjectID) throws EmptyDataBaseException
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + QUIZ_TABLE_NAME+ " WHERE " + QUIZ_COL5 + " = " + subjectID;
        Cursor data = db.rawQuery(query, null);
        ArrayList<Quiz> quizzes = new ArrayList<>();
        while(data.moveToNext())
        {
            quizzes.add(new Quiz(data.getInt(0), data.getString(1), fromStringToStringArrayList(data.getString(2)), fromStringToStringArrayList(data.getString(3)), data.getInt(4), noteIDsToArrayList(data.getString(5)), data.getInt(6)));
        }
        if(quizzes.size() == 0) throw new EmptyDataBaseException();
        data.close();
        return quizzes;
    }

    private ArrayList<Integer> noteIDsToArrayList(String noteIDs)
    {
        ArrayList<Integer> notes = new ArrayList<>();
        if(!noteIDs.equals(""))
        {
            String[] x = noteIDs.split("\n");
            for (int i = 0; i < x.length; i++) {
                notes.add(Integer.parseInt(x[i]));
            }
            return notes;
        }
        return null;
    }

    private ArrayList<String> fromStringToStringArrayList(String text)
    {
        ArrayList<String> list = new ArrayList<>();
        if(!text.equals(""))
        {
            String[] x = text.split("\n");
            for (int i = 0; i < x.length; i++) {
                list.add(x[i]);
            }
            return list;
        }
        return null;
    }

    public void dropQuizByID(int ID)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + QUIZ_TABLE_NAME + " WHERE " + QUIZ_COL1 + " = " + ID;
        db.execSQL(query);
    }

    public void dropQuizBySubjectID(int subjectID)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + QUIZ_TABLE_NAME + " WHERE " + QUIZ_COL5 + " = " + subjectID;
        db.execSQL(query);
    }

    public Quiz getLatelyAddedQuiz() throws EmptyDataBaseException
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + QUIZ_TABLE_NAME + " ORDER BY " + QUIZ_COL1 + " DESC LIMIT 1";
        Cursor data = db.rawQuery(query, null);
        if (data == null) throw new EmptyDataBaseException();
        data.moveToNext();
        Quiz quiz = new Quiz(data.getInt(0), data.getString(1), fromStringToStringArrayList(data.getString(2)), fromStringToStringArrayList(data.getString(3)), data.getInt(4), noteIDsToArrayList(data.getString(5)), data.getInt(6));
        data.close();
        return quiz;
    }
}
