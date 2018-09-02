package com.yanleiz.waitbus.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;


public class DrawView extends View {

    private Paint sPaint;
    private Paint sPaint2;
    private Paint fPaint;
    private Paint redPaint;

    public DrawView(Context context) {
        super(context);
        init();
    }

    public DrawView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DrawView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

        sPaint = new Paint();
        sPaint2 = new Paint();
        fPaint = new Paint();
        redPaint = new Paint();

        sPaint.setAntiAlias(true);          //抗锯齿
        sPaint2.setAntiAlias(true);          //抗锯齿
        fPaint.setAntiAlias(true);          //抗锯齿
        redPaint.setAntiAlias(true);          //抗锯齿

        sPaint.setColor(Color.GREEN);//画笔颜色
        sPaint2.setColor(Color.GREEN);//画笔颜色
        fPaint.setColor(Color.GREEN);//画笔颜色
        redPaint.setColor(Color.RED);//画笔颜色

        sPaint.setStyle(Paint.Style.STROKE);  //画笔风格STROKE
        sPaint2.setStyle(Paint.Style.STROKE);  //画笔风格STROKE
        fPaint.setStyle(Paint.Style.FILL);  //画笔风格
        redPaint.setStyle(Paint.Style.FILL);  //画笔风格

        sPaint.setTextSize(36);             //绘制文字大小，单位px
        sPaint2.setTextSize(36);             //绘制文字大小，单位px
        fPaint.setTextSize(40);             //绘制文字大小，单位px
        redPaint.setTextSize(40);             //绘制文字大小，单位px

        sPaint.setStrokeWidth(8);           //画笔粗细
        sPaint2.setStrokeWidth(2);           //画笔粗细
        fPaint.setStrokeWidth(4);           //画笔粗细
        redPaint.setStrokeWidth(4);           //画笔粗细

    }

    //重写该方法，在这里绘图
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int center = getWidth() / 10;

        //int innerCircle = dip2px(getContext(), 20); //设置内圆半径
        //int ringWidth = dip2px(getContext(), 5); //设置圆环宽度

        //绘制内圆
        //mPaint.setARGB(155, 167, 190, 206);
        //mPaint.setStrokeWidth(2);
        //canvas.drawCircle(center,center, innerCircle, mPaint);

        //绘制圆环
        //mPaint.setARGB(255, 212 ,225, 233);
        // mPaint.setStrokeWidth(ringWidth);
        int center1 = center;
        for (int i = 1; i <= Utils.busStations.size(); i++) {
//        canvas.drawCircle(0,0, 30, sPaint);
//        canvas.drawCircle(0,0, 20, sPaint2);
//        canvas.drawCircle(0,0, 10, fPaint);
            if (i % 2 == 1) {
                if (Utils.busLocation.get(i - 1).toString().trim() != "") {
                    canvas.drawCircle(center, center1, 30, redPaint);
                    canvas.drawCircle(center, center1, 18, fPaint);
                    canvas.drawLine(center + 34, center1 + 27, center * 4, center1 + 27, sPaint2);
                    canvas.drawText(Utils.busStations.get(i - 1).toString(), center * 4 + 4, center1 + 25, fPaint);
                } else {
                    canvas.drawCircle(center, center1, 30, sPaint);
                    canvas.drawCircle(center, center1, 20, fPaint);
                    canvas.drawLine(center + 34, center1 + 27, center * 4, center1 + 27, sPaint2);
                    canvas.drawText(Utils.busStations.get(i - 1).toString(), center * 4 + 4, center1 + 25, fPaint);
                }

                center1 += center;

                //mPaint.setARGB(100, 0, 0, 0);
                //mPaint.setStrokeWidth(2);
                //canvas.drawCircle(center,center, innerCircle+ringWidth, mPaint);
            } else {
                if (Utils.busLocation.get(i - 1).toString().trim() != "") {
                    canvas.drawLine(center, center1 - center + 30, center, center1 - 30, redPaint);
                } else {
                    canvas.drawLine(center, center1 - center + 30, center, center1 - 30, sPaint2);
                }
            }

        }
        postInvalidateDelayed(500);//300毫秒刷新
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getMeasuredWidth(), getMeasuredHeight());

    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }


}