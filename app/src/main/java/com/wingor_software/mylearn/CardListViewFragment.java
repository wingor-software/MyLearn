package com.wingor_software.mylearn;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

public class CardListViewFragment extends Fragment
{
    private DataBaseHelper dataBaseHelper;
    private CardListViewAdapter cardListViewAdapter;
    private Context context;
    private ListView listView;
    private Card currentCard;
    private Dialog myDialog;

    public CardListViewFragment(Context context, DataBaseHelper dataBaseHelper)
    {
        this.dataBaseHelper = dataBaseHelper;
        this.context = context;
        myDialog = new Dialog(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.subject_list_view_fragment, null);
        listView = (ListView) view.findViewById(R.id.subjectListView);
        listView.setBackgroundColor((dataBaseHelper.getDisplayMode() == DisplayMode.LIGHT) ? getResources().getColor(R.color.white) : getResources().getColor(R.color.colorDarkModeBackground));
        cardListViewAdapter = new CardListViewAdapter(context, dataBaseHelper);
        listView.setAdapter(cardListViewAdapter);
        prepareCardListViewItems();
        return view;
    }

    private void prepareCardListViewItems()
    {
        listView.setEnabled(true);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                try
                {
                    Card card = dataBaseHelper.getCardList(MainActivity.getCurrentSubject().getSubjectID()).get(position);
                    currentCard = card;
                    showCardPopup(position);
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                try
                {
                    Card card = dataBaseHelper.getCardList(MainActivity.getCurrentSubject().getSubjectID()).get(position);
                    showDeletePopup(card.getID());
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
                return false;
            }
        });
    }

    private void showCardPopup(int currentPosition)
    {
        myDialog.setContentView(R.layout.popup_scrolling_open_card);
        myDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        CardPagerAdapter mCardPageAdapter = new CardPagerAdapter(dataBaseHelper, context, myDialog);
        ViewPager mViewPager = (ViewPager) myDialog.findViewById(R.id.cardPager);
        mViewPager.setAdapter(mCardPageAdapter);
        mViewPager.setCurrentItem(currentPosition);

        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();
    }

    private void showDeletePopup(final int elementID)
    {
        myDialog.setContentView(R.layout.popup_delete_subject);
        Button no_button = myDialog.findViewById(R.id.no_button);
        Button yes_button = myDialog.findViewById(R.id.yes_button);


        no_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listView.setEnabled(true);
                myDialog.dismiss();
            }
        });

        yes_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listView.setEnabled(true);
                cardDeletingOnClick(elementID);
            }
        });

        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();
        listView.setEnabled(false);
    }

    private void cardDeletingOnClick(int cardID)
    {
        dataBaseHelper.dropCardByID(cardID);
        cardListViewAdapter.notifyDataSetChanged();

        myDialog.dismiss();
    }

    public CardListViewAdapter getCardListViewAdapter() {
        return cardListViewAdapter;
    }
}
