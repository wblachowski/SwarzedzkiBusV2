package com.wblachowski.swarzedzkibus.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.wblachowski.swarzedzkibus.R;

/**
 * Created by wblachowski on 2/19/2018.
 */

public class StopsCursorAdapter extends CursorAdapter {

    public StopsCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.stop_item, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        String stopName = cursor.getString(cursor.getColumnIndex("name"));
        TextView stopText = view.findViewById(R.id.stop_name);
        stopText.setText(cursor.getString(cursor.getColumnIndex("name")));
        ImageView stopImage = view.findViewById(R.id.stop_image);
        if (cursor.isFirst()) {
            stopImage.setImageResource(R.drawable.oval_stop_start);
            stopImage.setRotation(0.0f);
        } else if (cursor.isLast()) {
            stopImage.setImageResource(R.drawable.oval_stop_start);
            stopImage.setRotation(180.0f);
        }else{
            stopImage.setImageResource(R.drawable.oval_middle);
        }
    }
}
