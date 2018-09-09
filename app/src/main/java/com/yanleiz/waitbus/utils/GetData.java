package com.yanleiz.waitbus.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

import static com.yanleiz.waitbus.utils.Utils.ascii2native;
import static com.yanleiz.waitbus.utils.Utils.sendHttpRequest;

public class GetData extends AsyncTask<String, String, String> {
    private Context context;
    private String url;
    //private DrawView dv;

    private ArrayList<Element> stations;
    private ArrayList<Element> abstracts;

    public GetData(Context context, String url) {
        this.context = context;
        this.url = url;
        // this.dv = dv;

    }

    @Override
    protected String doInBackground(String... strings) {
        String response = ascii2native(sendHttpRequest(url)).replace("\\", "");

        return response;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if (s == "" || s == null) {
            Toast.makeText(context, "数据异常！", Toast.LENGTH_LONG).show();
        } else if (s == "err") {
            Toast.makeText(context, "数据异常！", Toast.LENGTH_LONG).show();
        } else {
            Element doc = Jsoup.parse(s);
            Elements station_eles = doc.select("div").select("ul").select("li").select("div");
            Elements abstract_eles = doc.select("article").select("p");
            Utils.busStationId.clear();
            Utils.busStations.clear();
            Utils.busLocation.clear();
            Utils.busAbstracts.clear();

            if (station_eles.size() > 0 && abstract_eles.size() > 0) {
                stations = new ArrayList<>();
                abstracts = new ArrayList<>();

                for (int i = 0; i < station_eles.size(); i++) {
                    Element a = station_eles.get(i);
                    Utils.busStationId.add(a.select("div").attr("id").toString());
                    Utils.busStations.add(a.select("div span").attr("title").toString());
                    if (i % 2 == 0) {
                        Utils.busLocation.add(a.select("div i.buss").parents().attr("id").toString());
                    } else {
                        Utils.busLocation.add(a.select("div i.busc").parents().attr("id").toString());
                    }
                }
                for (int i = 0; i < abstract_eles.size(); i++) {
                    Utils.busAbstracts.add(abstract_eles.get(i).text().toString().trim());
                }
            }
          /*  for (int i = 0; i < stations.size(); i++) {
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
                }*/

            /*for (int i = 0; i < stations.size(); i++) {
                b = stations.get(i);
                Utils.busStationId.add(b.select("div").attr("id").toString());
                Utils.busStations.add(b.select("div span").attr("title").toString());
                if (i % 2 == 0) {
                    Utils.busLocation.add(b.select("div i.buss").parents().attr("id").toString());
                } else {
                    Utils.busLocation.add(b.select("div i.busc").parents().attr("id").toString());
                }
                //showStr += ascii2native(busStations.get(i).toString() + "============" + busLocation.get(i).toString() + "\n");
            }*//*
            for (int i=0;i<abstracts.size();i++){
                Utils.busAbstracts.add(abstracts.get(i).text())
            }*/
            //dv.setBusStations(busLocation);
            //dv.setBusLocation(busLocation);
        }
    }
}
