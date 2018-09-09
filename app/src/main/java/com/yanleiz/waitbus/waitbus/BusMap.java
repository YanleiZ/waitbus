package com.yanleiz.waitbus.waitbus;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.core.SuggestionCity;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.yanleiz.waitbus.utils.GetData;
import com.yanleiz.waitbus.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class BusMap extends AppCompatActivity implements PoiSearch.OnPoiSearchListener {


    String lin;
    String dir;
    String dirCode;
    String otherDir;
    String otherDirCode;

    String url;
    Timer timer;


    private String keyWord = "路公交站";
    private String city = "北京市";
    private LocationManager lm;
    Location lc;
    private LatLonPoint lp;//
    private PoiSearch poiSearch;
    private PoiResult poiResult; // poi返回的结果
    private int currentPage = 0;// 当前页面，从0开始计数
    private PoiSearch.Query query;// Poi查询条件类
    private List<PoiItem> poiItems;// poi数据


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

        //url = Utils.URL3 + lin + Utils.URL3_EX1 + dirCode + Utils.URL3_EX2 + Utils.aboardStation;


        //LinearLayout linLay = findViewById(R.id.linLay);
        //linLay.addView(new DrawView(BusMap.this));

        //

        lm = (LocationManager) getSystemService(TestMapPoiAc.LOCATION_SERVICE);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(BusMap.this, "无权限获取地理位置！", Toast.LENGTH_SHORT).show();
            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } /*else {
            Toast.makeText(TestMapPoiAc.this,"无权限获取地理位置！",Toast.LENGTH_SHORT).show();
        }*/
        if (Utils.isGpsAble(lm)) {
            lc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        } else {
            lc = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        }
        Double a = lc.getLongitude();

        Double b = lc.getLatitude();
        lp = new LatLonPoint(a, b);
        doSearchQuery();

        //

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (timer != null) {
            timer.cancel();

        }
        startTimer();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (timer != null) {
            timer.cancel();
        }
    }

    private void startTimer() {

        timer = new java.util.Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                //url = Utils.URL3 + lin + Utils.URL3_EX1 + dirCode + Utils.URL3_EX2 + Utils.aboardStation;

                GetData getData = new GetData(BusMap.this, Utils.URL3 + lin + Utils.URL3_EX1 + dirCode + Utils.URL3_EX2 + Utils.aboardStation);
                getData.execute();
            }
        }, 1000, 10000);
    }


    /**
     * 开始进行poi搜索
     */
    protected void doSearchQuery() {
        currentPage = 0;
        query = new PoiSearch.Query(lin + keyWord, "", city);// 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
        query.setPageSize(10);// 设置每页最多返回多少条poiitem
        query.setPageNum(currentPage);// 设置查第一页

        poiSearch = new PoiSearch(this, query);
        poiSearch.setOnPoiSearchListener(this);
        poiSearch.setBound(new PoiSearch.SearchBound(lp, 1000, true));//
        // 设置搜索区域为以lp点为圆心，其周围5000米范围
        poiSearch.searchPOIAsyn();// 异步搜索
        //String s = poiSearch.getBound().toString();
        //Log.e("111111",s);

    }

    @Override
    public void onPoiSearched(PoiResult result, int rcode) {
        Utils.nearStation.clear();
        String s = "";
        if (rcode == AMapException.CODE_AMAP_SUCCESS) {
            if (result != null && result.getQuery() != null) {// 搜索poi的结果
                if (result.getQuery().equals(query)) {// 是否是同一条
                    poiResult = result;
                    poiItems = poiResult.getPois();// 取得第一页的poiitem数据，页数从数字0开始

                    List<SuggestionCity> suggestionCities = poiResult
                            .getSearchSuggestionCitys();// 当搜索不到poiitem数据时，会返回含有搜索关键字的城市信息
                    if (poiItems != null && poiItems.size() > 0) {
                        int tmpDistance = 1000;
                        for (int i = 0; i < poiItems.size(); i++) {
                            if (poiItems.get(i).toString().contains("公交站")) {
                                if (poiItems.get(i).getDistance() < tmpDistance) {
                                    Utils.nearStation.clear();
                                    tmpDistance = poiItems.get(i).getDistance();
                                    Utils.nearStation.add(poiItems.get(i).toString());
                                }

                                s += poiItems.get(i).toString() + "\n";
                            }
                            Log.e("111111111", s);
                        }
                        //清除POI信息显示
                        //whetherToShowDetailInfo(false);
                        //并还原点击marker样式

                    }
                }

            }


        }
    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {

    }
}
