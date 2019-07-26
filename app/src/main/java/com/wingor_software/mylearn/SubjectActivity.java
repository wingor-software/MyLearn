package com.wingor_software.mylearn;

import android.Manifest;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ColorSpace;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.PathUtils;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

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

    private enum BarAction {CARDS, QUIZ, NOTES}

    ;
    private BarAction whichAction;

    private static Note currentNote;
    private static Card currentCard;

    private Uri imageUri;
    private ArrayList<Uri> uriList;
    private Intent gallery;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_cards:
                    whichAction = BarAction.CARDS;
                    clearContent();
                    drawAllCardButtons();
                    return true;
                case R.id.action_quiz:
                    whichAction = BarAction.QUIZ;
                    clearContent();
                    return true;
                case R.id.action_notes:
                    whichAction = BarAction.NOTES;
                    clearContent();
                    drawAllNoteButtons();
                    return true;
            }
            return false;
        }
    };
    StringBuilder path_to_save = new StringBuilder();

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
                gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                gallery.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);

                startActivityForResult(gallery,100);
                setResult(RESULT_OK,gallery);


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
