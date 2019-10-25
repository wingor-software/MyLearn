package com.wingor_software.mylearn;

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
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
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
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

/**
 * Klasa słuzaca do interakcji z przedmiotami
 */
public class SubjectActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ViewPager subjectViewPager;

    Dialog myDialog;

    static DataBaseHelper dataBaseHelper;

    EnumColors chosen_color = EnumColors.valueOf(5);
    EnumColors temp_note_add_color = EnumColors.valueOf(5);

    private static boolean color_picked = false;

    private SharedPreferences sharedPref;

    private static Note currentNote;

    private ArrayList<Uri> uriList;
    private Intent gallery;

    StringBuilder path_to_save = new StringBuilder();

    private static ExamType examType;
    private static int questionsCountToExam = 0;

    private static final int REQUEST_CODE_READING_FILE_CARDS = 300;
    private static final int REQUEST_CODE_READING_FILE_QUIZ = 400;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject);
        Toolbar toolbar = findViewById(R.id.toolbar_subject);
        setSupportActionBar(toolbar);

        dataBaseHelper = new DataBaseHelper(this);
        int light = getResources().getColor(R.color.white);
        int dark = getResources().getColor(R.color.colorDarkModeBackground);

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

        myDialog = new Dialog(this);

        subjectViewPager = findViewById(R.id.subjectViewPager);
        subjectViewPager.setAdapter(new SubjectMenuPagerAdapter(getSupportFragmentManager(), 4, this, dataBaseHelper));

        TabLayout topTabLayout = findViewById(R.id.topTabLayout);
        topTabLayout.setupWithViewPager(subjectViewPager);
        topTabLayout.setBackgroundColor(getResources().getColor(R.color.colorLightPrimary));
        topTabLayout.getTabAt(0).setIcon(R.drawable.baseline_call_to_action_white_48);
        topTabLayout.getTabAt(1).setIcon(R.drawable.baseline_speaker_notes_white_48);
        topTabLayout.getTabAt(2).setIcon(R.drawable.baseline_note_white_48);
        topTabLayout.getTabAt(3).setIcon(R.drawable.baseline_school_white_48);

        uriList = new ArrayList<>();

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
        int defaultValue = 3;
        int submenuValue = sharedPref.getInt(getString(R.string.preference), defaultValue);
        subjectViewPager.setCurrentItem(submenuValue);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(getString(R.string.preference), subjectViewPager.getCurrentItem());
        editor.apply();
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
                    switch(subjectViewPager.getCurrentItem())
                    {
                        case 0:
                        {
                            drawAllCardButtonsContaining(textInputEditText.getText().toString());
                            myDialog.dismiss();

                            break;
                        }
                        case 1:
                        {
                            drawAllQuizButtonsContaining(textInputEditText.getText().toString());
                            myDialog.dismiss();

                            break;
                        }
                        case 2:
                        {
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

        if (id == R.id.nav_darkMode_ON) {
            dataBaseHelper.setDisplayMode(DisplayMode.DARK);
            restartApp();
        } else if (id == R.id.nav_darkMode_OFF) {
            dataBaseHelper.setDisplayMode(DisplayMode.LIGHT);
            restartApp();
        } else if (id == R.id.nav_statistics) {
            toastMessage("TU DODAC STATYSTYKI");

        } else if (id == R.id.nav_tutorial) {
            toastMessage("TU DODAC TUTORIAL");

        } else if (id == R.id.nav_destroy_database) {
            dataBaseHelper.dropSubjectTable();
            restartApp();
            toastMessage("DATABASE DESTROYED!");
        }
        else if (id == R.id.nav_license)
        {
            Intent intent = new Intent(SubjectActivity.this,Info.class);
            startActivity(intent);
        }
        else if(id == R.id.nav_about_us)
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

    private void toastMessage(String message)
    {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    // TODO: 20.10.2019 do naprawy
    private void drawAllNoteButtonsContaining(String phrase)
    {
        try
        {
            List<Note> notes = dataBaseHelper.getNoteList(MainActivity.getCurrentSubject().getSubjectID());
            Iterator it = notes.iterator();
            while(it.hasNext())
            {
                Note note = (Note)it.next();
//                if(note.getTitle().toLowerCase().contains(phrase.toLowerCase()))
//                    drawNoteButton(note);
            }
        }
        catch (EmptyDataBaseException em)
        {
            TextView warning = new TextView(SubjectActivity.this);
            warning.setText("Can't find such a note");
            warning.setTag("note_warning_tag");
//            subjectLayout.addView(warning);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    // TODO: 20.10.2019 do naprawy
    private void drawAllQuizButtonsContaining(String phrase)
    {
        try
        {
            List<Quiz> quizzes = dataBaseHelper.getQuizList(MainActivity.getCurrentSubject().getSubjectID());
            Iterator it = quizzes.iterator();
            while(it.hasNext())
            {
                Quiz quiz = (Quiz) it.next();
//                if(quiz.getQuestion().toLowerCase().contains(phrase.toLowerCase()))
//                    drawQuizButton(quiz);
            }
        }
        catch (EmptyDataBaseException em)
        {
            TextView warning = new TextView(SubjectActivity.this);
            warning.setText(R.string.quiz_warning);
            warning.setTag("quiz_warning_tag");
//            subjectLayout.addView(warning);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    // TODO: 20.10.2019 do naprawy
    private void drawAllCardButtonsContaining(String phrase)
    {
        try
        {
            List<Card> cards = dataBaseHelper.getCardList(MainActivity.getCurrentSubject().getSubjectID());
            Iterator it = cards.iterator();
            while(it.hasNext())
            {
                Card card = (Card)it.next();
//                if(card.getWord().toLowerCase().contains(phrase.toLowerCase()))
//                    drawCardButton(card);
            }
        }
        catch (EmptyDataBaseException em)
        {
            TextView warning = new TextView(SubjectActivity.this);
            warning.setText("Can't find such a card");
            warning.setTag("card_warning_tag");
//            subjectLayout.addView(warning);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public void addingContent(View view)
    {
        switch(subjectViewPager.getCurrentItem())
        {
            case 0:
            {
                showPopupCardAdding(view);
                break;
            }
            case 1:
            {
                showPopupQuizAdding_1(view);
                break;
            }
            case 2:
            {
                showPopupNoteAdding(view);
                break;
            }
            case 3:
            {
                break;
            }
        }
    }

    public void showPopupNoteAdding(View view)
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
                subjectViewPager.getAdapter().notifyDataSetChanged();
                Log.d("uritest", getStringFromUriList());
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    private void cardAddingOnClick(TextInputEditText wordGetter, TextInputEditText answerGetter)
    {
        String word;
        String answer;
        try
        {
            word = wordGetter.getText().toString();
            answer = answerGetter.getText().toString();

            addCardData(word, answer, "");
            subjectViewPager.getAdapter().notifyDataSetChanged();
            Log.d("cardAdding", "dodano do bazy");
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    private void quizAddingOnClick(String question, String goodAnswer,String badAnswer)
    {
        try
        {
            addQuizData(question, goodAnswer, badAnswer, "");
            subjectViewPager.getAdapter().notifyDataSetChanged();
            Log.d("quizAdding", "dodano do bazy");
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

    public static void setQuestionsCountToExam(int questionsCountToExam) {
        SubjectActivity.questionsCountToExam = questionsCountToExam;
    }

    public static void setExamType(ExamType examType) {
        SubjectActivity.examType = examType;
    }

    public static void updateNoteContent(int noteID, String newContent)
    {
        dataBaseHelper.updateNoteContent(noteID, newContent);
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
