package com.wingor_software.mylearn;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

public class CardListViewAdapter extends BaseAdapter {

    private Context context;
    private DataBaseHelper dataBaseHelper;
    private int subjectID;
    private LayoutInflater mInflater;

    public CardListViewAdapter(Context context, DataBaseHelper dataBaseHelper)
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
    public int getCount() {
        try
        {
            return dataBaseHelper.getCardList(subjectID).size();

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
        ViewHolder holder;
        if(view == null){
            view = mInflater.inflate(R.layout.card_list_row, null);
            holder = new ViewHolder();
            holder.constraintLayout = (ConstraintLayout) view.findViewById(R.id.card_row_layout);
            view.setTag(holder);
        }
        else{
            holder = (ViewHolder) view.getTag();
        }
        TextView word = (TextView) holder.constraintLayout.getChildAt(1);
//        TextView answer = (TextView) holder.linearLayout.getChildAt(1);

        try
        {
            Card card = dataBaseHelper.getCardList(subjectID).get(i);
            word.setText(card.getWord());
            setColor(EnumColors.valueOf(card.getColor()), word);
//            answer.setText(card.getAnswer());
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
