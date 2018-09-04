package com.yanleiz.waitbus.waitbus;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.yanleiz.waitbus.utils.Utils;

import java.util.Timer;
import java.util.TimerTask;

import static com.yanleiz.waitbus.utils.Utils.isInternetAvailable;

public class LaunchActivity extends Activity implements View.OnClickListener {


    private int recLen = 5;//跳过倒计时提示5秒
    private TextView tv;
    Timer timer = new Timer();
    private Handler handler;
    private Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //定义全屏参数
        //设置当前窗体为全屏显示
        // getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_launch);

        if (isInternetAvailable(LaunchActivity.this)) {
            Utils.getAllBus();

            initView();
            timer.schedule(task, 1000, 1000);//等待时间一秒，停顿时间一秒
            /**
             * 正常情况下不点击跳过
             */
            handler = new Handler();
            handler.postDelayed(runnable = new Runnable() {
                @Override
                public void run() {
                    if (isInternetAvailable(LaunchActivity.this)) {
                        //从闪屏界面跳转到首界面
                        Intent intent = new Intent(LaunchActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(LaunchActivity.this, "网络错误！", Toast.LENGTH_LONG).show();
                    }
                }
            }, 5000);//延迟5S后发送handler信息
        } else {
            Toast.makeText(LaunchActivity.this, "网络未联通！！", Toast.LENGTH_LONG).show();

        }
    }

    private void initView() {
        tv = findViewById(R.id.tv);//跳过
        tv.setOnClickListener(this);//跳过监听
    }

    TimerTask task = new TimerTask() {
        @Override
        public void run() {
            runOnUiThread(new Runnable() { // UI thread
                @Override
                public void run() {
                    recLen--;
                    tv.setText("跳过 " + recLen);
                    if (recLen < 0) {
                        timer.cancel();
                        tv.setVisibility(View.GONE);//倒计时到0隐藏字体
                    }
                }
            });
        }
    };

    /**
     * 点击跳过
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv:
                if (isInternetAvailable(LaunchActivity.this)) {
                    //从闪屏界面跳转到首界面
                    Intent intent = new Intent(LaunchActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(LaunchActivity.this, "网络错误！", Toast.LENGTH_LONG).show();

                }
                if (runnable != null) {
                    handler.removeCallbacks(runnable);
                }
                break;
            default:
                break;
        }
    }
}
