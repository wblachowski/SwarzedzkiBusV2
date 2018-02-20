package com.wblachowski.swarzedzkibus.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.wblachowski.swarzedzkibus.R;

/**
 * Created by wblachowski on 2/18/2018.
 */

public class FavouritesFragment extends Fragment {

    LinearLayout noFavouritesLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_favourites, container, false);
        noFavouritesLayout = (LinearLayout) rootView.findViewById(R.id.fragment_favourites_empty_layout);

        noFavouritesLayout.setVisibility(View.VISIBLE);
        return rootView;
    }
}
