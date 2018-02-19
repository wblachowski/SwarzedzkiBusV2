package com.wblachowski.swarzedzkibus.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codewaves.stickyheadergrid.StickyHeaderGridLayoutManager;
import com.wblachowski.swarzedzkibus.R;
import com.wblachowski.swarzedzkibus.activities.MainActivity;
import com.wblachowski.swarzedzkibus.adapters.AllAdapter;
import com.wblachowski.swarzedzkibus.data.Bus;

import java.util.ArrayList;

/**
 * Created by wblachowski on 2/18/2018.
 */

public class AllFragment extends Fragment {

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
        mRecycler = (RecyclerView) rootView.findViewById(R.id.recycler);
        mLayoutManager = new StickyHeaderGridLayoutManager(2);
        mLayoutManager.setHeaderBottomOverlapMargin(0);

        ArrayList<String> headers = new ArrayList<>();
        ArrayList<ArrayList<Bus>> buses = new ArrayList<ArrayList<Bus>>();


        parseDatabaseData(((MainActivity) getActivity()).getDataBaseHelper().getAllBusesCursor(), headers, buses);

        mRecycler.setLayoutManager(mLayoutManager);
        mRecycler.setAdapter(new AllAdapter(headers, buses));
        return rootView;
    }

    private void parseDatabaseData(Cursor cursor, ArrayList<String> headers, ArrayList<ArrayList<Bus>> buses) {
        if (cursor == null) {
            return;
        } else if (cursor.moveToFirst()) {
            do {
                String busNr = cursor.getString(cursor.getColumnIndex("bus_name"));
                String region = cursor.getString(cursor.getColumnIndex("region_name"));
                String startStop = cursor.getString(cursor.getColumnIndex("START_STOP"));
                String endStop = cursor.getString(cursor.getColumnIndex("END_STOP"));
                String routeId=cursor.getString(cursor.getColumnIndex("ROUTE_ID_A"));
                Bus bus = new Bus(busNr, startStop, endStop);
                bus.setRouteId(routeId);
                if (!headers.contains(region)) {
                    headers.add(region);
                    buses.add(new ArrayList<Bus>());
                }
                buses.get(headers.indexOf(region)).add(bus);
            } while (cursor.moveToNext());
        }
        cursor.close();
    }
}
