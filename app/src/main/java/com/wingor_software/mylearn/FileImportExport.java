package com.wingor_software.mylearn;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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

    public static void exportZipSubject(Context context, DataBaseHelper dataBaseHelper)
    {
        File serialized = exportAndShareSubject(context, dataBaseHelper);
        int BUFFER = 6 * 1024;
        try
        {
            BufferedInputStream origin;
            File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            String name = serialized.getName().substring(0, serialized.getName().length() - 4);
            File file = new File(dir, name + ".zip");
            FileOutputStream fout = new FileOutputStream(file.getPath());
            ZipOutputStream zout = new ZipOutputStream(new BufferedOutputStream(fout));
            byte[] data = new byte[BUFFER];

            FileInputStream fi = new FileInputStream(serialized.getPath());
            origin = new BufferedInputStream(fi, BUFFER);
            ZipEntry entry = new ZipEntry(serialized.getName());
            zout.putNextEntry(entry);
            int count;
            while((count = origin.read(data, 0, BUFFER)) != -1)
            {
                zout.write(data, 0, count);
            }

            ArrayList<ArrayList<String>> uriPaths = getFilePaths(dataBaseHelper);
            for (int i = 0; i < uriPaths.size(); i++) {
                for (int j = 0; j < uriPaths.get(i).size(); j++) {
                    entry = new ZipEntry(uriPaths.get(i).get(j).substring(uriPaths.get(i).get(j).lastIndexOf("%2F") + 3));
                    zout.putNextEntry(entry);
                    InputStream is = context.getContentResolver().openInputStream(Uri.parse(uriPaths.get(i).get(j)));
                    origin = new BufferedInputStream(is, BUFFER);
                    while((count = origin.read(data, 0, BUFFER)) != -1)
                        zout.write(data, 0 , count);
                }
            }

            origin.close();
            zout.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    private static ArrayList<ArrayList<String>> getFilePaths(DataBaseHelper dataBaseHelper)
    {
        ArrayList<ArrayList<String>> paths = new ArrayList<>();
        try
        {
            int subjectID = MainActivity.getCurrentSubject().getSubjectID();
            List<Note> noteList = dataBaseHelper.getNoteList(subjectID);
            for (Note note : noteList)
            {
                String[] notePaths = note.getFilePath().split("\n");
                ArrayList<String> notePathsList = new ArrayList<>();
                for (String notePath : notePaths)
                {
                    Log.d("path", notePath);
                    notePathsList.add(notePath);
                }
                paths.add(notePathsList);
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return paths;
    }
}
