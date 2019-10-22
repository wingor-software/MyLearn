package com.wingor_software.mylearn;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

public class MainListViewAdapter extends BaseAdapter
{
    private Context context;
    private DataBaseHelper dataBaseHelper;
    private LayoutInflater mInflater;

    public MainListViewAdapter(Context context, DataBaseHelper dataBaseHelper)
    {
        this.context = context;
        this.dataBaseHelper = dataBaseHelper;
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        try
        {
            return dataBaseHelper.getSubjectsList().size();

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


    private class ViewHolder{
        ConstraintLayout constraintLayout;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if(view == null){
            view = mInflater.inflate(R.layout.main_list_row, null);
            holder = new MainListViewAdapter.ViewHolder();
            holder.constraintLayout = (ConstraintLayout) view.findViewById(R.id.main_row_layout);
            view.setTag(holder);
        }
        else{
            holder = (ViewHolder) view.getTag();
        }

        TextView subjectName = (TextView) holder.constraintLayout.getChildAt(1);

        try
        {
            Subject subject = dataBaseHelper.getSubjectsList().get(i);
            subjectName.setText(subject.getSubjectName());
            setColor(EnumColors.valueOf(subject.getColor()), subjectName);
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

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }
}
