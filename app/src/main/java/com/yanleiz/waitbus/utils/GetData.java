package com.yanleiz.waitbus.utils;

import android.os.AsyncTask;
import android.widget.TextView;
import android.widget.Toast;

import com.yanleiz.waitbus.waitbus.BusMap;
import com.yanleiz.waitbus.waitbus.MainActivity;
import com.yanleiz.waitbus.waitbus.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

import static com.yanleiz.waitbus.utils.Utils.ascii2native;
import static com.yanleiz.waitbus.utils.Utils.sendHttpRequest;

public class GetData extends AsyncTask<String, String, String> {
    private String url;
    private TextView txt;

    private ArrayList busStationId;
    private ArrayList busStations;
    private ArrayList busLocation;
    private ArrayList<Element> stations;
    private ArrayList<Element> abstracts;

    public GetData(TextView txt, String url) {
        this.url = url;
        this.txt = txt;
    }

    @Override
    protected String doInBackground(String... strings) {
        String response = ascii2native(sendHttpRequest(url)).replace("\\", "");

        return response;
    }

    @Override
    protected void onPostExecute(String s) {
        //  super.onPostExecute(s);
        if (s != "" && s != null) {

            Element doc = Jsoup.parse(s);
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
            busStationId = new ArrayList();
            busStations = new ArrayList();
            busLocation = new ArrayList();
            String showStr = "";
            Element b;
            for (int i = 0; i < stations.size(); i++) {
                b = stations.get(i);
                busStationId.add(b.select("div").attr("id").toString().replace("\\", "").replace("\"", ""));
                busStations.add(b.select("div span").text().toString().replace("<\\/span><\\/div><\\/li>", ""));
                if (i % 2 == 0) {
                    busLocation.add(b.select("div i.buss").parents().attr("id").toString());
                } else {
                    busLocation.add(b.select("div i.busc").parents().attr("id").toString());
                }
                if (busLocation.get(i).toString().trim() != "") {
                    showStr += ascii2native(busStations.get(i).toString() + "============" + busLocation.get(i).toString() + "\n");

                } else {
                    showStr += ascii2native(busStations.get(i).toString() + busLocation.get(i).toString() + "\n");
                }
            }
            txt.setText(showStr);
        }
    }
}
