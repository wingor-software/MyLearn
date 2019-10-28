package com.wingor_software.mylearn;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.ArrayList;
import java.util.List;

public class NoteListViewAdapter extends BaseAdapter implements Filterable
{
    private Context context;
    private DataBaseHelper dataBaseHelper;
    private int subjectID;
    private LayoutInflater mInflater;

    private ValueFilter valueFilter;
    private List<Note> filterList;
    private List<Note> mData;

    public NoteListViewAdapter(Context context, DataBaseHelper dataBaseHelper)
    {
        this.context = context;
        this.dataBaseHelper = dataBaseHelper;
        this.mInflater = LayoutInflater.from(context);
        try
        {
            this.subjectID = MainActivity.getCurrentSubject().getSubjectID();
            this.mData = dataBaseHelper.getNoteList(subjectID);
            this.filterList = dataBaseHelper.getNoteList(subjectID);
        }
        catch(Exception e)
        {
            e.printStackTrace();
            this.subjectID = 0;
            this.mData = new ArrayList<>();
            this.filterList = new ArrayList<>();
        }
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int i) {
        return mData.get(i);
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
            Note note = mData.get(i);
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

    @Override
    public Filter getFilter() {
        if(valueFilter == null)
            valueFilter = new ValueFilter();
        return valueFilter;
    }

    private class ValueFilter extends Filter{
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            FilterResults results = new FilterResults();
            if(charSequence != null && charSequence.length() > 0){
                List<Note> resultFilterList = new ArrayList<>();
                for (int i = 0; i < filterList.size(); i++) {
                    if(filterList.get(i).getTitle().toUpperCase().contains(charSequence.toString().toUpperCase())){
                        resultFilterList.add(filterList.get(i));
                    }
                }
                results.count = resultFilterList.size();
                results.values = resultFilterList;
            }else
            {
                results.count = filterList.size();
                results.values = filterList;
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            mData = (List<Note>) filterResults.values;
            notifyDataSetChanged();
        }
    }
}
