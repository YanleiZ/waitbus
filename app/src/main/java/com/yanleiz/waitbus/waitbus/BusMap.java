package com.yanleiz.waitbus.waitbus;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_map);
        initDataAndView();
        // Intent intent1 = getIntent();
        //stations =MainActivity.stations;//= intent1.getStringArrayListExtra("stations");
        //abstracts =MainActivity.abstracts;//= intent1.getStringArrayListExtra("abstracts");
    }

    private void initDataAndView() {
        Element doc = Jsoup.parse(MainActivity.response);
        Elements station_eles = doc.select("div").select("ul").select("li").select("div");
        Elements abstract_eles = doc.select("article").select("p");
        if (station_eles.size() > 0 && abstract_eles.size() > 0) {
            stations = new ArrayList<>();
            abstracts = new ArrayList<>();

            for (int i = 0; i < station_eles.size(); i++) {
                Element a = station_eles.get(i);
                stations.add(a);
            }
            for (int i = 0; i < abstract_eles.size(); i++) {
                Element b = abstract_eles.get(i);
                abstracts.add(b);
            }
        }
        TextView tv = findViewById(R.id.show);
        busStationId = new ArrayList();
        busStations = new ArrayList();
        busLocation = new ArrayList();
        String showStr = "";
        Element b;
        for (int i = 0; i < stations.size(); i++) {
            b =  stations.get(i);
            busStationId.add(b.select("div").attr("id").toString().replace("\\", "").replace("\"", ""));
            busStations.add(b.select("div span").text().toString().replace("<\\/span><\\/div><\\/li>", ""));
            busLocation.add(b.select("div i.busc").parents().attr("id").toString());
            showStr += ascii2native(busStations.get(i).toString() + busLocation.get(i).toString() + "\n");
        }
        tv.setText(showStr);
    }
}
