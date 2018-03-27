package com.wblachowski.swarzedzkibus.activities;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.wblachowski.swarzedzkibus.R;
import com.wblachowski.swarzedzkibus.adapters.StopsCursorAdapter;
import com.wblachowski.swarzedzkibus.data.MainDataBaseHelper;

public class StopsActivity extends AppCompatActivity {
    StopsCursorAdapter cursorAdapter;
    Cursor cursor;
    ListView listView;
    static boolean isActive = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stops);

        final String nr=getIntent().getStringExtra("nr");
        final String routeId=getIntent().getStringExtra("routeId");
        setTitle("Linia " + nr);

        cursor = MainDataBaseHelper.getInstance(this).getStopsCursor(routeId);
        listView = (ListView)findViewById(R.id.stops_list_view);
        cursorAdapter = new StopsCursorAdapter(this,cursor);
        listView.setAdapter(cursorAdapter);

        final Activity activity = this;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(isActive) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            cursorAdapter.swapCursor(MainDataBaseHelper.getInstance(activity).getStopsCursor(routeId));
                        }
                    });
                    try {
                        Thread.sleep(10*1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> a, View v, int position,
                                    long id) {

                Intent intent = new Intent(v.getContext(), TimeTableActivity.class);
                cursor.moveToPosition(position);
                intent.putExtra("nr",nr);
                intent.putExtra("id",cursor.getString(cursor.getColumnIndex("stop_id")));
                intent.putExtra("stopName",cursor.getString(cursor.getColumnIndex("name")));
                cursor.moveToLast();
                intent.putExtra("direction",cursor.getString(cursor.getColumnIndex("name")));
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume(){
        super.onResume();
        cursorAdapter.swapCursor(MainDataBaseHelper.getInstance(this).getStopsCursor(getIntent().getStringExtra("routeId")));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        isActive = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        isActive = false;
    }
}
