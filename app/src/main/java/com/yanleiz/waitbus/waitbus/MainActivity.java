package com.yanleiz.waitbus.waitbus;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.yanleiz.waitbus.beans.Directs;
import com.yanleiz.waitbus.utils.Utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

import static com.yanleiz.waitbus.utils.Utils.ascii2native;
import static com.yanleiz.waitbus.utils.Utils.sendHttpRequest;

////div[i[@class="buss"]]/@id
public class MainActivity extends AppCompatActivity {

    String lin;
    Directs direct;
    String dirCode;
    String dir;
    String url;

    private ArrayList<Element> stations;

    private AutoCompleteTextView autocomText = null;
    private Spinner sel_dir = null;
    private Button query_button = null;
    // private String[] text = null;
    ArrayList<String> directs;
    ArrayList<String> directs_code;
    public static String response;


    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == Utils.SELECT_LIN) {
                ArrayList ary = new ArrayList(Utils.allBus);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, ary);
                autocomText.setAdapter(adapter);
                ////dd[@id="selBLine"]
                //ArrayList<Node> list = (ArrayList<Node>) doc.selectNodes("//dd[@id='selBLine']");
            } else if (msg.what == Utils.SELECT_DIR) {

                ArrayList list = new ArrayList();
                for (int i = 0; i < directs.size(); i++) {
                    list.add(new Directs(directs.get(i), directs_code.get(i)));
                }
                ArrayAdapter<Directs> adapter = new ArrayAdapter<Directs>(getApplicationContext(),
                        android.R.layout.simple_spinner_item, list);

                sel_dir.setAdapter(adapter);
            } else if (msg.what == Utils.QUERY) {
                Intent intent = new Intent(MainActivity.this, BusMap.class);
                ArrayList<String> tmp_directs = directs;
                ArrayList<String> tmp_directs_code = directs_code;
                //intent.putExtra("url", url);
                if (tmp_directs_code.size() >= 1 && tmp_directs.size() >= 1) {
                    tmp_directs.remove(dirCode);
                    tmp_directs_code.remove(dir);

                    if (tmp_directs_code.size() == 1 && tmp_directs.size() == 1) {
                        String otherDir = tmp_directs.get(0);
                        String otherDirCode = tmp_directs_code.get(0);
                        intent.putExtra("otherDir", otherDir);
                        intent.putExtra("otherDirCode", otherDirCode);
                    } else {
                        String otherDir = "0";
                        String otherDirCode = "0";
                        intent.putExtra("otherDir", otherDir);
                        intent.putExtra("otherDirCode", otherDirCode);
                    }

                    //String url = Utils.URL3 + lin + Utils.URL3_EX1 + dirCode + Utils.URL3_EX2 + "2";
                    //GetData getData = new GetData(MainActivity.this,url);
                    //getData.execute();

                    intent.putExtra("lin", lin);
                    intent.putExtra("dir", dir);
                    intent.putExtra("dirCode", dirCode);

                    startActivityForResult(intent, 1);


                } else {
                    query_button.setClickable(true);
                    Toast.makeText(getApplicationContext(), "数据获取错误！", Toast.LENGTH_LONG).show();
                }
            } else if (msg.what == Utils.CLICKBLE) {
                query_button.setClickable(true);
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        checkGetAllBuss();
        //获取定位权限
        requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
    }
