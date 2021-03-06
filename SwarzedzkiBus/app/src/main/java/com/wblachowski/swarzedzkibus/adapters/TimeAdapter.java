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
        hours = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        try {


            if (v == null) {
                LayoutInflater vi;
                vi = LayoutInflater.from(getContext());
                v = vi.inflate(R.layout.time_item, parent, false);
            }

            TextView hour = v.findViewById(R.id.time_item_hour);
            FlexboxLayout layout = v.findViewById(R.id.time_item_minutes_layout);
            layout.removeAllViews();
            for (int i = 0; i < hours.get(position).getMinutes().size(); i++) {
                layout.addView(prepareMinuteView(v.getContext(), hours.get(position).getMinutes().get(i), hours.get(position).getRemarks().get(i)));
            }
            hour.setText(String.valueOf(hours.get(position).getHour()));
        }catch(Exception ex){
            System.out.println(ex.getMessage());
        }
        return v;
    }

    private LinearLayout prepareMinuteView(Context context, int minute, String remark) {
        LinearLayout parent = new LinearLayout(context);
        parent.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        parent.setOrientation(LinearLayout.HORIZONTAL);
        parent.setMinimumWidth((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 46, context.getResources().getDisplayMetrics()));
        TextView minuteText = prepareMinuteText(context, minute);
        TextView remarkText = prepareRemarkText(context, remark);
        parent.addView(minuteText);
        parent.addView(remarkText);
        return parent;
    }

    private TextView prepareMinuteText(Context context, int minute) {
        TextView textView = new TextView(context);
        LinearLayout.LayoutParams tvParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        textView.setLayoutParams(tvParams);
        textView.setGravity(Gravity.CENTER_VERTICAL);
        textView.setText(String.format("%02d", minute));
        textView.setTextSize(18);
        return textView;
    }

    private TextView prepareRemarkText(Context context, String remark) {
        TextView textView = new TextView(context);
        LinearLayout.LayoutParams tvParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
        textView.setLayoutParams(tvParams);
        textView.setGravity(Gravity.LEFT | Gravity.TOP);
        if (remark == null) remark = "";
        textView.setText(remark);
        textView.setTextSize(12);
        textView.setPadding(4,5,0,0);
        textView.setTextColor(context.getResources().getColor(R.color.colorPrimaryDark));
        return textView;
    }

}
