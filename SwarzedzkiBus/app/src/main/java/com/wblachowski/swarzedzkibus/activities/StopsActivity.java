package com.wblachowski.swarzedzkibus.activities;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.wblachowski.swarzedzkibus.R;
import com.wblachowski.swarzedzkibus.adapters.StopsCursorAdapter;
import com.wblachowski.swarzedzkibus.data.DataBaseHelper;

public class StopsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stops);
        String nr=getIntent().getStringExtra("nr").toString();
        String routeId=getIntent().getStringExtra("routeId").toString();
        setTitle("Linia " + nr + " route " + routeId);

        Cursor cursor = DataBaseHelper.getInstance(this).getStopsCursor(routeId);
        ListView listView = (ListView)findViewById(R.id.stops_list_view);
        StopsCursorAdapter cursorAdapter = new StopsCursorAdapter(this,cursor);
        listView.setAdapter(cursorAdapter);

    }
}
