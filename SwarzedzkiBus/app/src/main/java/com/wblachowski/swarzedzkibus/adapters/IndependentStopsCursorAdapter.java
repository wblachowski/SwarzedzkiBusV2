package com.wblachowski.swarzedzkibus.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.wblachowski.swarzedzkibus.R;

/**
 * Created by wblachowski on 2/22/2018.
 */

public class IndependentStopsCursorAdapter extends CursorAdapter {

    public IndependentStopsCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.independent_stop_item, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        String bus = cursor.getString(cursor.getColumnIndex("bus_name"));
        String stopName = cursor.getString(cursor.getColumnIndex("STOP"));
        String destination = cursor.getString(cursor.getColumnIndex("FINAL_STOP"));
        ((TextView)view.findViewById(R.id.independent_bus_nr)).setText(bus);
        ((TextView)view.findViewById(R.id.independent_bus_stop)).setText(stopName);
        ((TextView)view.findViewById(R.id.independent_bus_to)).setText(destination);
    }
}
