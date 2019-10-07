package com.wingor_software.mylearn;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class CardPagerAdapter extends PagerAdapter
{
    private Context context;
    private DataBaseHelper dataBaseHelper;
    private Dialog myDialog;

    public CardPagerAdapter(DataBaseHelper dataBaseHelper, Context context, Dialog myDialog) {
        this.dataBaseHelper = dataBaseHelper;
        this.context = context;
        this.myDialog = myDialog;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup collection, int position) {
        try
        {
            TextView word = new TextView(context);
            final Card card = dataBaseHelper.getCardList(MainActivity.getCurrentSubject().getSubjectID()).get(position);
            word.setText(card.getWord());
            word.setGravity(Gravity.CENTER);
            word.setTextColor(context.getResources().getColor(R.color.colorDarkPurple));
            word.setTextSize(20);
            word.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    myDialog.dismiss();
                    final Dialog answerDialog = new Dialog(context);
                    answerDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                    answerDialog.setContentView(R.layout.popup_scrolling_open_card_answer);

                    TextView answerText = answerDialog.findViewById(R.id.openCardAnswerText);
                    answerText.setText(card.getAnswer());

                    Button button = answerDialog.findViewById(R.id.openCardAnswerButton);
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            answerDialog.dismiss();
                            myDialog.show();
                        }
                    });

                    answerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    answerDialog.show();
                    return false;
                }
            });
            ((ViewPager) collection).addView(word);
            return word;
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        ((ViewPager) collection).removeView((View) view);
    }

    @Override
    public int getCount() {
        try
        {
            return dataBaseHelper.getCardList(MainActivity.getCurrentSubject().getSubjectID()).size();
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return 0;
        }
    }
}
