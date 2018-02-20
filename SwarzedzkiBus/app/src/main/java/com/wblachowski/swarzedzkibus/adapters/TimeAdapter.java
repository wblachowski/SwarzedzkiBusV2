package com.wblachowski.swarzedzkibus.adapters;

import android.content.Context;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.flexbox.FlexboxLayout;
import com.wblachowski.swarzedzkibus.R;
import com.wblachowski.swarzedzkibus.data.Hour;

import java.util.ArrayList;

/**
 * Created by wblachowski on 2/19/2018.
 */

public class TimeAdapter extends ArrayAdapter<Hour> {

    ArrayList<Hour> hours;

    public TimeAdapter(Context context, int resource, ArrayList<Hour> items) {
        super(context, resource, items);
        hours=items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.time_item, parent,false);
        }

        TextView hour = v.findViewById(R.id.time_item_hour);
        FlexboxLayout layout = v.findViewById(R.id.time_item_minutes_layout);
        layout.removeAllViews();
        for(int i=0;i<hours.get(position).getMinutes().size();i++){
            layout.addView(prepareMinutTextView(v.getContext(),hours.get(position).getMinutes().get(i),hours.get(position).getRemarks().get(i)));
            layout.addView(prepareMinutTextView(v.getContext(),hours.get(position).getMinutes().get(i),hours.get(position).getRemarks().get(i)));
            layout.addView(prepareMinutTextView(v.getContext(),hours.get(position).getMinutes().get(i),hours.get(position).getRemarks().get(i)));
            layout.addView(prepareMinutTextView(v.getContext(),hours.get(position).getMinutes().get(i),hours.get(position).getRemarks().get(i)));

        }
        hour.setText(String.valueOf(hours.get(position).getHour()));

        return v;
    }

    private TextView prepareMinutTextView(Context context,int minute,String remark){
        TextView textView = new TextView(context);
        LinearLayout.LayoutParams tvParams = new LinearLayout.LayoutParams((int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 36, context.getResources().getDisplayMetrics()), LinearLayout.LayoutParams.MATCH_PARENT);
        textView.setLayoutParams(tvParams);
        textView.setGravity(Gravity.CENTER_VERTICAL);
        if(remark==null){
            remark="";
        }
        textView.setText(String.format("%02d", minute)+" "+remark);
        textView.setTextSize(18);
        return textView;
    }

}
