package com.wingor_software.mylearn;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.ViewManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Iterator;
import java.util.List;

public class SubjectActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private TextView mTextMessage;
    private ConstraintLayout subjectLayout;
    Dialog myDialog;
    private TextView warning;
    NoteDataBaseHelper noteDataBaseHelper;

    private enum BarAction {CARDS, QUIZ, NOTES};
    private BarAction whichAction;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_cards:
                    whichAction = BarAction.CARDS;
                    clearContent();
                    addIfNotChildren(mTextMessage);
                    mTextMessage.setText(R.string.cards);
                    return true;
                case R.id.action_quiz:
                    whichAction = BarAction.QUIZ;
                    clearContent();
                    addIfNotChildren(mTextMessage);
                    mTextMessage.setText(R.string.quiz);
                    return true;
                case R.id.action_notes:
                    whichAction = BarAction.NOTES;
                    clearContent();
                    addIfNotChildren(mTextMessage);
                    mTextMessage.setText(R.string.notes);
                    drawAllNoteButtons();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject);
        Toolbar toolbar = findViewById(R.id.toolbar_subject);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab_subject);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        subjectLayout = findViewById(R.id.subjectActivityLayout);
        myDialog = new Dialog(this);
        noteDataBaseHelper = new NoteDataBaseHelper(this);
        noteDataBaseHelper.dropTable();

        BottomNavigationView navView = findViewById(R.id.nav_bottom_view);
        mTextMessage = findViewById(R.id.message);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

//        noteDataBaseHelper.addData("Tytuł", "Content", 1);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.subject, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_tools) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void clearContent()
    {
        subjectLayout.removeAllViewsInLayout();
    }

    private void drawNoteButton(Note note)
    {
        final Button b =  new Button(SubjectActivity.this);

        b.setText(note.getTitle());
        b.setTag("note_" + note.getID());
        b.setMinimumWidth(200);
        b.setMinimumHeight(200);
        b.setBackground(getResources().getDrawable(R.drawable.subject_drawable));

        b.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                showDeletePopup(view);
                return true;
            }
        });
//        b.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(MainActivity.this, SubjectActivity.class);
//                startActivity(intent);
//            }
//        });

        if(warning != null && warning.getParent() != null)
        {
            ((ViewManager)warning.getParent()).removeView(warning);
        }
        subjectLayout.addView(b);
    }

    private void showDeletePopup(final View view)
    {
        myDialog.setContentView(R.layout.popup_delete_subject);
        Button no_button = myDialog.findViewById(R.id.no_button);
        Button yes_button = myDialog.findViewById(R.id.yes_button);


        no_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myDialog.dismiss();
            }
        });

        yes_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s = view.getTag().toString();
                String r_s = s.substring(5);
                noteDataBaseHelper.dropNoteByID(Integer.parseInt(r_s));
                toastMessage("Poprawnie usunieto notatke" + r_s);

                ((ViewManager)view.getParent()).removeView(view);

                myDialog.dismiss();
            }
        });

        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();
    }

    private void toastMessage(String message)
    {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void drawAllNoteButtons()
    {
        try
        {
            List<Note> notes = noteDataBaseHelper.getNoteList(MainActivity.getCurrentSubject().getSubjectID());
            Iterator it = notes.iterator();
            while(it.hasNext())
            {
                drawNoteButton((Note) it.next());
            }
        }
        catch (EmptyDataBaseException em)
        {
            warning = new TextView(SubjectActivity.this);
            warning.setText(R.string.note_warning);
            warning.setTag("note_warning_tag");
            subjectLayout.addView(warning);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    private void addIfNotChildren(View view)
    {
        if(!(view.getParent() == subjectLayout))
            subjectLayout.addView(view);
    }

    public void showPopupSubject(final View v)
    {
        Log.d("test", "jestem w funkcji showPopup");
        Button addButton;
        myDialog.setContentView(R.layout.popup_main);
        addButton = myDialog.findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextInputEditText nameGetter = myDialog.findViewById(R.id.nameGetter);
                String s;
                if(subjectLayout==null)
                {
                    Log.d("tesciki","subjectslayout jest nullem");
                }
                Note note;
                try {
                    s=nameGetter.getText().toString();
                    addData(s, "test content");
                    note = noteDataBaseHelper.getLatelyAddedNote();
                    Log.d("tesciki","dodano do bazy");
                    drawNoteButton(note);
                    Log.d("tesciki","powinno tutaj dodac przycisk");
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                myDialog.dismiss();
            }
        });

        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();
    }

    public void addData(String title, String content)
    {
        try
        {
            boolean insertData = noteDataBaseHelper.addData(title, content, MainActivity.getCurrentSubject().getSubjectID());
            if(insertData)
                toastMessage("Dodano poprawnie - " + title);
            else
                toastMessage("Cos sie wysralo");
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}
