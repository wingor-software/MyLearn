package com.wingor_software.mylearn;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class NoteListViewFragment extends Fragment
{
    private DataBaseHelper dataBaseHelper;
    private NoteListViewAdapter noteListViewAdapter;
    private Context context;
    private ListView listView;
    private Dialog myDialog;

    public NoteListViewFragment(Context context, DataBaseHelper dataBaseHelper)
    {
        this.context = context;
        this.dataBaseHelper = dataBaseHelper;
        myDialog = new Dialog(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.subject_list_view_fragment, null);
        listView = (ListView) view.findViewById(R.id.subjectListView);
        listView.setBackgroundColor((dataBaseHelper.getDisplayMode() == DisplayMode.LIGHT) ? getResources().getColor(R.color.white) : getResources().getColor(R.color.colorDarkModeBackground));
        noteListViewAdapter = new NoteListViewAdapter(context, dataBaseHelper);
        listView.setAdapter(noteListViewAdapter);
        prepareNoteListViewItems();
        return view;
    }

    private void prepareNoteListViewItems() {
        listView.setEnabled(true);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                try {
                    Note note = dataBaseHelper.getNoteList(MainActivity.getCurrentSubject().getSubjectID()).get(position);
                    SubjectActivity.setCurrentNote(note);
                    Intent intent = new Intent(context, NoteActivity.class);
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                try {
                    Note note = dataBaseHelper.getNoteList(MainActivity.getCurrentSubject().getSubjectID()).get(position);
                    showDeletePopup(note.getID());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }
        });
    }

    private void showDeletePopup(final int elementID)
    {
        myDialog.setContentView(R.layout.popup_delete_subject);
        Button no_button = myDialog.findViewById(R.id.no_button);
        Button yes_button = myDialog.findViewById(R.id.yes_button);


        no_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listView.setEnabled(true);
                myDialog.dismiss();
            }
        });

        yes_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listView.setEnabled(true);
                noteDeletingOnClick(elementID);
            }
        });

        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();
        listView.setEnabled(false);
    }

    private void noteDeletingOnClick(int noteID) {
        dataBaseHelper.dropNoteByID(noteID);
        noteListViewAdapter.notifyDataSetChanged();

        myDialog.dismiss();
    }

    public NoteListViewAdapter getNoteListViewAdapter() {
        return noteListViewAdapter;
    }
}
