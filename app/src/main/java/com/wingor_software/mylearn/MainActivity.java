package com.wingor_software.mylearn;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.util.Log;
import android.view.View;

import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.w3c.dom.Text;

import android.widget.Toast;

import java.util.Random;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    //tu cos zmieniam
    Dialog myDialog;
    LinearLayout subjectsLayout;

    SubjectDataBaseHelper subjectDataBaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //o i tutaj
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

        subjectDataBaseHelper = new SubjectDataBaseHelper(this);
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
            addData("Test" + new Random().nextInt());
        } else if (id == R.id.nav_gallery) {
            Intent intent = new Intent(MainActivity.this, ListDataActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_tools) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void showPopup(View v)
    {
        Log.d("test", "jestem w funkcji showPopup");
        TextView txtclose;
        Button addButton;
        myDialog.setContentView(R.layout.popup_main);
        txtclose = myDialog.findViewById(R.id.txtclose);
        addButton = myDialog.findViewById(R.id.addButton);
        txtclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myDialog.dismiss();
            }
        });
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = MainActivity.this;
                TextInputEditText nameGetter = myDialog.findViewById(R.id.nameGetter);
                String s;
                if(subjectsLayout==null)
                {
                    Log.d("test","subjectslayout jest nullem");
                }
                s=nameGetter.getText().toString();

                Button b = new Button(context);
                b.setText(s);
                b.setMinimumWidth(200);
                b.setMinimumHeight(200);

                subjectsLayout.addView(b);

                myDialog.dismiss();
            }
        });
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();

    }




    public void addData(String newEntry)
    {
        boolean insertData = subjectDataBaseHelper.addData(newEntry);

        if(insertData)
           toastMessage("Dodano poprawnie - " + newEntry);
        else
            toastMessage("Cos sie wysralo");
    }

    private void toastMessage(String message)
    {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

}
