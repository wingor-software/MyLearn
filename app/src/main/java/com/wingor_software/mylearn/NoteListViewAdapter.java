package com.wingor_software.mylearn;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

public class NoteListViewAdapter extends BaseAdapter
{
    private Context context;
    private DataBaseHelper dataBaseHelper;
    private int subjectID;
    private LayoutInflater mInflater;

    public NoteListViewAdapter(Context context, DataBaseHelper dataBaseHelper)
    {
        this.context = context;
        this.dataBaseHelper = dataBaseHelper;
        this.mInflater = LayoutInflater.from(context);
        try
        {
            this.subjectID = MainActivity.getCurrentSubject().getSubjectID();
        }
        catch(Exception e)
        {
            e.printStackTrace();
            this.subjectID = 0;
        }
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        try
        {
            return dataBaseHelper.getNoteList(subjectID).size();
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        NoteListViewAdapter.ViewHolder holder;
        if(view == null){
            view = mInflater.inflate(R.layout.note_list_row, null);
            holder = new NoteListViewAdapter.ViewHolder();
            holder.constraintLayout = (ConstraintLayout) view.findViewById(R.id.note_row_layout);
            view.setTag(holder);
        }
        else{
            holder = (NoteListViewAdapter.ViewHolder) view.getTag();
        }

        TextView noteTitle = (TextView) holder.constraintLayout.getChildAt(1);

        try
        {
            Note note = dataBaseHelper.getNoteList(subjectID).get(i);
            noteTitle.setText(note.getTitle());
            setColor(EnumColors.valueOf(note.getColor()), noteTitle);
            return view;
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    private void setColor(EnumColors color, TextView textView)
    {
        switch(color)
        {
            case red:
            {
                textView.setBackgroundColor(context.getResources().getColor(R.color.colorRed));
                break;
            }
            case blue:
            {
                textView.setBackgroundColor(context.getResources().getColor(R.color.colorBlue));
                break;
            }
            case green:
            {
                textView.setBackgroundColor(context.getResources().getColor(R.color.colorGreen));
                break;
            }
            case purple:
            {
                textView.setBackgroundColor(context.getResources().getColor(R.color.colorPurple));
                break;
            }
            case yellow:
            {
                textView.setBackgroundColor(context.getResources().getColor(R.color.colorYellow));
                break;
            }
        }
    }

    private class ViewHolder{
        ConstraintLayout constraintLayout;
    }
}
