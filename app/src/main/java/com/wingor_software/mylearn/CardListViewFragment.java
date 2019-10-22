package com.wingor_software.mylearn;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class CardListViewFragment extends Fragment
{
    private DataBaseHelper dataBaseHelper;
    private CardListViewAdapter cardListViewAdapter;
    private Context context;
    private ListView listView;

    public CardListViewFragment(Context context, DataBaseHelper dataBaseHelper)
    {
        this.dataBaseHelper = dataBaseHelper;
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.subject_list_view_fragment, null);
        listView = (ListView) view.findViewById(R.id.subjectListView);
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
//                    currentCard = card;
//                    showCardPopup(position);
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
//                    showDeletePopup(card.getID());
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
                return false;
            }
        });
    }
}
