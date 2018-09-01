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

    private AutoCompleteTextView autocomText = null;
    private Spinner sel_dir = null;
    private Button query_button = null;
    private String[] text = null;
    ArrayList<String> directs;
    ArrayList<String> directs_code;
    public static String response;


    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == Utils.SELECT_LIN) {
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, text);
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
            }
            /*else if (msg.what == Utils.QUERY) {
                Intent intent = new Intent(MainActivity.this, BusMap.class);
                //intent.putExtra("stations", "stations");
                //intent.putExtra("abstracts", abstracts);
                startActivityForResult(intent, 1);
            }*/

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
                    doc = Jsoup.connect(Utils.URL1).get();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Elements ele_bus = doc.body().select("dd#selBLine a");
                String bus[] = new String[ele_bus.size()];
                for (int i = 0; i < ele_bus.size(); i++) {
                    bus[i] = ele_bus.get(i).text();
                }
                text = bus;

                handler.sendEmptyMessage(Utils.SELECT_LIN);
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
                if (autocomText.getText() != null && sel_dir.getSelectedItem() != null) {
//                    new Thread(new Runnable() {
//                        //Document doc = null;
//
//                        @Override
//                        public void run() {
                            String lin = autocomText.getText().toString();
                            Directs direct = (Directs) sel_dir.getSelectedItem();
                            String dir = direct.getDir_code();
                            String dirCode = direct.getDir();

                            //TODO
                            //参数2暂时随便写的，是上车站点（留做后期扩展）
                            //String url = Utils.URL3 + lin + Utils.URL3_EX1 + dir + Utils.URL3_EX2 + "2";
                            Intent intent = new Intent(MainActivity.this, BusMap.class);
                            //intent.putExtra("url", url);
                            if (directs_code.size() >= 1 && directs.size() >= 1) {
                                directs_code.remove(dirCode);
                                directs.remove(dir);

                                if(directs_code.size()==1||directs.size()==1) {
                                    String otherDir = directs.get(0);
                                    String otherDirCode = directs_code.get(0);
                                    intent.putExtra("otherDir", otherDir);
                                    intent.putExtra("otherDirCode", otherDirCode);
                                }else{
                                    intent.putExtra("otherDir", "0");
                                    intent.putExtra("otherDirCode", "0");
                                }
                                intent.putExtra("lin", lin);
                                intent.putExtra("dir", dir);
                                intent.putExtra("dirCode", dirCode);

                                startActivityForResult(intent, 1);
                            }else {
                                Toast.makeText(getApplicationContext(), "数据获取错误！", Toast.LENGTH_LONG).show();
                            }
                            //
//                            response = ascii2native(sendHttpRequest(url)).replace("\\", "");
//                            if (response.trim() != "" && response != null) {
//                                handler.sendEmptyMessage(Utils.QUERY);
//                            } else {
//                                Toast.makeText(getApplicationContext(), "发生错误！", Toast.LENGTH_LONG).show();
//
//                            }
//                        }
//                    }).start();
                } else {
                    Toast.makeText(getApplicationContext(), "请选择公交线路", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

}
