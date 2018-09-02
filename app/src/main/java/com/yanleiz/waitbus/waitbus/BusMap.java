package com.yanleiz.waitbus.waitbus;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;

import com.yanleiz.waitbus.utils.GetData;
import com.yanleiz.waitbus.utils.Utils;

import java.util.Timer;
import java.util.TimerTask;

public class BusMap extends AppCompatActivity {


    String lin;
    String dir;
    String dirCode;
    String otherDir;
    String otherDirCode;

    String url;
    Timer timer =  new java.util.Timer(true);
    TimerTask task;

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

        setTitle(dir);

        url = Utils.URL3 + lin + Utils.URL3_EX1 + dirCode + Utils.URL3_EX2 + "2";


        task = new TimerTask() {
            public void run() {
                GetData getData = new GetData(BusMap.this, url);
                getData.execute();
            }
        };
        timer.schedule(task, 10000, 10000);

        //LinearLayout linLay = findViewById(R.id.linLay);
        //linLay.addView(new DrawView(BusMap.this));

    }

    @Override
    protected void onPause() {
        super.onPause();
        timer.cancel();
    }
/*
    @Override
    protected void onRestart() {
        super.onRestart();
        timer.schedule(task, 10000, 10000);
    }*/

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BusMap.this.finish();
    }
}
