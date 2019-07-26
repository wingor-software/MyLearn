package com.wingor_software.mylearn;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.UriPermission;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.io.FileDescriptor;
import java.io.IOException;

/**
 * Klasa odpowiadająca za interakcje z notatkami
 */
public class NoteActivity extends AppCompatActivity {

    private ViewSwitcher viewSwitcher;
    private TextView noteContent;
    private EditText editNote;
    private boolean isTextBeingEdited = false;
    DataBaseHelper dataBaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        dataBaseHelper = new DataBaseHelper(this);

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
        //tytul

        this.setTitle(SubjectActivity.getCurrentNote().getTitle());
        //zdjecia

        LinearLayout fotosLayout = findViewById(R.id.note_fotos_layout);

        Intent intent = getIntent();


        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        //intent.addFlags(Intent.FLAG_GRANT_PREFIX_URI_PERMISSION);




        final int takeFlags = getIntent().getFlags();


        ContentResolver resolver = NoteActivity.this.getContentResolver();

        Log.d("test","MOZLIWE POZWOLENIA CO JE MOZNA ZABRAC" + resolver.getPersistedUriPermissions().toString());

        int i = 1;
        for (String s : SubjectActivity.getCurrentNote().getFilePath().split("\n"))
        {
            ImageView imageView = new ImageView(this);
            imageView.setMaxHeight(250);
            imageView.setAdjustViewBounds(true);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setImageBitmap(decodeSampledBitmapFromResource(s, 150, 150));
            fotosLayout.addView(imageView);
        }



        //tresc notatki
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
    private void toastMessage(String message)
    {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void addPhoto(String photoPath)
    {
        Note note = SubjectActivity.getCurrentNote();
        note.addPhoto(photoPath);
        dataBaseHelper.updateNotePhotosByID(note.getID(), note.getFilePath());
    }

    private void deletePhoto(String photoPath)
    {
        Note note = SubjectActivity.getCurrentNote();
        note.deletePhoto(photoPath);
        dataBaseHelper.updateNotePhotosByID(note.getID(), note.getFilePath());
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