/*
    private void startGet() {
        new Thread(new Runnable() {
            Document doc = null;

            @Override
            public void run() {
                try {
                    doc = Jsoup.connect(Utils.URL1).get();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Elements ele_bus = doc.body().select("dd#selBLine a");
                String bus[] = new String[ele_bus.size()];
                for (int i = 0; i < ele_bus.size(); i++) {
                    bus[i] = ele_bus.get(i).text();
                }
                //text = bus;

                handler.sendEmptyMessage(Utils.SELECT_LIN);
            }
        }).start();
    }*/

    private void checkGetAllBuss() {
        if (Utils.allBus.size() != 0 && Utils.allBus != null) {
            handler.sendEmptyMessage(Utils.SELECT_LIN);
        } else {
            Utils.getAllBus();
            Toast.makeText(MainActivity.this, "网络连接缓慢！", Toast.LENGTH_SHORT).show();
        }
    }

    private void initView() {
        autocomText = findViewById(R.id.autocom);
        sel_dir = findViewById(R.id.select_dir);
        query_button = findViewById(R.id.query);
        autocomText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                new Thread(new Runnable() {
                    Document doc = null;

                    @Override
                    public void run() {

                        String buslin = autocomText.getText().toString();
                        try {
                            doc = Jsoup.connect(Utils.URL2 + buslin).get();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Elements directs_ele = doc.body().select("a");

                        if (directs_ele != null) {
                            directs = new ArrayList<>();
                            directs_code = new ArrayList<>();
                            for (int i = 0; i < directs_ele.size(); i++) {
                                String a = directs_ele.get(i).text().toString();
                                String b = directs_ele.get(i).attr("data-uuid").toString();
                                directs.add(a);
                                directs_code.add(b);
                            }
                            handler.sendEmptyMessage(Utils.SELECT_DIR);
                        }
                    }
                }).start();
            }
        });
        query_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lin = autocomText.getText().toString().trim();
                direct = (Directs) sel_dir.getSelectedItem();
                if (lin != "" && direct != null) {
                    dirCode = direct.getDir_code();

                    query_button.setClickable(false);

                    dir = direct.getDir();
                    url = Utils.URL3 + lin + Utils.URL3_EX1 + dirCode + Utils.URL3_EX2 + Utils.aboardStation;
                    new Thread(new Runnable() {
                        //Document doc = null;
                        @Override
                        public void run() {
                            //清空上次查询的数据
                            Utils.busStationId.clear();
                            Utils.busStations.clear();
                            Utils.busLocation.clear();
                            Utils.busAbstracts.clear();
                            //TODO
                            //参数2暂时随便写的，是上车站点（留做后期扩展）
                            response = ascii2native(sendHttpRequest(url)).replace("\\", "");

                            if (response.trim() == "" || response == null) {
                                handler.sendEmptyMessage(Utils.CLICKBLE);
                                Toast.makeText(MainActivity.this, "数据异常！", Toast.LENGTH_LONG).show();
                            } else if (response == "err") {
                                handler.sendEmptyMessage(Utils.CLICKBLE);
                                Toast.makeText(MainActivity.this, "数据异常！", Toast.LENGTH_LONG).show();
                            } else {
                                Element doc = Jsoup.parse(response);
                                Elements station_eles = doc.select("div").select("ul").select("li").select("div");
                                Elements abstract_eles = doc.select("article").select("p");
                                if (station_eles.size() > 0 && abstract_eles.size() > 0) {
                                    stations = new ArrayList<>();

                                    for (int i = 0; i < station_eles.size(); i++) {
                                        Element a = station_eles.get(i);
                                        stations.add(a);
                                    }
                                    for (int i = 0; i < abstract_eles.size(); i++) {
                                        Element b = abstract_eles.get(i);
                                        Utils.busAbstracts.add(b.text().toString().trim());
                                    }
                                }
                                //String showStr = "";
                                Element b;

                                for (int i = 0; i < stations.size(); i++) {
                                    b = stations.get(i);
                                    Utils.busStationId.add(b.select("div").attr("id").toString());
                                    Utils.busStations.add(b.select("div span").attr("title").toString());
                                    if (i % 2 == 0) {
                                        Utils.busLocation.add(b.select("div i.buss").parents().attr("id").toString());
                                    } else {
                                        Utils.busLocation.add(b.select("div i.busc").parents().attr("id").toString());
                                    }
                                    //showStr += ascii2native(busStations.get(i).toString() + "============" + busLocation.get(i).toString() + "\n");

                                }
                                handler.sendEmptyMessage(Utils.QUERY);
                            }
                        }
                    }).start();
                } else {
                    Toast.makeText(getApplicationContext(), "请选择公交线路", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        query_button.setClickable(true);
    }
}
