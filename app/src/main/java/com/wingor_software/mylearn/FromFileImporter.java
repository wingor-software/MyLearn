package com.wingor_software.mylearn;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

public class FromFileImporter
{
    private static char separator;

    public static char getSeparator() {
        return separator;
    }

    public static void setSeparator(char cardSeparator) {
        FromFileImporter.separator = cardSeparator;
    }

    public static void importCardsFromFile(int subjectID, DataBaseHelper dataBaseHelper, Context context, Intent data)
    {
        String word, answer, line;
        if(data != null)
        {
            try
            {
                InputStream inputStream = context.getContentResolver().openInputStream(data.getData());
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                while((line = bufferedReader.readLine()) != null)
                {
                    String[] cardStr = line.split(String.valueOf(separator));
                    word = cardStr[0];
                    answer = cardStr[1];
                    addCardData(subjectID, word, answer, "", dataBaseHelper, context);
                }
                inputStream.close();
                Toast.makeText(context, "Cards imported correctly", Toast.LENGTH_LONG).show();
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    public static void importQuizzesFromFile(int subjectID, DataBaseHelper dataBaseHelper, Context context, Intent data)
    {
        String question, goodAnswers, badAnswers;
        String line;
        if(data != null)
        {
            try
            {
                InputStream inputStream = context.getContentResolver().openInputStream(data.getData());
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                while((line = bufferedReader.readLine()) != null)
                {
                    question = line;
                    goodAnswers = bufferedReader.readLine();
                    badAnswers = bufferedReader.readLine();
                    goodAnswers = goodAnswers.replaceAll(String.valueOf(separator), "\n");
                    badAnswers = badAnswers.replaceAll(String.valueOf(separator), "\n");
                    addQuizData(subjectID, question, goodAnswers, badAnswers, "", dataBaseHelper, context);
                }
                inputStream.close();
                Toast.makeText(context, "Questions imported correctly", Toast.LENGTH_LONG).show();
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    private static void addCardData(int subjectID, String word, String answer, String attachedNotes, DataBaseHelper dataBaseHelper, Context context)
    {
        try
        {
            Random rand = new Random();
            int color = rand.nextInt(5) + 1;
            boolean insertData = dataBaseHelper.addCardData(word, answer, subjectID, attachedNotes, color);
            if(!insertData)
                Toast.makeText(context, "An error occured", Toast.LENGTH_LONG).show();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    private static void addQuizData(int subjectID, String question, String goodAnswer, String badAnswers, String attachedNotes, DataBaseHelper dataBaseHelper, Context context)
    {
        try
        {
            Random rand = new Random();
            int color = rand.nextInt(5) + 1;
            boolean insertData = dataBaseHelper.addQuizData(question, goodAnswer, badAnswers, subjectID, attachedNotes, color);
            if(!insertData)
                Toast.makeText(context, "An error occured", Toast.LENGTH_LONG).show();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}
