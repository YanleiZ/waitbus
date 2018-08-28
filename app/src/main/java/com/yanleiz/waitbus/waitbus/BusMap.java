package com.yanleiz.waitbus.waitbus;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

public class BusMap extends AppCompatActivity {
    ArrayList stations;
    ArrayList abstracts;
    private ArrayList busStationId;
    private ArrayList busStations;
    private ArrayList busLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_map);
        Intent intent1 = getIntent();
        stations = intent1.getStringArrayListExtra("stations");
        abstracts = intent1.getStringArrayListExtra("abstracts");
        TextView tv = findViewById(R.id.show);
        String a ="";
        Element b ;
        for (int i=0;i<stations.size();i++){
            b = (Element) stations.get(i);
            busStationId.add(b.select("div").attr("id").toString());
            busStations.add(b.select("div").toString());
            busLocation.add(b.select("div i.buss").parents().attr("id").toString());
            //a+=ascii2native(stations.get(i).toString())+"\n";
            a+=ascii2native(busStations.get(i).toString())+"\n";
        }
        tv.setText(a);
    }
    private static String ascii2native ( String asciicode )
    {
        String[] asciis = asciicode.split ("\\\\u");
        String nativeValue = asciis[0];
        try
        {
            for ( int i = 1; i < asciis.length; i++ )
            {
                String code = asciis[i];
                nativeValue += (char) Integer.parseInt (code.substring (0, 4), 16);
                if (code.length () > 4)
                {
                    nativeValue += code.substring (4, code.length ());
                }
            }
        }
        catch (NumberFormatException e)
        {
            return asciicode;
        }
        return nativeValue;
    }
}
