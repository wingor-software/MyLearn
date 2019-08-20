package com.wingor_software.mylearn;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class MyFragment extends Fragment
{
    private static final String ARG_PAGE = "arg_page";
    private LinearLayout examableLayout;

    public MyFragment()
    {

    }

    public static MyFragment newInstance(int pageNumber)
    {
        MyFragment myFragment = new MyFragment();
        Bundle arguments = new Bundle();
        arguments.putInt(ARG_PAGE, pageNumber);
        myFragment.setArguments(arguments);
        return myFragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        int pageNumber = arguments.getInt(ARG_PAGE);
        Examable[] examables = ExamActivity.getExamables();
        try
        {
            LinearLayout linearLayout = examables[pageNumber].getLayoutToDisplay(getActivity());
            linearLayout.setGravity(Gravity.CENTER);
            examableLayout = linearLayout;
            return linearLayout;
        }
        catch(Exception e)
        {
            TextView myText = new TextView(getActivity());
            myText.setText("Example Text " + pageNumber);
            myText.setGravity(Gravity.CENTER);
            examableLayout = null;
            return myText;
        }
    }

    public LinearLayout getExamableLayout()
    {
        return examableLayout;
    }
}
