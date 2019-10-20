package com.wingor_software.mylearn;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

/**
 * Klasa odpowiadająca za wyswietlanie struktury bazy danych
 */

public class ListDataActivity extends AppCompatActivity
{
    DataBaseHelper dataBaseHelper;
    private ListView listView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_layout);

        listView = (ListView) findViewById(R.id.listView);
        dataBaseHelper = new DataBaseHelper(this);

        populateListView();
    }

    private void populateListView()
    {
        Log.d("ListView", "populateListView : Displaying data in the ListView");
        Cursor data = dataBaseHelper.getSubjectData();
        ArrayList<String> listData = new ArrayList<>();
        while(data.moveToNext())
        {
            listData.add(data.getString(1));
        }
        ListAdapter listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listData);
        listView.setAdapter(listAdapter);
    }
}
