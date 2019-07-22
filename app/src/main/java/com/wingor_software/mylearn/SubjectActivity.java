package com.wingor_software.mylearn;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Iterator;
import java.util.List;

public class SubjectActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private TextView mTextMessage;
    private ConstraintLayout subjectLayout;
    Dialog myDialog;
    private TextView warning;

    static DataBaseHelper dataBaseHelper;

    EnumColors chosen_color = EnumColors.valueOf(5);

    private enum BarAction {CARDS, QUIZ, NOTES}

    ;
    private BarAction whichAction;

    private static Note currentNote;
    private static Card currentCard;

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
                    drawAllCardButtons();
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

        dataBaseHelper = new DataBaseHelper(this);

        BottomNavigationView navView = findViewById(R.id.nav_bottom_view);
        mTextMessage = findViewById(R.id.message);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            this.setTitle(MainActivity.getCurrentSubject().getSubjectName());
        } catch (Exception e) {
            this.setTitle(R.string.title_activity_subject);
        }
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

    private void clearContent() {
        subjectLayout.removeAllViewsInLayout();
    }

    private void drawNoteButton(final Note note) {
        final Button b = new Button(SubjectActivity.this);

        b.setText(note.getTitle());
        b.setTag("note_" + note.getID());
        b.setMinimumWidth(200);
        b.setMinimumHeight(200);
        b.setBackground(getResources().getDrawable(R.drawable.subject_drawable_default));

        b.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                showDeletePopup(view);
                return true;
            }
        });

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentNote = note;
                Intent intent = new Intent(SubjectActivity.this, NoteActivity.class);
                startActivity(intent);
            }
        });

        b.setBackground(getResources().getDrawable(R.drawable.subject_drawable_default));
        chosen_color = EnumColors.valueOf(note.getColor());
        switch (chosen_color) {
            case red: {
                b.setBackground(getResources().getDrawable(R.drawable.subject_drawable_red));
                break;
            }
            case yellow: {
                b.setBackground(getResources().getDrawable(R.drawable.subject_drawable_yellow));
                break;
            }
            case green: {
                b.setBackground(getResources().getDrawable(R.drawable.subject_drawable_green));
                break;
            }
            case blue: {
                b.setBackground(getResources().getDrawable(R.drawable.subject_drawable_blue));
                break;
            }
            case purple: {
                b.setBackground(getResources().getDrawable(R.drawable.subject_drawable_purple));
                break;
            }
        }

        if (warning != null && warning.getParent() != null) {
            ((ViewManager) warning.getParent()).removeView(warning);
        }
        subjectLayout.addView(b);
    }

    private void drawCardButton(final Card card) {
        final Button b = new Button(SubjectActivity.this);

        b.setText(card.getWord());
        b.setTag("card_" + card.getID());
        b.setMinimumWidth(200);
        b.setMinimumHeight(200);
        b.setBackground(getResources().getDrawable(R.drawable.subject_drawable_default));

        b.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                showDeletePopup(view);
                return true;
            }
        });

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toastMessage("Nic sie nie dzieje");
//                currentCard = card;
//                Intent intent = new Intent(SubjectActivity.this, CardActivity.class);
//                startActivity(intent);
            }
        });

        b.setBackground(getResources().getDrawable(R.drawable.subject_drawable_default));

        if (warning != null && warning.getParent() != null) {
            ((ViewManager) warning.getParent()).removeView(warning);
        }
        subjectLayout.addView(b);
    }

    public void choseColor(View view) {
        switch (view.getId()) {
            case R.id.button_red: {
                chosen_color = EnumColors.valueOf(1);
                break;
            }
            case R.id.button_yellow: {
                chosen_color = EnumColors.valueOf(2);
                break;
            }
            case R.id.button_green: {
                chosen_color = EnumColors.valueOf(3);
                break;
            }
            case R.id.button_blue: {
                chosen_color = EnumColors.valueOf(4);
                break;
            }
            case R.id.button_purple: {
                chosen_color = EnumColors.valueOf(5);
                break;
            }
        }
    }

    private void showDeletePopup(final View viewButton) {
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
                switch (whichAction) {
                    case CARDS: {
                        cardDeletingOnClick(viewButton);
                        break;
                    }
                    case QUIZ: {
                        break;
                    }
                    case NOTES: {
                        noteDeletingOnClick(viewButton);
                        break;
                    }
                }
            }
        });

        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();
    }

    private void noteDeletingOnClick(View viewButton) {
        String s = viewButton.getTag().toString();
        String r_s = s.substring(5);
        dataBaseHelper.dropNoteByID(Integer.parseInt(r_s));
        toastMessage("Poprawnie usunieto notatke" + r_s);

        ((ViewManager) viewButton.getParent()).removeView(viewButton);

        myDialog.dismiss();
    }

    private void cardDeletingOnClick(View viewButton)
    {
        String s = viewButton.getTag().toString();
        String r_s = s.substring(5);
        dataBaseHelper.dropCardByID(Integer.parseInt(r_s));
        toastMessage("Poprawnie usunieto fiszke" + r_s);

        ((ViewManager) viewButton.getParent()).removeView(viewButton);

        myDialog.dismiss();
    }

    private void toastMessage(String message)
    {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void drawAllNoteButtons()
    {
        try
        {
            List<Note> notes = dataBaseHelper.getNoteList(MainActivity.getCurrentSubject().getSubjectID());
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

    private void drawAllCardButtons()
    {
        try
        {
            List<Card> cards = dataBaseHelper.getCardList(MainActivity.getCurrentSubject().getSubjectID());
            Iterator it = cards.iterator();
            while(it.hasNext())
            {
                drawCardButton((Card) it.next());
            }
        }
        catch (EmptyDataBaseException em)
        {
            warning = new TextView(SubjectActivity.this);
            warning.setText(R.string.card_warning);
            warning.setTag("card_warning_tag");
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
        //przycisk add
        Button addButton;

        //ustala focus na okienko pop up
        myDialog.setContentView(R.layout.popup_main);

        //wyszukuje powiązania
        addButton = myDialog.findViewById(R.id.addButton);

        Button red_button = myDialog.findViewById(R.id.button_red);
        Button yellow_button = myDialog.findViewById(R.id.button_yellow);
        Button green_button = myDialog.findViewById(R.id.button_green);
        Button blue_button = myDialog.findViewById(R.id.button_blue);
        Button purple_button = myDialog.findViewById(R.id.button_purple);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextInputEditText nameGetter = myDialog.findViewById(R.id.nameGetter);
                if(subjectLayout==null)
                {
                    Log.d("tesciki","subjectslayout jest nullem");
                }
                switch(whichAction)
                {
                    case CARDS:
                    {
                        cardAddingOnClick(nameGetter);
                        break;
                    }
                    case QUIZ:
                    {
                        break;
                    }
                    case NOTES:
                    {
                        noteAddingOnClick(nameGetter);
                        break;
                    }
                }
                myDialog.dismiss();
            }
        });

        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();
    }

    private void noteAddingOnClick(TextInputEditText nameGetter)
    {
        Note note;
        String s;
        try {
            s=nameGetter.getText().toString();
            addNoteData(s, "Empty note");
            note = dataBaseHelper.getLatelyAddedNote();
            Log.d("tesciki","dodano do bazy");
            drawNoteButton(note);
            Log.d("tesciki","powinno tutaj dodac przycisk");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void cardAddingOnClick(TextInputEditText nameGetter)
    {
        Card card;
        String s;
        try
        {
            s = nameGetter.getText().toString();
            addCardData(s+" word", s + " answer");
            card = dataBaseHelper.getLatelyAddedCard();
            Log.d("cardAdding", "dodano do bazy");
            drawCardButton(card);
            Log.d("cardAdding", "powinnoo dodac przycisk fiszkki");
        }
        catch(Exception e)
        {

        }
    }

    public void addNoteData(String title, String content)
    {
        try
        {
            boolean insertData = dataBaseHelper.addNoteData(title, content, MainActivity.getCurrentSubject().getSubjectID(), chosen_color.getValue());
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

    public void addCardData(String word, String answer)
    {
        try
        {
            boolean insertData = dataBaseHelper.addCardData(word, answer, MainActivity.getCurrentSubject().getSubjectID());
            if(insertData)
                toastMessage("Dodano poprawnie - " + word + ", " + answer);
            else
                toastMessage("Cos sie wysralo");
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void dropAllSubjectNotes(int subjectID)
    {
        dataBaseHelper.dropNotesBySubjectID(subjectID);
    }

    public static void dropAllSubjectCards(int subjectID)
    {
        dataBaseHelper.dropCardsBySubjectID(subjectID);
    }

    public static Note getCurrentNote() {
        return currentNote;
    }

    public static void setCurrentNote(Note currentNote) {
        SubjectActivity.currentNote = currentNote;
    }

    public static void updateNoteContent(int noteID, String newContent)
    {
        dataBaseHelper.updateNoteContent(noteID, newContent);
    }

    public static Card getCurrentCard() {
        return currentCard;
    }

    public static void setCurrentCard(Card currentCard) {
        SubjectActivity.currentCard = currentCard;
    }
}
