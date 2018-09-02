package com.yanleiz.waitbus.waitbus;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class BusMap extends AppCompatActivity {


    String lin;
    String dir;
    String dirCode;
    String otherDir;
    String otherDirCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_map);

        //DrawView dv = findViewById(R.id.drawView);
        //dv.setBusStations(busStations);
        //dv.setBusLocation(busLocation);
        //ScrollView sv = findViewById(R.id.scrollView);
        //sv.addView(sv);
        Intent intent = getIntent();
        lin = intent.getStringExtra("lin");
        dir = intent.getStringExtra("dir");
        dirCode = intent.getStringExtra("dirCode");
        otherDir = intent.getStringExtra("otherDir");
        otherDirCode = intent.getStringExtra("otherDirCode");

        //String url = Utils.URL3 + lin + Utils.URL3_EX1 + dirCode + Utils.URL3_EX2 + "2";
        //GetData getData = new GetData(BusMap.this, url);
        //getData.execute();

        //LinearLayout linLay = findViewById(R.id.linLay);
        //linLay.addView(new DrawView(BusMap.this));

    }
}
