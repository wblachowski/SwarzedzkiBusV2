package com.wblachowski.swarzedzkibus.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.wblachowski.swarzedzkibus.R;

public class StopsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stops);
        String nr=getIntent().getStringExtra("nr").toString();
        String routeId=getIntent().getStringExtra("routeId").toString();
        setTitle("Linia " + nr + " route " + routeId);
    }
}
