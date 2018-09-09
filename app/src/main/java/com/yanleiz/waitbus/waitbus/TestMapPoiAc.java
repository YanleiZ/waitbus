package com.yanleiz.waitbus.waitbus;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.core.SuggestionCity;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.yanleiz.waitbus.utils.Utils;

import java.util.List;

public class TestMapPoiAc extends AppCompatActivity implements PoiSearch.OnPoiSearchListener {
    private PoiResult poiResult; // poi返回的结果
    private int currentPage = 0;// 当前页面，从0开始计数
    private PoiSearch.Query query;// Poi查询条件类
    private LatLonPoint lp;//

    private PoiSearch poiSearch;
    // private myPoiOverlay poiOverlay;// poi图层
    private List<PoiItem> poiItems;// poi数据
    private RelativeLayout mPoiDetail;
    private TextView mPoiName;
    private String keyWord = "607路公交站";
    private String city = "北京市";
    private LocationManager lm;
    Location lc;

   

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_map_poi);
        mPoiName = findViewById(R.id.texta);


        lm = (LocationManager) getSystemService(TestMapPoiAc.LOCATION_SERVICE);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
           
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


    }


    /**
     * 开始进行poi搜索
     */
    protected void doSearchQuery() {
        currentPage = 0;
        query = new PoiSearch.Query(keyWord, "", city);// 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
        query.setPageSize(200);// 设置每页最多返回多少条poiitem
        query.setPageNum(currentPage);// 设置查第一页

        poiSearch = new PoiSearch(this, query);
        poiSearch.setOnPoiSearchListener(this);
        poiSearch.setBound(new PoiSearch.SearchBound(lp, 5000, true));//
        // 设置搜索区域为以lp点为圆心，其周围5000米范围
        poiSearch.searchPOIAsyn();// 异步搜索
        //String s = poiSearch.getBound().toString();
        //Log.e("111111",s);

    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        //whetherToShowDetailInfo(false);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onPoiItemSearched(PoiItem poiitem, int rcode) {

    }


    @Override
    public void onPoiSearched(PoiResult result, int rcode) {
        String s = "";
        if (rcode == AMapException.CODE_AMAP_SUCCESS) {
            if (result != null && result.getQuery() != null) {// 搜索poi的结果
                if (result.getQuery().equals(query)) {// 是否是同一条
                    poiResult = result;
                    poiItems = poiResult.getPois();// 取得第一页的poiitem数据，页数从数字0开始
                    List<SuggestionCity> suggestionCities = poiResult
                            .getSearchSuggestionCitys();// 当搜索不到poiitem数据时，会返回含有搜索关键字的城市信息
                    if (poiItems != null && poiItems.size() > 0) {
                        for (int i = 0; i < poiItems.size(); i++) {
                            if(poiItems.get(i).toString().contains("公交站")){
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
        mPoiName.setText(s);
    }
}