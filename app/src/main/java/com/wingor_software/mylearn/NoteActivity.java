package com.wingor_software.mylearn;

import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.PathUtils;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Klasa odpowiadająca za interakcje z notatkami
 */
public class NoteActivity extends AppCompatActivity{

    private ViewSwitcher viewSwitcher;
    private TextView noteContent;
    private EditText editNote;
    private boolean isTextBeingEdited = false;
    DataBaseHelper dataBaseHelper;
    private static String currentPath;
    private static final int REQUEST_CODE_READING_FILE = 50;
    private static final int REQUEST_CODE_GALLERY_PICK = 100;
    private static final int REQUEST_CODE_REFFERENCE_TO_FILE = 150;

    private SharedPreferences sharedPref;

    Intent actual_intent ;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        actual_intent = getIntent();

        dataBaseHelper = new DataBaseHelper(this);

        setContentView(R.layout.activity_note);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        viewSwitcher = (ViewSwitcher) findViewById(R.id.noteViewSwitcher);
        noteContent = (TextView) findViewById(R.id.noteContent);
        editNote = (EditText) findViewById(R.id.editNote);

        initFabButtons();

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

        final LinearLayout fotosLayout = findViewById(R.id.note_fotos_layout);

        Intent intent = getIntent();


        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

        final int takeFlags = getIntent().getFlags();


        ContentResolver resolver = NoteActivity.this.getContentResolver();

        Log.d("test","MOZLIWE POZWOLENIA CO JE MOZNA ZABRAC" + resolver.getPersistedUriPermissions().toString());

