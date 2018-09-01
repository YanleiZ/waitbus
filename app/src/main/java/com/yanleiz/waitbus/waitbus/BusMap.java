package com.yanleiz.waitbus.waitbus;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.yanleiz.waitbus.utils.GetData;
import com.yanleiz.waitbus.utils.Utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

import static com.yanleiz.waitbus.utils.Utils.ascii2native;

public class BusMap extends AppCompatActivity {
    private ArrayList busStationId;
    private ArrayList busStations;
    private ArrayList busLocation;
    private ArrayList<Element> stations;
    private ArrayList<Element> abstracts;

    String lin;
    private String dir;
    String dirCode;
    String otherDir;
    String otherDirCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_map);
        //initDataAndView();
        TextView tv = findViewById(R.id.show);

        Intent intent = getIntent();
        lin = intent.getStringExtra("lin");
        dir = intent.getStringExtra("dir");
        dirCode = intent.getStringExtra("dirCode");
        otherDir= intent.getStringExtra("otherDir");
        otherDirCode = intent.getStringExtra("otherDirCode");

        String url = Utils.URL3 + lin + Utils.URL3_EX1 + dir + Utils.URL3_EX2 + "2";
        GetData getData = new GetData(tv,url);
        getData.execute(url);
    }
}
