package com.wblachowski.swarzedzkibus.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.wblachowski.swarzedzkibus.R;
import com.wblachowski.swarzedzkibus.adapters.TimeAdapter;
import com.wblachowski.swarzedzkibus.data.DataBaseHelper;
import com.wblachowski.swarzedzkibus.data.Hour;

import java.util.ArrayList;

/**
 * Created by wblachowski on 2/19/2018.
 */

public class TimeTableFragment extends Fragment {

    int type;
    String stopId;
    ListView listView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_time_table, container, false);
        listView = (ListView) rootView.findViewById(R.id.time_table_listview);
        loadTimes();
        return rootView;
    }

    public void setType(int type){
        this.type=type;
    }

    public void setStopId(String stopId){
        this.stopId=stopId;
    }

    public void loadTimes(){
        Cursor cursor = DataBaseHelper.getInstance(getContext()).getStopsTimes(stopId,type);
        ArrayList<Hour> hours = loadHoursFromCursor(cursor);
        TimeAdapter adapter = new TimeAdapter(getActivity(),R.layout.time_item,hours);
        listView.setAdapter(adapter);
    }

    private ArrayList<Hour> loadHoursFromCursor(Cursor cursor) {
        ArrayList<Hour> hours = new ArrayList<>();
        for(int i=4;i<24;i++){
            hours.add(new Hour(i,new ArrayList<Integer>(),new ArrayList<String>()));
        }
        if (cursor.moveToFirst()) {
            do {
                String hourS = cursor.getString(cursor.getColumnIndex("hour"));
                String minuteS = cursor.getString(cursor.getColumnIndex("minute"));
                String remark = cursor.getString(cursor.getColumnIndex("remark"));
                hours.get(Integer.parseInt(hourS)-4).addToMinutes(new Integer(minuteS));
                hours.get(Integer.parseInt(hourS)-4).addToRemarks(remark);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return hours;
    }
}
