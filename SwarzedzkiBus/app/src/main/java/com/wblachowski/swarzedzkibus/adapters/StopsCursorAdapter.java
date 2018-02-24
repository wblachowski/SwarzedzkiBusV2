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
import com.wblachowski.swarzedzkibus.data.SettingsDataBaseHelper;

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
    public void bindView(View view, final Context context, Cursor cursor) {
        final String stopName = cursor.getString(cursor.getColumnIndex("name"));
        final String stopId = cursor.getString(cursor.getColumnIndex("stop_id"));
        final String direction = cursor.getString(cursor.getColumnIndex("LAST_STOP"));
        final boolean isFavourite = cursor.getString(cursor.getColumnIndex("favourite")).equals("1");
        TextView stopText = view.findViewById(R.id.stop_name);
        stopText.setText(cursor.getString(cursor.getColumnIndex("name")));
        ImageView stopImage = view.findViewById(R.id.stop_image);
        if (cursor.isFirst()) {
            stopImage.setImageResource(R.drawable.oval_stop_start);
            stopImage.setRotation(0.0f);
        } else if (cursor.isLast()) {
            stopImage.setImageResource(R.drawable.oval_stop_start);
            stopImage.setRotation(180.0f);
        } else {
            stopImage.setImageResource(R.drawable.oval_middle);
        }

        final ImageView favImage = view.findViewById(R.id.imageButton);
        if(isFavourite) {
            favImage.setColorFilter(context.getResources().getColor(R.color.colorPrimary));
            favImage.setTag(R.id.favId,"1");
        }else{
            favImage.setColorFilter(context.getResources().getColor(R.color.colorNotFavourite));
            favImage.setTag(R.id.favId,"0");
        }
        favImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(((String)favImage.getTag(R.id.favId)).equals("0")) {
                    //not favourite
                    SettingsDataBaseHelper.getInstance(context).insertIntoFavourites(stopId,direction);
                    favImage.setColorFilter(context.getResources().getColor(R.color.colorPrimary));
                    favImage.setTag(R.id.favId,"1");
                }else{
                    SettingsDataBaseHelper.getInstance(context).deleteFromFavourites(stopId,direction);
                    favImage.setColorFilter(context.getResources().getColor(R.color.colorNotFavourite));
                    favImage.setTag(R.id.favId,"0");
                }
            }
        });
    }
}
