package com.wingor_software.mylearn;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewManager;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.view.GravityCompat;
import androidx.core.widget.CompoundButtonCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.DialogFragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Random;

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
    EnumColors temp_note_add_color = EnumColors.valueOf(5);

    private static boolean color_picked = false;


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

    private ConstraintLayout scoreLayout;
    private ScrollView subjectScrollView;

    private static ExamType examType;
    private static int questionsCountToExam = 0;

    private static final int REQUEST_CODE_READING_FILE_CARDS = 300;
    private static final int REQUEST_CODE_READING_FILE_QUIZ = 400;

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
        scoreLayout.setVisibility(View.VISIBLE);
        ProgressBar progressBar = scoreLayout.findViewById(R.id.scoreBar);

        TextView scoreText = scoreLayout.findViewById(R.id.scoreText);
        int light = getResources().getColor(R.color.colorPrimary);
        int dark = getResources().getColor(R.color.white);
        scoreText.setTextColor((dataBaseHelper.getDisplayMode() == DisplayMode.LIGHT) ? light : dark);

        Button examStartButton = (Button) findViewById(R.id.examStartButton);
        examStartButton.setTextColor((dataBaseHelper.getDisplayMode() == DisplayMode.LIGHT) ? light : dark);

        CheckBox checkBoxCards = (CheckBox) findViewById(R.id.checkBoxCards);
        checkBoxCards.setTextColor((dataBaseHelper.getDisplayMode() == DisplayMode.LIGHT) ? light : dark);
        checkBoxCards.setChecked(false);

        CheckBox checkBoxQuiz = (CheckBox) findViewById(R.id.checkBoxQuestions);
        checkBoxQuiz.setTextColor((dataBaseHelper.getDisplayMode() == DisplayMode.LIGHT) ? light : dark);
        checkBoxQuiz.setChecked(false);

        SeekBar seekBar = (SeekBar) findViewById(R.id.questionCountSeekBar);
        seekBar.setMax(2);
        seekBar.setEnabled(false);
        seekBar.setProgress(1);

        final TextView questionsCount = (TextView) findViewById(R.id.questionsCountText);
        questionsCount.setText("Choose exam options");

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                questionsCount.setText("" + i);
                questionsCountToExam = i;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });

        checkBoxCards.setOnCheckedChangeListener(new CheckBoxStateListener(checkBoxCards, checkBoxQuiz, seekBar, questionsCount, dataBaseHelper));
        checkBoxQuiz.setOnCheckedChangeListener(new CheckBoxStateListener(checkBoxCards, checkBoxQuiz, seekBar, questionsCount, dataBaseHelper));

        dark = getResources().getColor(R.color.colorLightPrimary);

        CheckBox checkBox = (CheckBox) findViewById(R.id.checkBoxQuestions);
        for (int i = 0; i < 2; i++) {
            if (Build.VERSION.SDK_INT < 21) {
                CompoundButtonCompat.setButtonTintList(checkBox, ColorStateList.valueOf((dataBaseHelper.getDisplayMode() == DisplayMode.LIGHT) ? light : dark));//Use android.support.v4.widget.CompoundButtonCompat when necessary else
            } else {
                checkBox.setButtonTintList(ColorStateList.valueOf((dataBaseHelper.getDisplayMode() == DisplayMode.LIGHT) ? light : dark));//setButtonTintList is accessible directly on API>19
            }
            checkBox = (CheckBox) findViewById(R.id.checkBoxCards);
        }

        int examsTaken = 0;
        int examsPassed = 0;
        try
        {
            examsTaken = MainActivity.getCurrentSubject().getExamsTaken();
            examsPassed = MainActivity.getCurrentSubject().getExamsPassed();
            progressBar.setMax(examsTaken);
            progressBar.setProgress(examsPassed);
            scoreText.setText(new String(examsPassed + "/" + examsTaken));
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject);
        Toolbar toolbar = findViewById(R.id.toolbar_subject);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab_subject);

        dataBaseHelper = new DataBaseHelper(this);
        subjectScrollView = findViewById(R.id.scrollView2);
        int light = getResources().getColor(R.color.white);
        int dark = getResources().getColor(R.color.colorDarkModeBackground);
        subjectScrollView.setBackgroundColor((dataBaseHelper.getDisplayMode() == DisplayMode.LIGHT) ? light : dark);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setBackgroundColor((dataBaseHelper.getDisplayMode() == DisplayMode.LIGHT) ? light : dark);
        light = getResources().getColor(R.color.black);
        dark = getResources().getColor(R.color.white);
        navigationView.setItemTextColor(ColorStateList.valueOf((dataBaseHelper.getDisplayMode() == DisplayMode.LIGHT) ? light : dark));

        subjectLayout = findViewById(R.id.subjectActivityLayout);
        myDialog = new Dialog(this);

        navView = findViewById(R.id.nav_bottom_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        uriList = new ArrayList<>();

        scoreLayout = findViewById(R.id.scoreLayout);



        final CalendarView calendarView = navigationView.getHeaderView(0).findViewById(R.id.calendarView);
        final TextView titleOfDay = navigationView.getHeaderView(0).findViewById(R.id.titleOfDay);
        final LinearLayout contentOfDay = navigationView.getHeaderView(0).findViewById(R.id.contentOfDay);
        final Button addnewCalendarEventButton = navigationView.getHeaderView(0).findViewById(R.id.addCalendarEventButton);



        titleOfDay.setText("To do on : " + new SimpleDateFormat("yyyy-M-d", Locale.getDefault()).format(new Date()));


        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(final CalendarView calendarView, final int i, final int i1, final int i2) {

                titleOfDay.setText("To do on : " + i + "-" +i1 + "-" +i2);
                contentOfDay.removeAllViews();

                try {
                    for (CalendarEvent c:dataBaseHelper.getCalendarEventList()) {
                        if(c.getDate().equals(i+"-"+i1+"-"+i2))
                        {
                            TextView textView = new TextView(SubjectActivity.this);
                            textView.setGravity(Gravity.CENTER);
                            textView.setTextColor(getResources().getColor(R.color.black));
                            textView.setText(c.getContent());
                            contentOfDay.addView(textView);


                        }
                    }
                }
                catch (EmptyDataBaseException e)
                {
                    e.printStackTrace();
                }

                addnewCalendarEventButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        myDialog.setContentView(R.layout.popup_add_calendarevent);
                        final TextInputEditText wordgetter = myDialog.findViewById(R.id.wordGetterpopupcalendar);
                        Button addCalendarEventButtonpopup = myDialog.findViewById(R.id.addCalendarEventButtonpopup);
                        addCalendarEventButtonpopup.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if(!wordgetter.getText().toString().equals(""))
                                {
                                    dataBaseHelper.addCalendarEventData(i+"-"+i1+"-"+i2,wordgetter.getText().toString());
                                    myDialog.dismiss();

                                    contentOfDay.removeAllViews();

                                    try {
                                        for (CalendarEvent c:dataBaseHelper.getCalendarEventList()) {
                                            if(c.getDate().equals(i+"-"+i1+"-"+i2))
                                            {
                                                TextView textView = new TextView(SubjectActivity.this);
                                                textView.setGravity(Gravity.CENTER);
                                                textView.setTextColor(getResources().getColor(R.color.black));
                                                textView.setText(c.getContent());
                                                contentOfDay.addView(textView);


                                            }
                                        }
                                    }
                                    catch (EmptyDataBaseException e)
                                    {
                                        e.printStackTrace();
                                    }


                                }
                                else
                                {
                                    toastMessage("Please enter a non-empty value!");
                                }

                            }
                        });
                        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        myDialog.show();
                    }
                });

            }
        });

        //zmiany w scroll view
        ScrollView dayScrollView = navigationView.getHeaderView(0).findViewById(R.id.dayScrollView);


        dayScrollView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        break;

                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }

                return false;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        try {
            if(MainActivity.getCurrentSubject().getSubjectName().length()>18)
            {
                String newTitle = MainActivity.getCurrentSubject().getSubjectName().substring(0,17) + "...";
                this.setTitle(newTitle);
            }
            else
            {
                this.setTitle(MainActivity.getCurrentSubject().getSubjectName());
            }
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
    protected void onPause() {
        super.onPause();
        sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(getString(R.string.preference), actionToInt());
    }

    private int actionToInt()
    {
        switch(whichAction)
        {
            case CARDS:{
                return 1;
            }
            case QUIZ:{
                return 2;
            }
            case NOTES:{
                return 3;
            }
            case EXAMS:{
                return 4;
            }
            default:{
                return 3;
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
        switch(id)
        {
            case R.id.action_subject_search:
            {
                showPopupSearch();
                return true;
            }
            case R.id.action_export_subject:
            {
                new BackgroudExport(SubjectActivity.this, dataBaseHelper).execute();
                return true;
            }
            case R.id.action_share_subject:
            {
                new BackgroudShare(SubjectActivity.this, dataBaseHelper).execute();
                return true;
            }
            case R.id.action_import_cards_from_file:
            {
                showSeparatorPopup(REQUEST_CODE_READING_FILE_CARDS);
                return true;
            }
            case R.id.action_import_quiz_from_file:
            {
               showSeparatorPopup(REQUEST_CODE_READING_FILE_QUIZ);
                return true;
            }
            case R.id.action_zip_file_export:
            {
                new BackgroundZipExport(SubjectActivity.this, dataBaseHelper).execute();
                return true;
            }
            case R.id.action_zip_file_share:
            {
                new BackgroundZipShare(SubjectActivity.this, dataBaseHelper).execute();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void showSeparatorPopup(final int requestCode)
    {
        myDialog.setContentView(R.layout.popup_import_card_from_file);
        final EditText editText = (EditText) myDialog.findViewById(R.id.editTextCardSeparator);
        Button button = (Button) myDialog.findViewById(R.id.okButtonImportCard);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!editText.getText().toString().equals(""))
                {
                    FromFileImporter.setSeparator(editText.getText().charAt(0));
                    myDialog.dismiss();
                    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType("text/*");
                    startActivityForResult(intent, requestCode);
                }
                else
                {
                    toastMessage("Please enter a non-empty value!");
                }

            }
        });
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();
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
                            myDialog.dismiss();

                            break;
                        }
                        case QUIZ:
                        {
                            clearContent();
                            drawAllQuizButtonsContaining(textInputEditText.getText().toString());
                            myDialog.dismiss();

                            break;
                        }
                        case NOTES:
                        {
                            clearContent();
                            drawAllNoteButtonsContaining(textInputEditText.getText().toString());
                            myDialog.dismiss();

                            break;
                        }
                    }
                }
                else
                {
                    toastMessage("Please enter a non-empty value!");
                }
            }
        });
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();
    }

    public void startExam(View view) {
        CheckBox cards = findViewById(R.id.checkBoxCards);
        CheckBox questions = findViewById(R.id.checkBoxQuestions);
        Intent intent;

        int cardsCount = 0;
        int quizzesCount = 0;
        try
        {
            cardsCount = dataBaseHelper.getCardList(MainActivity.getCurrentSubject().getSubjectID()).size();
            quizzesCount = dataBaseHelper.getQuizList(MainActivity.getCurrentSubject().getSubjectID()).size();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        if(questionsCountToExam == 0)
        {
            Toast.makeText(SubjectActivity.this, "Please, select exam size", Toast.LENGTH_LONG).show();
        }
        else if(cards.isChecked() && questions.isChecked() && cardsCount + quizzesCount > 0)
        {
            examType = ExamType.ALL;
            Toast.makeText(SubjectActivity.this, "Cards and questions exam", Toast.LENGTH_LONG).show();
            intent = new Intent(SubjectActivity.this, ExamActivity.class);
            startActivity(intent);
        }
        else if(cards.isChecked() && cardsCount > 0)
        {
            examType = ExamType.CARDS;
            Toast.makeText(SubjectActivity.this, "Cards exam", Toast.LENGTH_LONG).show();
            intent = new Intent(SubjectActivity.this, ExamActivity.class);
            startActivity(intent);
        }
        else if(questions.isChecked() && quizzesCount > 0)
        {
            examType = ExamType.QUESTIONS;
            Toast.makeText(SubjectActivity.this, "Questions exam", Toast.LENGTH_LONG).show();
            intent = new Intent(SubjectActivity.this, ExamActivity.class);
            startActivity(intent);
        }
        else if(!cards.isChecked() && !questions.isChecked())
        {
            Toast.makeText(SubjectActivity.this, "Please, select exam type", Toast.LENGTH_LONG).show();
        }
        else
        {
            Toast.makeText(SubjectActivity.this, "Looks like you don't have any cards or questions", Toast.LENGTH_LONG).show();
        }
    }

    public static int getQuestionsCountToExam()
    {
        return questionsCountToExam;
    }

    public static ExamType getExamType()
    {
        return examType;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action
            //addData("Test" + new Random().nextInt(), 1);
        } else if (id == R.id.nav_gallery) {
            //Intent intent = new Intent(SubjectActivity.this, ListDataActivity.class);
            //startActivity(intent);
        } else if (id == R.id.nav_slideshow) {
            //dataBaseHelper.dropSubjectTable();
            //subjectsLayout.removeAllViews();
        } else if (id == R.id.nav_tools) {

        } else if (id == R.id.nav_share) {
            dataBaseHelper.setDisplayMode(DisplayMode.LIGHT);
            restartApp();
        } else if (id == R.id.nav_send) {
            dataBaseHelper.setDisplayMode(DisplayMode.DARK);
            restartApp();
        }

        else if (id == R.id.nav_info)
        {
            Intent intent = new Intent(SubjectActivity.this,Info.class);
            startActivity(intent);
        }
        else if(id == R.id.nav_contact)
        {
            Intent intent = new Intent(SubjectActivity.this,Contact.class);
            startActivity(intent);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void restartApp()
    {
        Intent i = new Intent(getApplicationContext(), SubjectActivity.class);
        startActivity(i);
        finish();
    }

    public static void addPassedExam()
    {
        try
        {
            Subject currentSubject = MainActivity.getCurrentSubject();
            currentSubject.setExamsTaken(currentSubject.getExamsTaken() + 1);
            currentSubject.setExamsPassed(currentSubject.getExamsPassed() + 1);
            dataBaseHelper.updateExamsCount(currentSubject.getSubjectID(), currentSubject.getExamsTaken(), currentSubject.getExamsPassed());
//            Toast.makeText(SubjectActivity.this, "Dodano zaliczony egzamin", Toast.LENGTH_LONG).show();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void addNotPassedExam()
    {
        try
        {
            Subject currentSubject = MainActivity.getCurrentSubject();
            currentSubject.setExamsTaken(currentSubject.getExamsTaken() + 1);
            dataBaseHelper.updateExamsCount(currentSubject.getSubjectID(), currentSubject.getExamsTaken(), currentSubject.getExamsPassed());
//            Toast.makeText(SubjectActivity.this, "Dodano nie zaliczony egzamin", Toast.LENGTH_LONG).show();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    private void clearContent() {
        subjectLayout.removeAllViewsInLayout();
        scoreLayout.setVisibility(View.GONE);
    }

    private void drawNoteButton(final Note note) {
        final Button b = new Button(SubjectActivity.this);

        b.setText(note.getTitle());
        int light = getResources().getColor(R.color.black);
        int dark = getResources().getColor(R.color.white);
        b.setTextColor((dataBaseHelper.getDisplayMode() == DisplayMode.LIGHT) ? light : dark);
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
        int light = getResources().getColor(R.color.black);
        int dark = getResources().getColor(R.color.white);
        b.setTextColor((dataBaseHelper.getDisplayMode() == DisplayMode.LIGHT) ? light : dark);
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
//                showOpenCardPopup();
                showCardPopup(subjectLayout.indexOfChild(view));
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

    private void showCardPopup(int currentPosition)
    {
        myDialog.setContentView(R.layout.popup_scrolling_open_card);
        myDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        CardPagerAdapter mCardPageAdapter = new CardPagerAdapter(dataBaseHelper, this, myDialog);
        ViewPager mViewPager = (ViewPager) myDialog.findViewById(R.id.cardPager);
        mViewPager.setAdapter(mCardPageAdapter);
        mViewPager.setCurrentItem(currentPosition);

        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();
    }

    private void drawQuizButton(final Quiz quiz)
    {
        final Button b = new Button(SubjectActivity.this);

        b.setText(quiz.getQuestion());
        int light = getResources().getColor(R.color.black);
        int dark = getResources().getColor(R.color.white);
        b.setTextColor((dataBaseHelper.getDisplayMode() == DisplayMode.LIGHT) ? light : dark);
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
                showOpenQuizPopup(subjectLayout.indexOfChild(view));
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
                temp_note_add_color = EnumColors.valueOf(1);
                color_picked=true;

                myDialog.findViewById(R.id.button_red).setAlpha(1f);
                myDialog.findViewById(R.id.button_yellow).setAlpha(0.3f);
                myDialog.findViewById(R.id.button_green).setAlpha(0.3f);
                myDialog.findViewById(R.id.button_blue).setAlpha(0.3f);
                myDialog.findViewById(R.id.button_purple).setAlpha(0.3f);
                break;
            }
            case R.id.button_yellow:
            {
                chosen_color=EnumColors.valueOf(2);
                temp_note_add_color = EnumColors.valueOf(2);
                color_picked=true;

                myDialog.findViewById(R.id.button_red).setAlpha(0.3f);
                myDialog.findViewById(R.id.button_yellow).setAlpha(1f);
                myDialog.findViewById(R.id.button_green).setAlpha(0.3f);
                myDialog.findViewById(R.id.button_blue).setAlpha(0.3f);
                myDialog.findViewById(R.id.button_purple).setAlpha(0.3f);
                break;
            }
            case R.id.button_green:
            {
                chosen_color=EnumColors.valueOf(3);
                temp_note_add_color = EnumColors.valueOf(3);
                color_picked=true;

                myDialog.findViewById(R.id.button_red).setAlpha(0.3f);
                myDialog.findViewById(R.id.button_yellow).setAlpha(0.3f);
                myDialog.findViewById(R.id.button_green).setAlpha(1f);
                myDialog.findViewById(R.id.button_blue).setAlpha(0.3f);
                myDialog.findViewById(R.id.button_purple).setAlpha(0.3f);
                break;
            }
            case R.id.button_blue:
            {
                chosen_color=EnumColors.valueOf(4);
                temp_note_add_color = EnumColors.valueOf(4);
                color_picked=true;

                myDialog.findViewById(R.id.button_red).setAlpha(0.3f);
                myDialog.findViewById(R.id.button_yellow).setAlpha(0.3f);
                myDialog.findViewById(R.id.button_green).setAlpha(0.3f);
                myDialog.findViewById(R.id.button_blue).setAlpha(1f);
                myDialog.findViewById(R.id.button_purple).setAlpha(0.3f);
                break;
            }
            case R.id.button_purple:
            {
                chosen_color=EnumColors.valueOf(5);
                color_picked=true;

                myDialog.findViewById(R.id.button_red).setAlpha(0.3f);
                myDialog.findViewById(R.id.button_yellow).setAlpha(0.3f);
                myDialog.findViewById(R.id.button_green).setAlpha(0.3f);
                myDialog.findViewById(R.id.button_blue).setAlpha(0.3f);
                myDialog.findViewById(R.id.button_purple).setAlpha(1f);

                chosen_color=EnumColors.valueOf(5);
                temp_note_add_color = EnumColors.valueOf(5);
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
                Log.d("test","Wybrany kolor przed wybraniem zdjec to" + chosen_color);

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
                if(!nameGetter.getText().toString().equals(""))
                {

                    noteAddingOnClick(nameGetter);
                    myDialog.dismiss();
                    Log.d("test","KOLOR WYBRANY PO WYBRANIU ZDJEC TO " + chosen_color);


                }
                else
                {
                    toastMessage("Please enter a non-empty value!");
                }
            }
        });

        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();
    }

    private void showPopupCardAdding(View view)
    {
        myDialog.setContentView(R.layout.popup_add_card);
        Button addButton = myDialog.findViewById(R.id.addCardButton);

        final TextInputEditText wordGetter = myDialog.findViewById(R.id.wordGetter);
        final TextInputEditText answerGetter = myDialog.findViewById(R.id.answerGetter);
        final String word = wordGetter.getText().toString();
        final String answer = answerGetter.getText().toString();

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!(word.equals("") && !answer.equals("")))
                {
                    cardAddingOnClick(wordGetter, answerGetter);
                    myDialog.dismiss();
                }
                else
                {
                    toastMessage("Please enter a non-empty value!");
                }
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
                if(!question.getText().toString().equals(""))
                {
                    showPopupQuizAdding_2(view1, Integer.parseInt(spinner1.getSelectedItem().toString()), Integer.parseInt(spinner2.getSelectedItem().toString()), question.getText().toString());
                }
                else
                {
                    toastMessage("Please enter a non-empty value!");
                }
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
                boolean emptySomewhere = false;
                for (int i=1;i<good_answers+bad_answers+1;i++)
                {
                    TextInputEditText t = myDialog.findViewById(i);
                    Log.d("test",t.getId()+"");
                    if(t.getText().toString().equals(""))
                    {
                        emptySomewhere = true;
                        break;
                    }
                    if(t.getTag().equals("good answer"))
                    {
                        if(!good_answers_strings.toString().equals(""))
                            good_answers_strings.append("\n");
                        good_answers_strings.append(t.getText());
                    }
                    else if(t.getTag().equals("bad answer"))
                    {
                        if(!bad_answers_strings.toString().equals(""))
                            bad_answers_strings.append("\n");
                        bad_answers_strings.append(t.getText());
                    }
                }

                Log.d("test",name_of_quiz);
                Log.d("test",good_answers_strings.toString());
                Log.d("test",bad_answers_strings.toString());

                if(!emptySomewhere)
                {
                    quizAddingOnClick(name_of_quiz, good_answers_strings.toString(),bad_answers_strings.toString());
                    myDialog.dismiss();
                }
                else
                {
                    toastMessage("Please enter a non-empty value!");
                }
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

        final ImageButton leftArrow = myDialog.findViewById(R.id.leftArrow);
        final ImageButton rightArrow = myDialog.findViewById(R.id.rightArrow);

        try
        {
            int id_of_current_card=currentCard.getID();
            int index_in_array_of_current_card=0;

            Log.d("test","ID kartki z bazy danych wynosi: " + id_of_current_card);

            List <Card> list = dataBaseHelper.getCardList(currentCard.getSubjectID());

            for(int i=0;i<list.size();i++)
            {
                if(list.get(i).getID()==id_of_current_card)
                {
                    index_in_array_of_current_card=i;
                    Log.d("test","znaleziono index w liscie: " + index_in_array_of_current_card );
                    break;

                }
            }
            if(index_in_array_of_current_card==0)
            {
                leftArrow.setAlpha(0.2f);
                leftArrow.setClickable(false);
                leftArrow.setEnabled(false);
                if(index_in_array_of_current_card==(list.size()-1))
                {
                    rightArrow.setAlpha(0.2f);
                    rightArrow.setClickable(false);
                    rightArrow.setEnabled(false);
                }
            }
            else if(index_in_array_of_current_card==list.size()-1)
            {
                rightArrow.setAlpha(0.2f);
                rightArrow.setClickable(false);
                rightArrow.setEnabled(false);
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

         word.setText(currentCard.getWord());

        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(answer.getText().toString().equalsIgnoreCase(currentCard.getAnswer()))
                {
                    //showResultCardPopup(true);
                    word.setText("Correct");
                }
                else
                {
                    //showResultCardPopup(false);
                    word.setText("Incorrect");
                }
            }
        });
        leftArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                answer.setText("");
                Log.d("test","lewa strzalka");
                rightArrow.setAlpha(1f);
                rightArrow.setClickable(true);
                rightArrow.setEnabled(true);
                try
                {
                    int id_of_current_card=currentCard.getID();
                    int index_in_array_of_current_card=0;

                    Log.d("test","ID kartki z bazy danych wynosi: " + id_of_current_card);

                    List <Card> list = dataBaseHelper.getCardList(currentCard.getSubjectID());



                    for(int i=0;i<list.size();i++)
                    {
                        if(list.get(i).getID()==id_of_current_card)
                        {
                            index_in_array_of_current_card=i;
                            Log.d("test","znaleziono index w liscie: " + index_in_array_of_current_card );
                            break;

                        }
                    }
                    if(index_in_array_of_current_card>0)
                    {

                        currentCard = list.get(index_in_array_of_current_card-1);
                        if(index_in_array_of_current_card-1==0)
                        {
                            leftArrow.setAlpha(0.2f);
                            leftArrow.setClickable(false);
                            leftArrow.setEnabled(false);
                        }
                        Log.d("test","id nowej kartki: " + currentCard.getID());
                        word.setText(currentCard.getWord());

                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }

            }
        });
        rightArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                answer.setText("");
                Log.d("test","prawa strzalka");
                leftArrow.setAlpha(1f);
                leftArrow.setClickable(true);
                leftArrow.setEnabled(true);
                try
                {

                    int id_of_current_card=currentCard.getID();
                    int index_in_array_of_current_card=0;

                    Log.d("test","ID kartki z bazy danych wynosi: " + id_of_current_card);

                    List <Card> list = dataBaseHelper.getCardList(currentCard.getSubjectID());



                    for(int i=0;i<list.size();i++)
                    {
                        if(list.get(i).getID()==id_of_current_card)
                        {
                            index_in_array_of_current_card=i;
                            Log.d("test","znaleziono index w liscie: " + index_in_array_of_current_card );
                            break;

                        }
                    }
                    if(index_in_array_of_current_card<list.size()-1)
                    {
                        currentCard = list.get(index_in_array_of_current_card+1);
                        if(index_in_array_of_current_card+1==list.size()-1)
                        {
                            rightArrow.setAlpha(0.2f);
                            rightArrow.setClickable(false);
                            rightArrow.setEnabled(false);
                        }
                        Log.d("test","id nowej kartki: " + currentCard.getID());
                        word.setText(currentCard.getWord());

                    }

                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }

            }
        });
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();
    }

