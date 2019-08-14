package com.wingor_software.mylearn;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class FileImportExport
{
    public static void exportSubject(Context context, DataBaseHelper dataBaseHelper)
    {
        try
        {
            Subject subject = MainActivity.getCurrentSubject();
            int subjectID = subject.getSubjectID();
            List<Card> cards;
            List<Quiz> quizzes;
            List<Note> notes;
            try {   //tu dla fiszek
                cards = dataBaseHelper.getCardList(subjectID);
            }
            catch(EmptyDataBaseException e) {
                cards = new ArrayList<>();
            }

            try {       //tu dla quizow
                quizzes = dataBaseHelper.getQuizList(subjectID);
            }
            catch(EmptyDataBaseException e) {
                quizzes = new ArrayList<>();
            }

            try {       //tu dla notatek
                notes = dataBaseHelper.getNoteList(subjectID);
            }
            catch(EmptyDataBaseException e) {
                notes = new ArrayList<>();
            }
            OutputSubject outputSubject = new OutputSubject(subject.getSubjectName(), subject.getColor(), cards, quizzes, notes, getPhotosFromNote(subjectID, dataBaseHelper));
            String fileName = subject.getSubjectName() + ".txt";
            File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File file = new File(dir, fileName);
            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutputStream out = new ObjectOutputStream(fos);
            out.writeObject(outputSubject);
            out.close();
            fos.close();
//            Toast.makeText(context ,"Subject exported correcly", Toast.LENGTH_LONG).show();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public static File exportAndShareSubject(Context context, DataBaseHelper dataBaseHelper)
    {
        try
        {
            Subject subject = MainActivity.getCurrentSubject();
            int subjectID = subject.getSubjectID();
            List<Card> cards;
            List<Quiz> quizzes;
            List<Note> notes;
            try {   //tu dla fiszek
                cards = dataBaseHelper.getCardList(subjectID);
            }
            catch(EmptyDataBaseException e) {
                cards = new ArrayList<>();
            }

            try {       //tu dla quizow
                quizzes = dataBaseHelper.getQuizList(subjectID);
            }
            catch(EmptyDataBaseException e) {
                quizzes = new ArrayList<>();
            }

            try {       //tu dla notatek
                notes = dataBaseHelper.getNoteList(subjectID);
            }
            catch(EmptyDataBaseException e) {
                notes = new ArrayList<>();
            }
            OutputSubject outputSubject = new OutputSubject(subject.getSubjectName(), subject.getColor(), cards, quizzes, notes, getPhotosFromNote(subjectID, dataBaseHelper));
            String fileName = subject.getSubjectName() + ".txt";
            File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File file = new File(dir, fileName);
            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutputStream out = new ObjectOutputStream(fos);
            out.writeObject(outputSubject);
            out.close();
            fos.close();
//            Toast.makeText(context ,"Subject exported correcly", Toast.LENGTH_LONG).show();
            return file;
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public static void addImportedSubject(OutputSubject outputSubject, DataBaseHelper dataBaseHelper) throws Exception
    {
        dataBaseHelper.addSubjectData(outputSubject.getSubjectName(), outputSubject.getSubjectColor(), 0, 0);
        int importedSubjectID = dataBaseHelper.getLatelyAddedSubject().getSubjectID();
        addImportedCards(outputSubject, importedSubjectID, dataBaseHelper);
        addImportedQuizzes(outputSubject, importedSubjectID, dataBaseHelper);
        addImportedNotes(outputSubject, importedSubjectID, dataBaseHelper);
    }

    private static void addImportedCards(OutputSubject outputSubject, int importedSubjectID, DataBaseHelper dataBaseHelper)
    {
        for (int i = 0; i < outputSubject.getCards().size(); i++) {
            Card card = outputSubject.getCards().get(i);
            dataBaseHelper.addCardData(card.getWord(), card.getAnswer(), importedSubjectID, null, card.getColor());
        }
    }

    private static void addImportedQuizzes(OutputSubject outputSubject, int importedSubjectID, DataBaseHelper dataBaseHelper)
    {
        for (int i = 0; i < outputSubject.getQuizzes().size(); i++) {
            Quiz quiz = outputSubject.getQuizzes().get(i);
            dataBaseHelper.addQuizData(quiz.getQuestion(), quiz.goodAnwersToString(), quiz.badAnwersToString(), importedSubjectID, "", quiz.getColor());
        }
    }

    private static void addImportedNotes(OutputSubject outputSubject, int importedSubjectID, DataBaseHelper dataBaseHelper)
    {
        for (int i = 0; i < outputSubject.getNotes().size(); i++) {
            Note note = outputSubject.getNotes().get(i);
            dataBaseHelper.addNoteData(note.getTitle(), note.getContent(), importedSubjectID, note.getColor(), getFilePathToPhotos(outputSubject, i),"");
        }
    }

    private static ArrayList<ArrayList<byte[]>> getPhotosFromNote(int subjectID, DataBaseHelper dataBaseHelper)
    {
        ArrayList<ArrayList<byte[]>> photos = new ArrayList<>();
        try
        {
            List<Note> notes = dataBaseHelper.getNoteList(subjectID);

            for (int i = 0; i < notes.size(); i++) {
                ArrayList<byte[]> currentNotePhotos = new ArrayList<>();
                
                for (String s : notes.get(i).getPhotoPath().split("\n"))
                {
                    if(notes.get(i).getPhotoPath().equals(""))
                    {
                        currentNotePhotos.add(new byte[0]);
                        break;
                    }
                    Bitmap bitmap = BitmapFactory.decodeFile(s);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90 , baos);
                    byte[] b = baos.toByteArray();
                    currentNotePhotos.add(b);
                }
                photos.add(currentNotePhotos);
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return photos;
    }

    private static String getFilePathToPhotos(OutputSubject outputSubject, int index)
    {
        StringBuilder filePath = new StringBuilder();
        ArrayList<byte[]> listOfPhotos = outputSubject.getPhotos().get(index);
        for (int i = 0; i < listOfPhotos.size(); i++) {
            try
            {
                Bitmap bitmap = decodeBitmap(listOfPhotos.get(i));
                File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                File file = new File(dir, outputSubject.getSubjectName() + "_" + index + "_" + i);
                FileOutputStream fos = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
                fos.close();
                if(!filePath.toString().equals(""))
                    filePath.append("\n");
                filePath.append(file.toString());
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
        return filePath.toString();
    }

    private static Bitmap decodeBitmap(byte[] bytes)
    {
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
}
