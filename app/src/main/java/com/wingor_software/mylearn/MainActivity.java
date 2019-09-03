package com.wingor_software.mylearn;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewManager;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Random;

/**
 * Główna klasa aplikacji
 */

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    //słuzy jako odnosnik do aktualnej sceny
    Dialog myDialog;

    //odnosnik do layoutu przechowujacego przedmioty
    LinearLayout subjectsLayout;

    //odnoscnik do pola tekstowego ostrzezenia
    TextView warning;

    //pomocnik do bazy danych
    DataBaseHelper dataBaseHelper;

    //wartosc wybranego koloru z popupu
    EnumColors chosen_color = EnumColors.valueOf(5);

    //aktualny przedmiot
    private static Subject currentSubject;

    private final static int REQUEST_IMPORT_SUBJECT = 200;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        checkPermission();

        myDialog = new Dialog(this);
        subjectsLayout = findViewById(R.id.subjectsLayout);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        dataBaseHelper = new DataBaseHelper(this);

        final CalendarView calendarView = navigationView.getHeaderView(0).findViewById(R.id.calendarView);
        final TextView titleOfDay = navigationView.getHeaderView(0).findViewById(R.id.titleOfDay);
        final TextView contentOfDay = navigationView.getHeaderView(0).findViewById(R.id.contentOfDay);
        final Button addnewCalendarEventButton = navigationView.getHeaderView(0).findViewById(R.id.addCalendarEventButton);



        titleOfDay.setText("To do on : " + new SimpleDateFormat("yyyy-M-d", Locale.getDefault()).format(new Date()));


        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView calendarView, final int i, final int i1, final int i2) {

                titleOfDay.setText("To do on : " + i + "-" +i1 + "-" +i2);

                try {
                    contentOfDay.setText("Nothing to do :)");
                    for (CalendarEvent c:dataBaseHelper.getCalendarEventList()) {
                        if(c.getDate().equals(i+"-"+i1+"-"+i2))
                        {
                            contentOfDay.setText(c.getContent());
                            break;
                        }

                    }
                }
                catch (EmptyDataBaseException e)
                {

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

    }

    @Override
    protected void onResume() {
        super.onResume();
        subjectsLayout.removeAllViewsInLayout();
        drawAllSubjectButtons();
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
        getMenuInflater().inflate(R.menu.main, menu);
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
            case R.id.action_settings:
            {
                return true;
            }
            case R.id.action_import_subject:
            {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("text/*");
                startActivityForResult(intent, REQUEST_IMPORT_SUBJECT);
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == REQUEST_IMPORT_SUBJECT)
        {
            if(data != null)
            {
                try
                {
                    InputStream is = getContentResolver().openInputStream(data.getData());
                    ObjectInputStream ois = new ObjectInputStream(is);
                    OutputSubject outputSubject = (OutputSubject) ois.readObject();
                    ois.close();
                    is.close();
                    FileImportExport.addImportedSubject(outputSubject, dataBaseHelper);
                    Toast.makeText(this, "Subject imported correctly: " + outputSubject.getSubjectName(), Toast.LENGTH_LONG).show();
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action
            addData("Test" + new Random().nextInt(), 1);
        } else if (id == R.id.nav_gallery) {
            Intent intent = new Intent(MainActivity.this, ListDataActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_slideshow) {
            dataBaseHelper.dropSubjectTable();
            subjectsLayout.removeAllViews();
        } else if (id == R.id.nav_tools) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        else if (id == R.id.nav_info)
        {
            Intent intent = new Intent(MainActivity.this,Info.class);
            startActivity(intent);
        }
        else if(id == R.id.nav_contact)
        {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Metoda odpowiadająca za wyskakujace okienko przy tworzeniu przedmiotu
     * @param v
     */
    public void showPopup(final View v)
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
                String s;
                if(subjectsLayout==null)
                {
                    Log.d("tesciki","subjectslayout jest nullem");
                }
                Subject subject;
                s=nameGetter.getText().toString();
                if(!s.equals(""))
                {
                    try {
                        //pobiera wpisana nazwe przedmiotu z pola tekstowego
                        //dodaje wpis do tabeli
                        addData(s, chosen_color.getValue());

                        //pobiera ostatnio dodany i go rysuje
                        subject = dataBaseHelper.getLatelyAddedSubject();
                        Log.d("tesciki","dodano do bazy");
                        drawSubjectButton(subject);
                        Log.d("tesciki","powinno tutaj dodac przycisk");
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                    myDialog.dismiss();
                }
            }
        });

        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();
    }

    public void addData(String newEntry, int color)
    {
        boolean insertData = dataBaseHelper.addSubjectData(newEntry, color, 0, 0);

        if(insertData)
           toastMessage("Dodano poprawnie - " + newEntry);
        else
            toastMessage("Cos sie wysralo");
    }

    private void toastMessage(String message)
    {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    /**
     * Metoda dodająca graficznie przedmiot do sceny
     * @param subject przedmiot do narsowania
     */
    private void drawSubjectButton(final Subject subject)
    {
        final Button b =  new Button(MainActivity.this);

        b.setText(subject.getSubjectName());
        b.setTag("subject_" + subject.getSubjectID());
        b.setMinimumWidth(200);
        b.setMinimumHeight(200);
        //ustalam plik w zaleznosci od wybranego wczesniej koloru

        b.setBackground(getResources().getDrawable(R.drawable.subject_drawable_default));
        chosen_color = EnumColors.valueOf(subject.getColor());
        switch (chosen_color)
        {
            case red:
            {
                b.setBackground(getResources().getDrawable(R.drawable.subject_drawable_red));
                break;
            }
            case yellow:
            {
                b.setBackground(getResources().getDrawable(R.drawable.subject_drawable_yellow));
                break;
            }
            case green:
            {
                b.setBackground(getResources().getDrawable(R.drawable.subject_drawable_green));
                break;
            }
            case blue:
            {
                b.setBackground(getResources().getDrawable(R.drawable.subject_drawable_blue));
                break;
            }
            case purple:
            {
                b.setBackground(getResources().getDrawable(R.drawable.subject_drawable_purple));
                break;
            }
        }

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
                setCurrentSubject(subject);
                Intent intent = new Intent(MainActivity.this, SubjectActivity.class);
                startActivity(intent);
            }
        });

        if(warning != null && warning.getParent() != null)
        {
            ((ViewManager)warning.getParent()).removeView(warning);
        }
        subjectsLayout.addView(b);
    }

    /**
     * Metoda rysująca wszystkie przedmioty (uzywanie np. przy restarcie aplikacji)
     */
    private void drawAllSubjectButtons()
    {
        try
        {
            List<Subject> subjects = dataBaseHelper.getSubjectsList();
            Iterator it = subjects.iterator();
            while(it.hasNext())
            {
                drawSubjectButton((Subject) it.next());
            }
        }
        catch (EmptyDataBaseException em)
        {
            warning = new TextView(MainActivity.this);
            warning.setText(R.string.subject_warning);
            warning.setTag("subject_warning_tag");
            subjectsLayout.addView(warning);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Metoda pokazujaca okienko przy usuwaniu przemiotu
     * @param subject_view
     */

    private void showDeletePopup(final View subject_view)
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
                String s = subject_view.getTag().toString();
                String r_s = s.substring(8);
                int subjectID = Integer.parseInt(r_s);
                dataBaseHelper.dropNotesBySubjectID(subjectID);
                dataBaseHelper.dropCardsBySubjectID(subjectID);
                dataBaseHelper.dropQuizBySubjectID(subjectID);
                dataBaseHelper.dropSubject(subjectID);
                toastMessage("Poprawnie usunieto przedmiot" + r_s);

                ((ViewManager)subject_view.getParent()).removeView(subject_view);

                myDialog.dismiss();
            }
        });

        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();

    }

    /**
     * Metoda ustawiająca kolor w zaleznosci od wybranego przycisku z kolorem i zmieniajaca opacity pozostałych buttonow
     * @param view
     */
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

    public static Subject getCurrentSubject() throws Exception
    {
        try
        {
            return currentSubject;
        }
        catch(Exception e)
        {
            e.printStackTrace();
            throw new Exception();
        }
    }

    private static void setCurrentSubject(Subject subject)
    {
        currentSubject = subject;
    }

    //pozwolenia
    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
        }
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
        }
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_MEDIA_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_MEDIA_LOCATION}, 100);
        }
    }



}