//    private void showOpenQuizPopup()
//    {
//        myDialog.setContentView(R.layout.popup_open_quiz);
//        Button check = myDialog.findViewById(R.id.checkQuizButton);
//        final TextView question = myDialog.findViewById(R.id.quizQuiestion);
//        question.setText(currentQuiz.getQuestion());
//        final LinearLayout checkBoxLayout = myDialog.findViewById(R.id.checkbox_quiz_layout);
//
//        HashSet<Answer> answersSet = new HashSet<>();
//        for (int i = 0; i < currentQuiz.getGoodAnswers().size(); i++) {
//            answersSet.add(new Answer(currentQuiz.getGoodAnswers().get(i), true));
//        }
//        for (int i = 0; i < currentQuiz.getBadAnswers().size(); i++) {
//            answersSet.add(new Answer(currentQuiz.getBadAnswers().get(i), false));
//        }
//
//        for(Answer answer : answersSet)
//        {
//            CheckBox checkBox = new CheckBox(SubjectActivity.this);
//            checkBox.setText(answer.getAnswer());
//            if(answer.isCorrect()) checkBox.setTag("correct");
//            else checkBox.setTag("incorrect");
//            checkBoxLayout.addView(checkBox);
//        }
//
//        check.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                boolean wasWrong = false;
//                for (int i = 0; i < checkBoxLayout.getChildCount(); i++) {
//                    CheckBox box = (CheckBox) checkBoxLayout.getChildAt(i);
//                    if(!((box.isChecked() && box.getTag().toString().equals("correct")) || (!box.isChecked() && box.getTag().toString().equals("incorrect"))))
//                    {
//                        question.setText("Wrong");
//                        if(box.getTag().toString().equals("correct")) box.setTextColor(Color.GREEN);
//                        else box.setTextColor(Color.RED);
//                        wasWrong = true;
//                    }
//                }
//                if(!wasWrong)
//                    question.setText("Correct");
////                myDialog.dismiss();
//            }
//        });
//
//        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        myDialog.show();
//    }

    private void showOpenQuizPopup(int currentPosition)
    {
        myDialog.setContentView(R.layout.popup_scrolling_open_card);
        myDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        QuizPagerAdapter quizPagerAdapter = new QuizPagerAdapter(dataBaseHelper, this, myDialog);
        ViewPager mViewPager = (ViewPager) myDialog.findViewById(R.id.cardPager);
        mViewPager.setAdapter(quizPagerAdapter);
        mViewPager.setCurrentItem(currentPosition);

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
            uriList.clear();

        }
        else if(resultCode == RESULT_OK && requestCode == REQUEST_CODE_READING_FILE_CARDS)
        {
            try
            {
                FromFileImporter.importCardsFromFile(MainActivity.getCurrentSubject().getSubjectID(), dataBaseHelper, this, data);
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
        else if(resultCode == RESULT_OK && requestCode == REQUEST_CODE_READING_FILE_QUIZ)
        {
            try
            {
                FromFileImporter.importQuizzesFromFile(MainActivity.getCurrentSubject().getSubjectID(), dataBaseHelper, this, data);
            }
            catch(Exception e)
            {
                e.printStackTrace();
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
        s=nameGetter.getText().toString();
        if(!s.equals(""))
        {
            try {
                Log.d("test","DO bazy poszedl kolor " + chosen_color);
                addNoteData(s, "Empty note", path_to_save.toString(),"");
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

    public void addNoteData(String title, String content, String photoPath,String filePath)
    {
        try
        {
            boolean insertData = dataBaseHelper.addNoteData(title, content, MainActivity.getCurrentSubject().getSubjectID(), /*chosen_color.getValue()*/ temp_note_add_color.getValue(), photoPath, filePath);

            if(color_picked)
            {
                insertData = dataBaseHelper.addNoteData(title, content, MainActivity.getCurrentSubject().getSubjectID(), chosen_color.getValue(), photoPath, filePath);
            }
            else
            {
                insertData = dataBaseHelper.addNoteData(title, content, MainActivity.getCurrentSubject().getSubjectID(), EnumColors.valueOf(5).getValue(), photoPath, filePath);
            }
            color_picked=false;
            chosen_color=EnumColors.valueOf(5);

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
            boolean insertData;
            if(color_picked)
            {
                insertData = dataBaseHelper.addCardData(word, answer, MainActivity.getCurrentSubject().getSubjectID(), attachedNotes, chosen_color.getValue());

            }
            else
            {
                insertData = dataBaseHelper.addCardData(word, answer, MainActivity.getCurrentSubject().getSubjectID(), attachedNotes, EnumColors.valueOf(5).getValue());
            }
            color_picked=false;
            chosen_color=EnumColors.valueOf(5);
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
            boolean insertData;
            if(color_picked)
            {
                insertData = dataBaseHelper.addQuizData(question, goodAnswer, badAnswers, MainActivity.getCurrentSubject().getSubjectID(), attachedNotes, chosen_color.getValue());
            }
            else
            {
                insertData = dataBaseHelper.addQuizData(question, goodAnswer, badAnswers, MainActivity.getCurrentSubject().getSubjectID(), attachedNotes, EnumColors.valueOf(5).getValue());
            }
            color_picked=false;
            chosen_color=EnumColors.valueOf(5);
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
