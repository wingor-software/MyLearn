package com.wingor_software.mylearn;

import android.app.Activity;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

public class BackgroundZipExport extends AsyncTask
{
    private Activity activity;
    private ProgressBar progressBar;
    private DataBaseHelper dataBaseHelper;

    public BackgroundZipExport(Activity activity, DataBaseHelper dataBaseHelper)
    {
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
        Toast.makeText(activity ,"Subject exported correcly", Toast.LENGTH_LONG).show();
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        FileImportExport.exportZipSubject(activity, dataBaseHelper);
        return null;
    }
}