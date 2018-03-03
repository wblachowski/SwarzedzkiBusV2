package com.wblachowski.swarzedzkibus.fragments;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.codewaves.stickyheadergrid.StickyHeaderGridLayoutManager;
import com.wblachowski.swarzedzkibus.R;
import com.wblachowski.swarzedzkibus.adapters.FavouritesAdapter;
import com.wblachowski.swarzedzkibus.data.MainDataBaseHelper;
import com.wblachowski.swarzedzkibus.data.Stop;

import java.util.ArrayList;

/**
 * Created by wblachowski on 2/18/2018.
 */

public class FavouritesFragment extends Fragment {

    LinearLayout noFavouritesLayout;
    RecyclerView mRecycler;
    StickyHeaderGridLayoutManager mLayoutManager;
    ArrayList<Stop> stops = new ArrayList<>();
    FavouritesAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_favourites, container, false);
        noFavouritesLayout = rootView.findViewById(R.id.fragment_favourites_empty_layout);

        // Setup recycler
        mRecycler = (RecyclerView) rootView.findViewById(R.id.recycler_favourites);
        mLayoutManager = new StickyHeaderGridLayoutManager(1);
        mLayoutManager.setHeaderBottomOverlapMargin(0);
        mRecycler.setLayoutManager(mLayoutManager);
        adapter=null;
        refreshStopsList(false);
        startRefreshThreadIfNeeded();
        return rootView;
    }

    private void startRefreshThreadIfNeeded(){
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        boolean val = pref.getBoolean(getString(R.string.key_departure_time),true);
        if(!val)return;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    refreshStopsList(true);
                    try {
                        Thread.sleep(10*1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public void notifyStopsChanged() {
        setEmptyLayoutVisibility();
    }

    public void refreshStopsList(final boolean repeated) {
        if(!repeated){
            adapter=null;
        }
        final FavouritesFragment fragment = this;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ArrayList<Stop> currStops = parseCursorToStops(MainDataBaseHelper.getInstance(getActivity()).getFavouriteStops());
                    stops.clear();
                    stops.addAll(currStops);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setEmptyLayoutVisibility();
                            if (adapter == null) {
                                adapter = new FavouritesAdapter(stops, fragment);
                                mRecycler.setAdapter(adapter);
                            } else {
                                adapter.notifyDataSetChanged();
                            }
                        }
                    });
                }catch (Exception ex){
                    System.out.println(ex.getMessage());
                }
            }
        }).start();
    }

    private void setEmptyLayoutVisibility() {
        noFavouritesLayout.setVisibility((stops != null && stops.size() > 0) ? View.INVISIBLE : View.VISIBLE);
    }

    private ArrayList<Stop> parseCursorToStops(Cursor cursor) {
        ArrayList<Stop> stops = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String stopId = cursor.getString(cursor.getColumnIndex("id"));
                String busNr = cursor.getString(cursor.getColumnIndex("bus_name"));
                String stopName = cursor.getString(cursor.getColumnIndex("STOP"));
                String direction = cursor.getString(cursor.getColumnIndex("FINAL_STOP"));
                Stop stop = new Stop(stopId, busNr, stopName, direction);
                if(cursor.getColumnIndex("hour")>-1 && cursor.getColumnIndex("minute")>-1){
                    stop=new Stop(stopId,busNr,stopName,direction,cursor.getString(cursor.getColumnIndex("hour")),cursor.getString(cursor.getColumnIndex("minute")));
                }
                stops.add(stop);
            } while (cursor.moveToNext());
        }
        return stops;
    }
}