        //dodawanie zdjec po wznowieniu apki
        for (String s : SubjectActivity.getCurrentNote().getPhotoPath().split("\n"))
        {
            final String finals = s;
            ImageView imageView = new ImageView(this);
            imageView.setMaxHeight(250);
            imageView.setAdjustViewBounds(true);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setImageBitmap(decodeSampledBitmapFromResource(s, 150, 150));
            imageView.setTag(s);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    currentPath = finals;
                    Intent intent = new Intent(NoteActivity.this, PhotoActivity.class);
                    startActivity(intent);
                }
            });
            imageView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    fotosLayout.removeView(view);
                    Note currentNote = SubjectActivity.getCurrentNote();
                    String path = currentNote.getPhotoPath();
                    path = path.replace("\n" + view.getTag().toString(), "");
                    path = path.replace(view.getTag().toString() + "\n", "");
                    path = path.replace(view.getTag().toString(), "");
                    dataBaseHelper.updateNotePhotosByID(currentNote.getID(), path);
                    currentNote.setPhotoPath(path);
                    SubjectActivity.setCurrentNote(currentNote);
                    return true;
                }
            });
            fotosLayout.addView(imageView);
        }

        //dodawanie plikow po wznowieniu
        for(final String s : SubjectActivity.getCurrentNote().getFilePath().split("\n"))
        {
            if(!s.equals(""))
            {
                final LinearLayout filesLaout = findViewById(R.id.note_files_layout);

                final Button b = new Button(NoteActivity.this);

                Log.d("test","napis to: " + s);

                final Uri uri_from_button = Uri.parse(s);

                File file = new File(uri_from_button.getPath());
                b.setText(file.getName());
                b.setTag(s);
                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                        //Uri uri_from_button = (Uri) b.getTag();

                        Log.d("test",uri_from_button.toString());

                        Log.d("test",getMimeType(NoteActivity.this,uri_from_button));

                        Intent intent = new Intent();
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
                        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setAction(Intent.ACTION_VIEW);

                        getContentResolver().getPersistedUriPermissions();

                        String type = "application/pdf";

                        switch (getMimeType(NoteActivity.this,uri_from_button))
                        {
                            case "pdf":
                            {
                                type = "application/pdf";
                                break;
                            }
                            case "doc":
                            {
                                type = "application/msword";
                                break;
                            }
                            case "docx":
                            {
                                type = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
                                break;
                            }
                            case "xls":
                            {
                                type = "application/vnd.ms-excel";
                                break;
                            }
                            case "xlsx":
                            {
                                type = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
                                break;
                            }
                            case "ppt":
                            {
                                type = "application/vnd.ms-powerpoint";
                                break;
                            }
                            case "pptx":
                            {
                                type = "application/vnd.openxmlformats-officedocument.presentationml.presentation";
                                break;
                            }
                            case "txt":
                            {
                                type = "text/plain";
                                break;
                            }
                        }

                        intent.setDataAndType(uri_from_button, type);
                        try
                        {
                            startActivity(intent);
                        }
                        catch (ActivityNotFoundException e)
                        {
                            toastMessage("Looks like you don't have the right application to open this file");
                        }

                    }
                });
                b.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        filesLaout.removeView(view);
                        Note currentNote = SubjectActivity.getCurrentNote();
                        String path = currentNote.getFilePath();
                        path = path.replace("\n" + view.getTag().toString(), "");
                        path = path.replace(view.getTag().toString() + "\n", "");
                        path = path.replace(view.getTag().toString(), "");
                        dataBaseHelper.updateNoteFilesByID(currentNote.getID(), path);
                        currentNote.setFilePath(path);
                        SubjectActivity.setCurrentNote(currentNote);
                        return true;
                    }
                });

                filesLaout.addView(b);
            }

        }

        //tresc notatki
        noteContent.setText(SubjectActivity.getCurrentNote().getContent());
        sharedPref = getPreferences(Context.MODE_PRIVATE);
        int defaultValue = 18;
        int textSize = sharedPref.getInt(getString(R.string.preference_text_size), defaultValue);
        noteContent.setTextSize(textSize);
        editNote.setTextSize(textSize);

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

    @Override
    protected void onStop() {           //bo sie od nowa rysuja przy powrocie z PhotoActivity
        super.onStop();
        LinearLayout fotosLayout = findViewById(R.id.note_fotos_layout);
        fotosLayout.removeAllViewsInLayout();
        LinearLayout filesLayout = findViewById(R.id.note_files_layout);
        filesLayout.removeAllViewsInLayout();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == REQUEST_CODE_READING_FILE)     //dla wczytywania  notatek z plikow txt
        {
            StringBuilder textfromFile = new StringBuilder();
            if(data != null)
            {
                Note currentNote = SubjectActivity.getCurrentNote();
                String str;
                try
                {
                    InputStream inputStream = getContentResolver().openInputStream(data.getData());
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    while((str = bufferedReader.readLine()) != null)
                    {
                        textfromFile.append(str + "\n");
                    }
                    inputStream.close();
                    noteContent.setText(textfromFile.toString());
                    currentNote.setContent(textfromFile.toString());
                    dataBaseHelper.updateNoteContent(currentNote.getID(), textfromFile.toString());
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
        else if(resultCode == RESULT_OK && requestCode == REQUEST_CODE_GALLERY_PICK) {      //dla dodawania zdjec
            ArrayList<Uri> uriList = new ArrayList<>();
            StringBuilder path_to_save = new StringBuilder();
            ClipData cd = data.getClipData();
            if(cd == null) {
                Uri uri = data.getData();
                uriList.add(uri);
            }
            else{
                for (int i = 0; i < cd.getItemCount(); i++) {
                    ClipData.Item item = cd.getItemAt(i);
                    Uri uri = item.getUri();
                    uriList.add(uri);
                }
            }

            Note currentNote = SubjectActivity.getCurrentNote();
            String path = currentNote.getPhotoPath();
            for (int i = 0; i < uriList.size(); i++) {
                try
                {
                    String currentPhotoPath = getRealPathFromURI(NoteActivity.this, uriList.get(i));
                    if(!path.contains(currentPhotoPath))
                    {
                        if (!path_to_save.toString().equals("")) {
                            path_to_save.append("\n");
                        }
                        path_to_save.append(getRealPathFromURI(NoteActivity.this, uriList.get(i)));
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if(path.equals("")) path = path_to_save.toString();
            else path += "\n" + path_to_save.toString();
            currentNote.setPhotoPath(path);
            dataBaseHelper.updateNotePhotosByID(currentNote.getID(), currentNote.getPhotoPath());
            SubjectActivity.setCurrentNote(currentNote);
        }
        else if(resultCode == RESULT_OK && requestCode == REQUEST_CODE_REFFERENCE_TO_FILE) // dodawanie odnoscnikow to pliku
        {
            ArrayList<Uri> uriList = new ArrayList<>();
            StringBuilder path_to_save = new StringBuilder();
            getContentResolver().getPersistedUriPermissions();
            ClipData cd = data.getClipData();
            if(cd == null) {
                Uri uri = data.getData();
                getContentResolver().getPersistedUriPermissions();

                getContentResolver().getPersistedUriPermissions();
                uriList.add(uri);
            }
            else{
                for (int i = 0; i < cd.getItemCount(); i++) {
                    ClipData.Item item = cd.getItemAt(i);
                    Uri uri = item.getUri();
                    getContentResolver().getPersistedUriPermissions();

                    getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION & Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    uriList.add(uri);
                }
            }
            Note currentNote = SubjectActivity.getCurrentNote();
            String path = currentNote.getFilePath();
            Log.d("test","aktualna sciezka plikow to:" + path);
            for (int i = 0; i < uriList.size(); i++) {
                try
                {
                    if (!path_to_save.toString().equals("")) {
                        path_to_save.append("\n");
                    }

                    path_to_save.append(uriList.get(i).toString());
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if(path.equals("")) path = path_to_save.toString();
            else path += "\n" + path_to_save.toString();
            currentNote.setFilePath(path);
            dataBaseHelper.updateNoteFilesByID(currentNote.getID(), currentNote.getFilePath());
            SubjectActivity.setCurrentNote(currentNote);
            Log.d("test","po tym wszystkim sciezka do plikow to:" + currentNote.getFilePath() );
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

    public static String getCurrentPath()
    {
        return currentPath;
    }

    private void initFabButtons()
    {
        FloatingActionButton fab_edit = (FloatingActionButton) findViewById(R.id.fab_edit);
        fab_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isTextBeingEdited) {
                    viewSwitcher.showPrevious();
                    isTextBeingEdited = false;
                    String newText = editNote.getText().toString();
                    noteContent.setText(newText);
                    Note newNote = SubjectActivity.getCurrentNote();
                    newNote.setContent(newText);
                    SubjectActivity.setCurrentNote(newNote);
                    SubjectActivity.updateNoteContent(SubjectActivity.getCurrentNote().getID(), editNote.getText().toString());
                } else {
                    viewSwitcher.showNext();
                    isTextBeingEdited = true;
                    editNote.setText(noteContent.getText().toString());
                }
            }
        });

        com.getbase.floatingactionbutton.FloatingActionButton fabAddPhoto = findViewById(R.id.fab_add_photo);
        fabAddPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gallery = new Intent();
                gallery.setType("image/*");
                gallery.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
                gallery.setAction(Intent.ACTION_PICK);
                startActivityForResult(Intent.createChooser(gallery, "Select Picture"),REQUEST_CODE_GALLERY_PICK);
            }
        });

        com.getbase.floatingactionbutton.FloatingActionButton fabAddReferenceToFile = findViewById(R.id.fab_add_reference_to_file);
        fabAddReferenceToFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String[] extraMimeTypes =
                        {"application/msword","application/vnd.openxmlformats-officedocument.wordprocessingml.document", // .doc & .docx
                                "application/vnd.ms-powerpoint","application/vnd.openxmlformats-officedocument.presentationml.presentation", // .ppt & .pptx
                                "application/vnd.ms-excel","application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", // .xls & .xlsx
                                "text/plain",
                                "application/pdf",
                                "application/zip"};

                Intent intent = new Intent();
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                intent.setType("*/*");
                intent.putExtra(Intent.EXTRA_MIME_TYPES, extraMimeTypes);

                startActivityForResult(intent,REQUEST_CODE_REFFERENCE_TO_FILE);



            }
        });

        com.getbase.floatingactionbutton.FloatingActionButton fabAddFromFile = findViewById(R.id.fab_add_from_file);
        fabAddFromFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("text/*");
                startActivityForResult(intent, REQUEST_CODE_READING_FILE);
            }
        });

        com.getbase.floatingactionbutton.FloatingActionButton fabZoomIn = findViewById(R.id.fab_zoom_in);
        fabZoomIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharedPref = getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                int defaultValue = 18;
                int textSize = sharedPref.getInt(getString(R.string.preference_text_size), defaultValue);
                if (textSize <= 42)
                {
                    noteContent.setTextSize(++textSize);
                    editNote.setTextSize(textSize);
                    editor.putInt(getString(R.string.preference_text_size), textSize);
                    editor.apply();
                }
            }
        });

        com.getbase.floatingactionbutton.FloatingActionButton fabZoomOut = findViewById(R.id.fab_zoom_out);
        fabZoomOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharedPref = getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                int defaultValue = 18;
                int textSize = sharedPref.getInt(getString(R.string.preference_text_size), defaultValue);
                if(textSize >= 12)
                {
                    noteContent.setTextSize(--textSize);
                    editNote.setTextSize(textSize);
                    editor.putInt(getString(R.string.preference_text_size), textSize);
                    editor.apply();
                }
            }
        });
    }

    public String getFileName(Uri uri) {


        String result = null;

        if (uri.getScheme().equals("content")) {

            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    public static String getMimeType(Context context, Uri uri) {
        String extension;

        //Check uri format to avoid null
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            //If scheme is a content
            final MimeTypeMap mime = MimeTypeMap.getSingleton();
            extension = mime.getExtensionFromMimeType(context.getContentResolver().getType(uri));
        } else {
            //If scheme is a File
            //This will replace white spaces with %20 and also other special characters. This will avoid returning null values on file name with spaces and special characters.
            extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(new File(uri.getPath())).toString());

        }

        return extension;
    }

}
