package com.yanleiz.waitbus.utils;

import org.jsoup.nodes.Element;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class Utils {
    public final static int SELECT_LIN = 11;
    public final static int SELECT_DIR = 22;
    public final static int QUERY = 33;

    public final static String URL1 = "http://www.bjbus.com/home/index.php";
    public final static String URL2 = "http://www.bjbus.com/home/ajax_rtbus_data.php?act=getLineDir&selBLine=";
    public final static String URL3 = "http://www.bjbus.com/home/ajax_rtbus_data.php?act=busTime&selBLine=";
    public final static String URL3_EX1 = "&selBDir=";
    public final static String URL3_EX2 = "&selBStop=";

    public static ArrayList<Element> stations = new ArrayList<>();
    public static ArrayList<Element> abstracts = new ArrayList<>();
    /**
     * Unicode转中文
     * @param asciicode
     * @return
     */
    public static String ascii2native ( String asciicode )
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

    /**
     * 发送网络请求获取返回内容
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
            InputStream in = connection.getInputStream();
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(in));
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);// 一行行的读取内容并追加到builder中去
            }
            return builder.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        } finally {
            if (connection != null) {
                connection.disconnect();//连接不为空就关闭连接
            }
        }
    }
}
