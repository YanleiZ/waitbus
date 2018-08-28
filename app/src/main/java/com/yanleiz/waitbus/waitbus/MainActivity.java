package com.yanleiz.waitbus.waitbus;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.yanleiz.waitbus.beans.Directs;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
////div[i[@class="buss"]]/@id
public class MainActivity extends AppCompatActivity {
    final int SELECT_LIN = 11;
    final int SELECT_DIR = 22;
    final int QUERY = 33;

    final static String URL1 = "http://www.bjbus.com/home/index.php";
    final static String URL2 = "http://www.bjbus.com/home/ajax_rtbus_data.php?act=getLineDir&selBLine=";
    final static String URL3 = "http://www.bjbus.com/home/ajax_rtbus_data.php?act=busTime&selBLine=";
    final static String URL3_EX1 = "&selBDir=";
    final static String URL3_EX2 = "&selBStop=";
    private AutoCompleteTextView autocomText = null;
    private Spinner sel_dir = null;
    private Button query_button = null;
    private String[] text = null;
    ArrayList<String> directs;
    ArrayList<String> directs_code;
    ArrayList<Element> stations;
    ArrayList<Element> abstracts;


    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == SELECT_LIN) {
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, text);
                autocomText.setAdapter(adapter);
                ////dd[@id="selBLine"]
                //ArrayList<Node> list = (ArrayList<Node>) doc.selectNodes("//dd[@id='selBLine']");
            } else if (msg.what == SELECT_DIR) {

                ArrayList list = new ArrayList();
                for (int i = 0; i < directs.size(); i++) {
                    list.add(new Directs(directs.get(i), directs_code.get(i)));
                }
                ArrayAdapter<Directs> adapter = new ArrayAdapter<Directs>(getApplicationContext(),
                        android.R.layout.simple_spinner_item, list);

                sel_dir.setAdapter(adapter);
            } else if (msg.what == QUERY) {
                Intent intent =new Intent(MainActivity.this,BusMap.class);
                intent.putExtra("stations",stations);
                intent.putExtra("abstracts",abstracts);
                startActivityForResult(intent,1);
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        startGet();


    }

    private void startGet() {
        new Thread(new Runnable() {
            Document doc = null;

            @Override
            public void run() {
                try {
                    doc = Jsoup.connect(URL1).get();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Elements ele_bus = doc.body().select("dd#selBLine a");
                String bus[] = new String[ele_bus.size()];
                for (int i = 0; i < ele_bus.size(); i++) {
                    bus[i] = ele_bus.get(i).text();
                }
                text = bus;

                handler.sendEmptyMessage(SELECT_LIN);
            }
        }).start();
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
                            doc = Jsoup.connect(URL2 + buslin).get();
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
                            handler.sendEmptyMessage(SELECT_DIR);
                        }
                    }
                }).start();
            }
        });
        query_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (autocomText.getText() != null && sel_dir.getSelectedItem() != null) {
                    new Thread(new Runnable() {
                        Document doc = null;

                        @Override
                        public void run() {
                            String lin = autocomText.getText().toString();
                            Directs direct = (Directs) sel_dir.getSelectedItem();
                            String dir = direct.getDir_code();
                            //参数2暂时随便写的，是上车站点
                            String url = URL3 + lin + URL3_EX1 + dir + URL3_EX2 + "2";
                            try {
                                doc = Jsoup.connect(url).get();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            Elements station_eles = doc.body().select("div ul li");
                            Elements abstract_eles = doc.body().select("article p");
                            if (station_eles != null && abstract_eles != null) {
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
                                handler.sendEmptyMessage(QUERY);
                            }
                        }
                    }).start();
                } else {
                    Toast.makeText(getApplicationContext(), "请选择公交线路", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
