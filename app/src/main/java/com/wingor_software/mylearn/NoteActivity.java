package com.wingor_software.mylearn;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

/**
 * Klasa odpowiadająca za interakcje z notatkami
 */
public class NoteActivity extends AppCompatActivity {

    private ViewSwitcher viewSwitcher;
    private TextView noteContent;
    private EditText editNote;
    private boolean isTextBeingEdited = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_note);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isTextBeingEdited) {
                    viewSwitcher.showNext();
                    isTextBeingEdited = false;
                    String newText = editNote.getText().toString();
                    noteContent.setText(newText);
                    Note newNote = SubjectActivity.getCurrentNote();
                    newNote.setContent(newText);
                    SubjectActivity.setCurrentNote(newNote);
                    SubjectActivity.updateNoteContent(SubjectActivity.getCurrentNote().getID(), editNote.getText().toString());
                } else {
                    Snackbar.make(view, "W trybie edycji przycisk zapisuje zmiany", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });

        viewSwitcher = (ViewSwitcher) findViewById(R.id.noteViewSwitcher);
        noteContent = (TextView) findViewById(R.id.noteContent);
        editNote = (EditText) findViewById(R.id.editNote);

        noteContent.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                viewSwitcher.showNext();
                isTextBeingEdited = true;
                editNote.setText(noteContent.getText());
                return true;
            }
        });
    }

    /**
     * Metoda ustawiająca tytul gornego pasaka na nazwe przedmiotu i zmieniajaca kolor w zaleznosci od koloru przedmiotu
     */
    @Override
    protected void onResume() {
        super.onResume();
        this.setTitle(SubjectActivity.getCurrentNote().getTitle());
        noteContent.setText(SubjectActivity.getCurrentNote().getContent());
        CollapsingToolbarLayout tolbar_layout = findViewById(R.id.toolbar_layout);
        int color_of_subject = SubjectActivity.getCurrentNote().getColor();
        switch (color_of_subject)
        {
            case 1:
            {
                tolbar_layout.setBackgroundColor(getResources().getColor(R.color.colorRed));
                break;
            }
            case 2:
            {
                tolbar_layout.setBackgroundColor(getResources().getColor(R.color.colorYellow));
                break;
            }
            case 3:
            {
                tolbar_layout.setBackgroundColor(getResources().getColor(R.color.colorGreen));
                break;
            }
            case 4:
            {
                tolbar_layout.setBackgroundColor(getResources().getColor(R.color.colorBlue));
                break;
            }
            case 5:
            {
                tolbar_layout.setBackgroundColor(getResources().getColor(R.color.colorPurple));
                break;
            }
        }

    }
}
