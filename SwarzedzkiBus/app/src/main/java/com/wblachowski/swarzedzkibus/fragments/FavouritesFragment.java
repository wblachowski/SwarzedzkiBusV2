package com.wblachowski.swarzedzkibus.fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wblachowski.swarzedzkibus.R;

/**
 * Created by wblachowski on 2/18/2018.
 */

public class FavouritesFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_favourites, container, false);
        TextView textView = (TextView) rootView.findViewById(R.id.section_label);
        textView.setText("ULUBIONE");
        return rootView;
    }
}
