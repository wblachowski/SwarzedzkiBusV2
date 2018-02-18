package com.wblachowski.swarzedzkibus.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codewaves.stickyheadergrid.StickyHeaderGridLayoutManager;
import com.wblachowski.swarzedzkibus.R;
import com.wblachowski.swarzedzkibus.adapters.AllAdapter;
import com.wblachowski.swarzedzkibus.data.Bus;

import java.util.ArrayList;

/**
 * Created by wblachowski on 2/18/2018.
 */

public class AllFragment extends Fragment{

    private static final int SPAN_SIZE = 3;
    private static final int SECTIONS = 10;
    private static final int SECTION_ITEMS = 5;

    private RecyclerView mRecycler;
    private StickyHeaderGridLayoutManager mLayoutManager;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_all, container, false);
        // Setup recycler
        mRecycler = (RecyclerView)rootView.findViewById(R.id.recycler);
        mLayoutManager = new StickyHeaderGridLayoutManager(2);
        mLayoutManager.setHeaderBottomOverlapMargin(0);

        ArrayList<String> headers = new ArrayList<>();
        headers.add("Linie do Poznania");
        headers.add("Linie swarzędzkie");
        headers.add("Linie międzygminne");
        ArrayList<ArrayList<Bus>> buses = new ArrayList<ArrayList<Bus>>();
        ArrayList<Bus> busl=new ArrayList<>();
        busl.add(new Bus("401","Start","Koniec"));
        busl.add(new Bus("451","SASD","ZXCXZC"));
        busl.add(new Bus("443","sad","adasd"));
        busl.add(new Bus("401","Start","Koniec"));
        busl.add(new Bus("451","SASD","ZXCXZC"));
        busl.add(new Bus("443","sad","adasd"));
        buses.add(busl);
        buses.add(busl);
        buses.add(busl);

        mRecycler.setLayoutManager(mLayoutManager);
        mRecycler.setAdapter(new AllAdapter(headers, buses));
        return rootView;
    }
}
