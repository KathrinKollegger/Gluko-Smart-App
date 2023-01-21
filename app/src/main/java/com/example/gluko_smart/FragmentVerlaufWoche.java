package com.example.gluko_smart;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class FragmentVerlaufWoche extends Fragment {

    View view;


    public FragmentVerlaufWoche() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_verlauf_woche, container, false);
        return view;
    }
}