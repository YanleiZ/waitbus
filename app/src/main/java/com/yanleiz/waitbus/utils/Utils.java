package com.yanleiz.waitbus.utils;

import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;

public class Utils {
    public final static int SELECT_LIN = 11;
    public final static int SELECT_DIR = 22;
    public final static int QUERY = 33;
    public final static int CLICKBLE = 44;

    public final static String URL1 = "http://www.bjbus.com/home/index.php";
    public final static String URL2 = "http://www.bjbus.com/home/ajax_rtbus_data.php?act=getLineDir&selBLine=";
    public final static String URL3 = "http://www.bjbus.com/home/ajax_rtbus_data.php?act=busTime&selBLine=";
    public final static String URL3_EX1 = "&selBDir=";
    public final static String URL3_EX2 = "&selBStop=";

    public final static ArrayList busStationId = new ArrayList();
    public final static ArrayList busStations = new ArrayList();
    public final static ArrayList busLocation = new ArrayList();
    public final static ArrayList busAbstracts = new ArrayList();

    public final static HashSet allBus = new HashSet();

    public static ArrayList<String> nearStation = new ArrayList<>();

    public static int aboardStation = 1;


    //public static ArrayList<Element> stations = new ArrayList<>();
    //public static ArrayList<Element> abstracts = new ArrayList<>();

    /**
     * Unicode转中文
     *
     * @param asciicode
     * @return
     */
    public static String ascii2native(String asciicode) {
        String[] asciis = asciicode.split("\\\\u");
        String nativeValue = asciis[0];
        try {
            for (int i = 1; i < asciis.length; i++) {
                String code = asciis[i];
                nativeValue += (char) Integer.parseInt(code.substring(0, 4), 16);
                if (code.length() > 4) {
                    nativeValue += code.substring(4, code.length());
                }
            }
        } catch (NumberFormatException e) {
            return asciicode;
        }
        return nativeValue;
    }

    /**
     * 发送网络请求获取返回内容
     *
     * @param address
     * @return
     */
    public static String sendHttpRequest(String address) {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(address);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(8000);
            connection.setReadTimeout(8000);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            int resCode = connection.getResponseCode();
            if (resCode == 200) {
                InputStream in = connection.getInputStream();
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(in));
                StringBuilder builder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);// 一行行的读取内容并追加到builder中去
                }
                return builder.toString();
            } else {
                return "err";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "err";
        } finally {
            if (connection != null) {
                connection.disconnect();//连接不为空就关闭连接
            }
        }
    }

    /**
     * 判断网络是否联通北京公交网站（不需要网络状态权限）
     *
     * @return
     */
    public static boolean isInternetAvailable(Context context) {

        ConnectivityManager manager = (ConnectivityManager) context
                .getApplicationContext().getSystemService(
                        Context.CONNECTIVITY_SERVICE);

        if (manager == null) {
            return false;
        }

        NetworkInfo networkinfo = manager.getActiveNetworkInfo();

        if (networkinfo == null || !networkinfo.isAvailable()) {
            return false;
        }

        return true;
    }

    public static void getAllBus() {

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
                for (int i = 0; i < ele_bus.size(); i++) {
                    Utils.allBus.add(ele_bus.get(i).text());
                }

                // handler.sendEmptyMessage(Utils.SELECT_LIN);
            }
        }).start();
    }

    /**
     * 判断GPS是否打开
     *
     * @param lm
     * @return
     */
    public static boolean isGpsAble(LocationManager lm) {
        return lm.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER) ? true : false;
    }
}
