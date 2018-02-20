package com.wblachowski.swarzedzkibus.fragments;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.flexbox.FlexboxLayout;
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

    public void setType(int type) {
        this.type = type;
    }

    public void setStopId(String stopId) {
        this.stopId = stopId;
    }

    public void loadTimes() {
        Cursor cursor = DataBaseHelper.getInstance(getContext()).getStopsTimes(stopId, type);
        ArrayList<Hour> hours = loadHoursFromCursor(cursor);
        TimeAdapter adapter = new TimeAdapter(getActivity(), R.layout.time_item, hours);
        if (isAnyRemarks(hours)) {
            addRemarksFooter(listView);
        }
        listView.setAdapter(adapter);
    }

    private boolean isAnyRemarks(ArrayList<Hour> hours) {
        for (Hour hour : hours) {
            for (String remark : hour.getRemarks()) {
                if (remark != null && remark.length() > 0) {
                    return true;
                }
            }
        }
        return false;
    }

    private void addRemarksFooter(ListView listView) {
        Cursor cursor = DataBaseHelper.getInstance(getActivity()).getRemarksByStop(stopId);
        View view = ((LayoutInflater) this.getActivity()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
                R.layout.footer_remarks, null, false);
        LinearLayout parent = view.findViewById(R.id.footer_remarks_layout);
        if (cursor == null) {
            return;
        } else if (cursor.moveToFirst()) {
            do {
                String symbol = cursor.getString(cursor.getColumnIndex("symbol"));
                String description = cursor.getString(cursor.getColumnIndex("description"));
                LinearLayout layoutRemark = prepareRemarkLayout(symbol, description);
                parent.addView(layoutRemark);
            } while (cursor.moveToNext());
        }
        cursor.close();
        listView.addFooterView(view);
    }

    private LinearLayout prepareRemarkLayout(String symbol, String description){
        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));
        layout.addView(prepareSymbolLayout(symbol.trim()));
        layout.addView(prepareDescriptionLayout(description.trim()));
        return layout;
    }

    private TextView prepareSymbolLayout(String symbol) {
        TextView textView = new TextView(getContext());
        textView.setLayoutParams(new LinearLayout.LayoutParams((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 36, getContext().getResources().getDisplayMetrics()), LinearLayout.LayoutParams.WRAP_CONTENT));
        textView.setText(symbol);
        textView.setTextSize(18);
        textView.setWidth(20);
        textView.setGravity(Gravity.RIGHT);
        return textView;
    }

    private FlexboxLayout prepareDescriptionLayout(String description) {
        FlexboxLayout layout = new FlexboxLayout(getContext());
        TextView textView = new TextView(getContext());
        textView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        textView.setGravity(Gravity.CENTER_VERTICAL);
        textView.setText(description);
        textView.setTextSize(18);
        textView.setPadding((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getContext().getResources().getDisplayMetrics()),0,8,0);
        layout.addView(textView);
        return layout;
    }

    private ArrayList<Hour> loadHoursFromCursor(Cursor cursor) {
        ArrayList<Hour> hours = new ArrayList<>();
        for (int i = 0; i < 24; i++) {
            hours.add(new Hour(i, new ArrayList<Integer>(), new ArrayList<String>()));
        }
        if (cursor.moveToFirst()) {
            do {
                String hourS = cursor.getString(cursor.getColumnIndex("hour"));
                String minuteS = cursor.getString(cursor.getColumnIndex("minute"));
                String remark = cursor.getString(cursor.getColumnIndex("remark"));
                hours.get(Integer.parseInt(hourS)).addToMinutes(new Integer(minuteS));
                hours.get(Integer.parseInt(hourS)).addToRemarks(remark);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return hours;
    }
}
