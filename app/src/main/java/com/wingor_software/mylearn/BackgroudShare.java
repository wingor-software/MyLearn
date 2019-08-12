package com.wingor_software.mylearn;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import java.io.File;

public class BackgroudShare extends AsyncTask
{
    private Activity activity;
    private ProgressBar progressBar;
    private DataBaseHelper dataBaseHelper;
    private File file;

    public BackgroudShare(Activity activity, DataBaseHelper dataBaseHelper) {
        this.activity = activity;
        progressBar = (ProgressBar) activity.findViewById(R.id.progressBarExportImport);
        this.dataBaseHelper = dataBaseHelper;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressBar.bringToFront();
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        progressBar.setVisibility(View.GONE);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        String subject = file.getName().substring(0, file.getName().lastIndexOf("."));
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        Uri uri = FileProvider.getUriForFile(activity, BuildConfig.APPLICATION_ID + ".provider", file);
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        activity.startActivity(Intent.createChooser(intent, "Share using"));
        Toast.makeText(activity ,"Subject exported correcly", Toast.LENGTH_LONG).show();
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        file = FileImportExport.exportAndShareSubject(activity, dataBaseHelper);
        return null;
    }
}
