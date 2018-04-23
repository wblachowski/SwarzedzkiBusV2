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
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by wblachowski on 2/18/2018.
 */

public class FavouritesFragment extends Fragment {

    LinearLayout noFavouritesLayout;
    RecyclerView mRecycler;
    StickyHeaderGridLayoutManager mLayoutManager;
    ArrayList<Stop> stops = new ArrayList<>();
    FavouritesAdapter adapter;
    MainDataBaseHelper dataBaseHelper;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_favourites, container, false);
        try {
            noFavouritesLayout = rootView.findViewById(R.id.fragment_favourites_empty_layout);

            // Setup recycler
            mRecycler = (RecyclerView) rootView.findViewById(R.id.recycler_favourites);
            mLayoutManager = new StickyHeaderGridLayoutManager(1);
            mLayoutManager.setHeaderBottomOverlapMargin(0);
            mRecycler.setLayoutManager(mLayoutManager);
            adapter = null;
            dataBaseHelper=MainDataBaseHelper.getInstance(getActivity());
            refreshStopsList(false);
            startRefreshThreadIfNeeded();
        } catch (Exception ex) {
            System.out.print(ex.getMessage());
        }
        return rootView;
    }

    Timer timer;
    public void startRefreshThreadIfNeeded() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        boolean val = pref.getBoolean(getString(R.string.key_departure_time), true);
        if (!val) return;

        if(timer!=null)timer.cancel();

        timer = new Timer(true);
        long delay = 60*1000 - System.currentTimeMillis()%(60*1000);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                refreshStopsList(true);
            }
        },  delay,  60*1000);
    }

    public void notifyStopsChanged() {
        setEmptyLayoutVisibility();
    }

    public synchronized void refreshStopsList(final boolean repeated) {
        final FavouritesFragment fragment = this;
        new Thread(new Runnable() {
            @Override
            public void run() {
                refresh(repeated);
            }
        }).start();
    }

    private synchronized void refresh(final boolean repeated){
        final FavouritesFragment fragment = this;
        try {
            ArrayList<Stop> currStops = parseCursorToStops(dataBaseHelper.getFavouriteStops());
            if (stops == null) stops = new ArrayList<Stop>();
            stops.clear();
            stops.addAll(currStops);
            if(getActivity()==null)return;
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        setEmptyLayoutVisibility();
                        if (repeated && adapter!=null) {
                            adapter.notifyDataSetChanged();
                        } else {
                            adapter = new FavouritesAdapter(stops, fragment);
                            mRecycler.setAdapter(adapter);
                        }
                    } catch (Exception ex) {
                        System.out.println(ex.getMessage());
                    }
                }
            });
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
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
                if (cursor.getColumnIndex("hour") > -1 && cursor.getColumnIndex("minute") > -1) {
                    stop = new Stop(stopId, busNr, stopName, direction, cursor.getString(cursor.getColumnIndex("hour")), cursor.getString(cursor.getColumnIndex("minute")));
                }
                stops.add(stop);
            } while (cursor.moveToNext());
        }
        return stops;
    }
}
