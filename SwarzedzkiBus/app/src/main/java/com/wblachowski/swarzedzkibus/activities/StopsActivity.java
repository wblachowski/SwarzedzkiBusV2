package com.wblachowski.swarzedzkibus.activities;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
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
        setTitle("Linia " + nr);

        Cursor cursor = DataBaseHelper.getInstance(this).getStopsCursor(routeId);
        ListView listView = (ListView)findViewById(R.id.stops_list_view);
        StopsCursorAdapter cursorAdapter = new StopsCursorAdapter(this,cursor);
        listView.setAdapter(cursorAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> a, View v, int position,
                                    long id) {

                Intent intent = new Intent(v.getContext(), TimeTableActivity.class);

                startActivity(intent);
            }
        });
    }
}
