package com.wingor_software.mylearn;

import android.app.Dialog;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Klasa słuzaca do interakcji z przedmiotami
 */
public class SubjectActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    //private ConstraintLayout subjectLayout;
    private LinearLayout subjectLayout;
    Dialog myDialog;
    private TextView warning;

    static DataBaseHelper dataBaseHelper;

    EnumColors chosen_color = EnumColors.valueOf(5);

    private enum BarAction {CARDS, QUIZ, NOTES, EXAMS}

    private SharedPreferences sharedPref;
    private BarAction whichAction;

    private static Note currentNote;
    private static Card currentCard;
    private static Quiz currentQuiz;

    private Uri imageUri;
    private ArrayList<Uri> uriList;
    private Intent gallery;

    private BottomNavigationView navView;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            sharedPref = getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            switch (item.getItemId()) {
                case R.id.action_cards:
                    actionCards(editor);
                    return true;
                case R.id.action_quiz:
                    actionQuiz(editor);
                    return true;
                case R.id.action_notes:
                    actionNotes(editor);
                    return true;
                case R.id.action_exams:
                    actionExams(editor);
                    return true;
            }
            return false;
        }
    };
    StringBuilder path_to_save = new StringBuilder();

    private void actionCards(SharedPreferences.Editor editor)
    {
        whichAction = BarAction.CARDS;
        editor.putInt(getString(R.string.preference), 1);
        editor.apply();     //albo commit ale to moze zawiesic UI
        clearContent();
        drawAllCardButtons();
    }

    private void actionQuiz(SharedPreferences.Editor editor)
    {
        whichAction = BarAction.QUIZ;
        editor.putInt(getString(R.string.preference), 2);
        editor.apply();
        clearContent();
        drawAllQuizButtons();
    }

    private void actionNotes(SharedPreferences.Editor editor)
    {
        whichAction = BarAction.NOTES;
        editor.putInt(getString(R.string.preference), 3);
        editor.apply();
        clearContent();
        drawAllNoteButtons();
    }

    private void actionExams(SharedPreferences.Editor editor)
    {
        whichAction = BarAction.EXAMS;
        editor.putInt(getString(R.string.preference), 4);
        editor.apply();
        clearContent();
    }

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

        navView = findViewById(R.id.nav_bottom_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        uriList = new ArrayList<>();
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            this.setTitle(MainActivity.getCurrentSubject().getSubjectName());
        } catch (Exception e) {
            this.setTitle(R.string.title_activity_subject);
        }

        sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        int defaultValue = 3;
        int submenuValue = sharedPref.getInt(getString(R.string.preference), defaultValue);
        switch(submenuValue)
        {
            case 1:
            {
                navView.setSelectedItemId(R.id.action_cards);
                actionCards(editor);
                break;
            }
            case 2:
            {
                navView.setSelectedItemId(R.id.action_quiz);
                actionQuiz(editor);
                break;
            }
            case 3:
            {
                navView.setSelectedItemId(R.id.action_notes);
                actionNotes(editor);
                break;
            }
            case 4:
            {
                navView.setSelectedItemId(R.id.action_exams);
                actionQuiz(editor);
                break;
            }
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
        if (id == R.id.action_subject_search) {
            showPopupSearch();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showPopupSearch()
    {
        myDialog.setContentView(R.layout.popup_search);
        final TextInputEditText textInputEditText = myDialog.findViewById(R.id.searchGetter);
        Button button = myDialog.findViewById(R.id.searchButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!textInputEditText.getText().toString().equals(""))
                {
                    switch(whichAction)
                    {
                        case CARDS:
                        {
                            clearContent();
                            drawAllCardButtonsContaining(textInputEditText.getText().toString());
                            break;
                        }
                        case QUIZ:
                        {
                            clearContent();
                            drawAllQuizButtonsContaining(textInputEditText.getText().toString());
                            break;
                        }
                        case NOTES:
                        {
                            clearContent();
                            drawAllNoteButtonsContaining(textInputEditText.getText().toString());
                            break;
                        }
                    }
                }
                myDialog.dismiss();
            }
        });
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();
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
//                toastMessage(card.getWord() + " " + card.getAnswer());
                currentCard = card;
                showOpenCardPopup();
            }
        });

        b.setBackground(getResources().getDrawable(R.drawable.subject_drawable_default));
        chosen_color = EnumColors.valueOf(card.getColor());
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

    private void drawQuizButton(final Quiz quiz)
    {
        final Button b = new Button(SubjectActivity.this);

        b.setText(quiz.getQuestion());
        b.setTag("quiz_" + quiz.getID());
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
                currentQuiz = quiz;
                Toast.makeText(SubjectActivity.this, "Wyswietlanie quizu", Toast.LENGTH_LONG).show();
            }
        });

        b.setBackground(getResources().getDrawable(R.drawable.subject_drawable_default));
        chosen_color = EnumColors.valueOf(quiz.getColor());
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

    public void choseColor(View view)
    {
        switch (view.getId())
        {
            case R.id.button_red:
            {
                chosen_color=EnumColors.valueOf(1);

                myDialog.findViewById(R.id.button_red).setAlpha(1f);
                myDialog.findViewById(R.id.button_yellow).setAlpha(0.5f);
                myDialog.findViewById(R.id.button_green).setAlpha(0.5f);
                myDialog.findViewById(R.id.button_blue).setAlpha(0.5f);
                myDialog.findViewById(R.id.button_purple).setAlpha(0.5f);
                break;
            }
            case R.id.button_yellow:
            {
                chosen_color=EnumColors.valueOf(2);

                myDialog.findViewById(R.id.button_red).setAlpha(0.5f);
                myDialog.findViewById(R.id.button_yellow).setAlpha(1f);
                myDialog.findViewById(R.id.button_green).setAlpha(0.5f);
                myDialog.findViewById(R.id.button_blue).setAlpha(0.5f);
                myDialog.findViewById(R.id.button_purple).setAlpha(0.5f);
                break;
            }
            case R.id.button_green:
            {
                chosen_color=EnumColors.valueOf(3);

                myDialog.findViewById(R.id.button_red).setAlpha(0.5f);
                myDialog.findViewById(R.id.button_yellow).setAlpha(0.5f);
                myDialog.findViewById(R.id.button_green).setAlpha(1f);
                myDialog.findViewById(R.id.button_blue).setAlpha(0.5f);
                myDialog.findViewById(R.id.button_purple).setAlpha(0.5f);
                break;
            }
            case R.id.button_blue:
            {
                chosen_color=EnumColors.valueOf(4);

                myDialog.findViewById(R.id.button_red).setAlpha(0.5f);
                myDialog.findViewById(R.id.button_yellow).setAlpha(0.5f);
                myDialog.findViewById(R.id.button_green).setAlpha(0.5f);
                myDialog.findViewById(R.id.button_blue).setAlpha(1f);
                myDialog.findViewById(R.id.button_purple).setAlpha(0.5f);
                break;
            }
            case R.id.button_purple:
            {
                myDialog.findViewById(R.id.button_red).setAlpha(0.5f);
                myDialog.findViewById(R.id.button_yellow).setAlpha(0.5f);
                myDialog.findViewById(R.id.button_green).setAlpha(0.5f);
                myDialog.findViewById(R.id.button_blue).setAlpha(0.5f);
                myDialog.findViewById(R.id.button_purple).setAlpha(1f);

                chosen_color=EnumColors.valueOf(5);
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
                        quizDeletingOnClick(viewButton);
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

    private void quizDeletingOnClick(View viewButton)
    {
        String s = viewButton.getTag().toString();
        String r_s = s.substring(5);
        dataBaseHelper.dropQuizByID(Integer.parseInt(r_s));
        toastMessage("Poprawnie usunieto quiz" + r_s);

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

    private void drawAllNoteButtonsContaining(String phrase)
    {
        try
        {
            List<Note> notes = dataBaseHelper.getNoteList(MainActivity.getCurrentSubject().getSubjectID());
            Iterator it = notes.iterator();
            while(it.hasNext())
            {
                Note note = (Note)it.next();
                if(note.getTitle().toLowerCase().contains(phrase.toLowerCase()))
                    drawNoteButton(note);
            }
        }
        catch (EmptyDataBaseException em)
        {
            warning = new TextView(SubjectActivity.this);
            warning.setText("Can't find such a note");
            warning.setTag("note_warning_tag");
            subjectLayout.addView(warning);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    private void drawAllQuizButtons()
    {
        try
        {
            List<Quiz> quizzes = dataBaseHelper.getQuizList(MainActivity.getCurrentSubject().getSubjectID());
            Iterator it = quizzes.iterator();
            while(it.hasNext())
            {
                drawQuizButton((Quiz)it.next());
            }
        }
        catch (EmptyDataBaseException em)
        {
            warning = new TextView(SubjectActivity.this);
            warning.setText(R.string.quiz_warning);
            warning.setTag("quiz_warning_tag");
            subjectLayout.addView(warning);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    private void drawAllQuizButtonsContaining(String phrase)
    {
        try
        {
            List<Quiz> quizzes = dataBaseHelper.getQuizList(MainActivity.getCurrentSubject().getSubjectID());
            Iterator it = quizzes.iterator();
            while(it.hasNext())
            {
                Quiz quiz = (Quiz) it.next();
                if(quiz.getQuestion().toLowerCase().contains(phrase.toLowerCase()))
                    drawQuizButton(quiz);
            }
        }
        catch (EmptyDataBaseException em)
        {
            warning = new TextView(SubjectActivity.this);
            warning.setText(R.string.quiz_warning);
            warning.setTag("quiz_warning_tag");
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

    private void drawAllCardButtonsContaining(String phrase)
    {
        try
        {
            List<Card> cards = dataBaseHelper.getCardList(MainActivity.getCurrentSubject().getSubjectID());
            Iterator it = cards.iterator();
            while(it.hasNext())
            {
                Card card = (Card)it.next();
                if(card.getWord().toLowerCase().contains(phrase.toLowerCase()))
                    drawCardButton(card);
            }
        }
        catch (EmptyDataBaseException em)
        {
            warning = new TextView(SubjectActivity.this);
            warning.setText("Can't find such a card");
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

    public void addingContent(View view)
    {
        switch(whichAction)
        {
            case CARDS:
            {
                showPopupCardAdding(view);
                break;
            }
            case QUIZ:
            {
                showPopupQuizAdding_1(view);
                break;
            }
            case NOTES:
            {
                showPopupSubject(view);
                break;
            }
            case EXAMS:
            {
                break;
            }
        }
    }

    public void showPopupSubject(View view)
    {
        myDialog.setContentView(R.layout.popup_foto_note);
        Button select_foto = myDialog.findViewById(R.id.selectFotoButton);
        Button addButtonFoto = myDialog.findViewById(R.id.addButton);

        //usuwam zapisane uri po narysoaniu zdjec
        uriList.clear();
        path_to_save=new StringBuilder();

        select_foto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                //gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
//                gallery.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
//
//                startActivityForResult(gallery,100);
//                setResult(RESULT_OK,gallery);


                gallery = new Intent();
// Show only images, no videos or anything else
                gallery.setType("image/*");
                gallery.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
                gallery.setAction(Intent.ACTION_PICK);
// Always show the chooser (if there are multiple options available)
                startActivityForResult(Intent.createChooser(gallery, "Select Picture"),100);



            }
        });
        addButtonFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextInputEditText nameGetter = myDialog.findViewById(R.id.nameGetterfoto);
                if(subjectLayout==null)
                {
                    Log.d("tesciki","subjectslayout jest nullem");
                }
                noteAddingOnClick(nameGetter);
                myDialog.dismiss();
            }
        });

        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();
    }

    private void showPopupCardAdding(View view)
    {
        myDialog.setContentView(R.layout.popup_add_card);
        Button addButton = myDialog.findViewById(R.id.addCardButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextInputEditText wordGetter = myDialog.findViewById(R.id.wordGetter);
                TextInputEditText answerGetter = myDialog.findViewById(R.id.answerGetter);
                cardAddingOnClick(wordGetter, answerGetter);
                myDialog.dismiss();
            }
        });

        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();
    }

    private void showPopupQuizAdding_1(View view)
    {
        final View view1 = view;
        myDialog.setContentView(R.layout.popup_add_quiz_1);
        Button nextButton = myDialog.findViewById(R.id.nextQuizButton);

        final TextInputEditText question = myDialog.findViewById(R.id.questionGetter);


        final Spinner spinner1= myDialog.findViewById(R.id.spinner1);
        final Spinner spinner2= myDialog.findViewById(R.id.spinner2);



        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupQuizAdding_2(view1,Integer.parseInt(spinner1.getSelectedItem().toString()),Integer.parseInt(spinner2.getSelectedItem().toString()),question.getText().toString());
            }
        });



        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();}

    private void showPopupQuizAdding_2(View view, final int good_answers, final int bad_answers, final String name_of_quiz)
    {
        myDialog.setContentView(R.layout.popup_add_quiz_2);
        Button addButton = myDialog.findViewById(R.id.addQuizButton);

        final LinearLayout answers_layout = myDialog.findViewById(R.id.answers_layout);

        final StringBuilder good_answers_strings = new StringBuilder();
        final StringBuilder bad_answers_strings = new StringBuilder();

        for(int i=0;i<good_answers;i++)
        {
            TextInputEditText t = new TextInputEditText(SubjectActivity.this);
            t.setHint("Correct answer #" + (i+1));
            t.setId(i+1);
            t.setTag("good answer");
            answers_layout.addView(t);
        }
        for(int i=good_answers;i<bad_answers+good_answers;i++)
        {
            TextInputEditText t = new TextInputEditText(SubjectActivity.this);
            t.setHint("Incorrect answer #" + (i+1-good_answers));
            t.setId(i+1);
            t.setTag("bad answer");
            answers_layout.addView(t);
        }

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                for (int i=1;i<good_answers+bad_answers+1;i++)
                {
                    TextInputEditText t = myDialog.findViewById(i);
                    Log.d("test",t.getId()+"");
                    if(t.getTag().equals("good answer"))
                    {
                        good_answers_strings.append(t.getText());
                        good_answers_strings.append("\n");
                    }
                    else if(t.getTag().equals("bad answer"))
                    {
                        bad_answers_strings.append(t.getText());
                        bad_answers_strings.append("\n");
                    }
                }

                Log.d("test",name_of_quiz);
                Log.d("test",good_answers_strings.toString());
                Log.d("test",bad_answers_strings.toString());



                quizAddingOnClick(name_of_quiz, good_answers_strings.toString(),bad_answers_strings.toString());
                myDialog.dismiss();
            }
        });

        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();
    }

    private void showOpenCardPopup()
    {
        myDialog.setContentView(R.layout.popup_open_card);
        Button check = myDialog.findViewById(R.id.checkCardButton);
        final TextView word = myDialog.findViewById(R.id.wordText);
        final TextInputEditText answer = myDialog.findViewById(R.id.openCardAnswerGetter);
        word.setText(currentCard.getWord());

        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myDialog.dismiss();
                if(answer.getText().toString().equalsIgnoreCase(currentCard.getAnswer()))
                    showResultCardPopup(true);
                else
                    showResultCardPopup(false);
            }
        });
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();
    }

    private void showResultCardPopup(boolean correct)
    {
        myDialog.setContentView(R.layout.popup_result_card);
        Button okButton = myDialog.findViewById(R.id.okCardButton);
        TextView result = myDialog.findViewById(R.id.resultText);
        TextView correctAnswer = myDialog.findViewById(R.id.correctAnswer);
        if(correct)
        {
            result.setText("Correct!");
            result.setTextColor(Color.GREEN);
            correctAnswer.setVisibility(View.INVISIBLE);
        }
        else
        {
            result.setText("Wrong!");
            result.setTextColor(Color.RED);
            correctAnswer.setVisibility(View.VISIBLE);
            correctAnswer.setText("Correct answer is : '" + currentCard.getAnswer() + "'");
        }
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myDialog.dismiss();
            }
        });
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Intent intent = getIntent();
        setResult(RESULT_OK,intent);
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK && requestCode==100)
        {
            ClipData cd = data.getClipData();

            if ( cd == null ) {
                Uri uri = data.getData();
                uriList.add(uri);
            }
            else {
                for (int i = 0; i < cd.getItemCount(); i++) {
                    ClipData.Item item = cd.getItemAt(i);
                    Uri uri = item.getUri();
                    uriList.add(uri);
                }
            }


            LinearLayout fotosLayout = myDialog.findViewById(R.id.fotos_layout);

            for (int i = 0; i < uriList.size(); i++) {

                ImageView imageView = new ImageView(SubjectActivity.this);
                try {
                    if(!path_to_save.toString().equals(""))
                    {
                        path_to_save.append("\n");
                    }
                    path_to_save.append(getRealPathFromURI(SubjectActivity.this,uriList.get(i)));
                    imageView.setMaxHeight(450);
                    imageView.setAdjustViewBounds(true);
                    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    imageView.setImageBitmap(decodeSampledBitmapFromResource(getRealPathFromURI(SubjectActivity.this,uriList.get(i)), 450, 450));

                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                fotosLayout.addView(imageView);
            }

        }
    }


    private String getStringFromUriList()
    {
        StringBuilder uriString = new StringBuilder();
        for (int i = 0; i < uriList.size(); i++) {
            if(uriString.length() != 0)
                uriString.append("\n");
            uriString.append(uriList.get(i).toString());
        }
        return uriString.toString();
    }

    private void noteAddingOnClick(TextInputEditText nameGetter)
    {
        //tooooo zienic zeby dzialalo ze zdjeciami, uri do zdjecia jest globalnie
        Note note;
        String s;
        try {
            s=nameGetter.getText().toString();
            addNoteData(s, "Empty note", path_to_save.toString());
            Log.d("uritest", getStringFromUriList());
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

    private void cardAddingOnClick(TextInputEditText wordGetter, TextInputEditText answerGetter)
    {
        Card card;
        String word;
        String answer;
        try
        {
            word = wordGetter.getText().toString();
            answer = answerGetter.getText().toString();

            addCardData(word, answer, "");
            card = dataBaseHelper.getLatelyAddedCard();
            Log.d("cardAdding", "dodano do bazy");
            drawCardButton(card);
            Log.d("cardAdding", "powinnoo dodac przycisk fiszkki");
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    private void quizAddingOnClick(String question, String goodAnswer,String badAnswer)
    {
        Quiz quiz;
        try
        {
            addQuizData(question, goodAnswer, badAnswer, "");
            quiz = dataBaseHelper.getLatelyAddedQuiz();
            Log.d("quizAdding", "dodano do bazy");
            drawQuizButton(quiz);
            Log.d("quizAdding", "powinnoo dodac przycisk quizu");
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public void addNoteData(String title, String content, String filePath)
    {
        try
        {
            boolean insertData = dataBaseHelper.addNoteData(title, content, MainActivity.getCurrentSubject().getSubjectID(), chosen_color.getValue(), filePath);
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

    public void addCardData(String word, String answer, String attachedNotes)
    {
        try
        {
            boolean insertData = dataBaseHelper.addCardData(word, answer, MainActivity.getCurrentSubject().getSubjectID(), attachedNotes, chosen_color.getValue());
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

    public void addQuizData(String question, String goodAnswer, String badAnswers, String attachedNotes)
    {
        try
        {
            boolean insertData = dataBaseHelper.addQuizData(question, goodAnswer, badAnswers, MainActivity.getCurrentSubject().getSubjectID(), attachedNotes, chosen_color.getValue());
            if(insertData)
                toastMessage("Dodano poprawnie - " + question);
            else
                toastMessage("Cos sie wysralo");
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
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

    private String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } catch (Exception e) {
            Log.e("test", "getRealPathFromURI Exception : " + e.toString());
            return "";
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromResource(String s, int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(s, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(s, options);
    }
}
